package ru.vk.itmo.solution.iterator;

import ru.vk.itmo.Entry;

import java.lang.foreign.MemorySegment;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class PeekIterator implements Iterator<Entry<MemorySegment>> {

    private final Iterator<Entry<MemorySegment>> iterator;
    private final int priority;

    private Entry<MemorySegment> data;

    public PeekIterator(Iterator<Entry<MemorySegment>> iterator, int priority) {
        this.iterator = iterator;
        this.priority = priority;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext() || data != null;
    }

    @Override
    public Entry<MemorySegment> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        Entry<MemorySegment> next = peek();
        data = null;
        return next;
    }

    public Entry<MemorySegment> peek() {
        if (data == null) {
            data = iterator.next();
        }
        return data;
    }

    public int getPriority() {
        return priority;
    }
}
