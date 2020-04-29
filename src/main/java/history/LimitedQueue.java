package history;

import java.util.LinkedList;

public class LimitedQueue<E> extends LinkedList<E> {
    private int limit;
    private boolean overflown = false;

    public LimitedQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        boolean added = super.add(o);
        while (added && size() > limit) {
            super.remove();
            overflown = true;
        }
        return added;
    }

    public boolean isOverflown() {
        return overflown;
    }
}