package com.mybrowser

import android.app.Activity
import android.os.Bundle
import android.widget.Button

class TabsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.tabs_page)

        val newTabButton: Button = findViewById(R.id.newTabButton)

        newTabButton.setOnClickListener {
            finish()
        }
    }
}