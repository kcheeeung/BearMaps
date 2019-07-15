package bearmaps.proj2ab;

import java.util.HashSet;
import java.util.List;
import java.util.Random;

import bearmaps.proj2c.AugmentedStreetMapGraph;

/**
 * KDTree
 */
public class KDTree implements PointSet {
    private static final int xDim = 0;
    private static final int yDim = 1;
    private Node kdtree;
    private HashSet<Point> pointSet;

    private class Node {
        private Point point;
        private int orientation;
        private Node left;
        private Node right;
    
        public Node (Point p, int o) {
            point = p;
            orientation = o;
            left = null;
            right = null;
        }
        public double nodeX() {
            return point.getX();
        }
        public double nodeY() {
            return point.getY();
        }
    }

    public KDTree(List<Point> points) {
        kdtree = null;
        pointSet = new HashSet<>(AugmentedStreetMapGraph.HASH_SIZE);
        int[] randomIndex = randomizeIndices(points.size());
        for (int i = 0; i < randomIndex.length; i++) {
            Point p = points.get(i);
            if (!pointSet.contains(p)) {
                pointSet.add(p);
                insert(p);
            }
        }
    }

    /** Returns a randomized int[] of indices for given size */
    private int[] randomizeIndices(int size) {
        int[] array = new int[size];
        for (int j = 0; j < size; j++) {
            array[j] = j;
        }
        // Fisher–Yates shuffle
        int index;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            if (index != i) {
                array[index] ^= array[i];
                array[i] ^= array[index];
                array[index] ^= array[i];
            }
        }
        return array;
    }

    private void insert(Point p) {
        kdtree = add(kdtree, p, -9999);
    }

    private Node add(Node current, Point toInsert, int prevOrientation) {
        if (current == null) {
            switch (prevOrientation) {
                case xDim:
                    return new Node(toInsert, yDim);
                case yDim:
                    return new Node(toInsert, xDim);
                default:
                    return new Node(toInsert, xDim);
            }
        } else {
            switch (current.orientation) {
                case xDim:
                    if (toInsert.getX() < current.nodeX()) {
                        current.left = add(current.left, toInsert, current.orientation);
                    } else {
                        current.right = add(current.right, toInsert, current.orientation);
                    }
                    break;
                case yDim:
                    if (toInsert.getY() < current.nodeY()) {
                        current.left = add(current.left, toInsert, current.orientation);
                    } else {
                        current.right = add(current.right, toInsert, current.orientation);
                    }
                    break;
                default:
                    throw new Error("Something is wrong with the add helper!");
            }
        }
        return current;
    }

    @Override
    public Point nearest(double x, double y) {
        return nearestHelper(kdtree, new Point(x, y), kdtree).point;
    }

    private Node nearestHelper(Node n, Point target, Node best) {
        if (n == null) {
            return best;
        } else {
            if (Point.distance(n.point, target) < Point.distance(best.point, target)) {
                best = n;
            }
            Node goodSide, badSide;
            if (traverse(n, target)) {
                goodSide = n.left;
                badSide = n.right;
            } else {
                goodSide = n.right;
                badSide = n.left;
            }

            best = nearestHelper(goodSide, target, best);

            switch (n.orientation) {
                case xDim:
                    Point xsplit = new Point(n.nodeX(), target.getY());
                    if (Point.distance(xsplit, target) < Point.distance(best.point, target)) {
                        best = nearestHelper(badSide, target, best);
                    }
                    break;
                case yDim:
                    Point ysplit = new Point(target.getX(), n.nodeY());
                    if (Point.distance(ysplit, target) < Point.distance(best.point, target)) {
                        best = nearestHelper(badSide, target, best);
                    }
                    break;
                default:
                    throw new Error("Something wrong with the nearest helper!");
            }
        }
        return best;
    }

    private boolean traverse(Node n, Point target) {
        // Good left first, then right (true)
        // Good right first, then left (false)
        switch (n.orientation) {
            case xDim:
                return Math.floor(target.getX() - n.point.getX()) < 0;
            case yDim:
                return Math.floor(target.getY() - n.point.getY()) < 0;
            default:
                throw new Error("Something is wrong with the traversal comparison!");
        }
    }
    
    // public static void main(String[] args) {
    //         Point p0 = new Point(5, 6);
    //         Point p1 = new Point(1, 5);
    //         Point p2 = new Point(7, 3);
    //         Point p3 = new Point(2, 5);
    //         Point p4 = new Point(3, 3);
    //         Point p5 = new Point(4, 4);

    //         KDTree x = new KDTree(List.of(p0, p1, p2, p3, p4, p5));
    //         System.out.println(x.nearest(2, 5));
    // }
}
