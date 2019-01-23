package com.lexicalanalyzer.nusret.analyzer.Utils

/**
 * Created by Halil on 11.03.2018.
 */

class Node(internal var node_word: Word) {
    internal var left: Node? = null
    internal var right: Node? = null

    init {
        left = null
        right = null
    }

}
