package ru.vk.itmo.solution.iterator;

import ru.vk.itmo.Entry;
import ru.vk.itmo.solution.comparator.PeekIteratorsComparator;

import java.lang.foreign.MemorySegment;
import java.util.*;

public class MergeIterator implements Iterator<Entry<MemorySegment>> {

    private final Queue<PeekIterator> peekIterators;

    public MergeIterator(List<PeekIterator> peekIterators) {
        this.peekIterators = new PriorityQueue<>(peekIterators.size(), PeekIteratorsComparator.getInstance());
        List<PeekIterator> filteredIterators = peekIterators.stream()
                                                            .filter(Objects::nonNull)
                                                            .filter(Iterator::hasNext)
                                                            .toList();
        this.peekIterators.addAll(filteredIterators);
        skipDeletedEntry();
    }

    @Override
    public boolean hasNext() {
        return !peekIterators.isEmpty();
    }

    @Override
    public Entry<MemorySegment> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        PeekIterator it = peekIterators.remove();
        Entry<MemorySegment> current = it.next();
        removeEntry(current.key());
        if (it.hasNext()) {
            peekIterators.add(it);
        }
        skipDeletedEntry();
        return current;
    }

    private void skipDeletedEntry() {
        while (currentElementIsEmpty()) {
            PeekIterator headIterator = peekIterators.remove();
            removeEntry(headIterator.next()
                                    .key());
            if (headIterator.hasNext()) {
                peekIterators.add(headIterator);
            }
        }
    }

    private boolean currentElementIsEmpty() {
        if (peekIterators.isEmpty()) {
            return false;
        }
        PeekIterator headPeekIterator = peekIterators.peek();
        Entry<MemorySegment> headEntry = headPeekIterator.peek();
        return headEntry.value() == null;
    }

    private void removeEntry(MemorySegment key) {
        while (!peekIterators.isEmpty() && peekIterators.peek()
                                                        .peek()
                                                        .key()
                                                        .mismatch(key) == -1) {
            PeekIterator headPeekIterator = peekIterators.remove();
            headPeekIterator.next();
            if (headPeekIterator.hasNext()) {
                peekIterators.add(headPeekIterator);
            }
        }
    }
}
