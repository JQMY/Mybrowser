package com.mybrowser

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup

class SettingsActivity : Activity() {

    private lateinit var backgroundGroup: RadioGroup
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings_page)

        backgroundGroup =
            findViewById(R.id.backgroundGroup)

        saveButton =
            findViewById(R.id.saveSettingsButton)

        val cosmic =
            findViewById<RadioButton>(
                R.id.cosmicBackground
            )

        val midnight =
            findViewById<RadioButton>(
                R.id.midnightBackground
            )

        val ocean =
            findViewById<RadioButton>(
                R.id.oceanBackground
            )

        val purple =
            findViewById<RadioButton>(
                R.id.purpleBackground
            )

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

        when (savedBackground) {

            "midnight" -> {
                midnight.isChecked = true
            }

            "ocean" -> {
                ocean.isChecked = true
            }

            "purple" -> {
                purple.isChecked = true
            }

            else -> {
                cosmic.isChecked = true
            }
        }

        saveButton.setOnClickListener {

            val selectedId =
                backgroundGroup.checkedRadioButtonId

            val background =
                when (selectedId) {

                    R.id.midnightBackground ->
                        "midnight"

                    R.id.oceanBackground ->
                        "ocean"

                    R.id.purpleBackground ->
                        "purple"

                    else ->
                        "cosmic"
                }

            preferences
                .edit()
                .putString(
                    "background",
                    background
                )
                .apply()

            finish()
        }
    }
}