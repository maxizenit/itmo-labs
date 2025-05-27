package ru.vk.itmo.solution.iterator;

import ru.vk.itmo.BaseEntry;
import ru.vk.itmo.Entry;
import ru.vk.itmo.solution.comparator.MemorySegmentsWithOffsetsComparator;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SSTableIterator implements Iterator<Entry<MemorySegment>> {

    private final MemorySegment to;
    private final MemorySegment mappedIndex;
    private final MemorySegment mappedData;

    private MemorySegmentsWithOffsetsComparator memorySegmentsWithOffsetsComparator;

    private long indexOffset;
    private long currentKeyOffset = -1;
    private long currentKeySize = -1;

    public SSTableIterator(MemorySegment from,
                           MemorySegment to,
                           MemorySegment mappedIndex,
                           MemorySegment mappedData,
                           MemorySegmentsWithOffsetsComparator memorySegmentsWithOffsetsComparator,
                           long indexOffset) {
        this.to = to;
        this.mappedIndex = mappedIndex;
        this.mappedData = mappedData;
        this.memorySegmentsWithOffsetsComparator = memorySegmentsWithOffsetsComparator;
        this.indexOffset = indexOffset;
    }

    @Override
    public boolean hasNext() {
        if (indexOffset == mappedIndex.byteSize()) {
            return false;
        }
        if (to == null) {
            return true;
        }
        currentKeyOffset = mappedIndex.get(ValueLayout.JAVA_LONG_UNALIGNED, indexOffset);
        currentKeySize = mappedData.get(ValueLayout.JAVA_LONG_UNALIGNED, currentKeyOffset);

        long fromOffset = currentKeyOffset + Long.BYTES;
        return memorySegmentsWithOffsetsComparator.compare(to, mappedData, fromOffset, fromOffset + currentKeySize) > 0;
    }

    @Override
    public Entry<MemorySegment> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        long keyOffset = getCurrentKeyOffset();
        long keySize = getCurrentKeySize(keyOffset);

        indexOffset += Long.BYTES;
        keyOffset += Long.BYTES;
        MemorySegment key = mappedData.asSlice(keyOffset, keySize);
        keyOffset += keySize;

        long valueSize = mappedData.get(ValueLayout.JAVA_LONG_UNALIGNED, keyOffset);
        MemorySegment value = valueSize == -1
                              ? null
                              : mappedData.asSlice(keyOffset + Long.BYTES, valueSize);
        return new BaseEntry<>(key, value);
    }

    private long getCurrentKeyOffset() {
        return currentKeyOffset == -1
               ? mappedIndex.get(ValueLayout.JAVA_LONG_UNALIGNED, indexOffset)
               : currentKeyOffset;
    }

    private long getCurrentKeySize(long keyOffset) {
        return currentKeySize == -1
               ? mappedData.get(ValueLayout.JAVA_LONG_UNALIGNED, keyOffset)
               : currentKeySize;
    }
}
