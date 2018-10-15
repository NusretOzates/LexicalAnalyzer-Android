package com.lexicalanalyzer.nusret.analyzer.Utils;

/**
 * Created by Halil on 11.03.2018.
 */

public class Node {

    Word node_word;
    Node left;
    Node right;

    public Node(Word node_word) {
        this.node_word = node_word;
        left = null;
        right = null;
    }

}
