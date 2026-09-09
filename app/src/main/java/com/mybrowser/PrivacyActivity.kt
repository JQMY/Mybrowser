package com.mybrowser

import android.app.Activity
import android.os.Bundle
import android.webkit.CookieManager
import android.webkit.WebStorage
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast

class PrivacyActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.privacy_page)

        val clearButton: Button =
            findViewById(R.id.clearBrowsingDataButton)

        clearButton.setOnClickListener {

            // Clear browser history
            HistoryManager.history.clear()

            // Clear WebView cache
            val webView = WebView(this)
            webView.clearCache(true)
            webView.clearHistory()

            // Clear cookies
            CookieManager.getInstance().removeAllCookies {
                CookieManager.getInstance().flush()
            }

            // Clear website storage
            WebStorage.getInstance().deleteAllData()

            Toast.makeText(
                this,
                "Browsing data cleared",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}