package com.lexicalanalyzer.nusret.analyzer;

/**
 * Created by Halil on 11.03.2018.
 */

public class Word {

    String word;
    String root;

    public Word(String word) {
        this.word = word;
        this.root = "undefined";
    }

    public Word(String word, String type) {
        this.word = word;
        this.root = type;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getType() {
        return root;
    }

    public void setType(String type) {
        this.root = type;
    }

    @Override
    public String toString() {

        if (!root.equals("undefined")) {
            return word + " " + root;
        } else
            return word;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Word word1 = (Word) o;

        return word.equals(word1.word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }
}
