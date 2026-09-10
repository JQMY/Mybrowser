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

        val goButton: Button =
            findViewById(R.id.goButton)

        val backButton: Button =
            findViewById(R.id.backButton)

        val forwardButton: Button =
            findViewById(R.id.forwardButton)

        val homeButton: Button =
            findViewById(R.id.homeButton)

        val refreshButton: Button =
            findViewById(R.id.refreshButton)

        val historyButton: Button =
            findViewById(R.id.historyButton)

        val bookmarkButton: Button =
            findViewById(R.id.bookmarkButton)

        val tabsButton: Button =
            findViewById(R.id.tabsButton)

        val settingsButton: Button =
            findViewById(R.id.settingsButton)

        TabManager.initialize()

        configureBrowser()

        webView.webViewClient =
            object : WebViewClient() {

                override fun onPageFinished(
                    view: WebView?,
                    url: String?
                ) {
                    super.onPageFinished(
                        view,
                        url
                    )

                    if (
                        url != null &&
                        !url.startsWith(
                            "https://nexora.local"
                        )
                    ) {

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
            intent.getStringExtra(
                "selectedUrl"
            )

        if (selectedUrl != null) {

            webView.loadUrl(
                selectedUrl
            )

        } else {

            loadNexoraHome()
        }

        goButton.setOnClickListener {
            openWebsite()
        }

        urlBar.setOnEditorActionListener {
                _, _, _ ->

            openWebsite()

            true
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
            webView.reload()
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

    private fun configureBrowser() {

        val settings =
            webView.settings

        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true

        settings.allowFileAccess = false
        settings.allowContentAccess = false

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.LOLLIPOP
        ) {

            settings.mixedContentMode =
                WebSettings
                    .MIXED_CONTENT_NEVER_ALLOW
        }

        if (
            Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.O
        ) {

            settings.safeBrowsingEnabled =
                true
        }

        CookieManager
            .getInstance()
            .setAcceptThirdPartyCookies(
                webView,
                false
            )
    }

    private fun getBackground(): String {

        val preferences =
            getSharedPreferences(
                "nexora_settings",
                MODE_PRIVATE
            )

        return preferences.getString(
            "background",
            "cosmic"
        ) ?: "cosmic"
    }

    private fun getBackgroundCss(): String {

        return when (getBackground()) {

            "midnight" -> {
                """
                background:
                radial-gradient(
                    circle at 50% 0%,
                    #202020 0%,
                    #0d0d0d 40%,
                    #020202 100%
                );
                """.trimIndent()
            }

            "ocean" -> {
                """
                background:
                radial-gradient(
                    circle at 50% 0%,
                    #087f9b 0%,
                    #063c61 40%,
                    #021522 100%
                );
                """.trimIndent()
            }

            "purple" -> {
                """
                background:
                radial-gradient(
                    circle at 50% 0%,
                    #7c35a8 0%,
                    #35165c 40%,
                    #0b0315 100%
                );
                """.trimIndent()
            }

            else -> {
                """
                background:
                radial-gradient(
                    circle at 50% 0%,
                    #315fc4 0%,
                    #121f55 35%,
                    #070b24 70%,
                    #02030e 100%
                );
                """.trimIndent()
            }
        }
    }

    private fun loadNexoraHome() {

        val background =
            getBackgroundCss()

        val html = """
<!DOCTYPE html>

<html>

<head>

<meta name="viewport"
content="width=device-width,
initial-scale=1.0">

<style>

* {
    box-sizing: border-box;
}

body {
    margin: 0;

    min-height: 100vh;

    font-family: sans-serif;

    color: white;

    $background

    overflow-x: hidden;
}

.container {
    max-width: 720px;

    margin: auto;

    padding: 38px 20px;

    text-align: center;
}

.logo {
    width: 90px;
    height: 90px;

    margin: auto;

    border-radius: 28px;

    display: flex;

    align-items: center;

    justify-content: center;

    font-size: 60px;

    font-weight: bold;

    background:
    linear-gradient(
        145deg,
        #00eaff,
        #407cff,
        #b04cff
    );

    box-shadow:
    0 0 30px
    rgba(0,200,255,0.35);
}

.brand {
    margin-top: 14px;

    font-size: 42px;

    font-weight: bold;

    letter-spacing: 2px;
}

.tagline {
    margin-top: 5px;

    font-size: 13px;

    letter-spacing: 6px;

    color: #c9d5ff;
}

.status {
    margin: 24px auto;

    display: inline-block;

    padding: 10px 18px;

    border-radius: 22px;

    background:
    rgba(255,255,255,0.08);

    color: #dce5ff;
}

.search {
    width: 100%;

    height: 60px;

    display: flex;

    align-items: center;

    padding: 5px;

    border-radius: 32px;

    background: white;
}

.search input {
    flex: 1;

    height: 50px;

    border: none;

    outline: none;

    padding: 0 20px;

    font-size: 16px;
}

.search button {
    width: 50px;

    height: 50px;

    border: none;

    border-radius: 50%;

    background: #101a40;

    color: white;

    font-size: 22px;
}

.section {
    margin-top: 32px;

    text-align: left;

    color: #b7c5f5;

    font-size: 15px;
}

.shortcuts {
    display: grid;

    grid-template-columns:
    repeat(3, 1fr);

    gap: 12px;

    margin-top: 14px;
}

.shortcut {
    padding: 15px 6px;

    border-radius: 20px;

    background:
    rgba(255,255,255,0.07);

    border:
    1px solid
    rgba(255,255,255,0.12);

    text-decoration: none;

    color: white;
}

.icon {
    width: 52px;

    height: 52px;

    margin: auto;

    border-radius: 16px;

    display: flex;

    align-items: center;

    justify-content: center;

    background:
    rgba(255,255,255,0.12);

    font-size: 24px;
}

.name {
    margin-top: 8px;

    font-size: 12px;
}

.info {
    margin-top: 28px;

    padding: 18px;

    border-radius: 20px;

    text-align: left;

    background:
    rgba(255,255,255,0.07);
}

.info-small {
    color: #9eaddc;

    font-size: 12px;
}

.info-large {
    margin-top: 7px;

    font-size: 23px;

    font-weight: bold;
}

.footer {
    margin-top: 30px;

    color: #7786b9;

    font-size: 12px;
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
⚡ Fast &nbsp; • &nbsp;
🛡 Secure &nbsp; • &nbsp;
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

<div class="section">
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

<div class="info-small">
NEXORA BROWSER
</div>

<div class="info-large">
Ready to explore
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
            html,
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

        bookmarkButton.text =
            if (
                BookmarkManager.isBookmarked(
                    currentUrl
                )
            ) {
                "★"
            } else {
                "☆"
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