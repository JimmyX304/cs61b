package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private Comparator<T> defaultComparator;
    private ArrayDeque<T> ad = new ArrayDeque<>();

    public MaxArrayDeque(Comparator<T> c) {
        defaultComparator = c;
    }

    public T max() {
        return max(defaultComparator);
    }

    public T max(Comparator<T> c) {
        if (ad.isEmpty()) {
            return null;
        }
        T best = ad.get(0);
        for (int i = 0; i < ad.size(); i++) {
            int cmp = c.compare(ad.get(i), best);
            if (cmp > 0) {
                best = ad.get(i);
            }
        }
        return best;
    }
}
