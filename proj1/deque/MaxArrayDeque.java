package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> defaultComparator;

    public MaxArrayDeque(Comparator<T> c) {
        defaultComparator = c;
    }

    public T max() {
        return max(defaultComparator);
    }

    public T max(Comparator<T> c) {
        if (super.isEmpty()) {
            return null;
        }
        T best = super.get(0);
        for (int i = 1; i < super.size(); i++) {
            int cmp = c.compare(super.get(i), best);
            if (cmp > 0) {
                best = super.get(i);
            }
        }
        return best;
    }
}
