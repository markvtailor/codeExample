package com.wallpaperscraft.keby.app.dictionary

import com.wallpaperscraft.keby.app.dictionary.DictionaryCollection.Companion.SUGGESTIONS_SIZE
import com.wallpaperscraft.keby.domain.models.SupportedLocales
import com.wallpaperscraft.keby.domain.models.DictionaryWord

open class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()
}

class LowestTrieNode(frequency: Int, word: String) : TrieNode() {
    var wordFrequency = frequency
    var nodeWord: String = word
}

class Trie {
    val root = TrieNode()

    fun insertWordIntoDictionary(word: String, frequency: Int) {
        var node = root

        for ((index, char) in word.withIndex()) {
            val isLastChar = index == word.length - 1

            node.children.computeIfAbsent(char) {
                if (isLastChar) LowestTrieNode(
                    frequency, word
                ) else TrieNode()
            }

            node = node.children[char]!!
        }

    }

    fun searchForLowestTrieNodes(word: String): List<LowestTrieNode> {
        var node = root

        for (char in word) {
            node = node.children[char] ?: return mutableListOf()
        }

        val result = mutableListOf<LowestTrieNode>()
        collectLowestTrieNodes(node, result)

        result.sortBy { it.wordFrequency }
        return if (result.size > SUGGESTIONS_SIZE) result.drop(result.size - SUGGESTIONS_SIZE) else result
    }

    private fun collectLowestTrieNodes(node: TrieNode, result: MutableList<LowestTrieNode>) {
        if (node is LowestTrieNode) {
            result.add(node)
        }

        for (child in node.children.values) {
            collectLowestTrieNodes(child, result)
        }
    }
}

class DictionaryCollection {

    private val tries: MutableMap<String, Trie> = mutableMapOf()

    private var rawDictionaries: MutableMap<String, List<DictionaryWord>> = mutableMapOf()

    fun createDictionary(local: SupportedLocales) {
        tries[local.name] = Trie()
    }

    fun setRawDictionary(locale: SupportedLocales, raw: List<DictionaryWord>) {
        rawDictionaries[locale.name] = raw
    }

    fun getRawDictionary(locale: SupportedLocales): List<DictionaryWord>? {
        return rawDictionaries[locale.name]
    }

    fun getDictionariesList(): List<String> {
        return tries.entries.map { it.key }
    }

    fun addWordToDictionary(word: String, frequency: Int, locale: SupportedLocales) {
        tries[locale.name]?.insertWordIntoDictionary(word, frequency)
    }

    fun getSuggestions(word: String, locale: SupportedLocales): List<String> {

        val res = tries[locale.name]?.searchForLowestTrieNodes(word)

        return res?.map { it.nodeWord } ?: emptyList()//sortedSuggestions
    }

    companion object {
        const val SUGGESTIONS_SIZE = 3
    }
}