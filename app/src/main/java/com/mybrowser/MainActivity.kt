package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.os.Build
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
        val privacyButton: Button = findViewById(R.id.privacyButton)
        val tabsButton: Button = findViewById(R.id.tabsButton)

        TabManager.initialize()

        // Browser settings
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Security
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode =
                WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.settings.safeBrowsingEnabled = true
        }

        // Block third-party cookies
        CookieManager.getInstance()
            .setAcceptThirdPartyCookies(webView, false)

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(
                view: WebView?,
                url: String?
            ) {
                super.onPageFinished(view, url)

                if (url != null &&
                    !url.startsWith("https://nexora.local")
                ) {

                    val currentTab = TabManager.currentTab()

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

        /*
         * If another screen requested a website,
         * open that website.
         *
         * Otherwise show the Nexora home screen.
         */
        val selectedUrl =
            intent.getStringExtra("selectedUrl")

        if (selectedUrl != null) {

            webView.loadUrl(selectedUrl)

        } else {

            loadNexoraHome()
        }

        // Go button
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
            loadNexoraHome()
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

        // Long press bookmark
        bookmarkButton.setOnLongClickListener {

            val intent =
                Intent(
                    this,
                    BookmarksActivity::class.java
                )

            startActivity(intent)

            true
        }

        // Privacy
        privacyButton.setOnClickListener {

            val intent =
                Intent(
                    this,
                    PrivacyActivity::class.java
                )

            startActivity(intent)
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

    private fun loadNexoraHome() {

        val homePage = """
            <!DOCTYPE html>
            <html>
            <head>

                <meta name="viewport"
                    content="width=device-width,
                    initial-scale=1.0,
                    maximum-scale=1.0">

                <style>

                    * {
                        box-sizing: border-box;
                    }

                    body {
                        margin: 0;
                        min-height: 100vh;
                        font-family: sans-serif;
                        color: white;

                        background:
                            radial-gradient(
                                circle at 50% 25%,
                                #263d87 0%,
                                #111b4a 35%,
                                #070b22 70%,
                                #030513 100%
                            );

                        display: flex;
                        justify-content: center;
                        align-items: center;
                        overflow-x: hidden;
                    }

                    .container {
                        width: 100%;
                        max-width: 700px;
                        padding: 35px 22px;
                        text-align: center;
                    }

                    .logo {
                        font-size: 68px;
                        font-weight: bold;

                        background:
                            linear-gradient(
                                135deg,
                                #00eaff,
                                #4978ff,
                                #b85cff
                            );

                        -webkit-background-clip: text;
                        color: transparent;

                        margin-bottom: 2px;
                    }

                    .brand {
                        font-size: 42px;
                        font-weight: bold;
                        letter-spacing: 2px;
                    }

                    .tagline {
                        font-size: 16px;
                        letter-spacing: 6px;
                        color: #c9d3ff;
                        margin-top: 6px;
                        margin-bottom: 40px;
                    }

                    .search {
                        width: 100%;
                        height: 58px;

                        border-radius: 30px;
                        border: 1px solid #6172b8;

                        background: rgba(
                            255,
                            255,
                            255,
                            0.96
                        );

                        display: flex;
                        align-items: center;

                        padding: 5px;
                        margin-bottom: 35px;
                    }

                    .search input {
                        flex: 1;
                        height: 48px;

                        border: none;
                        outline: none;

                        padding: 0 18px;

                        font-size: 16px;
                        color: #111;
                        background: transparent;
                    }

                    .search button {
                        width: 48px;
                        height: 48px;

                        border: none;
                        border-radius: 50%;

                        background: #171f42;
                        color: white;

                        font-size: 20px;
                    }

                    .shortcuts {
                        display: flex;
                        justify-content: center;
                        flex-wrap: wrap;
                        gap: 18px;
                    }

                    .shortcut {
                        width: 82px;
                        text-decoration: none;
                        color: white;
                    }

                    .icon {
                        width: 62px;
                        height: 62px;

                        margin: auto;

                        border-radius: 18px;

                        background: rgba(
                            255,
                            255,
                            255,
                            0.12
                        );

                        border: 1px solid rgba(
                            255,
                            255,
                            255,
                            0.18
                        );

                        display: flex;
                        align-items: center;
                        justify-content: center;

                        font-size: 25px;
                    }

                    .name {
                        margin-top: 8px;
                        font-size: 12px;
                        color: #dce2ff;
                    }

                    .welcome {
                        margin-top: 48px;
                        color: #8f9dcc;
                        font-size: 13px;
                    }

                </style>

            </head>

            <body>

                <div class="container">

                    <div class="logo">N</div>

                    <div class="brand">
                        Nexora
                    </div>

                    <div class="tagline">
                        BROWSE BEYOND.
                    </div>

                    <form
                        class="search"
                        action="https://www.google.com/search"
                        method="get">

                        <input
                            type="text"
                            name="q"
                            placeholder="Search the web..."
                            autocomplete="off">

                        <button type="submit">
                            →
                        </button>

                    </form>

                    <div class="shortcuts">

                        <a
                            class="shortcut"
                            href="https://www.youtube.com">

                            <div class="icon">
                                ▶
                            </div>

                            <div class="name">
                                YouTube
                            </div>

                        </a>

                        <a
                            class="shortcut"
                            href="https://www.google.com">

                            <div class="icon">
                                G
                            </div>

                            <div class="name">
                                Google
                            </div>

                        </a>

                        <a
                            class="shortcut"
                            href="https://x.com">

                            <div class="icon">
                                𝕏
                            </div>

                            <div class="name">
                                X
                            </div>

                        </a>

                        <a
                            class="shortcut"
                            href="https://www.instagram.com">

                            <div class="icon">
                                ◎
                            </div>

                            <div class="name">
                                Instagram
                            </div>

                        </a>

                        <a
                            class="shortcut"
                            href="https://chatgpt.com">

                            <div class="icon">
                                ✦
                            </div>

                            <div class="name">
                                ChatGPT
                            </div>

                        </a>

                        <a
                            class="shortcut"
                            href="https://www.google.com">

                            <div class="icon">
                                +
                            </div>

                            <div class="name">
                                Add
                            </div>

                        </a>

                    </div>

                    <div class="welcome">
                        Fast • Secure • Smart • Minimal
                    </div>

                </div>

            </body>
            </html>
        """.trimIndent()

        webView.loadDataWithBaseURL(
            "https://nexora.local/",
            homePage,
            "text/html",
            "UTF-8",
            null
        )

        urlBar.setText("")
        TabManager.currentTab().url = "https://nexora.local/"
        TabManager.currentTab().title = "Nexora"
        updateBookmarkButton()
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