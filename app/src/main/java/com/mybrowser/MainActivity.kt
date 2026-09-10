package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.webkit.WebViewAssetLoader
import androidx.webkit.WebViewClientCompat
import java.io.File

class MainActivity : Activity() {

    private lateinit var webView: WebView
    private lateinit var urlBar: EditText

    private lateinit var assetLoader: WebViewAssetLoader

    companion object {

        private const val HOME_URL =
            "https://nexora.local/"

        private const val WALLPAPER_FILE =
            "nexora_wallpaper.jpg"
    }

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_main
        )

        urlBar =
            findViewById(R.id.urlBar)

        webView =
            findViewById(R.id.webView)

        setupAssetLoader()
        setupWebView()
        setupButtons()

        val selectedUrl =
            intent.getStringExtra(
                "selectedUrl"
            )

        if (
            !selectedUrl.isNullOrEmpty()
        ) {

            webView.loadUrl(
                selectedUrl
            )

        } else {

            loadNexoraHome()
        }
    }

    private fun setupAssetLoader() {

        assetLoader =
            WebViewAssetLoader.Builder()

                .addPathHandler(
                    "/wallpaper/",
                    WebViewAssetLoader
                        .InternalStoragePathHandler(
                            this,
                            filesDir
                        )
                )

                .build()
    }

    private fun setupWebView() {

        val settings =
            webView.settings

        settings.javaScriptEnabled = true

        settings.domStorageEnabled = true

        settings.loadsImagesAutomatically = true

        settings.mediaPlaybackRequiresUserGesture =
            false

        settings.allowFileAccess = false

        settings.allowContentAccess = false

        if (
            android.os.Build.VERSION.SDK_INT >=
            android.os.Build.VERSION_CODES.O
        ) {

            settings.safeBrowsingEnabled = true
        }

        webView.webViewClient =
            object : WebViewClientCompat() {

                override fun shouldInterceptRequest(
                    view: WebView,
                    request: WebResourceRequest
                ): WebResourceResponse? {

                    return assetLoader
                        .shouldInterceptRequest(
                            request.url
                        )
                }

                override fun onPageStarted(
                    view: WebView,
                    url: String?,
                    favicon: Bitmap?
                ) {

                    super.onPageStarted(
                        view,
                        url,
                        favicon
                    )

                    if (!url.isNullOrEmpty()) {
                        urlBar.setText(url)
                    }
                }

                override fun onPageFinished(
                    view: WebView,
                    url: String?
                ) {

                    super.onPageFinished(
                        view,
                        url
                    )

                    if (
                        !url.isNullOrEmpty() &&
                        url != HOME_URL &&
                        !url.startsWith(
                            "https://appassets.androidplatform.net/"
                        )
                    ) {

                        val title =
                            view.title
                                ?: url

                        HistoryManager.add(
                            title,
                            url
                        )
                    }

                    if (
                        !url.isNullOrEmpty()
                    ) {
                        urlBar.setText(url)
                    }
                }
            }
    }

    private fun setupButtons() {

        val goButton =
            findViewById<Button>(
                R.id.goButton
            )

        val backButton =
            findViewById<Button>(
                R.id.backButton
            )

        val forwardButton =
            findViewById<Button>(
                R.id.forwardButton
            )

        val homeButton =
            findViewById<Button>(
                R.id.homeButton
            )

        val refreshButton =
            findViewById<Button>(
                R.id.refreshButton
            )

        val historyButton =
            findViewById<Button>(
                R.id.historyButton
            )

        val bookmarkButton =
            findViewById<Button>(
                R.id.bookmarkButton
            )

        val tabsButton =
            findViewById<Button>(
                R.id.tabsButton
            )

        val settingsButton =
            findViewById<Button>(
                R.id.settingsButton
            )

        goButton.setOnClickListener {

            openWebsite(
                urlBar.text.toString()
            )
        }

        urlBar.setOnEditorActionListener {
                _,
                actionId,
                event ->

            if (
                actionId ==
                EditorInfo.IME_ACTION_GO ||
                actionId ==
                EditorInfo.IME_ACTION_SEARCH ||
                (
                    event != null &&
                    event.keyCode ==
                    KeyEvent.KEYCODE_ENTER
                )
            ) {

                openWebsite(
                    urlBar.text.toString()
                )

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

            if (
                webView.url ==
                HOME_URL
            ) {

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
                webView.url ?: ""

            if (
                currentUrl.isEmpty() ||
                currentUrl == HOME_URL
            ) {

                Toast.makeText(
                    this,
                    "Open a website first",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val currentTitle =
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
                    currentTitle,
                    currentUrl
                )

                Toast.makeText(
                    this,
                    "Bookmark saved",
                    Toast.LENGTH_SHORT
                ).show()
            }
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

    private fun openWebsite(
        input: String
    ) {

        var text =
            input.trim()

        if (text.isEmpty()) {
            return
        }

        val url: String

        if (
            text.startsWith(
                "http://"
            ) ||
            text.startsWith(
                "https://"
            )
        ) {

            url = text

        } else if (
            text.contains(".") &&
            !text.contains(" ")
        ) {

            url =
                "https://$text"

        } else {

            url =
                "https://www.google.com/search?q=" +
                android.net.Uri.encode(
                    text
                )
        }

        webView.loadUrl(url)
    }

    private fun loadNexoraHome() {

        val preferences =
            getSharedPreferences(
                "nexora_settings",
                MODE_PRIVATE
            )

        val background =
            preferences.getString(
                "background",
                "cosmic"
            )

        val wallpaperFile =
            File(
                filesDir,
                WALLPAPER_FILE
            )

        val hasWallpaper =
            wallpaperFile.exists() &&
            wallpaperFile.length() > 0

        val backgroundCss =
            when (background) {

                "midnight" ->
                    "linear-gradient(135deg, #050505, #151515, #000000)"

                "ocean" ->
                    "linear-gradient(135deg, #001f3f, #005f73, #001219)"

                "purple" ->
                    "linear-gradient(135deg, #16002b, #4b0082, #090014)"

                else ->
                    "linear-gradient(135deg, #020024, #090979, #00d4ff)"
            }

        val wallpaperCss =
            if (hasWallpaper) {

                """
                background-image:
                    linear-gradient(
                        rgba(0, 0, 0, 0.38),
                        rgba(0, 0, 0, 0.38)
                    ),
                    url(
                        'https://appassets.androidplatform.net/wallpaper/$WALLPAPER_FILE'
                    );
                background-size: cover;
                background-position: center;
                background-repeat: no-repeat;
                """.trimIndent()

            } else {

                """
                background: $backgroundCss;
                """.trimIndent()
            }

        val html =
            """
            <!DOCTYPE html>

            <html>

            <head>

                <meta
                    name="viewport"
                    content="width=device-width,
                    initial-scale=1.0"
                />

                <style>

                    * {
                        box-sizing: border-box;
                    }

                    html,
                    body {
                        margin: 0;
                        padding: 0;
                        width: 100%;
                        height: 100%;
                    }

                    body {

                        $wallpaperCss

                        color: white;

                        font-family:
                            Arial,
                            sans-serif;

                        display: flex;

                        flex-direction:
                            column;

                        align-items:
                            center;

                        overflow-y: auto;
                    }

                    .page {

                        width: 100%;

                        max-width: 900px;

                        min-height: 100%;

                        padding:
                            55px
                            20px
                            40px;

                        text-align: center;
                    }

                    .logo {

                        font-size: 48px;

                        font-weight: bold;

                        margin-top: 25px;

                        margin-bottom: 8px;

                        text-shadow:
                            0 2px 12px
                            rgba(0,0,0,0.5);
                    }

                    .subtitle {

                        font-size: 16px;

                        opacity: 0.85;

                        margin-bottom: 35px;
                    }

                    .search {

                        display: flex;

                        width: 100%;

                        max-width: 720px;

                        margin:
                            0 auto
                            35px;

                        background:
                            rgba(
                                255,
                                255,
                                255,
                                0.95
                            );

                        border-radius: 30px;

                        padding: 6px;

                        box-shadow:
                            0 8px 30px
                            rgba(
                                0,
                                0,
                                0,
                                0.3
                            );
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

                        border-radius: 24px;

                        padding:
                            0 22px;

                        background:
                            #1976d2;

                        color: white;

                        font-size: 15px;
                    }

                    .section {

                        font-size: 18px;

                        font-weight: bold;

                        margin:
                            25px 0
                            15px;
                    }

                    .shortcuts {

                        display: grid;

                        grid-template-columns:
                            repeat(
                                auto-fit,
                                minmax(
                                    130px,
                                    1fr
                                )
                            );

                        gap: 14px;
                    }

                    .shortcut {

                        display: block;

                        text-decoration: none;

                        color: white;

                        background:
                            rgba(
                                255,
                                255,
                                255,
                                0.14
                            );

                        border:
                            1px solid
                            rgba(
                                255,
                                255,
                                255,
                                0.2
                            );

                        border-radius: 18px;

                        padding: 20px 10px;

                        backdrop-filter:
                            blur(8px);

                        font-size: 15px;

                        transition:
                            transform 0.15s;
                    }

                    .shortcut:active {

                        transform:
                            scale(0.96);
                    }

                    .icon {

                        font-size: 30px;

                        display: block;

                        margin-bottom: 8px;
                    }

                    .footer {

                        margin-top: 40px;

                        opacity: 0.65;

                        font-size: 12px;
                    }

                </style>

            </head>

            <body>

                <div class="page">

                    <div class="logo">
                        NEXORA
                    </div>

                    <div class="subtitle">
                        Explore the web your way
                    </div>

                    <form
                        class="search"
                        onsubmit="searchWeb(); return false;"
                    >

                        <input
                            id="searchInput"
                            type="text"
                            placeholder="Search or enter address"
                        />

                        <button
                            type="submit"
                        >
                            Search
                        </button>

                    </form>

                    <div class="section">
                        Quick Access
                    </div>

                    <div class="shortcuts">

                        <a
                            class="shortcut"
                            href="https://www.google.com"
                        >
                            <span class="icon">
                                🔎
                            </span>
                            Google
                        </a>

                        <a
                            class="shortcut"
                            href="https://www.youtube.com"
                        >
                            <span class="icon">
                                ▶
                            </span>
                            YouTube
                        </a>

                        <a
                            class="shortcut"
                            href="https://www.facebook.com"
                        >
                            <span class="icon">
                                f
                            </span>
                            Facebook
                        </a>

                        <a
                            class="shortcut"
                            href="https://www.instagram.com"
                        >
                            <span class="icon">
                                ◎
                            </span>
                            Instagram
                        </a>

                        <a
                            class="shortcut"
                            href="https://www.wikipedia.org"
                        >
                            <span class="icon">
                                W
                            </span>
                            Wikipedia
                        </a>

                        <a
                            class="shortcut"
                            href="https://www.github.com"
                        >
                            <span class="icon">
                                ◉
       