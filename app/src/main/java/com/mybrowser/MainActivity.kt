package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var urlBar: EditText
    private lateinit var goButton: Button
    private lateinit var backButton: Button
    private lateinit var forwardButton: Button
    private lateinit var homeButton: Button
    private lateinit var refreshButton: Button
    private lateinit var historyButton: Button
    private lateinit var bookmarkButton: Button
    private lateinit var tabsButton: Button
    private lateinit var settingsButton: Button

    private var isHomePage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        urlBar = findViewById(R.id.urlBar)
        goButton = findViewById(R.id.goButton)

        backButton = findViewById(R.id.backButton)
        forwardButton = findViewById(R.id.forwardButton)
        homeButton = findViewById(R.id.homeButton)
        refreshButton = findViewById(R.id.refreshButton)
        historyButton = findViewById(R.id.historyButton)
        bookmarkButton = findViewById(R.id.bookmarkButton)
        tabsButton = findViewById(R.id.tabsButton)
        settingsButton = findViewById(R.id.settingsButton)

        setupWebView()
        setupButtons()

        val selectedUrl =
            intent.getStringExtra("selectedUrl")

        if (!selectedUrl.isNullOrEmpty()) {
            openWebsite(selectedUrl)
        } else {
            loadNexoraHome()
        }
    }

    private fun setupWebView() {

        val settings = webView.settings

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        settings.allowFileAccess = false
        settings.allowContentAccess = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode =
                WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            settings.safeBrowsingEnabled = true
        }

        CookieManager
            .getInstance()
            .setAcceptThirdPartyCookies(
                webView,
                false
            )

        webView.webChromeClient =
            WebChromeClient()

        webView.webViewClient =
            object : WebViewClient() {

                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?
                ): Boolean {

                    val url =
                        request?.url?.toString()

                    if (!url.isNullOrEmpty()) {
                        openWebsite(url)
                    }

                    return true
                }

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {

                    super.onPageFinished(
                        view,
                        url
                    )

                    if (url != null &&
                        url != "https://nexora.local/"
                    ) {

                        isHomePage = false

                        urlBar.setText(url)

                        val title =
                            view?.title
                                ?: "Untitled"

                        HistoryManager.add(
                            title,
                            url
                        )

                        updateBookmarkButton(
                            url
                        )

                        updateTab(
                            title,
                            url
                        )
                    }

                    updateNavigationButtons()
                }
            }
    }

    private fun setupButtons() {

        goButton.setOnClickListener {

            val text =
                urlBar.text
                    .toString()
                    .trim()

            if (text.isNotEmpty()) {
                openWebsite(text)
            }
        }

        urlBar.setOnEditorActionListener {
                _, actionId, _ ->

            if (
                actionId ==
                EditorInfo.IME_ACTION_GO ||
                actionId ==
                EditorInfo.IME_ACTION_SEARCH
            ) {

                val text =
                    urlBar.text
                        .toString()
                        .trim()

                if (text.isNotEmpty()) {
                    openWebsite(text)
                }

                true

            } else {
                false
            }
        }

        backButton.setOnClickListener {

            if (webView.canGoBack()) {
                webView.goBack()
            }
        }

        forwardButton.setOnClickListener {

            if (webView.canGoForward()) {
                webView.goForward()
            }
        }

        homeButton.setOnClickListener {
            loadNexoraHome()
        }

        refreshButton.setOnClickListener {

            if (isHomePage) {
                loadNexoraHome()
            } else {
                webView.reload()
            }
        }

        historyButton.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    HistoryActivity::class.java
                )
            )
        }

        bookmarkButton.setOnClickListener {

            val currentUrl =
                webView.url

            if (
                currentUrl.isNullOrEmpty() ||
                currentUrl ==
                "https://nexora.local/"
            ) {
                Toast.makeText(
                    this,
                    "Open a website first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val title =
                webView.title
                    ?: currentUrl

            if (
                BookmarkManager
                    .isBookmarked(currentUrl)
            ) {

                BookmarkManager.remove(
                    currentUrl
                )

                Toast.makeText(
                    this,
                    "Bookmark removed",
                    Toast.LENGTH_SHORT
                ).show()

            } else {

                BookmarkManager.add(
                    title,
                    currentUrl
                )

                Toast.makeText(
                    this,
                    "Bookmark saved",
                    Toast.LENGTH_SHORT
                ).show()
            }

            updateBookmarkButton(
                currentUrl
            )
        }

        bookmarkButton.setOnLongClickListener {

            startActivity(
                Intent(
                    this,
                    BookmarksActivity::class.java
                )
            )

            true
        }

        tabsButton.setOnClickListener {

            saveCurrentTab()

            startActivity(
                Intent(
                    this,
                    TabsActivity::class.java
                )
            )
        }

        settingsButton.setOnClickListener {

            startActivity(
                Intent(
                    this,
                    SettingsActivity::class.java
                )
            )
        }
    }

    private fun openWebsite(input: String) {

        var text = input.trim()

        if (text.isEmpty()) {
            return
        }

        if (
            text.startsWith("http://") ||
            text.startsWith("https://")
        ) {

            webView.loadUrl(text)

            return
        }

        if (
            text.contains(".") &&
            !text.contains(" ")
        ) {

            text =
                "https://$text"

            webView.loadUrl(text)

            return
        }

        val searchUrl =
            "https://www.google.com/search?q=" +
                    Uri.encode(text)

        webView.loadUrl(
            searchUrl
        )
    }

    private fun loadNexoraHome() {

        isHomePage = true

        val preferences =
            getSharedPreferences(
                "nexora_settings",
                MODE_PRIVATE
            )

        val savedBackground =
            preferences.getString(
                "background",
                "cosmic"
            )

        val savedWallpaper =
            preferences.getString(
                "wallpaperUri",
                null
            )

        val backgroundCss =
            if (!savedWallpaper.isNullOrEmpty()) {

                """
                #080B18 url('$savedWallpaper')
                center center / cover
                no-repeat fixed
                """

            } else {

                when (savedBackground) {

                    "midnight" ->
                        "linear-gradient(135deg, #000000, #333333)"

                    "ocean" ->
                        "linear-gradient(135deg, #004D40, #001B44)"

                    "purple" ->
                        "linear-gradient(135deg, #6A1B9A, #12002F)"

                    else ->
                        "linear-gradient(135deg, #1565C0, #020024)"
                }
            }

        val html = """
<!DOCTYPE html>

<html>

<head>

<meta name="viewport"
      content="width=device-width,
      initial-scale=1.0">

<title>Nexora</title>

<style>

* {
    box-sizing: border-box;
}

html,
body {
    width: 100%;
    height: 100%;
    margin: 0;
}

body {

    background:
        $backgroundCss;

    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;

    color: white;

    font-family:
        Arial,
        sans-serif;

    overflow-y: auto;
}

.overlay {

    min-height: 100vh;

    padding:
        45px
        20px
        40px;

    background:
        linear-gradient(
            rgba(0,0,0,0.15),
            rgba(0,0,0,0.35)
        );
}

.logo {

    text-align: center;

    font-size: 42px;

    font-weight: bold;

    letter-spacing: 2px;

    margin-top: 30px;

    text-shadow:
        0 4px 18px
        rgba(0,0,0,0.6);
}

.subtitle {

    text-align: center;

    margin-top: 8px;

    color:
        rgba(255,255,255,0.8);

    font-size: 14px;
}

.search {

    max-width: 650px;

    margin:
        35px auto
        30px;

    display: flex;

    background:
        rgba(255,255,255,0.95);

    border-radius: 30px;

    padding: 5px;

    box-shadow:
        0 10px 35px
        rgba(0,0,0,0.3);
}

.search input {

    flex: 1;

    border: none;

    outline: none;

    background:
        transparent;

    padding:
        14px
        18px;

    font-size: 16px;

    color: #111;
}

.search button {

    border: none;

    border-radius: 25px;

    padding:
        0 22px;

    background:
        #1565C0;

    color: white;

    font-weight: bold;
}

.section-title {

    max-width: 650px;

    margin:
        25px auto
        12px;

    font-size: 16px;

    font-weight: bold;
}

.shortcuts {

    max-width: 650px;

    margin: auto;

    display: grid;

    grid-template-columns:
        repeat(2, 1fr);

    gap: 14px;
}

.shortcut {

    padding: 20px;

    border-radius: 18px;

    background:
        rgba(255,255,255,0.13);

    border:
        1px solid
        rgba(255,255,255,0.2);

    backdrop-filter:
        blur(12px);

    color: white;

    text-decoration: none;

    text-align: center;

    font-size: 15px;

    box-shadow:
        0 8px 25px
        rgba(0,0,0,0.15);
}

.shortcut:active {

    transform:
        scale(0.97);
}

.info {

    max-width: 650px;

    margin:
        30px auto
        0;

    text-align: center;

    color:
        rgba(255,255,255,0.65);

    font-size: 12px;
}

@media (min-width: 600px) {

    .shortcuts {

        grid-template-columns:
            repeat(4, 1fr);
    }
}

</style>

</head>

<body>

<div class="overlay">

<div class="logo">
NEXORA
</div>

<div class="subtitle">
Your private gateway to the web
</div>

<form
    class="search"
    onsubmit="searchGoogle(); return false;">

<input
    id="searchBox"
    type="text"
    placeholder="Search or enter address"
    autocomplete="off">

<button type="submit">
Search
</button>

</form>

<div class="section-title">
Quick Access
</div>

<div class="shortcuts">

<a
    class="shortcut"
    href="https://www.google.com">
🔎<br>
Google
</a>

<a
    class="shortcut"
    href="https://www.youtube.com">
▶<br>
YouTube
</a>

<a
    class="shortcut"
    href="https://www.wikipedia.org">
📚<br>
Wikipedia
</a>

<a
    class="shortcut"
    href="https://www.github.com">
💻<br>
GitHub
</a>

</div>

<div class="info">
Nexora Browser
<br>
Fast • Private • Simple
</div>

</div>

<script>

function searchGoogle() {

    var value =
        document
        .getElementById("searchBox")
        .value
        .trim();

    if (value.length === 0) {
        return;
    }

    var url =
        "https://www.google.com/search?q=" +
        encodeURIComponent(value);

    window.location.href = url;
}

</script>

</body>

</html>
"""

        webView.loadDataWithBaseURL(
            "https://nexora.local/",
            html,
            "text/html",
            "UTF-8",
            null
        )

        urlBar.setText(
            "Nexora Home"
        )

        bookmarkButton.text = "☆"

        updateNavigationButtons()
    }

    private fun updateBookmarkButton(
        url: String
    ) {

        if (
            BookmarkManager
                .isBookmarked(url)
        ) {

            bookmarkButton.text = "★"

        } else {

            bookmarkButton.text = "☆"
        }
    }

    private fun updateNavigationButtons() {

        backButton.isEnabled =
            webView.canGoBack()

        forwardButton.isEnabled =
            webView.canGoForward()
    }

    private fun updateTab(
        title: String,
        url: String
    ) {

        if (
            TabManager.tabs.isEmpty()
        ) {
            TabManager.initialize()
        }

        val current =
            TabManager.currentTab()

        current.title =
            title.ifEmpty {
                "Nexora"
            }

        current.url = url
    }

    private fun saveCurrentTab() {

        if (
            TabManager.tabs.isEmpty()
        ) {
            TabManager.initialize()
        }

        val current =
            TabManager.currentTab()

        val currentUrl =
            webView.url

        if (!currentUrl.isNullOrEmpty()) {

            current.url =
                currentUrl

            current.title =
                webView.title
                    ?: current.title
        }
    }

    override fun onResume() {

        super.onResume()

        if (
            ::webView.isInitialized
        ) {

            val currentUrl =
                webView.url

            if (
                currentUrl ==
                "https://nexora.local/"
            ) {

                loadNexoraHome()
            }
        }
    }

    override fun onBackPressed() {

        if (webView.canGoBack()) {

            webView.goBack()

        } else {

            super.onBackPressed()
        }
    }

    override fun onDestroy() {

        webView.stopLoading()
        webView.destroy()

        super.onDestroy()
    }
}