package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    Comparator<T> prevComparator;
    ArrayDeque<T> ad = new ArrayDeque<>();

    public MaxArrayDeque(Comparator<T> c) {
        prevComparator = c;
    }

    public T max() {
        if (ad.isEmpty()) {
            return null;
        }
        T best = ad.get(0);
        for (int i = 0; i < ad.size(); i++) {
            int cmp = prevComparator.compare(ad.get(i), best);
            if (cmp > 0) {
                best = ad.get(i);
            }
        }
        return best;
    }

    public T max(Comparator<T> c) {
        prevComparator = c;
        return max();
    }
}
