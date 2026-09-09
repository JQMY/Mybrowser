package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.os.Bundle
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
        val tabsButton: Button = findViewById(R.id.tabsButton)
        val historyButton: Button = findViewById(R.id.historyButton)

        TabManager.initialize()

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = object : WebViewClient() {

            override fun onPageFinished(
                view: WebView?,
                url: String?
            ) {

                super.onPageFinished(view, url)

                if (url != null) {

                    val currentTab = TabManager.currentTab()

                    currentTab.url = url

                    currentTab.title =
                        view?.title ?: "New Tab"

                    urlBar.setText(url)

                    // Save page to browser history
                    HistoryManager.add(
                        currentTab.title,
                        url
                    )
                }
            }
        }

        val selectedUrl =
            intent.getStringExtra("selectedUrl")

        if (selectedUrl != null) {

            webView.loadUrl(selectedUrl)

        } else {

            val currentTab = TabManager.currentTab()

            webView.loadUrl(currentTab.url)
        }

        // Go button
        goButton.setOnClickListener {
            openWebsite()
        }

        // Keyboard search / enter
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

        // Tabs
        tabsButton.setOnClickListener {

            val intent =
                Intent(this, TabsActivity::class.java)

            startActivity(intent)
        }
    }

   // History 
   historyButton.setOnClickListener {

    val intent =
        Intent(this, HistoryActivity::class.java)

    startActivity(intent)
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

        TabManager.currentTab().url = address
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