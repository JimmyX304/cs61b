package deque;

public class LinkedListDeque<T> {
    private static class Node<T> {
        public T item;
        public Node prev;
        public Node next;

        public Node(T i, Node p, Node n) {
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
        size = 0;
    }

    public void addFirst(T item) {
        Node added = new Node(item, first, first.next);
        first.next.prev = added;
        first.next = added;

        size++;
    }

    public void addLast(T item) {
        Node added = new Node(item, last.prev, last);
        last.prev.next = added;
        last.prev = added;

        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void printDeque() {
        Node p = first;
        for (int i = 0; i < size; i++) {
            p = p.next;
            System.out.print(p.item + " ");
        }
        System.out.println();
    }

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

    public T get(int index) {

        if (index >= size) {
            return null;
        }

        Node p = first;
        for (int i = 0; i < index; i++) {
            p = p.next;
        }

        return (T) p.item;
    }

    public T recurse(Node n, int index) {
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
}
