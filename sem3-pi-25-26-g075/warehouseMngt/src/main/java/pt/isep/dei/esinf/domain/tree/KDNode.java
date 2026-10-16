package pt.isep.dei.esinf.domain.tree;

import java.awt.geom.Point2D;

/**
 * Node class for KD-Tree implementation
 * @param <E> the type of element stored in the node
 */
public class KDNode<E> extends Node<E> {
    private Point2D.Double coords;

    /**
     * Constructs a KDNode with the given element, coordinates, and children
     * @param element the element to store
     * @param coords the 2D coordinates (x=longitude, y=latitude)
     * @param leftChild reference to left child node
     * @param rightChild reference to right child node
     */
    public KDNode(E element, Point2D.Double coords, KDNode<E> leftChild, KDNode<E> rightChild) {
        super(element, leftChild, rightChild);
        this.coords = coords;
    }

    /**
     * Get the coordinates of this node
     * @return the 2D coordinates
     */
    public Point2D.Double getCoords() {
        return coords;
    }

    /**
     * Get the X coordinate (longitude)
     * @return the longitude
     */
    public double getX() {
        return coords.x;
    }

    /**
     * Get the Y coordinate (latitude)
     * @return the latitude
     */
    public double getY() {
        return coords.y;
    }

    /**
     * Set the coordinates
     * @param coords the new coordinates
     */
    public void setCoords(Point2D.Double coords) {
        this.coords = coords;
    }

    @Override
    public KDNode<E> getLeft() {
        return (KDNode<E>) super.getLeft();
    }

    @Override
    public KDNode<E> getRight() {
        return (KDNode<E>) super.getRight();
    }
}
