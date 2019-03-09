/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Iterator;
import edu.princeton.cs.algs4.StdRandom;

/**
 *
 * @author zhaonick
 */
public class RandomizedQueue<Item> implements Iterable<Item> {
    
    private Item[] queue;
    private int size;

    public RandomizedQueue() {
        // construct an empty randomized queue
        queue = (Item[]) new Object[1];
        size = 0;
    }

    private class ListIterator implements Iterator<Item> {
        
        int iteratorSize;
        int[] indices;
        
        ListIterator() {
            indices = StdRandom.permutation(size);
            iteratorSize = size;
        }

        @Override
        public boolean hasNext() {
            return iteratorSize >= 1;
        }

        @Override
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public Item next() {
            if (!hasNext()) throw new java.util.NoSuchElementException();
            return queue[indices[--iteratorSize]];
        }

    }

    public boolean isEmpty() {
        // is the randomized queue empty?
        return size == 0;
    }

    public int size() {
        // return the number of items on the randomized queue
        return size;
    }

    public void enqueue(Item item) {
        // add the item
        if (item == null) {
            throw new java.lang.IllegalArgumentException();
        }
        if (size == queue.length) {
            Item[] copy = (Item[]) new Object[2 * queue.length];
            for (int i = 0; i < size; i++) {
                copy[i] = queue[i];
            }
            queue = copy;
        }
        queue[size++] = item;
    }

    public Item dequeue() {
        // remove and return a random item
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        int index = StdRandom.uniform(size);
        Item item = queue[index];
        queue[index] = queue[--size];
        queue[size] = null;
        if (size > 0 && size == queue.length / 4) {
            Item[] copy = (Item[]) new Object[queue.length / 2];
            for (int i = 0; i < size; i++) {
                copy[i] = queue[i];
            }
            queue = copy;
        }
        return item;
    }

    public Item sample() {
        // return a random item (but do not remove it)
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        int index = StdRandom.uniform(size);
        return queue[index];
    }

    @Override
    public Iterator<Item> iterator() {
        // return an independent iterator over items in random order
        return new ListIterator();
    }
}