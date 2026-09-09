package com.mybrowser

object TabManager {

    val tabs = mutableListOf<BrowserTab>()

    var currentTabIndex = 0

    fun initialize() {
        if (tabs.isEmpty()) {
            tabs.add(
                BrowserTab(
                    "Google",
                    "https://www.google.com"
                )
            )
        }
    }

    fun addTab() {

        val number = tabs.size + 1

        tabs.add(
            BrowserTab(
                "New Tab $number",
                "https://www.google.com"
            )
        )

        currentTabIndex = tabs.lastIndex
    }

    fun currentTab(): BrowserTab {
        return tabs[currentTabIndex]
    }
}