package com.lexicalanalyzer.nusret.analyzer.Utils

import java.util.*

/**
 * Created by Halil on 11.03.2018.
 */

class BinarySearchTree {

    var root: Node? = null

    init {
        this.root = null
    }

    //Only matched words will be turned into Word instances  // void yaptım bulursa direk verilen listeye ekleyecek.
    fun find(p_word: String, add: MutableSet<Word>, wordset: HashSet<Word>) {
        var current = root
        while (current != null) {
            current = if (current.node_word.word.equals(p_word, ignoreCase = true)) {
                if (wordset.remove(current.node_word)) {
                    add.add(current.node_word)
                }
                break
            } else if (current.node_word.word.compareTo(p_word, ignoreCase = true) > 0) {
                current.left
            } else {
                current.right
            }
        }
    }

    fun delete(p_word: Word): Boolean {
        var parent = root
        var current = root
        var isLeftChild = false
        while (!current!!.node_word.word.equals(p_word.word, ignoreCase = true)) {
            parent = current
            if (current.node_word.word.compareTo(p_word.word, ignoreCase = true) > 0) {
                isLeftChild = true
                current = current.left
            } else {
                isLeftChild = false
                current = current.right
            }
            if (current == null) {
                return false
            }
        }
        // if i am here that means we have found the node
        // Case 1: if node to be deleted has no children
        if (current.left == null && current.right == null) {
            if (current === root) {
                root = null
            }
            if (isLeftChild == true) {
                parent!!.left = null
            } else {
                parent!!.right = null
            }
        } else if (current.right == null) {
            if (current === root) {
                root = current.left
            } else if (isLeftChild) {
                parent!!.left = current.left
            } else {
                parent!!.right = current.left
            }
        } else if (current.left == null) {
            if (current === root) {
                root = current.right
            } else if (isLeftChild) {
                parent!!.left = current.right
            } else {
                parent!!.right = current.right
            }
        } else if (current.left != null && current.right != null) {

            // now we have found the minimum element in the right sub tree
            val successor = getSuccessor(current)
            if (current === root) {
                root = successor
            } else if (isLeftChild) {
                parent!!.left = successor
            } else {
                parent!!.right = successor
            }
            successor!!.left = current.left
        }// Case 2 : if node to be deleted has only one child
        return true
    }

    fun getSuccessor(deleteNode: Node): Node? {
        var successsor: Node? = null
        var successsorParent: Node? = null
        var current: Node? = deleteNode.right
        while (current != null) {
            successsorParent = successsor
            successsor = current
            current = current.left
        }
        // check if successor has the right child, it cannot have left child for
        // sure
        // if it does have the right child, add it to the left of
        // successorParent.
        // successsorParent
        if (successsor !== deleteNode.right) {
            successsorParent!!.left = successsor!!.right
            successsor.right = deleteNode.right
        }
        return successsor
    }

    fun insert(p_word: Word) {
        val newNode = Node(p_word)
        if (root == null) {
            root = newNode
            return
        }
        var current = root
        var parent: Node? = null
        while (true) {
            parent = current
            if (p_word.word.compareTo(current!!.node_word.word, ignoreCase = true) < 0) {
                current = current.left
                if (current == null) {
                    parent!!.left = newNode
                    return
                }
            } else {
                current = current.right
                if (current == null) {
                    parent!!.right = newNode
                    return
                }
            }
        }
    }

    fun display(root: Node?) {
        if (root != null) {
            display(root.left)
            print(" " + root.node_word.word)
            display(root.right)
        }
    }

}
