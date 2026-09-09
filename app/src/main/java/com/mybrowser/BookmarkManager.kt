package com.mybrowser

object BookmarkManager {

    val bookmarks = mutableListOf<BookmarkItem>()

    fun add(title: String, url: String) {

        if (url.isEmpty()) {
            return
        }

        val alreadyExists = bookmarks.any {
            it.url == url
        }

        if (!alreadyExists) {

            bookmarks.add(
                BookmarkItem(
                    title,
                    url
                )
            )
        }
    }

    fun remove(url: String) {

        bookmarks.removeAll {
            it.url == url
        }
    }

    fun isBookmarked(url: String): Boolean {

        return bookmarks.any {
            it.url == url
        }
    }
}