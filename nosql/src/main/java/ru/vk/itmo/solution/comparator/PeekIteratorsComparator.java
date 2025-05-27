package ru.vk.itmo.solution.comparator;

import ru.vk.itmo.solution.iterator.PeekIterator;

import java.util.Comparator;

public class PeekIteratorsComparator implements Comparator<PeekIterator> {

    private static final PeekIteratorsComparator INSTANCE = new PeekIteratorsComparator();

    private PeekIteratorsComparator() {}

    public static PeekIteratorsComparator getInstance() {
        return INSTANCE;
    }

    @Override
    public int compare(PeekIterator peekIterator1, PeekIterator peekIterator2) {
        return Comparator.comparing((PeekIterator peekIterator) -> peekIterator.peek()
                                                                               .key(),
                                    MemorySegmentsComparator.getInstance())
                         .thenComparing(PeekIterator::getPriority, Comparator.reverseOrder())
                         .compare(peekIterator1, peekIterator2);
    }
}
