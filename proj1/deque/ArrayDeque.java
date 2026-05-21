package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;
    /** Start is the starting index of the ArrayDeque */
    int start;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
        start = 2;
    }

    private void resize() {

        if (isEmpty()) {
            start = 2;
            return;
        }

        T[] a = (T[]) new Object[3 * size];
        System.arraycopy(items, start, a, size, size);
        start = size;
        items = a;
    }

    public void addFirst(T item) {
        if (start == 0) {
            resize();
        }
        items[start - 1] = item;
        start--;
        size++;
    }

    public void addLast(T item) {
        if (start + size == items.length) {
            resize();
        }
        items[start + size] = item;
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = start; i < start + size; i++) {
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        if (size < items.length / 2 && size >= 16) {
            resize();
        }

        T res = items[start];
        items[start] = null;
        start++;
        size--;
        return res;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }

        if (size < items.length / 2 && size >= 16) {
            resize();
        }

        T res = items[start + size - 1];
        items[start + size - 1] = null;
        size--;
        return res;
    }

    public T get(int index) {
        return items[index];
    }
}
