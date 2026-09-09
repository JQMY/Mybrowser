package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.graphics.Color
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class TabsActivity : Activity() {

    private val tabs = mutableListOf<BrowserTab>()
    private lateinit var tabContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.tabs_page)

        tabContainer = findViewById(R.id.tabContainer)

        val newTabButton: Button = findViewById(R.id.newTabButton)

        if (tabs.isEmpty()) {
            tabs.add(
                BrowserTab(
                    "Google",
                    "https://www.google.com"
                )
            )
        }

        displayTabs()

        newTabButton.setOnClickListener {

            val tabNumber = tabs.size + 1

            tabs.add(
                BrowserTab(
                    "New Tab $tabNumber",
                    "https://www.google.com"
                )
            )

            displayTabs()
        }
    }

    private fun displayTabs() {

        tabContainer.removeAllViews()

        for (tab in tabs) {

            val tabLayout = LinearLayout(this)

            tabLayout.orientation = LinearLayout.VERTICAL
            tabLayout.setPadding(20, 20, 20, 20)
            tabLayout.gravity = Gravity.CENTER_VERTICAL

            val title = TextView(this)

            title.text = tab.title
            title.textSize = 18f
            title.setTextColor(Color.BLACK)

            val url = TextView(this)

            url.text = tab.url
            url.textSize = 14f
            url.setTextColor(Color.DKGRAY)

            tabLayout.addView(title)
            tabLayout.addView(url)

            tabLayout.setOnClickListener {

                val intent = Intent(this, MainActivity::class.java)

                intent.putExtra("selectedUrl", tab.url)

                startActivity(intent)

                finish()
            }

            tabContainer.addView(tabLayout)
        }
    }
}