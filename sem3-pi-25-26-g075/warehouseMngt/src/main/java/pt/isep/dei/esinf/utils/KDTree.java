package pt.isep.dei.esinf.utils;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import pt.isep.dei.esinf.domain.tree.KDNode;
import pt.isep.dei.esinf.utils.interfaces.KDTreeNodeInterface;

/**
 * KD-Tree implementation for 2D spatial data
 *
 * @param <E> the type of element stored in the tree, must implement KDTreeNodeInterface
 */
public class KDTree<E extends Comparable<E> & KDTreeNodeInterface> extends BST<E> {

    /**
     * Comparator for X coordinate (longitude)
     */
    private final Comparator<KDNode<E>> cmpX = Comparator.comparingDouble(KDNode::getX);

    /**
     * Comparator for Y coordinate (latitude)
     */
    private final Comparator<KDNode<E>> cmpY = Comparator.comparingDouble(KDNode::getY);

    /**
     * Inserts an element into the KD-tree
     *
     * @param element the element to insert
     */
    @Override
    public void insert(E element) {
        if (element == null || element.getCoords() == null) {
            throw new IllegalArgumentException("Element and coordinates cannot be null");
        }

        KDNode<E> newNode = new KDNode<>(element, element.getCoords(), null, null);

        if (root == null) {
            root = newNode;
        } else {
            insert((KDNode<E>) root, newNode, true);
        }
    }

    /**
     * Recursive insertion helper
     *
     * @param currentNode the current node being examined
     * @param node        the node to insert
     * @param divX        true if dividing by X axis (longitude), false for Y axis (latitude)
     */
    private void insert(KDNode<E> currentNode, KDNode<E> node, boolean divX) {
        if (node.getCoords().equals(currentNode.getCoords())) {
            return;
        }

        int cmpResult = (divX ? cmpX : cmpY).compare(node, currentNode);

        if (cmpResult < 0) {
            if (currentNode.getLeft() == null) {
                currentNode.setLeft(node);
            } else {
                insert(currentNode.getLeft(), node, !divX);
            }
        } else {
            if (currentNode.getRight() == null) {
                currentNode.setRight(node);
            } else {
                insert(currentNode.getRight(), node, !divX);
            }
        }
    }

    /**
     * Range search in the KD-tree with no filters
     *
     * @param latMin minimum latitude (inclusive)
     * @param latMax maximum latitude (inclusive)
     * @param lonMin minimum longitude (inclusive)
     * @param lonMax maximum longitude (inclusive)
     * @return list of elements within the specified range
     */
    public List<E> rangeSearch(double latMin, double latMax, double lonMin, double lonMax) {
        return rangeSearch(latMin, latMax, lonMin, lonMax, null);
    }

    /**
     * Overloaded method to range search in the KD-tree with additional filter parameter.
     *
     * @param latMin minimum latitude (inclusive)
     * @param latMax maximum latitude (inclusive)
     * @param lonMin minimum longitude (inclusive)
     * @param lonMax maximum longitude (inclusive)
     * @param filter optional predicate to filter results
     * @return list of elements within the specified range that match the filter
     */
    public List<E> rangeSearch(double latMin, double latMax, double lonMin, double lonMax, Predicate<E> filter) {
        if (latMin > latMax) {
            throw new IllegalArgumentException("latMin must be <= latMax");
        }
        if (lonMin > lonMax) {
            throw new IllegalArgumentException("lonMin must be <= lonMax");
        }

        List<E> results = new ArrayList<>();
        rangeSearchRecursive((KDNode<E>) root, latMin, latMax, lonMin, lonMax, true, results, filter);
        return results;
    }

    /**
     * Recursive range search helper
     *
     * @param node    current node being examined
     * @param latMin  minimum latitude
     * @param latMax  maximum latitude
     * @param lonMin  minimum longitude
     * @param lonMax  maximum longitude
     * @param divX    true if current level divides by X (longitude)
     * @param results list to accumulate matching elements
     * @param filter  optional predicate to filter results
     */
    private void rangeSearchRecursive(KDNode<E> node, double latMin, double latMax, double lonMin, double lonMax, boolean divX, List<E> results, Predicate<E> filter) {
        if (node == null) {
            return;
        }

        Point2D.Double coords = node.getCoords();
        double x = coords.x; // longitude
        double y = coords.y; // latitude

        // Check if current node is within range
        if (x >= lonMin && x <= lonMax && y >= latMin && y <= latMax) {
            E element = node.getElement();
            if (filter == null || filter.test(element)) {
                results.add(element);
            }
        }

        // Decide which subtrees to explore
        if (divX) {
            // Dividing by X (longitude)
            if (lonMin <= x) {
                // Explore left subtree
                rangeSearchRecursive(node.getLeft(), latMin, latMax, lonMin, lonMax, false, results, filter);
            }
            if (lonMax >= x) {
                // Explore right subtree
                rangeSearchRecursive(node.getRight(), latMin, latMax, lonMin, lonMax, false, results, filter);
            }
        } else {
            // Dividing by Y (latitude)
            if (latMin <= y) {
                // Explore left subtree
                rangeSearchRecursive(node.getLeft(), latMin, latMax, lonMin, lonMax, true, results, filter);
            }
            if (latMax >= y) {
                // Explore right subtree
                rangeSearchRecursive(node.getRight(), latMin, latMax, lonMin, lonMax, true, results, filter);
            }
        }
    }

    /**
     * Get the root node as a KDNode
     *
     * @return the root node
     */
    @Override
    protected KDNode<E> root() {
        return (KDNode<E>) root;
    }
}
