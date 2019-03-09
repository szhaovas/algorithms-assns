/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.Stack;

/**
 *
 * @author zhaonick
 */
public class Board {

    private final int[] blocks;
    private int randomIndex1;
    private int randomIndex2;

    public Board(int[][] blocks) {
        // construct a board from an n-by-n array of blocks
        // (where blocks[i][j] = block in row i, column j)
        this.blocks = new int[(int) Math.pow(blocks.length, 2)];
        for (int i = 0; i < this.blocks.length; i++) {
            this.blocks[i] = blocks[i / blocks.length][i % blocks.length];
        }
        
        //ensures that two non-equal random indices that don't point to blank
        //blocks are selected
        randomIndex1 = StdRandom.uniform(blocks.length);
        randomIndex2 = StdRandom.uniform(blocks.length);
        while (this.blocks[randomIndex1] == 0) {
            randomIndex1 = StdRandom.uniform(this.blocks.length);
        }
        while ((this.blocks[randomIndex2] == 0) || (randomIndex2 == randomIndex1)) {
            randomIndex2 = StdRandom.uniform(this.blocks.length);
        }
    }

    public int dimension() {
        // board dimension n
        return (int) Math.sqrt(blocks.length);
    }

    public int hamming() {
        // number of blocks out of place
        int counter = 0;
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] == 0) continue;
            if (blocks[i] != (i + 1)) {
                counter++;
            }
        }
        return counter;
    }

    public int manhattan() {
        // sum of Manhattan distances between blocks and goal
        int sum = 0;
        int dim = dimension();
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] == 0) continue;
            sum += Math.abs((blocks[i] - 1) / dim - i / dim) + Math.abs((blocks[i] - 1) % dim - i % dim);
        }
        return sum;
    }

    public boolean isGoal() {
        // is this board the goal board?
        for (int i = 0; i < blocks.length - 1; i++) {
            if (blocks[i] != (i + 1)) {
                return false;
            }
        }
        return blocks[blocks.length - 1] == 0;
    }

    public Board twin() {
        // a board that is obtained by exchanging any pair of blocks
        int dim = dimension();

        //initialize and fill the new matrix used to construct twin
        //then build and return the twin object
        int[][] matrix = new int[dim][dim];
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                if (i * dim + j == randomIndex1) {
                    matrix[i][j] = blocks[randomIndex2];
                } else if (i * dim + j == randomIndex2) {
                    matrix[i][j] = blocks[randomIndex1];
                } else {
                    matrix[i][j] = blocks[i * dim + j];
                }
            }
        }
        Board twin = new Board(matrix);
        return twin;
    }

    @Override
    public boolean equals(Object y) {
        // does this board equal y?
        if (y == this) {
            return true;
        }
        if (y == null) {
            return false;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }
        Board that = (Board) y;
        if (that.dimension() != dimension()) return false;
        for (int i = 0; i < that.blocks.length; i++) {
            if (blocks[i] != that.blocks[i]) {
                return false;
            }
        }
        return true;
    }

    public Iterable<Board> neighbors() {
        // all neighboring boards
        Stack<Board> result = new Stack<>();
        int dim = dimension();
        int blankIndex = 0;
        for (int i = 0; i < blocks.length; i++) {
            if (blocks[i] == 0) {
                blankIndex = i;
                break;
            }
        }
        int upIndex = 0;
        int rightIndex = 0;
        int downIndex = 0;
        int leftIndex = 0;
        if (blankIndex / dim != 0) {
            upIndex = blankIndex - dim;
            int[][] upMatrix = new int[dim][dim];
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (i * dim + j == upIndex) {
                        upMatrix[i][j] = 0;
                    } else if (i * dim + j == blankIndex) {
                        upMatrix[i][j] = blocks[upIndex];
                    } else {
                        upMatrix[i][j] = blocks[i * dim + j];
                    }
                }
            }
            result.push(new Board(upMatrix));
        }
        if (blankIndex % dim != (dim - 1)) {
            rightIndex = blankIndex + 1;
            int[][] rightMatrix = new int[dim][dim];
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (i * dim + j == rightIndex) {
                        rightMatrix[i][j] = 0;
                    } else if (i * dim + j == blankIndex) {
                        rightMatrix[i][j] = blocks[rightIndex];
                    } else {
                        rightMatrix[i][j] = blocks[i * dim + j];
                    }
                }
            }
            result.push(new Board(rightMatrix));
        }
        if (blankIndex / dim != (dim - 1)) {
            downIndex = blankIndex + dim;
            int[][] downMatrix = new int[dim][dim];
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (i * dim + j == downIndex) {
                        downMatrix[i][j] = 0;
                    } else if (i * dim + j == blankIndex) {
                        downMatrix[i][j] = blocks[downIndex];
                    } else {
                        downMatrix[i][j] = blocks[i * dim + j];
                    }
                }
            }
            result.push(new Board(downMatrix));
        }
        if (blankIndex % dim != 0) {
            leftIndex = blankIndex - 1;
            int[][] leftMatrix = new int[dim][dim];
            for (int i = 0; i < dim; i++) {
                for (int j = 0; j < dim; j++) {
                    if (i * dim + j == leftIndex) {
                        leftMatrix[i][j] = 0;
                    } else if (i * dim + j == blankIndex) {
                        leftMatrix[i][j] = blocks[leftIndex];
                    } else {
                        leftMatrix[i][j] = blocks[i * dim + j];
                    }
                }
            }
            result.push(new Board(leftMatrix));
        }
        return result;
    }

    @Override
    public String toString() {
        // string representation of this board (in the output format specified below)
        StringBuilder s = new StringBuilder();
        int dim = dimension();
        s.append(dim + "\n");
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                s.append(String.format("%2d ", blocks[i * dim + j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

}
