package pt.isep.dei.esinf.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import pt.isep.dei.esinf.domain.tree.Node;
import pt.isep.dei.esinf.utils.interfaces.BSTInterface;


/**
 *
 * @author DEI-ESINF
 */
public class BST<E extends Comparable<E>> implements BSTInterface<E> {

    /**
     * Root node of the tree
     */
    protected Node<E> root;

    public BST() {
        root = null;
    }

    /**
     * @return root Node of the tree (or null if tree is empty)
     */
    protected Node<E> root() {
        return root;
    }

    /**
     * Verifies if the tree is empty
     * @return true if the tree is empty, false otherwise
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Returns the Node containing a specific Element, or null otherwise.
     *
     * @param element the element to find
     * @return the Node that contains the Element, or null otherwise
     * <p>
     * This method despite not being essential is very useful.
     * It is written here in order to be used by this class and its
     * subclasses avoiding recoding.
     * So its access level is protected
     */
    protected Node<E> find(Node<E> node, E element) {
        if (node == null) {
            return null;
        }

        E currentElement = node.getElement();
        if (element.equals(currentElement)) {
            return node;
        } else if (element.compareTo(currentElement) < 0) {
            return find(node.getLeft(), element);
        } else {
            return find(node.getRight(), element);
        }
    }

    /**
     * Inserts an element in the tree.
     */
    public void insert(E element) {
        this.root = insert(element, root());
    }

    private Node<E> insert(E element, Node<E> node) {
        if (node == null) {
            return new Node<>(element, null, null);
        }

        int compare = element.compareTo(node.getElement());

        if (compare < 0) {
            node.setLeft(insert(element, node.getLeft()));
        } else if (compare > 0) {
            node.setRight(insert(element, node.getRight()));
        }

        return node;
    }

    /**
     * Removes an element from the tree maintaining its consistency as a Binary Search Tree.
     */
    public void remove(E element) {
        root = remove(element, root());
    }

    private Node<E> remove(E element, Node<E> node) {

        if (node == null) {
            return null;
        }

        if (element.compareTo(node.getElement()) == 0) {
            // node is the Node to be removed
            if (node.isLeaf()) { //node is a leaf (has no childs)
                return null;
            }
            if (!node.hasLeft()) {   //has only right child
                return node.getRight();
            }
            if (node.getRight() == null) {  //has only left child
                return node.getLeft();
            }
            E min = smallestElement(node.getRight());
            node.setElement(min);
            node.setRight(remove(min, node.getRight()));
        } else if (element.compareTo(node.getElement()) < 0) node.setLeft(remove(element, node.getLeft()));
        else node.setRight(remove(element, node.getRight()));

        return node;
    }

    /**
     * Returns the number of nodes in the tree.
     *
     * @return number of nodes in the tree
     */
    public int size() {
        if (this.root == null) {
            return 0;
        }

        return size(root());
    }

    private int size(Node<E> node) {
        if (node == null) {
            return 0;
        }

        return 1 + size(node.getLeft()) + size(node.getRight());
    }

    /**
     * Returns the height of the tree
     * @return height
     */
    public int height() {
        if (this.root == null) {
            return -1;
        }

        return height(root());
    }

    /**
     * Returns the height of the subtree rooted at Node node.
     * @param node A valid Node within the tree
     * @return height
     */
    protected int height(Node<E> node) {
        if (node == null) {
            return -1;
        }

        int leftHeight = height(node.getLeft());
        int rightHeight = height(node.getRight());

        return 1 + Math.max(leftHeight, rightHeight);
    }

    /**
     * Returns the smallest element within the tree.
     *
     * @return the smallest element within the tree
     */
    public E smallestElement() {
        if (this.root == null) {
            return null;
        }

        return smallestElement(root());
    }

    protected E smallestElement(Node<E> node) {
        if (node.getLeft() == null) {
            return node.getElement();
        }

        return smallestElement(node.getLeft());
    }

    /**
     * Returns an iterable collection of elements of the tree, reported in
     * in-order.
     *
     * @return iterable collection of the tree's elements reported in in-order
     */
    public Iterable<E> inOrder() {
        List<E> snapshot = new ArrayList<>();
        if (root != null) {
            inOrderSubtree(root, snapshot);   // fill the snapshot recursively
        }
        return snapshot;
    }

    /**
     * Adds elements of the subtree rooted at Node node to the given snapshot
     * using an in-order traversal
     *
     * @param node     Node serving as the root of a subtree
     * @param snapshot a list to which results are appended
     */
    private void inOrderSubtree(Node<E> node, List<E> snapshot) {
        if (node == null) {
            return;
        }
        inOrderSubtree(node.getLeft(), snapshot);
        snapshot.add(node.getElement());
        inOrderSubtree(node.getRight(), snapshot);
    }

    /**
     * Returns an iterable collection of elements of the tree, reported in
     * pre-order.
     *
     * @return iterable collection of the tree's elements reported in pre-order
     */
    public Iterable<E> preOrder() {

        List<E> snapshot = new ArrayList<>();

        if (root != null) {
            preOrderSubtree(root, snapshot);   // fill the snapshot recursively
        }
        return snapshot;
    }

    /**
     * Adds elements of the subtree rooted at Node node to the given snapshot
     * using an pre-order traversal
     *
     * @param node     Node serving as the root of a subtree
     * @param snapshot a list to which results are appended
     */
    private void preOrderSubtree(Node<E> node, List<E> snapshot) {

        if (node == null) {
            return;
        }

        snapshot.add(node.getElement());
        preOrderSubtree(node.getLeft(), snapshot);
        preOrderSubtree(node.getRight(), snapshot);
    }

    /**
     * Returns an iterable collection of elements of the tree, reported in
     * post-order.
     *
     * @return iterable collection of the tree's elements reported in post-order
     */
    public Iterable<E> posOrder() {

        List<E> snapshot = new ArrayList<>();

        if (root != null) {
            posOrderSubtree(root, snapshot);   // fill the snapshot recursively
        }
        return snapshot;
    }

    /**
     * Adds positions of the subtree rooted at Node node to the given snapshot
     * using an post-order traversal
     *
     * @param node     Node serving as the root of a subtree
     * @param snapshot a list to which results are appended
     */
    private void posOrderSubtree(Node<E> node, List<E> snapshot) {

        if (node == null) {
            return;
        }

        posOrderSubtree(node.getLeft(), snapshot);
        posOrderSubtree(node.getRight(), snapshot);
        snapshot.add(node.getElement());
    }

    /**
     * Returns a map with a list of nodes by each tree level.
     *
     * @return a map with a list of nodes by each tree level
     */
    public Map<Integer, List<E>> nodesByLevel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void processBstByLevel(Node<E> node, Map<Integer, List<E>> result, int level) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Returns a string representation of the tree.
     * Draw the tree horizontally
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        toStringRec(root, 0, sb);
        return sb.toString();
    }

    private void toStringRec(Node<E> root, int level, StringBuilder sb) {
        if (root == null) return;

        toStringRec(root.getRight(), level + 1, sb);
        if (level != 0) {
            sb.append("|\t".repeat(Math.max(0, level - 1)));
            sb.append("|-------").append(root.getElement()).append("\n");
        } else {
            sb.append(root.getElement()).append("\n");
        }
        toStringRec(root.getLeft(), level + 1, sb);
    }
}




  

  