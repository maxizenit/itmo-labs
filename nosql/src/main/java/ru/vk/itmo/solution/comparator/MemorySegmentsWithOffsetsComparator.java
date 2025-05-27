package ru.vk.itmo.solution.comparator;

import java.lang.foreign.MemorySegment;

@FunctionalInterface
public interface MemorySegmentsWithOffsetsComparator {

    int compare(MemorySegment segment1, MemorySegment segment2, long fromOffset, long toOffset);
}
