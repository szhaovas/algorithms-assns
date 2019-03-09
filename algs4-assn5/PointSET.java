/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import java.util.TreeSet;

/**
 *
 * @author zhaonick
 */
public class PointSET {
    
    private TreeSet<Point2D> set;

    public PointSET() {
        // construct an empty set of points
        set = new TreeSet<>();
    }

    public boolean isEmpty() {
        // is the set empty? 
        return set.isEmpty();
    }

    public int size() {
        // number of points in the set 
        return set.size();
    }

    public void insert(Point2D p) {
        // add the point to the set (if it is not already in the set)
        if (p == null) throw new java.lang.IllegalArgumentException();
        if (set.contains(p)) return;
        set.add(p);
    }

    public boolean contains(Point2D p) {
        // does the set contain point p? 
        if (p == null) throw new java.lang.IllegalArgumentException();
        return set.contains(p);
    }

    public void draw() {
        // draw all points to standard draw
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        set.forEach((p) -> {
            p.draw();
        });
    }

    public Iterable<Point2D> range(RectHV rect) {
        // all points that are inside the rectangle (or on the boundary) 
        if (rect == null) throw new java.lang.IllegalArgumentException();
        Stack<Point2D> result = new Stack<>();
        for (Point2D p : set) {
            if (rect.contains(p)) result.push(p);
        }
        return result;
    }

    public Point2D nearest(Point2D p) {
        // a nearest neighbor in the set to point p; null if the set is empty 
        if (p == null) throw new java.lang.IllegalArgumentException();
        if (isEmpty()) return null;
        double min = Double.MAX_VALUE;
        Point2D result = set.first();
        for (Point2D point : set) {
            double dist = p.distanceSquaredTo(point);
            if (dist < min) {
                min = dist;
                result = point;
            }
        }
        return result;
    }
}