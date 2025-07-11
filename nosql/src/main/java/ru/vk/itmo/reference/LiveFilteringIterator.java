package ru.vk.itmo.reference;

import ru.vk.itmo.Entry;

import java.lang.foreign.MemorySegment;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Filters non tombstone {@link Entry}s.
 *
 */
public final class LiveFilteringIterator implements Iterator<Entry<MemorySegment>> {
    private final Iterator<Entry<MemorySegment>> delegate;
    private Entry<MemorySegment> next;

    public LiveFilteringIterator(final Iterator<Entry<MemorySegment>> delegate) {
        this.delegate = delegate;
        skipTombstones();
    }

    private void skipTombstones() {
        while (delegate.hasNext()) {
            final Entry<MemorySegment> entry = delegate.next();
            if (entry.value() != null) {
                this.next = entry;
                break;
            }
        }
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public Entry<MemorySegment> next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // Consume
        final Entry<MemorySegment> result = next;
        next = null;

        skipTombstones();

        return result;
    }
}
