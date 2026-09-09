package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
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

            val tabCard = LinearLayout(this)

            tabCard.orientation = LinearLayout.HORIZONTAL
            tabCard.gravity = Gravity.CENTER_VERTICAL
            tabCard.setPadding(18, 18, 10, 18)

            val cardBackground = GradientDrawable()

            if (index == TabManager.currentTabIndex) {
                cardBackground.setColor(Color.rgb(232, 240, 254))
                cardBackground.setStroke(
                    3,
                    Color.rgb(33, 150, 243)
                )
            } else {
                cardBackground.setColor(Color.WHITE)
                cardBackground.setStroke(
                    1,
                    Color.LTGRAY
                )
            }

            cardBackground.cornerRadius = 24f

            tabCard.background = cardBackground

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            cardParams.setMargins(0, 0, 0, 12)

            tabCard.layoutParams = cardParams

            val informationLayout = LinearLayout(this)

            informationLayout.orientation = LinearLayout.VERTICAL
            informationLayout.gravity = Gravity.CENTER_VERTICAL

            informationLayout.layoutParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

            val title = TextView(this)

            title.text = if (index == TabManager.currentTabIndex) {
                "●  ${tab.title}"
            } else {
                tab.title
            }

            title.textSize = 18f
            title.setTextColor(Color.rgb(20, 20, 20))
            title.maxLines = 1

            val url = TextView(this)

            url.text = tab.url
            url.textSize = 13f
            url.setTextColor(Color.DKGRAY)
            url.maxLines = 1

            informationLayout.addView(title)
            informationLayout.addView(url)

            val closeButton = Button(this)

            closeButton.text = "✕"
            closeButton.textSize = 18f

            closeButton.setOnClickListener {

                if (TabManager.tabs.size > 1) {

                    TabManager.tabs.removeAt(index)

                    if (TabManager.currentTabIndex >=
                        TabManager.tabs.size) {

                        TabManager.currentTabIndex =
                            TabManager.tabs.lastIndex
                    }

                    displayTabs()
                }
            }

            informationLayout.setOnClickListener {

                TabManager.currentTabIndex = index

                val intent =
                    Intent(this, MainActivity::class.java)

                intent.putExtra(
                    "selectedUrl",
                    tab.url
                )

                startActivity(intent)

                finish()
            }

            tabCard.addView(informationLayout)
            tabCard.addView(closeButton)

            tabContainer.addView(tabCard)
        }
    }
}