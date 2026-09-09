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
        val tabsButton: Button = findViewById(R.id.tabsButton)

        TabManager.initialize()

        // Browser settings
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        // Security settings
        webView.settings.allowFileAccess = false
        webView.settings.allowContentAccess = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.settings.mixedContentMode =
                WebSettings.MIXED_CONTENT_NEVER_ALLOW
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            webView.settings.safeBrowsingEnabled = true
        }

        CookieManager.getInstance()
            .setAcceptThirdPartyCookies(webView, false)

        // WebView client
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

        // Open requested page or Nexora home
        val selectedUrl =
            intent.getStringExtra("selectedUrl")

        if (selectedUrl != null) {
            webView.loadUrl(selectedUrl)
        } else {
            loadNexoraHome()
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

            if (
                BookmarkManager.isBookmarked(
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

        // Long press bookmark opens bookmark list
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
        circle at 50% 0%,
        #284fa8 0%,
        #101d50 32%,
        #070c25 68%,
        #02030e 100%
    );

    overflow-x: hidden;
}

.container {
    width: 100%;
    max-width: 720px;
    margin: auto;
    padding: 35px 20px 45px;
    text-align: center;
}

.logo {
    width: 90px;
    height: 90px;
    margin: 5px auto 12px;

    border-radius: 28px;

    display: flex;
    align-items: center;
    justify-content: center;

    font-size: 62px;
    font-weight: bold;

    background:
    linear-gradient(
        145deg,
        #00eaff,
        #387cff,
        #b34cff
    );

    color: white;

    box-shadow:
    0 0 30px
    rgba(0,180,255,0.45);
}

.brand {
    font-size: 42px;
    font-weight: bold;
    letter-spacing: 2px;
}

.tagline {
    margin-top: 7px;
    color: #cbd6ff;
    font-size: 13px;
    letter-spacing: 6px;
}

.status {
    margin: 22px auto 28px;

    display: inline-flex;
    gap: 10px;

    padding: 9px 17px;

    border-radius: 20px;

    background:
    rgba(255,255,255,0.08);

    border:
    1px solid
    rgba(255,255,255,0.14);

    color: #dce4ff;

    font-size: 12px;
}

.search {
    width: 100%;
    height: 60px;

    display: flex;
    align-items: center;

    padding: 5px;

    border-radius: 32px;

    background:
    rgba(255,255,255,0.96);

    box-shadow:
    0 12px 35px
    rgba(0,0,0,0.25);
}

.search input {
    flex: 1;

    height: 50px;

    border: none;
    outline: none;

    background: transparent;

    padding: 0 20px;

    font-size: 16px;
    color: #111;
}

.search button {
    width: 50px;
    height: 50px;

    border: none;
    border-radius: 50%;

    background: #111b40;
    color: white;

    font-size: 22px;
}

.section-title {
    margin-top: 38px;
    margin-bottom: 17px;

    text-align: left;

    font-size: 15px;
    color: #aebcf0;
}

.shortcuts {
    display: grid;

    grid-template-columns:
    repeat(3, 1fr);

    gap: 13px;
}

.shortcut {
    text-decoration: none;
    color: white;

    padding: 16px 7px;

    border-radius: 20px;

    background:
    rgba(255,255,255,0.07);

    border:
    1px solid
    rgba(255,255,255,0.13);
}

.icon {
    width: 55px;
    height: 55px;

    margin: auto;

    border-radius: 17px;

    display: flex;
    align-items: center;
    justify-content: center;

    background:
    rgba(255,255,255,0.12);

    font-size: 24px;
    font-weight: bold;
}

.name {
    margin-top: 9px;

    font-size: 12px;
    color: #e1e7ff;
}

.info {
    margin-top: 28px;

    padding: 18px;

    border-radius: 20px;

    text-align: left;

    background:
    rgba(255,255,255,0.07);

    border:
    1px solid
    rgba(255,255,255,0.12);
}

.info-title {
    font-size: 12px;
    color: #9eacd9;
}

.info-main {
    margin-top: 7px;

    font-size: 24px;
    font-weight: bold;
}

.info-text {
    margin-top: 4px;

    font-size: 12px;
    color: #b9c5e9;
}

.footer {
    margin-top: 35px;

    font-size: 12px;
    color: #7784b2;
}

@media (max-width: 380px) {

    .brand {
        font-size: 36px;
    }

    .shortcuts {
        gap: 8px;
    }

    .shortcut {
        padding: 12px 4px;
    }
}

</style>

</head>

<body>

<div class="container">

<div class="logo">
N
</div>

<div class="brand">
Nexora
</div>

<div class="tagline">
BROWSE BEYOND.
</div>

<div class="status">
⚡ Fast
•
🛡 Secure
•
✦ Smart
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

<div class="section-title">
Quick access
</div>

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

<div class="info">

<div class="info-title">
YOUR BROWSING SPACE
</div>

<div class="info-main">
Ready to explore
</div>

<div class="info-text">
Fast, secure and minimal browsing.
</div>

</div>

<div class="footer">
Nexora Browser • Browse Beyond.
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

        TabManager.currentTab().url =
            "https://nexora.local/"

        TabManager.currentTab().title =
            "Nexora"

        updateBookmarkButton()
    }

    private fun openWebsite() {

        var address =
            urlBar.text.toString().trim()

        if (address.isEmpty()) {
            return
        }

        if (
            !address.startsWith("http://") &&
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
            findViewById(
                R.id.bookmarkButton
            )

        val currentUrl =
            TabManager.currentTab().url

        if (
            BookmarkManager.isBookmarked(
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