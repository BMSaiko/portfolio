package com.example.production.Domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementação de uma Árvore AVL genérica.
 *
 * @param <K> Tipo da chave que deve ser comparável.
 * @param <V> Tipo do valor armazenado.
 */
public class AVLTree<K extends Comparable<K>, V> {
    /**
     * Classe interna que representa um nó na árvore AVL.
     */
    private class Node {
        K key;
        V value;
        Node left, right;
        int height;

        /**
         * Construtor para um nó da árvore AVL.
         *
         * @param key   Chave do nó.
         * @param value Valor associado à chave.
         */
        Node(K key, V value) {
            this.key = key;
            this.value = value;
            height = 1;
        }
    }

    private Node root;

    /**
     * Insere uma chave-valor na árvore AVL.
     *
     * @param key   Chave a ser inserida.
     * @param value Valor associado.
     */
    public void insert(K key, V value) {
        root = insertRec(root, key, value);
    }

    /**
     * Método recursivo para inserir uma chave-valor na árvore AVL.
     *
     * @param node  Nó atual da árvore.
     * @param key   Chave a ser inserida.
     * @param value Valor associado.
     * @return Nó atualizado após a inserção.
     */
    private Node insertRec(Node node, K key, V value) {
        if (node == null) return new Node(key, value);

        if (key.compareTo(node.key) < 0)
            node.left = insertRec(node.left, key, value);
        else if (key.compareTo(node.key) > 0)
            node.right = insertRec(node.right, key, value);
        else {
            node.value = value;
            return node;
        }

        node.height = 1 + Math.max(height(node.left), height(node.right));

        int balance = getBalance(node);

        if (balance > 1 && key.compareTo(node.left.key) < 0)
            return rightRotate(node);

        if (balance < -1 && key.compareTo(node.right.key) > 0)
            return leftRotate(node);

        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = leftRotate(node.left);
            return rightRotate(node);
        }

        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rightRotate(node.right);
            return leftRotate(node);
        }

        return node;
    }

    /**
     * Obtém a altura de um nó.
     *
     * @param node Nó a ser verificado.
     * @return Altura do nó.
     */
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    /**
     * Calcula o fator de balanceamento de um nó.
     *
     * @param node Nó a ser verificado.
     * @return Fator de balanceamento.
     */
    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    /**
     * Realiza uma rotação à direita.
     *
     * @param y Nó a ser rotacionado.
     * @return Novo nó raiz após a rotação.
     */
    private Node rightRotate(Node y) {
        Node x = y.left;
        Node T2 = x.right;


        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    /**
     * Realiza uma rotação à esquerda.
     *
     * @param x Nó a ser rotacionado.
     * @return Novo nó raiz após a rotação.
     */
    private Node leftRotate(Node x) {
        Node y = x.right;
        Node T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    /**
     * Recupera os valores associados a uma chave específica.
     *
     * @param key Chave a ser buscada.
     * @return Valor associado ou {@code null} se não encontrado.
     */
    public V get(K key) {
        Node node = getRec(root, key);
        return node != null ? node.value : null;
    }

    /**
     * Método recursivo para buscar um nó na árvore AVL.
     *
     * @param node Nó atual da árvore.
     * @param key  Chave a ser buscada.
     * @return Nó encontrado ou {@code null} se não encontrado.
     */
    private Node getRec(Node node, K key) {
        if (node == null || node.key.compareTo(key) == 0)
            return node;

        if (key.compareTo(node.key) < 0)
            return getRec(node.left, key);
        else
            return getRec(node.right, key);
    }

    // Método para retornar as chaves em ordem (in-order traversal)
    public List<K> inOrderKeys() {
        List<K> keys = new ArrayList<>();
        inOrderTraversal(root, keys);
        return keys;
    }

    // Método auxiliar para realizar a travessia em ordem
    private void inOrderTraversal(Node node, List<K> keys) {
        if (node == null) {
            return;
        }
        inOrderTraversal(node.left, keys); // Visita o nó à esquerda
        keys.add(node.key);               // Adiciona a chave do nó atual
        inOrderTraversal(node.right, keys); // Visita o nó à direita
    }


}
