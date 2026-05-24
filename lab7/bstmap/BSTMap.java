package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V>{

    private int size;
    private BSTNode root;

    public BSTMap() {
        size = 0;
        root = null;
    }

    private class BSTNode {
        public K curKey;
        public V curVal;
        public BSTNode left;
        public BSTNode right;

        public BSTNode() {
            curKey = null;
            curVal = null;
            left = null;
            right = null;
        }

        public BSTNode(K key, V value) {
            curKey = key;
            curVal = value;
            left = null;
            right = null;
        }
    }


    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    private boolean containsKey(K key, BSTNode curNode) {
        if (curNode == null) {
            return false;
        }
        int cmp = curNode.curKey.compareTo(key);
        if (cmp == 0) {
            return true;
        } else if (cmp > 0) {
            return containsKey(key, curNode.left);
        } else {
            return containsKey(key, curNode.right);
        }
    }

    private V valueOfKey(K key, BSTNode curNode) {
        if (curNode == null) {
            return null;
        }
        int cmp = curNode.curKey.compareTo(key);
        if (cmp == 0) {
            return curNode.curVal;
        } else if (cmp > 0) {
            return valueOfKey(key, curNode.left);
        } else {
            return valueOfKey(key, curNode.right);
        }
    }

    @Override
    public boolean containsKey(K key) {
        return containsKey(key, root);
    }

    @Override
    public V get(K key) {
        return valueOfKey(key, root);
    }

    @Override
    public int size() {
        return size;
    }

    private boolean put(BSTNode putNode, BSTNode curNode) {
        if (curNode == null) {
            return true;
        }
        int cmp = curNode.curKey.compareTo(putNode.curKey);
        if (cmp > 0) {
            if (put(putNode, curNode.left)) {
                curNode.left = putNode;
            }
        } else {
            if (put(putNode, curNode.right)) {
                curNode.right = putNode;
            }
        }
        return false;
    }

    @Override
    public void put(K key, V value) {
        BSTNode putNode = new BSTNode(key, value);
        if (put(putNode, root)) {
            root = putNode;
        }
        size++;
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException();
    }

    private void recurse(BSTNode currentNode) {
        if (currentNode == null) {
            return;
        }
        System.out.print(currentNode.curKey + " ");
        recurse(currentNode.left);
        recurse(currentNode.right);
    }

    public void printInOrder() {
        recurse(root);
        System.out.println();
    }
}
