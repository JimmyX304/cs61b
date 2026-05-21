package deque;

public class ArrayDeque<T> {
    private T[] items;
    private int size;

    public ArrayDeque() {
        items = (T[]) new Object[8];
        size = 0;
    }

    private void resize(int capacity) {
        T[] a = (T[]) new Object[capacity];
        System.arraycopy(items, 0, a, 0, size);
        items = a;
    }

    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        for (int i = size; i >= 1; i--) {
            items[i] = items[i - 1];
        }
        items[0] = item;
        size++;
    }

    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[size] = item;
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        for (int i = 0; i < size; i++) {
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        if (size < items.length / 4 && items.length >= 32) {
            resize(items.length / 4);
        }

        T res = items[0];
        for (int i = 0; i < size - 1; i++) {
            items[i] = items[i + 1];
        }
        items[size - 1] = null;
        size--;
        return res;
    }

    public T removeLast() {
        if (size == 0) {
            return null;
        }

        if (size < items.length / 4 && items.length >= 32) {
            resize(items.length / 4);
        }

        T res = items[size - 1];
        items[size - 1] = null;
        size--;
        return res;
    }

    public T get(int index) {
        return items[index];
    }
}
