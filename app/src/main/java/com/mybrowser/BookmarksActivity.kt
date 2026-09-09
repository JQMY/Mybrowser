package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView

class BookmarksActivity : Activity() {

    private lateinit var bookmarkContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.bookmarks_page)

        bookmarkContainer =
            findViewById(R.id.bookmarkContainer)

        displayBookmarks()
    }

    private fun displayBookmarks() {

        bookmarkContainer.removeAllViews()

        if (BookmarkManager.bookmarks.isEmpty()) {

            val emptyText = TextView(this)

            emptyText.text = "No bookmarks yet"
            emptyText.textSize = 18f
            emptyText.gravity = Gravity.CENTER
            emptyText.setTextColor(Color.DKGRAY)

            bookmarkContainer.addView(emptyText)

            return
        }

        for (bookmark in BookmarkManager.bookmarks) {

            val row = LinearLayout(this)

            row.orientation =
                LinearLayout.HORIZONTAL

            row.gravity =
                Gravity.CENTER_VERTICAL

            row.setPadding(
                16,
                16,
                8,
                16
            )

            val information =
                LinearLayout(this)

            information.orientation =
                LinearLayout.VERTICAL

            information.layoutParams =
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )

            val title = TextView(this)

            title.text = bookmark.title
            title.textSize = 18f
            title.setTextColor(Color.BLACK)
            title.maxLines = 1

            val url = TextView(this)

            url.text = bookmark.url
            url.textSize = 13f
            url.setTextColor(Color.DKGRAY)
            url.maxLines = 1

            information.addView(title)
            information.addView(url)

            val deleteButton =
                Button(this)

            deleteButton.text = "✕"

            deleteButton.setOnClickListener {

                BookmarkManager.remove(
                    bookmark.url
                )

                displayBookmarks()
            }

            information.setOnClickListener {

                val intent =
                    Intent(
                        this,
                        MainActivity::class.java
                    )

                intent.putExtra(
                    "selectedUrl",
                    bookmark.url
                )

                startActivity(intent)

                finish()
            }

            row.addView(information)
            row.addView(deleteButton)

            bookmarkContainer.addView(row)
        }
    }
}