package ru.vk.itmo.solution;

import ru.vk.itmo.Config;
import ru.vk.itmo.Dao;
import ru.vk.itmo.Entry;
import ru.vk.itmo.solution.comparator.MemorySegmentsComparator;
import ru.vk.itmo.solution.iterator.MergeIterator;
import ru.vk.itmo.solution.iterator.PeekIterator;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class MemorySegmentDao implements Dao<MemorySegment, Entry<MemorySegment>> {


    private final List<SSTable> ssTables = new CopyOnWriteArrayList<>();
    private final Arena arena = Arena.ofShared();
    private final Path path;
    private final long flushThresholdBytes;

    private final ExecutorService flushExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService compactionExecutor = Executors.newSingleThreadExecutor();

    private final AtomicInteger nextTableId = new AtomicInteger(0);

    private final AtomicBoolean flushInProgress = new AtomicBoolean(false);
    private final AtomicBoolean compactionInProgress = new AtomicBoolean(false);
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private volatile NavigableMap<MemorySegment, Entry<MemorySegment>> storage =
            new ConcurrentSkipListMap<>(MemorySegmentsComparator.getInstance());
    private volatile NavigableMap<MemorySegment, Entry<MemorySegment>> flushPendingMap = null;

    private long currentMemoryUsage = 0;
    private volatile Future<?> flushFuture;
    private volatile Future<?> compactionFuture;

    public MemorySegmentDao(Config config) throws IOException {
        path = config.basePath();
        flushThresholdBytes = config.flushThresholdBytes();
        if (Files.exists(path)) {
            ssTables.addAll(SSTable.fromFiles(path, arena));
            int maxPriority = ssTables.stream()
                                      .mapToInt(SSTable::getPriority)
                                      .max()
                                      .orElse(-1);
            nextTableId.set(maxPriority + 1);
        } else {
            Files.createDirectories(path);
        }
    }

    @Override
    public Entry<MemorySegment> get(MemorySegment key) {
        if (closed.get()) {
            throw new IllegalStateException("DAO is closed");
        }

        if (path == null) {
            Entry<MemorySegment> result = null;
            if (flushPendingMap != null) {
                result = flushPendingMap.get(key);
            }
            if (result == null) {
                result = storage.get(key);
            }
            return (result != null && key.mismatch(result.key()) == -1)
                   ? result
                   : null;
        }

        Iterator<Entry<MemorySegment>> iterator = get(key, null);
        if (!iterator.hasNext()) {
            return null;
        }

        Entry<MemorySegment> result = iterator.next();
        return (key.mismatch(result.key()) == -1)
               ? result
               : null;
    }

    @Override
    public Iterator<Entry<MemorySegment>> get(MemorySegment from, MemorySegment to) {
        if (closed.get()) {
            throw new IllegalStateException("DAO is closed");
        }

        List<PeekIterator> iterators = new ArrayList<>();
        if (flushPendingMap != null) {
            NavigableMap<MemorySegment, Entry<MemorySegment>> pendingSubMap;
            if (from == null && to == null) {
                pendingSubMap = flushPendingMap;
            } else if (from == null) {
                pendingSubMap = flushPendingMap.headMap(to, false);
            } else if (to == null) {
                pendingSubMap = flushPendingMap.tailMap(from, true);
            } else {
                pendingSubMap = flushPendingMap.subMap(from, true, to, false);
            }
            iterators.add(new PeekIterator(pendingSubMap.values()
                                                        .iterator(), Integer.MAX_VALUE - 1));
        }

        NavigableMap<MemorySegment, Entry<MemorySegment>> storageSubMap;
        if (from == null && to == null) {
            storageSubMap = storage;
        } else if (from == null) {
            storageSubMap = storage.headMap(to, false);
        } else if (to == null) {
            storageSubMap = storage.tailMap(from, true);
        } else {
            storageSubMap = storage.subMap(from, true, to, false);
        }

        iterators.add(new PeekIterator(storageSubMap.values()
                                                    .iterator(), Integer.MAX_VALUE));
        iterators.addAll(getIteratorsFromSSTables(from, to));
        return new MergeIterator(iterators);
    }

    @Override
    public void upsert(Entry<MemorySegment> entry) {
        if (closed.get()) {
            throw new IllegalStateException("DAO is closed");
        }

        MemorySegment key = entry.key();
        MemorySegment value = entry.value();
        long keySize = key.byteSize();
        long newValueSize = (value == null)
                            ? 0
                            : value.byteSize();

        synchronized (this) {
            if (flushInProgress.get()) {
                Entry<MemorySegment> oldEntry = storage.get(key);
                long oldValueSize = (oldEntry == null || oldEntry.value() == null)
                                    ? 0
                                    : oldEntry.value()
                                              .byteSize();
                long newUsage = (oldEntry == null)
                                ? currentMemoryUsage + keySize + newValueSize + 2 * Long.BYTES
                                : currentMemoryUsage + (newValueSize - oldValueSize);
                if (newUsage > flushThresholdBytes) {
                    throw new IllegalStateException("Memtable overflow: flush in progress and threshold exceeded");
                }
                Entry<MemorySegment> replaced = storage.put(key, entry);
                if (replaced != null) {
                    long replacedValueSize = (replaced.value() == null)
                                             ? 0
                                             : replaced.value()
                                                       .byteSize();
                    currentMemoryUsage += (newValueSize - replacedValueSize);
                } else {
                    currentMemoryUsage += keySize + newValueSize + 2 * Long.BYTES;
                }
            } else {
                Entry<MemorySegment> replaced = storage.put(key, entry);
                if (replaced != null) {
                    long replacedValueSize = (replaced.value() == null)
                                             ? 0
                                             : replaced.value()
                                                       .byteSize();
                    currentMemoryUsage += (newValueSize - replacedValueSize);
                } else {
                    currentMemoryUsage += keySize + newValueSize + 2 * Long.BYTES;
                }
                if (currentMemoryUsage > flushThresholdBytes) {
                    flushInProgress.set(true);
                    flushPendingMap = storage;
                    storage = new ConcurrentSkipListMap<>(MemorySegmentsComparator.getInstance());
                    currentMemoryUsage = 0;
                    flushFuture = flushExecutor.submit(this::flushToDisk);
                }
            }
        }
    }

    @Override
    public void flush() {
        if (closed.get()) {
            throw new IllegalStateException("DAO is closed");
        }

        Future<?> ongoingFlush;

        synchronized (this) {
            if (storage.isEmpty() && flushPendingMap == null) {
                return;
            }
            if (flushInProgress.get()) {
                ongoingFlush = flushFuture;
            } else {
                flushInProgress.set(true);
                flushPendingMap = storage;
                storage = new ConcurrentSkipListMap<>(MemorySegmentsComparator.getInstance());
                currentMemoryUsage = 0;
                flushFuture = flushExecutor.submit(this::flushToDisk);
                return;
            }
        }

        try {
            ongoingFlush.get();
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread()
                      .interrupt();
            }
            throw new RuntimeException(e);
        }

        synchronized (this) {
            if (storage.isEmpty()) {
                return;
            }
            flushInProgress.set(true);
            flushPendingMap = storage;
            storage = new ConcurrentSkipListMap<>(MemorySegmentsComparator.getInstance());
            currentMemoryUsage = 0;
            flushFuture = flushExecutor.submit(this::flushToDisk);
        }
    }

    @Override
    public void compact() {
        if (closed.get()) {
            throw new IllegalStateException("DAO is closed");
        }

        if (compactionInProgress.getAndSet(true)) {
            return;
        }

        compactionFuture = compactionExecutor.submit(() -> {
            try {
                List<PeekIterator> iterators = new ArrayList<>(getIteratorsFromSSTables(null, null));
                if (iterators.isEmpty()) {
                    ssTables.clear();
                    return;
                }

                Iterator<Entry<MemorySegment>> mergedIterator = new MergeIterator(iterators);
                List<Entry<MemorySegment>> entriesToWrite = new ArrayList<>();
                while (mergedIterator.hasNext()) {
                    Entry<MemorySegment> entry = mergedIterator.next();
                    if (entry.value() != null) {
                        entriesToWrite.add(entry);
                    }
                }

                int oldTableCount = ssTables.size();
                List<Path> oldTableDirs = new ArrayList<>();
                for (int i = 0; i < oldTableCount; i++) {
                    oldTableDirs.add(path.resolve(SSTable.SSTABLE_DIRECTORY + i));
                }

                if (entriesToWrite.isEmpty()) {
                    deleteOldTableDirs(oldTableDirs);
                    ssTables.clear();
                    return;
                }

                int newTableIndex = nextTableId.getAndIncrement();
                try {
                    writeSSTable(entriesToWrite, newTableIndex);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }

                deleteOldTableDirs(oldTableDirs);

                Path newDir = path.resolve(SSTable.SSTABLE_DIRECTORY + newTableIndex);
                Path firstDir = path.resolve(SSTable.SSTABLE_DIRECTORY + 0);
                if (Files.exists(newDir)) {
                    try {
                        Files.move(newDir, firstDir, StandardCopyOption.REPLACE_EXISTING);
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                }

                ssTables.clear();
                try {
                    ssTables.addAll(SSTable.fromFiles(path, arena));
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            } finally {
                compactionInProgress.set(false);
            }
        });
    }

    @Override
    public void close() throws IOException {
        if (!closed.compareAndSet(false, true)) {
            return;
        }

        try {
            synchronized (this) {
                if (!storage.isEmpty() && !flushInProgress.get()) {
                    flushInProgress.set(true);
                    flushPendingMap = storage;
                    storage = new ConcurrentSkipListMap<>(MemorySegmentsComparator.getInstance());
                    currentMemoryUsage = 0;
                    flushFuture = flushExecutor.submit(this::flushToDisk);
                }
            }

            if (flushFuture != null) {
                try {
                    flushFuture.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread()
                          .interrupt();
                }
            }

            if (compactionFuture != null) {
                try {
                    compactionFuture.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                } catch (InterruptedException e) {
                    Thread.currentThread()
                          .interrupt();
                }
            }
        } finally {
            flushExecutor.shutdown();
            compactionExecutor.shutdown();
            if (arena.scope()
                     .isAlive()) {
                arena.close();
            }
        }
    }

    private void flushToDisk() {
        NavigableMap<MemorySegment, Entry<MemorySegment>> mapToFlush = flushPendingMap;
        List<Entry<MemorySegment>> entries = new ArrayList<>(mapToFlush.size());
        for (Entry<MemorySegment> entry : mapToFlush.values()) {
            entries.add(entry);
        }
        int tableIndex = nextTableId.getAndIncrement();
        try {
            writeSSTable(entries, tableIndex);
            Path ssTableDir = path.resolve(SSTable.SSTABLE_DIRECTORY + tableIndex);
            try (FileChannel dataChannel = FileChannel.open(ssTableDir.resolve(SSTable.DATA), StandardOpenOption.READ);
                 FileChannel indexChannel = FileChannel.open(ssTableDir.resolve(SSTable.INDEX),
                                                             StandardOpenOption.READ)) {
                MemorySegment msData = dataChannel.map(FileChannel.MapMode.READ_ONLY, 0, dataChannel.size(), arena);
                MemorySegment msIndex = indexChannel.map(FileChannel.MapMode.READ_ONLY, 0, indexChannel.size(), arena);
                SSTable newTable = new SSTable(msIndex, msData, tableIndex);
                ssTables.add(newTable);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            flushPendingMap = null;
            flushInProgress.set(false);
        }
    }

    private void writeSSTable(List<Entry<MemorySegment>> entries, int tableIndex) throws IOException {
        if (entries.isEmpty()) {
            return;
        }

        long indexSize = (long) entries.size() * Long.BYTES;
        long storageSize = entries.stream()
                                  .mapToLong(entry -> {
                                      long keySize = entry.key()
                                                          .byteSize();
                                      long valueSize = (entry.value() == null)
                                                       ? 0
                                                       : entry.value()
                                                              .byteSize();
                                      return keySize + valueSize + 2L * Long.BYTES;
                                  })
                                  .sum();

        Path ssTableDir = path.resolve(SSTable.SSTABLE_DIRECTORY + tableIndex);
        Files.createDirectories(ssTableDir);

        try (FileChannel fcTable = FileChannel.open(ssTableDir.resolve(SSTable.DATA), StandardOpenOption.WRITE,
                                                    StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING,
                                                    StandardOpenOption.CREATE);
             FileChannel fcIndex = FileChannel.open(ssTableDir.resolve(SSTable.INDEX), StandardOpenOption.WRITE,
                                                    StandardOpenOption.READ, StandardOpenOption.TRUNCATE_EXISTING,
                                                    StandardOpenOption.CREATE);
             Arena arenaForWriting = Arena.ofConfined()) {

            MemorySegment msData = fcTable.map(FileChannel.MapMode.READ_WRITE, 0, storageSize, arenaForWriting);
            MemorySegment msIndex = fcIndex.map(FileChannel.MapMode.READ_WRITE, 0, indexSize, arenaForWriting);

            long indexOffset = 0;
            long dataOffset = 0;
            for (Entry<MemorySegment> entry : entries) {
                msIndex.set(ValueLayout.JAVA_LONG_UNALIGNED, indexOffset, dataOffset);
                indexOffset += Long.BYTES;

                writeDataToMemorySegment(entry.key(), msData, dataOffset);
                dataOffset = getNextOffsetAfterInsertion(entry.key(), dataOffset);

                writeDataToMemorySegment(entry.value(), msData, dataOffset);
                dataOffset = getNextOffsetAfterInsertion(entry.value(), dataOffset);
            }
        }
    }

    private void writeDataToMemorySegment(MemorySegment dataToInsert, MemorySegment ms, long currentOffset) {
        if (dataToInsert == null) {
            ms.set(ValueLayout.JAVA_LONG_UNALIGNED, currentOffset, -1);
            return;
        }

        long dataSize = dataToInsert.byteSize();
        ms.set(ValueLayout.JAVA_LONG_UNALIGNED, currentOffset, dataSize);
        MemorySegment.copy(dataToInsert, 0, ms, currentOffset + Long.BYTES, dataSize);
    }

    private long getNextOffsetAfterInsertion(MemorySegment dataToInsert, long currentOffset) {
        long result = currentOffset + Long.BYTES;
        if (dataToInsert == null) {
            return result;
        }
        return result + dataToInsert.byteSize();
    }

    private List<PeekIterator> getIteratorsFromSSTables(MemorySegment from, MemorySegment to) {
        return ssTables.stream()
                       .map(table -> table.iterator(from, to))
                       .toList();
    }

    private void deleteOldTableDirs(List<Path> oldTableDirs) {
        for (Path dir : oldTableDirs) {
            if (Files.exists(dir)) {
                try (Stream<Path> paths = Files.walk(dir)) {
                    paths.sorted(Comparator.reverseOrder())
                         .forEach(p -> {
                             try {
                                 Files.deleteIfExists(p);
                             } catch (IOException e) {
                                 throw new UncheckedIOException(e);
                             }
                         });
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }
        }
    }
}
