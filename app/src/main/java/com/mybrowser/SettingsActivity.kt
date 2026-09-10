package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup

class SettingsActivity : Activity() {

    private lateinit var backgroundGroup: RadioGroup
    private lateinit var saveButton: Button

    companion object {
        private const val PICK_WALLPAPER = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.settings_page)

        backgroundGroup =
            findViewById(R.id.backgroundGroup)

        saveButton =
            findViewById(R.id.saveSettingsButton)

        val chooseWallpaperButton =
            findViewById<Button>(
                R.id.chooseWallpaperButton
            )

        val removeWallpaperButton =
            findViewById<Button>(
                R.id.removeWallpaperButton
            )

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

        // Choose a custom wallpaper
        chooseWallpaperButton.setOnClickListener {

            val intent = Intent(
                Intent.ACTION_OPEN_DOCUMENT
            )

            intent.type = "image/*"

            intent.addCategory(
                Intent.CATEGORY_OPENABLE
            )

            startActivityForResult(
                intent,
                PICK_WALLPAPER
            )
        }

        // Remove custom wallpaper
        removeWallpaperButton.setOnClickListener {

            preferences
                .edit()
                .remove("wallpaperUri")
                .apply()
        }

        // Save appearance settings
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

    @Deprecated("Deprecated in Android API")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(
            requestCode,
            resultCode,
            data
        )

        if (
            requestCode == PICK_WALLPAPER &&
            resultCode == RESULT_OK
        ) {

            val wallpaperUri: Uri? =
                data?.data

            if (wallpaperUri != null) {

                try {

                    contentResolver.takePersistableUriPermission(
                        wallpaperUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )

                } catch (_: Exception) {
                    // Some providers don't support
                    // persistable permissions.
                }

                getSharedPreferences(
                    "nexora_settings",
                    MODE_PRIVATE
                )
                    .edit()
                    .putString(
                        "wallpaperUri",
                        wallpaperUri.toString()
                    )
                    .apply()
            }
        }
    }
}