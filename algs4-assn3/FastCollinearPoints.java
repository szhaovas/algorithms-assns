/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author zhaonick
 */
public class FastCollinearPoints {
    
    private int lineCounter = 0;
    private ArrayList<LineSegment> segments = new ArrayList<>();
    private ArrayList<Point> ends = new ArrayList<>();
    private ArrayList<Point> starts = new ArrayList<>();
    
    public FastCollinearPoints(Point[] points) {
        // finds all line segments containing 4 or more copy
        if (points == null) {
            throw new java.lang.IllegalArgumentException();
        }
        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) {
                throw new java.lang.IllegalArgumentException();
            }
            for (int j = i + 1; j < points.length; j++) {
                if (points[j] == null) {
                    throw new java.lang.IllegalArgumentException();
                }
                if (points[i].compareTo(points[j]) == 0) {
                    throw new java.lang.IllegalArgumentException();
                }
            }
        }
        if (points.length < 4) {
            return;
        } else {
            Point[] copy = new Point[points.length];
            System.arraycopy(points, 0, copy, 0, points.length);
            for (int i = 0; i < (copy.length - 3); i++) {
                Arrays.sort(copy, i, copy.length);
                Arrays.sort(copy, i + 1, copy.length, copy[i].slopeOrder());
                double currentSlope = Double.NaN;
                int counter = 0;
                for (int j = i + 1; j < copy.length; j++) {
                    if (Double.compare(currentSlope, copy[i].slopeTo(copy[j])) == 0) {
                        counter++;
                        //only executed at the last entry, in case the unbroken series of
                        //equal entries continues to the end
                        if (j == (copy.length - 1) && counter >= 2) {
                            int[] indices = getAllIndices(copy[j]);
                            if ((indices != null) && compareStarts(indices, copy[i], currentSlope)) {
                                
                            } else {
                                segments.add(new LineSegment(copy[i], copy[j]));
                                starts.add(copy[i]);
                                ends.add(copy[j]);
                                lineCounter++;
                            }
                        }
                    } else {
                        //no more equal entries, check if the previous ones have enough equal entries
                        //to form a line segment
                        if (counter >= 2) {
                            //have enough entries, but is it just a sub segment?
                            int[] indices = getAllIndices(copy[j - 1]);
                            //if indices is null, no matching ends, can't be a sub segment
                            //add to list
                            //otherwise is the current slope equal to slope from the point
                            //to the potential segment start?
                            if ((indices != null) && compareStarts(indices, copy[i], currentSlope)) {
                                
                            }
                            //not a subsegment, add to list
                            else {
                                segments.add(new LineSegment(copy[i], copy[j - 1]));
                                starts.add(copy[i]);
                                ends.add(copy[j - 1]);
                                lineCounter++;
                            }
                        }
                        //not enough equal entries, proceed searching
                        currentSlope = copy[i].slopeTo(copy[j]);
                        counter = 0;
                    }
                }
            }
        }
        
    }
    
    public int numberOfSegments() {
        // the number of line segments
        return lineCounter;
    }
    
    public LineSegment[] segments() {
        // the line segments
        LineSegment[] result = new LineSegment[segments.size()];
        for (int i = 0; i < segments.size(); i++) {
            result[i] = segments.get(i);
        }
        return result;
    }
    
    //return null if the end doesn't match, i.e current segment is not a subsegment
    //otherwise the current segment is a potential sub segment, return all matching end
    //indices in an int array
    private int[] getAllIndices(Point point) {
        if (ends.isEmpty()) return null;
        ArrayList<Integer> indices = new ArrayList<>();
        for (int i = 0; i < ends.size(); i++) {
            if (point.compareTo(ends.get(i)) == 0) {
                indices.add(i);
            }
        }
        if (indices.isEmpty()) return null;
        int[] result = new int[indices.size()];
        for (int i = 0; i < indices.size(); i++) {
            result[i] = indices.get(i);
        }
        return result;
    }
    
    //return true if the current linesegment is a subsegment
    //otherwise return false
    private boolean compareStarts(int[] endIndices, Point point, Double currentSlope) {
        for (int i = 0; i < endIndices.length; i++) {
            if (Double.compare(currentSlope, point.slopeTo(starts.get(endIndices[i]))) == 0) {
                return true;
            }
        }
        return false;
    }
    
}
