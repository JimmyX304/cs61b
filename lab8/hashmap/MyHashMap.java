package hashmap;

import java.lang.reflect.Array;
import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {
    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private int size;
    private double loadFactor;
    private Collection<Node>[] buckets;
    private int bucketSize;

    /** Constructors */
    public MyHashMap() {
        clear();
    }

    public MyHashMap(int initialSize) {
        size = 0;
        loadFactor = 0.75;
        buckets = createTable(initialSize);
        bucketSize = initialSize;
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = 0;
        loadFactor = maxLoad;
        buckets = createTable(initialSize);
        bucketSize = initialSize;
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] newBuckets = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            newBuckets[i] = createBucket();
        }
        return newBuckets;
    }

    private int getHash(K key) {
        int hash = key.hashCode() % bucketSize;
        hash += bucketSize;
        return hash % bucketSize;
    }

    @Override
    public void clear() {
        size = 0;
        loadFactor = 0.75;
        buckets = createTable(16);
        bucketSize = 16;
    }

    @Override
    public boolean containsKey(K key) {
        int hash = getHash(key);

        for (Node n : buckets[hash]) {
            if (n.key.equals(key)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public V get(K key) {
        int hash = getHash(key);

        for (Node n : buckets[hash]) {
            if (n.key.equals(key)) {
                return n.value;
            }
        }

        return null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void put(K key, V value) {
        if (size >= Math.round(loadFactor * bucketSize)) {
            Collection<Node>[] newBucket = createTable(2 * bucketSize);
            for (int i = 0; i < bucketSize; i++) {
                for (Node n : buckets[i]) {
                    newBucket[getHash(n.key)].add(n);
                }
            }
            buckets = newBucket;
            bucketSize *= 2;
        }

        int hash = getHash(key);
        if (containsKey(key)) {
            for (Node n : buckets[hash]) {
                if (n.key.equals(key)) {
                    n.value = value;
                    break;
                }
            }
        } else {
            buckets[hash].add(createNode(key, value));
            size++;
        }
    }

    @Override
    public Set<K> keySet() {
        Set<K> elementSet = new HashSet<>();
        for (int i = 0; i < bucketSize; i++) {
            for (Node curNode : buckets[i]) {
                elementSet.add(curNode.key);
            }
        }

        return elementSet;
    }

    @Override
    public V remove(K key) {

        int hash = getHash(key);
        for (Node n : buckets[hash]) {
            if (n.key.equals(key)) {
                V res = n.value;
                buckets[hash].remove(n);
                return res;
            }
        }

        return null;
    }

    @Override
    public V remove(K key, V value) {
        int hash = getHash(key);
        Node n = createNode(key, value);
        if (buckets[hash].remove(n)) {
            return value;
        }
        return null;
    }

    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator<K> {
        private int wizPos;
        Object[] elements = keySet().toArray();

        public MyHashMapIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public K next() {
            K returnItem = (K) elements[wizPos];
            wizPos += 1;
            return returnItem;
        }
    }
}
