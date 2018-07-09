package com.example.nusret.ifoundthisoninternet;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Halil on 11.03.2018.
 */

public class BinarySearchTree {

    public Node root;

    public BinarySearchTree() {
        this.root = null;
    }

    //Only matched words will be turned into Word instances  // void yaptım bulursa direk verilen listeye ekleyecek.
    public void find(String p_word, Set<Word> add, HashSet<Word> wordset) {
        Node current = root;
        while (current != null) {
            if (current.node_word.word.equalsIgnoreCase(p_word)) {
                if(wordset.remove(current.node_word))
                {
                    add.add(current.node_word);
                }
                break;
            } else if (current.node_word.word.compareToIgnoreCase(p_word) > 0) {
                current = current.left;
            } else {
                current = current.right;
            }
        }
    }

    public boolean delete(Word p_word) {
        Node parent = root;
        Node current = root;
        boolean isLeftChild = false;
        while (!current.node_word.word.equalsIgnoreCase(p_word.word)) {
            parent = current;
            if (current.node_word.word.compareToIgnoreCase(p_word.word) > 0) {
                isLeftChild = true;
                current = current.left;
            } else {
                isLeftChild = false;
                current = current.right;
            }
            if (current == null) {
                return false;
            }
        }
        // if i am here that means we have found the node
        // Case 1: if node to be deleted has no children
        if (current.left == null && current.right == null) {
            if (current == root) {
                root = null;
            }
            if (isLeftChild == true) {
                parent.left = null;
            } else {
                parent.right = null;
            }
        }
        // Case 2 : if node to be deleted has only one child
        else if (current.right == null) {
            if (current == root) {
                root = current.left;
            } else if (isLeftChild) {
                parent.left = current.left;
            } else {
                parent.right = current.left;
            }
        } else if (current.left == null) {
            if (current == root) {
                root = current.right;
            } else if (isLeftChild) {
                parent.left = current.right;
            } else {
                parent.right = current.right;
            }
        } else if (current.left != null && current.right != null) {

            // now we have found the minimum element in the right sub tree
            Node successor = getSuccessor(current);
            if (current == root) {
                root = successor;
            } else if (isLeftChild) {
                parent.left = successor;
            } else {
                parent.right = successor;
            }
            successor.left = current.left;
        }
        return true;
    }

    public Node getSuccessor(Node deleteNode) {
        Node successsor = null;
        Node successsorParent = null;
        Node current = deleteNode.right;
        while (current != null) {
            successsorParent = successsor;
            successsor = current;
            current = current.left;
        }
        // check if successor has the right child, it cannot have left child for
        // sure
        // if it does have the right child, add it to the left of
        // successorParent.
        // successsorParent
        if (successsor != deleteNode.right) {
            successsorParent.left = successsor.right;
            successsor.right = deleteNode.right;
        }
        return successsor;
    }

    public void insert(Word p_word) {
        Node newNode = new Node(p_word);
        if (root == null) {
            root = newNode;
            return;
        }
        Node current = root;
        Node parent = null;
        while (true) {
            parent = current;
            if (p_word.word.compareToIgnoreCase(current.node_word.word) < 0) {
                current = current.left;
                if (current == null) {
                    parent.left = newNode;
                    return;
                }
            } else {
                current = current.right;
                if (current == null) {
                    parent.right = newNode;
                    return;
                }
            }
        }
    }

    public void display(Node root) {
        if (root != null) {
            display(root.left);
            System.out.print(" " + root.node_word.word);
            display(root.right);
        }
    }

}
