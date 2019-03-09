/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 *
 * @author zhaonick
 */
public class Permutation {
    
    public static void main(String args[]) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> queue = new RandomizedQueue();
        while (!StdIn.isEmpty()) {
            String item = StdIn.readString();
            queue.enqueue(item);
        }
        for (int i = 0; i < k; i++) {
            StdOut.println(queue.dequeue());
        }
    }
}