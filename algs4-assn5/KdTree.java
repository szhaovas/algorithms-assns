/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.Comparator;

/**
 *
 * @author zhaonick
 */
public class KdTree {

    private Node root;
    private int size;

    private RectHV previousRect;
    private int levelCounter;
    private boolean leftLeaf; //true if left leftLeaf, false if right
    private Comparator<Point2D> comparator;
    private Point2D previousPoint;

    private double min;
    private Point2D result;

    private static class Node {

        private Point2D p;      // the point
        private RectHV rect;    // the axis-aligned rectangle corresponding to this node
        private Node lb;        // the left/bottom subtree
        private Node rt;        // the right/top subtree

        Node(Point2D p, RectHV rect) {
            this.p = p;
            this.rect = rect;
        }
    }

    public KdTree() {
        // construct an empty set of points
        size = 0;
    }

    public boolean isEmpty() {
        // is the set empty? 
        return root == null;
    }

    public int size() {
        // number of points in the set 
        return size;
    }

    public void insert(Point2D p) {
        // add the point to the set (if it is not already in the set)
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        levelCounter = 1;
        leftLeaf = false;
        previousRect = new RectHV(0, 0, 1, 1);
        previousPoint = new Point2D(0, 0);
        root = insert(root, p);
    }

    private Node insert(Node node, Point2D p) {
        if (node == null) {
            //if this is an odd node, i.e. vertical node
            size++;
            if (levelCounter % 2 != 0) {
                //if below the parent node
                if (leftLeaf) {
                    return new Node(p, new RectHV(previousRect.xmin(), previousRect.ymin(), previousRect.xmax(), previousPoint.y()));
                } //if above the parent node
                else {
                    return new Node(p, new RectHV(previousRect.xmin(), previousPoint.y(), previousRect.xmax(), previousRect.ymax()));
                }
            } //if this is an even node, i.e. horizontal node
            else {
                //if on the left of the parent node
                if (leftLeaf) {
                    return new Node(p, new RectHV(previousRect.xmin(), previousRect.ymin(), previousPoint.x(), previousRect.ymax()));
                } //if on the right of the parent node
                else {
                    return new Node(p, new RectHV(previousPoint.x(), previousRect.ymin(), previousRect.xmax(), previousRect.ymax()));
                }
            }
        }
        if (levelCounter % 2 != 0) {
            comparator = Point2D.X_ORDER;
        } else {
            comparator = Point2D.Y_ORDER;
        }
        levelCounter++;
        previousRect = node.rect;
        previousPoint = node.p;
        //the key node is to the left of / below the current node
        if (comparator.compare(node.p, p) > 0) {
            leftLeaf = true;
            node.lb = insert(node.lb, p);
        } //the key node is to the right of / above the current node
        else if (comparator.compare(node.p, p) < 0) {
            leftLeaf = false;
            node.rt = insert(node.rt, p);
        } else {
            if (!node.p.equals(p)) {
                leftLeaf = false;
                node.rt = insert(node.rt, p);
            }
        }
        return node;
    }

    public boolean contains(Point2D p) {
        // does the set contain point p? 
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        levelCounter = 1;
        return contains(root, p);
    }

    private boolean contains(Node node, Point2D p) {
        if (node == null) {
            return false;
        }
        //on odd levels, use XOrder
        //on even levels, use YOrder 
        if (levelCounter % 2 == 0) {
            comparator = Point2D.Y_ORDER;
        } else {
            comparator = Point2D.X_ORDER;
        }
        levelCounter++;
        if (comparator.compare(node.p, p) > 0) {
            return contains(node.lb, p);
        }
        if (comparator.compare(node.p, p) < 0) {
            return contains(node.rt, p);
        } else {
            if (node.p.equals(p)) return true;
            else return contains(node.rt, p);
        }
    }

    public void draw() {
        // draw all points to standard draw
        draw(root, 1);
    }

    //cannot use closure here because both subtrees would need to be executed
    private void draw(Node node, int levelCounter) {
        if (node == null) {
            return;
        }
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        node.p.draw();
        StdDraw.setPenRadius();
        //if an odd node, i.e. vertical node, draw vertically
        if (levelCounter % 2 != 0) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.line(node.p.x(), node.rect.ymin(), node.p.x(), node.rect.ymax());
        } //otherwise draw horizontally
        else {
            StdDraw.setPenColor(StdDraw.BLUE);
            StdDraw.line(node.rect.xmin(), node.p.y(), node.rect.xmax(), node.p.y());
        }
        draw(node.lb, levelCounter + 1);
        draw(node.rt, levelCounter + 1);
    }

    public Iterable<Point2D> range(RectHV rect) {
        // all points that are inside the rectangle (or on the boundary) 
        if (rect == null) {
            throw new java.lang.IllegalArgumentException();
        }
        Stack<Point2D> stack = new Stack<>();
        range(root, rect, stack);
        return stack;
    }

    private void range(Node node, RectHV rect, Stack<Point2D> stack) {
        if (node == null) {
            return;
        }
        if (node.rect.intersects(rect)) {
            if (rect.contains(node.p)) {
                stack.push(node.p);
            }
            range(node.lb, rect, stack);
            range(node.rt, rect, stack);
        }
    }

    public Point2D nearest(Point2D p) {
        // a nearest neighbor in the set to point p; null if the set is empty 
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        if (isEmpty()) return null;
        min = Double.MAX_VALUE;
        result = root.p;
        nearest(root, p, 1);
        return result;
    }

    private void nearest(Node node, Point2D p, int levelCounter) {
        if (node == null) {
            return;
        }
        if (node.rect.distanceSquaredTo(p) < min) {
            if (node.p.distanceSquaredTo(p) < min) {
                min = node.p.distanceSquaredTo(p);
                result = node.p;
            }
            if (levelCounter % 2 != 0) {
                comparator = Point2D.X_ORDER;
            }
            else {
                comparator = Point2D.Y_ORDER;
            }
            //the key node is to the left of / below the current node
            if (comparator.compare(node.p, p) > 0) {
                nearest(node.lb, p, levelCounter + 1);
                nearest(node.rt, p, levelCounter + 1);
            } //the key node is to the right of / above the current node
            else {
                nearest(node.rt, p, levelCounter + 1);
                nearest(node.lb, p, levelCounter + 1);
            }
        }
    }
}
