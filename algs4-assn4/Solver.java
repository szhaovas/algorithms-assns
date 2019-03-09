/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;
import java.util.Comparator;

/**
 *
 * @author zhaonick
 */
public class Solver {

    private MinPQ<Node> queue = new MinPQ<>(new nodeComparator());
    private int numMoves = 0;
    private Node lastNode;
    private boolean solvable;

    private class Node {

        final Board board;
        final int numSteps;
        final Node previous;
        final int manhattan;
        final boolean isTwin; //true if is twin, otherwise false

        Node(Board board, int numSteps, Node previous, boolean isTwin) {
            this.board = board;
            this.numSteps = numSteps;
            this.previous = previous;
            this.manhattan = board.manhattan();
            this.isTwin = isTwin;
        }

    }

    private class nodeComparator implements Comparator<Node> {

        @Override
        public int compare(Node a, Node b) {
            int aScore = a.manhattan + a.numSteps;
            int bScore = b.manhattan + b.numSteps;

            if (bScore > aScore) {
                return -1;
            } else if (bScore < aScore) {
                return 1;
            } else {
                return 0;
            }
        }

    }

    public Solver(Board initial) {
        // find a solution to the initial board (using the A* algorithm)
        if (initial == null) {
            throw new java.lang.IllegalArgumentException();
        }

        //insert the initial search node (the initial board, 0 moves,
        //and a null predecessor search node)
        //also insert the twin node
        queue.insert(new Node(initial, 0, null, false));
        queue.insert(new Node(initial.twin(), 0, null, true));

        //delete from the priority queue the search node with the minimum priority, 
        //and insert onto the priority queue all neighboring search nodes
        //until the search node dequeued corresponds to a goal board
        while (true) {
            lastNode = queue.delMin();
            if (lastNode.board.isGoal()) {
                if (!lastNode.isTwin) {
                    solvable = true;
                    numMoves = lastNode.numSteps;
                }
                if (lastNode.isTwin) {
                    solvable = false;
                    numMoves = -1;
                }
                break;
            }
            for (Board b : lastNode.board.neighbors()) {
                if (lastNode.previous == null || !b.equals(lastNode.previous.board)) {
                    queue.insert(new Node(b, lastNode.numSteps + 1, lastNode, lastNode.isTwin));
                }
            }
        }
    }

    public boolean isSolvable() {
        // is the initial board solvable?
        return solvable;
    }

    public int moves() {
        // min number of moves to solve initial board; -1 if unsolvable
        return numMoves;
    }

    public Iterable<Board> solution() {
        // sequence of boards in a shortest solution; null if unsolvable
        if (!solvable) return null;
        Stack<Board> result = new Stack<>();
        result.push(lastNode.board);
        resultBuilder(lastNode, result);
        return result;
    }

    private void resultBuilder(Node node, Stack result) {
        if (node.previous != null) {
            result.push(node.previous.board);
            resultBuilder(node.previous, result);
        }
    }

    public static void main(String[] args) {

        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                blocks[i][j] = in.readInt();
            }
        }
        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        }
    }

}
