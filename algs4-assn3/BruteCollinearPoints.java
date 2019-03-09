/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.ArrayList;

/**
 *
 * @author zhaonick
 */
public class BruteCollinearPoints {
    
    private int counter = 0;
    private ArrayList<LineSegment> segments = new ArrayList<>();
    
    public BruteCollinearPoints(Point[] points) {
        // finds all line segments containing 4 points
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
            for (int i = 0; i < (points.length - 3); i++) {
                for (int j = i + 1; j < (points.length - 2); j++) {
                    for (int k = j + 1; k < (points.length - 1); k++) {
                        for (int l = k + 1; l < points.length; l++) {
                            if (Double.compare(points[i].slopeTo(points[j]), points[i].slopeTo(points[k])) == 0) {
                                if (Double.compare(points[i].slopeTo(points[k]), points[i].slopeTo(points[l])) == 0) {
                                    Point max = points[i];
                                    Point min = points[i];
                                    if (min.compareTo(points[j]) > 0) min = points[j];
                                    if (max.compareTo(points[j]) < 0) max = points[j];
                                    if (min.compareTo(points[k]) > 0) min = points[k];
                                    if (max.compareTo(points[k]) < 0) max = points[k];
                                    if (min.compareTo(points[l]) > 0) min = points[l];
                                    if (max.compareTo(points[l]) < 0) max = points[l];
                                    segments.add(new LineSegment(min, max));
                                    counter++;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public int numberOfSegments() {
        // the number of line segments
        return counter;
    }
    
    public LineSegment[] segments() {
        // the line segments
        LineSegment[] result = new LineSegment[segments.size()];
        for (int i = 0; i < segments.size(); i++) {
            result[i] = segments.get(i);
        }
        return result;
    }
    
}

