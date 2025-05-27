package ru.vk.itmo.solution;

import ru.vk.itmo.solution.iterator.PeekIterator;
import ru.vk.itmo.solution.iterator.SSTableIterator;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SSTable {

    public static final String SSTABLE_DIRECTORY = "sstable";
    public static final String INDEX = "index";
    public static final String DATA = "data";

    private final MemorySegment mappedIndex;
    private final MemorySegment mappedData;
    private final int priority;

    public SSTable(MemorySegment mappedIndex, MemorySegment mappedData, int priority) {
        this.mappedIndex = mappedIndex;
        this.mappedData = mappedData;
        this.priority = priority;
    }

    public static List<SSTable> fromFiles(Path path, Arena arena) throws IOException {
        List<SSTable> result = new ArrayList<>();
        Files.walkFileTree(path, Set.of(), 2, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                String currentDirName = dir.getFileName()
                                           .toString();
                if (currentDirName.startsWith(SSTABLE_DIRECTORY)) {
                    int priority = Integer.parseInt(currentDirName.substring(SSTABLE_DIRECTORY.length()));

                    Path dataFile = dir.resolve(DATA);
                    Path indexFile = dir.resolve(INDEX);
                    try (FileChannel dataChanel = FileChannel.open(dataFile, StandardOpenOption.READ)) {
                        try (FileChannel indexChanel = FileChannel.open(indexFile, StandardOpenOption.READ)) {
                            MemorySegment msIndex =
                                    indexChanel.map(FileChannel.MapMode.READ_ONLY, 0, Files.size(indexFile), arena);
                            MemorySegment msData =
                                    dataChanel.map(FileChannel.MapMode.READ_ONLY, 0, Files.size(dataFile), arena);
                            result.add(new SSTable(msIndex, msData, priority));
                        }
                    }
                }
                return super.postVisitDirectory(dir, exc);
            }
        });
        return result;
    }

    public PeekIterator iterator(MemorySegment from, MemorySegment to) {
        long indexOffset = from == null
                           ? 0
                           : binarySearch(from);
        SSTableIterator ssTableIterator =
                new SSTableIterator(from, to, mappedIndex, mappedData, this::compareMemorySegmentsWithOffsets,
                                    indexOffset);
        return new PeekIterator(ssTableIterator, priority);
    }

    private long binarySearch(MemorySegment searchedKey) {
        long left = 0;
        long right = mappedIndex.byteSize() / Long.BYTES - 1;

        while (left <= right) {
            long mid = ((right - left) >>> 1) + left;

            long offset = mappedIndex.get(ValueLayout.JAVA_LONG_UNALIGNED, mid * Long.BYTES);

            long currentSize = mappedData.get(ValueLayout.JAVA_LONG_UNALIGNED, offset);
            offset += Long.BYTES;

            int compareResult = compareMemorySegmentsWithOffsets(searchedKey, mappedData, offset, offset + currentSize);
            if (compareResult == 0) {
                return mid * Long.BYTES;
            }

            if (compareResult > 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return left * Long.BYTES;
    }

    private int compareMemorySegmentsWithOffsets(MemorySegment segment1,
                                                 MemorySegment segment2,
                                                 long fromOffset,
                                                 long toOffset) {
        long mismatchOffset = MemorySegment.mismatch(segment1, 0, segment1.byteSize(), segment2, fromOffset, toOffset);
        if (mismatchOffset == -1) {
            return 0;
        }
        if (mismatchOffset == segment1.byteSize()) {
            return -1;
        }
        if (mismatchOffset == segment2.byteSize()) {
            return 1;
        }
        byte first = segment1.get(ValueLayout.JAVA_BYTE, mismatchOffset);
        byte second = segment2.get(ValueLayout.JAVA_BYTE, fromOffset + mismatchOffset);
        return Byte.compare(first, second);
    }

    public int getPriority() {
        return priority;
    }
}
