package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> {
    Comparator<T> prevComparator;
    Deque<T> deque = new ArrayDeque<>();

    public MaxArrayDeque(Comparator<T> c) {
        prevComparator = c;
    }

    public T max() {
        if (deque.isEmpty()) {
            return null;
        }
        T best = deque.get(0);
        for (int i = 0; i < deque.size(); i++) {
            int cmp = prevComparator.compare(deque.get(i), best);
            if (cmp > 0) {
                best = deque.get(i);
            }
        }
        return best;
    }

    public T max(Comparator<T> c) {
        prevComparator = c;
        return max();
    }
}
