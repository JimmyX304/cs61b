package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Iterable<T>, Deque<T> {
    private static class Node<T> {
        private T item;
        private Node prev;
        private Node next;

        Node(T i, Node p, Node n) {
            item = i;
            prev = p;
            next = n;
        }
    }

    private Node first;
    private Node last;
    private int size;

    public LinkedListDeque() {
        first = new Node(null, null, null);
        last = new Node(null, null, null);
        first.next = last;
        last.prev = first;
        first.next = last;
        last.prev = first;
        size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node added = new Node(item, first, first.next);
        first.next.prev = added;
        first.next = added;

        size++;
    }

    @Override
    public void addLast(T item) {
        Node added = new Node(item, last.prev, last);
        last.prev.next = added;
        last.prev = added;

        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = first;
        for (int i = 0; i < size; i++) {
            p = p.next;
            System.out.print(p.item + " ");
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (size == 0) {
            return null;
        }

        T res = (T) first.next.item;

        first.next.next.prev = first;
        first.next = first.next.next;

        size--;
        return res;
    }

    @Override
    public T removeLast() {
        if (size == 0) {
            return null;
        }

        T res = (T) last.prev.item;

        last.prev.prev.next = last;
        last.prev = last.prev.prev;

        size--;
        return res;
    }

    @Override
    public T get(int index) {
        if (index >= size) {
            return null;
        }

        Node p = first.next;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }

        return (T) p.item;
    }

    private T recurse(Node n, int index) {
        if (index == 0) {
            return (T) n.item;
        }
        return recurse(n.next, index - 1);
    }

    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return recurse(first.next, index);
    }


    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private Node wizPos;

        LinkedListIterator() {
            wizPos = first.next;
        }

        public boolean hasNext() {
            return wizPos != last;
        }

        public T next() {
            T returnItem = (T) wizPos.item;
            wizPos = wizPos.next;
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

            if (other.getClass().getName().equals("ArrayDeque")) {
                ArrayDeque<T> o = (ArrayDeque<T>) other;

                for (int i = 0; i < size; i++) {
                    if (!this.get(i).equals(o.get(i))) {
                        return false;
                    }
                }

                return true;
            }

            return false;
        }
        LinkedListDeque<T> o = (LinkedListDeque<T>) other;
        if (o.size() != this.size()) {
            return false;
        }

        Node n1 = this.first.next;
        Node n2 = o.first.next;

        for (int i = 0; i < size; i++) {
            if (!n1.item.equals(n2.item)) {
                return false;
            }
            n1 = n1.next;
            n2 = n2.next;
        }

        return true;
    }

    /** Testing for LinkedListDeque's iterator and equals methods */

    /*
    public static void main(String[] args) {
        LinkedListDeque<Integer> lld = new LinkedListDeque<>();
        lld.addLast(0);
        lld.addLast(100);
        lld.addFirst(1801);

        for (int i : lld) {
           System.out.println(i);
        }

        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();
        lld2.addLast(0);
        lld2.addLast(100);
        lld2.addFirst(1801);

        System.out.println(lld.equals(lld2));
        System.out.println(lld.equals(null));
        System.out.println(lld.equals("fish"));
        System.out.println(lld.equals(lld));
    }
     */
}
