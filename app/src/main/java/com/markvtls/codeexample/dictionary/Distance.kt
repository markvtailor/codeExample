package com.wallpaperscraft.keby.app.dictionary

import kotlin.math.min

object DamerauLevenshtein {

    fun calculateDistance(source: CharSequence?, target: CharSequence?): Int {
        require(!(source == null || target == null)) { "Parameter must not be null" }

        val sourceLength = source.length
        val targetLength = target.length

        if (sourceLength == 0) return targetLength
        if (targetLength == 0) return sourceLength

        val dist = Array(sourceLength + 1) { IntArray(targetLength + 1) }

        for (i in 0..sourceLength) dist[i][0] = i
        for (j in 0..targetLength) dist[0][j] = j

        for (i in 1..sourceLength) {
            for (j in 1..targetLength) {
                val cost = if (source[i - 1] == target[j - 1]) 0 else 1

                dist[i][j] = min(
                    min(dist[i - 1][j] + 1, dist[i][j - 1] + 1),
                    dist[i - 1][j - 1] + cost
                )

                if (i > 1 && j > 1 && source[i - 1] == target[j - 2] && source[i - 2] == target[j - 1]) {
                    dist[i][j] = min(dist[i][j], dist[i - 2][j - 2] + cost)
                }
            }
        }

        return dist[sourceLength][targetLength]
    }

}