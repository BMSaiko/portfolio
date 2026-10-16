/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.isep.dei.esinf.utils;

import pt.isep.dei.esinf.domain.tree.Node;

/**
 *
 * @param <E>
 * @author DEI-ESINF
 */

public class AVL<E extends Comparable<E>> extends BST<E> {

    private int balanceFactor(Node<E> node) {
        if (node == null) return 0;
        return height(node.getLeft()) - height(node.getRight());
    }

    private Node<E> rightRotation(Node<E> node) {
        Node<E> leftChild = node.getLeft();
        Node<E> rightOfLeftChild = leftChild.getRight();

        leftChild.setRight(node);
        node.setLeft(rightOfLeftChild);

        return leftChild;
    }

    private Node<E> leftRotation(Node<E> node) {
        Node<E> rightChild = node.getRight();
        Node<E> leftOfRightChild = rightChild.getLeft();

        rightChild.setLeft(node);
        node.setRight(leftOfRightChild);

        return rightChild;
    }

    private Node<E> twoRotations(Node<E> node) {
        int balance = balanceFactor(node);

        if (balance > 1) {
            if (balanceFactor(node.getLeft()) < 0) {
                node.setLeft(leftRotation(node.getLeft()));
            }
            return rightRotation(node);
        } else if (balance < -1) {
            if (balanceFactor(node.getRight()) > 0) {
                node.setRight(rightRotation(node.getRight()));
            }
            return leftRotation(node);
        }

        return node;
    }

    private Node<E> balanceNode(Node<E> node) {
        if (node == null) return null;

        int balance = balanceFactor(node);

        if (Math.abs(balance) <= 1) {
            return node;
        }

        return twoRotations(node);
    }

    @Override
    public void insert(E element) {
        root = insert(element, root);
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
        } else {
            return node;
        }

        return balanceNode(node);
    }

    @Override
    public void remove(E element) {
        root = remove(element, root());
    }

    private Node<E> remove(E element, Node<E> node) {
        if (node == null) {
            return null;
        }
        if (element.compareTo(node.getElement()) == 0) {
            if (node.isLeaf()) {
                return null;
            }
            if (node.getLeft() == null) {
                return node.getRight();
            }
            if (node.getRight() == null) {
                return node.getLeft();
            }
            E smallElem = smallestElement(node.getRight());
            node.setElement(smallElem);
            node.setRight(remove(smallElem, node.getRight()));
            node = balanceNode(node);
        } else if (element.compareTo(node.getElement()) < 0) {
            node.setLeft(remove(element, node.getLeft()));
            node = balanceNode(node);
        } else {
            node.setRight(remove(element, node.getRight()));
            node = balanceNode(node);
        }
        return node;
    }

    public boolean equals(Object otherObj) {
        if (this == otherObj) return true;
        if (otherObj == null || this.getClass() != otherObj.getClass()) return false;
        AVL<E> second = (AVL<E>) otherObj;
        return equals(root, second.root);
    }

    public boolean equals(Node<E> root1, Node<E> root2) {
        if (root1 == null && root2 == null) {
            return true;
        } else if (root1 != null && root2 != null) {
            if (root1.getElement().compareTo(root2.getElement()) == 0) {
                return equals(root1.getLeft(), root2.getLeft()) && equals(root1.getRight(), root2.getRight());
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}