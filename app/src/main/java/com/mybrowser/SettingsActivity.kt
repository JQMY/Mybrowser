package com.mybrowser

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup

class SettingsActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings_page)

        val backgroundGroup: RadioGroup =
            findViewById(R.id.backgroundGroup)

        val saveButton: Button =
            findViewById(R.id.saveSettingsButton)

        val savedBackground =
            getPreferences(MODE_PRIVATE)
                .getString(
                    "background",
                    "cosmic"
                )

        when (savedBackground) {

            "cosmic" ->
                findViewById<RadioButton>(
                    R.id.cosmicBackground
                ).isChecked = true

            "midnight" ->
                findViewById<RadioButton>(
                    R.id.midnightBackground
                ).isChecked = true

            "ocean" ->
                findViewById<RadioButton>(
                    R.id.oceanBackground
                ).isChecked = true

            "purple" ->
                findViewById<RadioButton>(
                    R.id.purpleBackground
                ).isChecked = true
        }

        saveButton.setOnClickListener {

            val selectedId =
                backgroundGroup.checkedRadioButtonId

            val selectedBackground =
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

            getPreferences(MODE_PRIVATE)
                .edit()
                .putString(
                    "background",
                    selectedBackground
                )
                .apply()

            finish()
        }
    }
}