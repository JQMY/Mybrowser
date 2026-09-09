package com.mybrowser

object HistoryManager {

    val history = mutableListOf<HistoryItem>()

    fun add(title: String, url: String) {

        if (url.isEmpty()) {
            return
        }

        history.removeAll {
            it.url == url
        }

        history.add(
            0,
            HistoryItem(
                title,
                url
            )
        )
    }
}