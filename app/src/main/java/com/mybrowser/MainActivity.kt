package com.mybrowser

import android.app.Activity
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

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true

        webView.webViewClient = WebViewClient()

        webView.loadUrl("https://www.google.com")

        goButton.setOnClickListener {
            openWebsite()
        }

        urlBar.setOnEditorActionListener { _, _, _ ->
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
            webView.loadUrl("https://www.google.com")
        }

        refreshButton.setOnClickListener {
            webView.reload()
        }
    }

    private fun openWebsite() {

        var address = urlBar.text.toString().trim()

        if (address.isEmpty()) {
            return
        }

        if (!address.startsWith("http://") &&
            !address.startsWith("https://")) {

            address = "https://www.google.com/search?q=" +
                    address.replace(" ", "+")
        }

        webView.loadUrl(address)
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