package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class TabsActivity : Activity() {

    private lateinit var tabContainer: LinearLayout
    private lateinit var tabsTitle: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.tabs_page)

        tabContainer = findViewById(R.id.tabContainer)
        tabsTitle = findViewById(R.id.tabsTitle)

        val newTabButton: Button = findViewById(R.id.newTabButton)

        TabManager.initialize()

        displayTabs()

        newTabButton.setOnClickListener {

            TabManager.addTab()

            displayTabs()
        }
    }

    private fun displayTabs() {

        tabContainer.removeAllViews()

        tabsTitle.text = "Tabs (${TabManager.tabs.size})"

        for ((index, tab) in TabManager.tabs.withIndex()) {

            val tabLayout = LinearLayout(this)

            tabLayout.orientation = LinearLayout.HORIZONTAL
            tabLayout.gravity = Gravity.CENTER_VERTICAL
            tabLayout.setPadding(20, 15, 10, 15)

            val informationLayout = LinearLayout(this)

            informationLayout.orientation = LinearLayout.VERTICAL
            informationLayout.layoutParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

            val title = TextView(this)

            title.text = tab.title
            title.textSize = 18f
            title.setTextColor(Color.BLACK)

            val url = TextView(this)

            url.text = tab.url
            url.textSize = 14f
            url.setTextColor(Color.DKGRAY)

            informationLayout.addView(title)
            informationLayout.addView(url)

            val closeButton = Button(this)

            closeButton.text = "✕"

            closeButton.setOnClickListener {

                if (TabManager.tabs.size > 1) {

                    TabManager.tabs.removeAt(index)

                    if (TabManager.currentTabIndex >= TabManager.tabs.size) {
                        TabManager.currentTabIndex =
                            TabManager.tabs.lastIndex
                    }

                    displayTabs()

                }
            }

            tabLayout.addView(informationLayout)
            tabLayout.addView(closeButton)

            informationLayout.setOnClickListener {

                TabManager.currentTabIndex = index

                val intent = Intent(this, MainActivity::class.java)

                intent.putExtra(
                    "selectedUrl",
                    tab.url
                )

                startActivity(intent)

                finish()
            }

            tabContainer.addView(tabLayout)
        }
    }
}