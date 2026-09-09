package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class HistoryActivity : Activity() {

    private lateinit var historyContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.history_page)

        historyContainer =
            findViewById(R.id.historyContainer)

        val clearButton: Button =
            findViewById(R.id.clearHistoryButton)

        displayHistory()

        clearButton.setOnClickListener {

            HistoryManager.history.clear()

            displayHistory()
        }
    }

    private fun displayHistory() {

        historyContainer.removeAllViews()

        if (HistoryManager.history.isEmpty()) {

            val emptyText = TextView(this)

            emptyText.text = "No browsing history"
            emptyText.textSize = 18f
            emptyText.gravity = Gravity.CENTER
            emptyText.setTextColor(Color.DKGRAY)

            historyContainer.addView(emptyText)

            return
        }

        for (item in HistoryManager.history) {

            val historyItem = LinearLayout(this)

            historyItem.orientation =
                LinearLayout.VERTICAL

            historyItem.setPadding(
                20,
                18,
                20,
                18
            )

            val title = TextView(this)

            title.text = item.title
            title.textSize = 18f
            title.setTextColor(Color.BLACK)
            title.maxLines = 1

            val url = TextView(this)

            url.text = item.url
            url.textSize = 13f
            url.setTextColor(Color.DKGRAY)
            url.maxLines = 1

            historyItem.addView(title)
            historyItem.addView(url)

            historyItem.setOnClickListener {

                val intent =
                    Intent(
                        this,
                        MainActivity::class.java
                    )

                intent.putExtra(
                    "selectedUrl",
                    item.url
                )

                startActivity(intent)

                finish()
            }

            historyContainer.addView(historyItem)
        }
    }
}