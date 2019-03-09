/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.Iterator;

/**
 *
 * @author zhaonick
 */
public class Deque<Item> implements Iterable<Item> {

    //previous is last, next is first
    private Node sentinel;
    private int size;

    public Deque() {
        // construct an empty deque
        sentinel = new Node();
        sentinel.previous = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }
    
    private class Node {
        Item item;
        Node next;
        Node previous;
    }
    
    private class ListIterator implements Iterator<Item> {
        
        private Node current = sentinel.next;
        private int index = 0;

        @Override
        public boolean hasNext() {
            return index < size;
        }
        
        @Override
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }

        @Override
        public Item next() {
            if (!hasNext()) throw new java.util.NoSuchElementException();
            Item item = current.item;
            current = current.next;
            index++;
            return item;
        }
        
    }

    public boolean isEmpty() {
        // is the deque empty?
        return size == 0;
    }

    public int size() {
        // return the number of items on the deque
        return size;
    }

    public void addFirst(Item item) {
        // add the item to the front
        if (item == null) throw new java.lang.IllegalArgumentException();
        Node newFirst = new Node();
        newFirst.item = item;
        newFirst.previous = sentinel;
        newFirst.next = sentinel.next;
        sentinel.next.previous = newFirst;
        sentinel.next = newFirst;
        size++;
    }

    public void addLast(Item item) {
        // add the item to the end
        if (item == null) throw new java.lang.IllegalArgumentException();
        Node newLast = new Node();
        newLast.item = item;
        newLast.next = sentinel;
        newLast.previous = sentinel.previous;
        sentinel.previous.next = newLast;
        sentinel.previous = newLast;
        size++;
    }

    public Item removeFirst() {
        // remove and return the item from the front
        if (isEmpty()) throw new java.util.NoSuchElementException();
        Item firstItem = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.previous = sentinel;
        size--;
        return firstItem;  
    }

    public Item removeLast() {
        // remove and return the item from the end
        if (isEmpty()) throw new java.util.NoSuchElementException();
        Item lastItem = sentinel.previous.item;
        sentinel.previous = sentinel.previous.previous;
        sentinel.previous.next = sentinel;
        size--;
        return lastItem;
    }

    @Override
    public Iterator<Item> iterator() {
        // return an iterator over items in order from front to end
        return new ListIterator();
    }
}