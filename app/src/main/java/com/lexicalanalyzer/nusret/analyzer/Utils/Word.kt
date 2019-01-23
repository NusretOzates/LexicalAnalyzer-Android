package com.lexicalanalyzer.nusret.analyzer.Utils

/**
 * Created by Halil on 11.03.2018.
 */

class Word {

    var word: String
    var type: String

    constructor(word: String) {
        this.word = word
        this.type = "undefined"
    }

    constructor(word: String, type: String) {
        this.word = word
        this.type = type
    }

    override fun toString(): String {

        return if (type != "undefined") {
            "$word $type"
        } else
            word
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false

        val word1 = o as Word?

        return word == word1!!.word
    }

    override fun hashCode(): Int {
        return word.hashCode()
    }
}
