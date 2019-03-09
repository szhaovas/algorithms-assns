/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.algs4.Picture;

/**
 *
 * @author zhaonick
 */
public class SeamCarver {

    private int[][] color;
    private double[][] energy;
    //these are width and height of the energy matrix, not color matrix
    private int width;
    private int height;    
    private boolean transposed;

    public SeamCarver(Picture picture) {
        // create a seam carver object based on the given picture
        if (picture == null) {
            throw new java.lang.IllegalArgumentException();
        }

        width = picture.width();
        height = picture.height();
        color = new int[width][height];
        energy = new double[width][height];
        transposed = false;

        for (int i = 0; i < width; i++) {
            color[i][0] = picture.getRGB(i, 0);
            color[i][height - 1] = picture.getRGB(i, height - 1);
            energy[i][0] = 1000;
            energy[i][height - 1] = 1000;
        }

        for (int j = 1; j < height - 1; j++) {
            color[0][j] = picture.getRGB(0, j);
            color[width - 1][j] = picture.getRGB(width - 1, j);
            energy[0][j] = 1000;
            energy[width - 1][j] = 1000;
            for (int i = 1; i < width - 1; i++) {
                color[i][j] = picture.getRGB(i, j);
                int left_RGB = color[i - 1][j];
                int right_RGB = picture.getRGB(i + 1, j);
                int above_RGB = color[i][j - 1];
                int below_RGB = picture.getRGB(i, j + 1);
                int rx = ((left_RGB & 0xff0000) >> 16) - ((right_RGB & 0xff0000) >> 16);
                int gx = ((left_RGB & 0xff00) >> 8) - ((right_RGB & 0xff00) >> 8);
                int bx = (left_RGB & 0xff) - (right_RGB & 0xff);
                int ry = ((above_RGB & 0xff0000) >> 16) - ((below_RGB & 0xff0000) >> 16);
                int gy = ((above_RGB & 0xff00) >> 8) - ((below_RGB & 0xff00) >> 8);
                int by = (above_RGB & 0xff) - (below_RGB & 0xff);
                energy[i][j] = Math.sqrt(rx * rx + gx * gx + bx * bx + ry * ry + gy * gy + by * by);
            }
        }
    }

    public Picture picture() {
        // current picture
        if (transposed) {
            Picture pic = new Picture(height, width);
            for (int r = 0; r < width; r++) {
                for (int c = 0; c < height; c++) {
                    pic.setRGB(c, r, color[c][r]);
                }
            }
            return pic;
        }
        else {
            Picture pic = new Picture(width, height);
            for (int r = 0; r < height; r++) {
                for (int c = 0; c < width; c++) {
                    pic.setRGB(c, r, color[c][r]);
                }
            }
            return pic;
        }
    }

    public int width() {
        // width of current picture
        if (transposed) {
            return height;
        }
        else {
            return width;
        }
    }

    public int height() {
        // height of current picture
        if (transposed) {
            return width;
        }
        else {
            return height;
        }
    }

    public double energy(int x, int y) {
        // energy of pixel at column x and row y
        if (x < 0 || y < 0) {
            throw new java.lang.IllegalArgumentException();
        }
        if (transposed) {
            if (x >= height || y >= width) {
                throw new java.lang.IllegalArgumentException();
            }
            return energy[y][x];
        }
        else {
            if (x >= width || y >= height) {
                throw new java.lang.IllegalArgumentException();
            }
            return energy[x][y];
        }
    }

    private void transposeEnergy() {
        int tempW = width;
        width = height;
        height = tempW;

        double[][] transpose = new double[width][height];
        for (int j = 0; j < height; j++) {
            for (int i = 0; i < width; i++) {
                transpose[i][j] = energy[j][i];
            }
        }
        energy = transpose;
        transposed = !transposed;
    }

    public int[] findHorizontalSeam() {
        // sequence of indices for horizontal seam
        if (!transposed) {
            transposeEnergy();
        }
        return verticalSeamSearcher();
    }

    public int[] findVerticalSeam() {
        // sequence of indices for vertical seam
        if (transposed) {
            transposeEnergy();
        }
        return verticalSeamSearcher();
    }

    private int[] verticalSeamSearcher() {
        if (width == 1) {
            return new int[height];
        }
        
        //construct the energyTo and edgeTo arrays
        double[][] energyTo = new double[width][height];
        int[][] edgeTo = new int[width][height];
        
        //donnot update anything for the first row
        for (int j = 1; j < height; j++) {
            //start of the row
            //upper_middle < upper_right
            if (energyTo[0][j - 1] < energyTo[1][j - 1]) {
                energyTo[0][j] = energyTo[0][j - 1] + 1000;
                edgeTo[0][j] = 0;
            } //upper_right <= upper_middle
            else {
                energyTo[0][j] = energyTo[1][j - 1] + 1000;
                edgeTo[0][j] = 1;
            }

            //end of the row
            //upper_left < upper_middle
            if (energyTo[width - 2][j - 1] < energyTo[width - 1][j - 1]) {
                energyTo[width - 1][j] = energyTo[width - 2][j - 1] + 1000;
                edgeTo[width - 1][j] = width - 2;
            } //upper_middle <= upper_left
            else {
                energyTo[width - 1][j] = energyTo[width - 1][j - 1] + 1000;
                edgeTo[width - 1][j] = width - 1;
            }

            //rest of the row
            for (int i = 1; i < width - 1; i++) {
                double upper_left = energyTo[i - 1][j - 1];
                double upper_middle = energyTo[i][j - 1];
                double upper_right = energyTo[i + 1][j - 1];
                if (upper_left < upper_middle) {
                    //left < middle < right
                    if (upper_middle < upper_right) {
                        energyTo[i][j] = upper_left + energy[i][j];
                        edgeTo[i][j] = i - 1;
                    } //left < right <= middle
                    else if (upper_left < upper_right) {
                        energyTo[i][j] = upper_left + energy[i][j];
                        edgeTo[i][j] = i - 1;
                    } //right <= left < middle
                    else {
                        energyTo[i][j] = upper_right + energy[i][j];
                        edgeTo[i][j] = i + 1;
                    }
                } else {
                    //middle <= left < right
                    if (upper_left < upper_right) {
                        energyTo[i][j] = upper_middle + energy[i][j];
                        edgeTo[i][j] = i;
                    } //middle < right <= left
                    else if (upper_middle < upper_right) {
                        energyTo[i][j] = upper_middle + energy[i][j];
                        edgeTo[i][j] = i;
                    } //right <= middle <= left
                    else {
                        energyTo[i][j] = upper_right + energy[i][j];
                        edgeTo[i][j] = i + 1;
                    }
                }
            }
        }

        // sequence of indices for vertical seam
        double min_bottom_energyTo = Double.MAX_VALUE;
        int seam_tail = 0;
        for (int i = 0; i < width; i++) {
            if (energyTo[i][height - 1] < min_bottom_energyTo) {
                min_bottom_energyTo = energyTo[i][height - 1];
                seam_tail = i;
            }
        }
        int[] seam = new int[height];
        for (int i = height - 1; i > 0; i--) {
            seam[i] = seam_tail;
            seam_tail = edgeTo[seam_tail][i];
        }
        seam[0] = seam_tail;
        return seam;
    }
    
    //this is supposed to be used on not transposed energy
    //for use on transposed energy, swap col and row
    private double CalculateEnergy(int col, int row) {
        if (transposed) {
            if (col == 0 || row == 0 || col == height - 1 || row == width - 1) {
                return 1000;
            }
        } 
        else {
            if (col == 0 || row == 0 || col == width - 1 || row == height - 1) {
                return 1000;
            }
        }
        int left_RGB = color[col - 1][row];
        int right_RGB = color[col + 1][row];
        int above_RGB = color[col][row - 1];
        int below_RGB = color[col][row + 1];
        int rx = ((left_RGB & 0xff0000) >> 16) - ((right_RGB & 0xff0000) >> 16);
        int gx = ((left_RGB & 0xff00) >> 8) - ((right_RGB & 0xff00) >> 8);
        int bx = (left_RGB & 0xff) - (right_RGB & 0xff);
        int ry = ((above_RGB & 0xff0000) >> 16) - ((below_RGB & 0xff0000) >> 16);
        int gy = ((above_RGB & 0xff00) >> 8) - ((below_RGB & 0xff00) >> 8);
        int by = (above_RGB & 0xff) - (below_RGB & 0xff);
        return Math.sqrt(rx * rx + gx * gx + bx * bx + ry * ry + gy * gy + by * by);
    }

    public void removeHorizontalSeam(int[] seam) {
        // remove horizontal seam from current picture
        if (seam == null) {
            throw new java.lang.IllegalArgumentException();
        }
        if (!transposed) {
            if (seam.length != width || height <= 1) {
                throw new java.lang.IllegalArgumentException();
            }
            height--;
            for (int i = 0; i < seam.length; i++) {
                int rown = seam[i];
                if (rown < 0 || rown > height) {
                    throw new java.lang.IllegalArgumentException();
                }
                if (i - 1 >= 0 && Math.abs(rown - seam[i - 1]) > 1) {
                    throw new java.lang.IllegalArgumentException();
                }
                for (int j = rown; j < height; j++) {
                    color[i][j] = color[i][j + 1];
                }
                //color[i][height] = 0;
            }
            for (int c = 1; c < seam.length - 1; c++) {
                int rown = seam[c];
                if (rown > 0) {
                    energy[c][rown - 1] = CalculateEnergy(c, rown - 1);
                }
                if (rown < height) {
                    energy[c][rown] = CalculateEnergy(c, rown);
                    for (int r = rown + 1; r < height; r++) {
                        energy[c][r] = energy[c][r + 1];
                    }
                }
            }
        }
        else {
            if (seam.length != height || width <= 1) {
                throw new java.lang.IllegalArgumentException();
            }
            width--;
            for (int i = 0; i < seam.length; i++) {
                int rown = seam[i];
                if (rown < 0 || rown > width) {
                    throw new java.lang.IllegalArgumentException();
                }
                if (i - 1 >= 0 && Math.abs(rown - seam[i - 1]) > 1) {
                    throw new java.lang.IllegalArgumentException();
                }
                for (int j = rown; j < width; j++) {
                    color[i][j] = color[i][j + 1];
                }
                //color[i][height] = 0;
            }
            for (int r = 1; r < seam.length - 1; r++) {
                int coln = seam[r];
                //if there is a col before it, update the col as well
                if (coln > 0) {
                    energy[coln - 1][r] = CalculateEnergy(r, coln - 1);
                }
                if (coln < width) {
                    energy[coln][r] = CalculateEnergy(r, coln);
                    for (int c = coln + 1; c < width; c++) {
                        energy[c][r] = energy[c + 1][r];
                    }
                }
            }
        }
    }

    public void removeVerticalSeam(int[] seam) {
        // remove vertical seam from current picture
        if (seam == null) {
            throw new java.lang.IllegalArgumentException();
        }
        if (!transposed) {
            if (seam.length != height || width <= 1) {
                throw new java.lang.IllegalArgumentException();
            }
            width--;
            for (int j = 0; j < seam.length; j++) {
                int coln = seam[j];
                if (coln < 0 || coln > width) {
                    throw new java.lang.IllegalArgumentException();
                }
                if (j - 1 >= 0 && Math.abs(coln - seam[j - 1]) > 1) {
                    throw new java.lang.IllegalArgumentException();
                }
                for (int i = coln; i < width; i++) {
                    color[i][j] = color[i + 1][j];
                }
            }
            //the first and last rows don't need update
            for (int r = 1; r < seam.length - 1; r++) {
                int coln = seam[r];
                //if there is a col before it, update the col as well
                if (coln > 0) {
                    energy[coln - 1][r] = CalculateEnergy(coln - 1, r);
                }
                if (coln < width) {
                    energy[coln][r] = CalculateEnergy(coln, r);
                    for (int c = coln + 1; c < width; c++) {
                        energy[c][r] = energy[c + 1][r];
                    }
                }
            }
        }
        else {
            if (seam.length != width || height <= 1) {
                throw new java.lang.IllegalArgumentException();
            }
            height--;
            for (int j = 0; j < seam.length; j++) {
                int coln = seam[j];
                if (coln < 0 || coln > height) {
                    throw new java.lang.IllegalArgumentException();
                }
                if (j - 1 >= 0 && Math.abs(coln - seam[j - 1]) > 1) {
                    throw new java.lang.IllegalArgumentException();
                }
                for (int i = coln; i < height; i++) {
                    color[i][j] = color[i + 1][j];
                }
            }
            for (int c = 1; c < seam.length - 1; c++) {
                int rown = seam[c];
                if (rown > 0) {
                    energy[c][rown - 1] = CalculateEnergy(rown - 1, c);
                }
                if (rown < height) {
                    energy[c][rown] = CalculateEnergy(rown, c);
                    for (int r = rown + 1; r < height; r++) {
                        energy[c][r] = energy[c][r + 1];
                    }
                }
            }
        }
    }
}

