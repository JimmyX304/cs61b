package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Iterable<T>, Deque<T> {
    private T[] items;
    private int size;
    /** Start is the starting index of the ArrayDeque */
    private int start;

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

    @Override
    public void addFirst(T item) {
        if (start == 0) {
            resize();
        }
        items[start - 1] = item;
        start--;
        size++;
    }

    @Override
    public void addLast(T item) {
        if (start + size == items.length) {
            resize();
        }
        items[start + size] = item;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        for (int i = start; i < start + size; i++) {
            System.out.print(items[i] + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        if (size <= items.length / 4 && size >= 4) {
            resize();
        }

        T res = items[start];
        items[start] = null;
        start++;
        size--;
        return res;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        if (size <= items.length / 4 && size >= 4) {
            resize();
        }

        T res = items[start + size - 1];
        items[start + size - 1] = null;
        size--;
        return res;
    }

    @Override
    public T get(int index) {
        return items[start + index];
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }

    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;

        ArrayDequeIterator() {
            wizPos = start;
        }

        public boolean hasNext() {
            return wizPos < start + size;
        }

        public T next() {
            T returnItem = items[wizPos];
            wizPos++;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (other.getClass() != this.getClass()) {

            if (other.getClass().getName().equals("deque.LinkedListDeque")) {
                ArrayDeque<T> o = (ArrayDeque<T>) other;

                for (int i = 0; i < size; i++) {
                    if (!o.get(i).equals(this.get(i))) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }
        ArrayDeque<T> o = (ArrayDeque<T>) other;
        if (o.size() != this.size()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(o.get(i))) {
                return false;
            }
        }

        return true;
    }

    /** Testing for ArrayDeque's iterator and equals methods */

    /*
    public static void main(String[] args) {
        ArrayDeque<Integer> ad = new ArrayDeque<>();
        ad.addLast(0);
        ad.addLast(100);
        ad.addFirst(1801);

        for (int i : ad) {
            System.out.println(i);
        }

        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        ad2.addLast(0);
        ad2.addLast(100);
        ad2.addFirst(1801);

        System.out.println(ad.equals(ad2));
        System.out.println(ad.equals(null));
        System.out.println(ad.equals("fish"));
        System.out.println(ad.equals(ad));
    }
     */
}
