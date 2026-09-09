package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var urlBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        urlBar = findViewById(R.id.urlBar)

        val goButton: Button = findViewById(R.id.goButton)
        val backButton: Button = findViewById(R.id.backButton)
        val forwardButton: Button = findViewById(R.id.forwardButton)
        val homeButton: Button = findViewById(R.id.homeButton)
        val refreshButton: Button = findViewById(R.id.refreshButton)
        val historyButton: Button = findViewById(R.id.historyButton)
        val bookmarkButton: Button = findViewById(R.id.bookmarkButton)
        val tabsButton: Button = findViewById(R.id.tabsButton)

        TabManager.initialize()

        // Browser settings
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Security
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false

        if (android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.LOLLIPOP) {

            webView.settings.mixedContentMode =
                WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        if (android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.O) {

            webView.settings.safeBrowsingEnabled = true
        }

        // Block third-party cookies
        CookieManager.getInstance()
            .setAcceptThirdPartyCookies(
                webView,
                false
            )

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(
                view: WebView?,
                url: String?
            ) {
                super.onPageFinished(view, url)

                if (url != null) {

                    val currentTab =
                        TabManager.currentTab()

                    currentTab.url = url

                    currentTab.title =
                        view?.title ?: "New Tab"

                    urlBar.setText(url)

                    HistoryManager.add(
                        currentTab.title,
                        url
                    )

                    updateBookmarkButton()
                }
            }
        }

        val selectedUrl =
            intent.getStringExtra("selectedUrl")

        if (selectedUrl != null) {

            webView.loadUrl(selectedUrl)

        } else {

            val currentTab =
                TabManager.currentTab()

            webView.loadUrl(currentTab.url)
        }

        // Go
        goButton.setOnClickListener {
            openWebsite()
        }

        // Keyboard search
        urlBar.setOnEditorActionListener { _, _, _ ->
            openWebsite()
            true
        }

        // Back
        backButton.setOnClickListener {

            if (webView.canGoBack()) {
                webView.goBack()
            }
        }

        // Forward
        forwardButton.setOnClickListener {

            if (webView.canGoForward()) {
                webView.goForward()
            }
        }

        // Home
        homeButton.setOnClickListener {

            webView.loadUrl(
                "https://www.google.com"
            )
        }

        // Refresh
        refreshButton.setOnClickListener {
            webView.reload()
        }

        // History
        historyButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    HistoryActivity::class.java
                )

            startActivity(intent)
        }

        // Bookmark
        bookmarkButton.setOnClickListener {

            val currentTab =
                TabManager.currentTab()

            if (BookmarkManager.isBookmarked(
                    currentTab.url
                )
            ) {

                BookmarkManager.remove(
                    currentTab.url
                )

            } else {

                BookmarkManager.add(
                    currentTab.title,
                    currentTab.url
                )
            }

            updateBookmarkButton()
        }

        // Long press bookmark button
        // Opens bookmarks
        bookmarkButton.setOnLongClickListener {

            val intent =
                Intent(
                    this,
                    BookmarksActivity::class.java
                )

            startActivity(intent)

            true
        }

        // Tabs
        tabsButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    TabsActivity::class.java
                )

            startActivity(intent)
        }
    }

    private fun openWebsite() {

        var address =
            urlBar.text.toString().trim()

        if (address.isEmpty()) {
            return
        }

        if (!address.startsWith("http://") &&
            !address.startsWith("https://")
        ) {

            address =
                "https://www.google.com/search?q=" +
                        address.replace(" ", "+")
        }

        webView.loadUrl(address)

        TabManager.currentTab().url =
            address
    }

    private fun updateBookmarkButton() {

        val bookmarkButton: Button =
            findViewById(R.id.bookmarkButton)

        val currentUrl =
            TabManager.currentTab().url

        if (BookmarkManager.isBookmarked(
                currentUrl
            )
        ) {

            bookmarkButton.text = "★"

        } else {

            bookmarkButton.text = "☆"
        }
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {

        if (webView.canGoBack()) {

            webView.goBack()

        } else {

            super.onBackPressed()
        }
    }
}