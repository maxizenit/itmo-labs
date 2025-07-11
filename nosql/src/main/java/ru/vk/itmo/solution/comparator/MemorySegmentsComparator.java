package ru.vk.itmo.solution.comparator;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.Comparator;

public final class MemorySegmentsComparator implements Comparator<MemorySegment> {

    private static final MemorySegmentsComparator INSTANCE = new MemorySegmentsComparator();

    private MemorySegmentsComparator() {}

    public static MemorySegmentsComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public int compare(MemorySegment memorySegment1, MemorySegment memorySegment2) {
        long mismatch = memorySegment1.mismatch(memorySegment2);
        if (mismatch == -1) {
            return 0;
        }

        if (mismatch == memorySegment1.byteSize()) {
            return -1;
        }

        if (mismatch == memorySegment2.byteSize()) {
            return 1;
        }
        byte b1 = memorySegment1.get(ValueLayout.JAVA_BYTE, mismatch);
        byte b2 = memorySegment2.get(ValueLayout.JAVA_BYTE, mismatch);
        return Byte.compare(b1, b2);
    }
}
