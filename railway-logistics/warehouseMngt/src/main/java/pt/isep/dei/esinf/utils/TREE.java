package pt.isep.dei.esinf.utils;

import pt.isep.dei.esinf.domain.tree.Node;

import java.util.List;
import java.util.Set;

/*
 * @author DEI-ESINF
 * @param <E>
 */

public class TREE<E extends Comparable<E>> extends BST<E> {

    /**
     * @param elem A valid element within the tree
     * @return the path to a given element in the tree
     */
    public List<E> path(E elem) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the set of the leaf node elements of the tree
     */
    public Set<E> leafs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return an array with the minimum and the maximum values of the tree
     */
    public E[] range() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the set of elements belonging to the diameter of the BST
     */
    public Set<E> diameter() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the previous element of the tree for a given element
     */
    public E findPredecessor(E element) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * – verify if the current and tree BST are identical.
     */
    public boolean identical(BST<E> tree) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * – remove all elements in the current BST that are outside the range [low, high]
     */
    public void truncate(E low, E high) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * – return true if BST<E> tree is a sub tree of the BST<E>.
     */
    public boolean isSubTree(BST<E> tree) {
        if (tree.root == null) return true;

        Node<E> n = find(this.root, tree.root.getElement());

        if (n == null) return false;

        return identical(n, tree.root);
    }

    public boolean isSymmetric() {
        if (this.root == null) return true;

        return identical(this.root.getLeft(), this.root.getRight());
    }

    public boolean identical(Node<E> first, Node<E> second) {
        if (first == null && second == null) return true;
        if (first == null || second == null) return false;


        boolean elementsAreIdentical = first
                .getElement()
                .equals(second.getElement());

        boolean leftsAreIdentical = identical(first.getLeft(), second.getLeft());
        boolean rightsAreIdentical = identical(first.getRight(), second.getRight());


        return elementsAreIdentical && leftsAreIdentical && rightsAreIdentical;
    }

    public TREE<E> minimumSubtree(Set<E> elems) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
