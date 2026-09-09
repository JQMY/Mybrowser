package com.mybrowser

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class TabsActivity : Activity() {

    private lateinit var tabContainer: LinearLayout

    private val tabs = mutableListOf(
        BrowserTab("Google", "https://www.google.com")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.tabs_page)

        tabContainer = findViewById(R.id.tabContainer)

        val newTabButton: Button = findViewById(R.id.newTabButton)

        newTabButton.setOnClickListener {
            createNewTab()
        }

        displayTabs()
    }

    private fun createNewTab() {

        val newTabNumber = tabs.size + 1

        tabs.add(
            BrowserTab(
                "New Tab $newTabNumber",
                "https://www.google.com"
            )
        )

        displayTabs()
    }

    private fun displayTabs() {

        tabContainer.removeAllViews()

        for ((index, tab) in tabs.withIndex()) {

            val tabView = TextView(this)

            tabView.text =
                "${index + 1}. ${tab.title}\n${tab.url}"

            tabView.textSize = 18f
            tabView.setPadding(20, 25, 20, 25)

            tabContainer.addView(tabView)
        }
    }
}