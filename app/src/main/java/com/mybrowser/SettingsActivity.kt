package com.mybrowser

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import java.io.File
import java.io.FileOutputStream

class SettingsActivity : Activity() {

    private lateinit var backgroundGroup: RadioGroup
    private lateinit var saveButton: Button

    companion object {
        private const val PICK_WALLPAPER = 1001
        private const val WALLPAPER_FILE = "nexora_wallpaper.jpg"
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
            "midnight" -> midnight.isChecked = true
            "ocean" -> ocean.isChecked = true
            "purple" -> purple.isChecked = true
            else -> cosmic.isChecked = true
        }

        chooseWallpaperButton.setOnClickListener {

            val intent =
                Intent(Intent.ACTION_OPEN_DOCUMENT)

            intent.type = "image/*"

            intent.addCategory(
                Intent.CATEGORY_OPENABLE
            )

            startActivityForResult(
                intent,
                PICK_WALLPAPER
            )
        }

        removeWallpaperButton.setOnClickListener {

            val wallpaperFile =
                File(
                    filesDir,
                    WALLPAPER_FILE
                )

            if (wallpaperFile.exists()) {
                wallpaperFile.delete()
            }

            preferences
                .edit()
                .remove("wallpaperFile")
                .apply()

            Toast.makeText(
                this,
                "Custom wallpaper removed",
                Toast.LENGTH_SHORT
            ).show()
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

            Toast.makeText(
                this,
                "Settings saved",
                Toast.LENGTH_SHORT
            ).show()

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
            requestCode != PICK_WALLPAPER ||
            resultCode != RESULT_OK
        ) {
            return
        }

        val selectedUri =
            data?.data

        if (selectedUri == null) {
            return
        }

        try {

            val input =
                contentResolver.openInputStream(
                    selectedUri
                )

            if (input == null) {
                throw Exception(
                    "Unable to open image"
                )
            }

            val bitmap =
                BitmapFactory.decodeStream(input)

            input.close()

            if (bitmap == null) {
                throw Exception(
                    "Invalid image"
                )
            }

            val destination =
                File(
                    filesDir,
                    WALLPAPER_FILE
                )

            FileOutputStream(
                destination
            ).use { output ->

                bitmap.compress(
                    Bitmap.CompressFormat.JPEG,
                    90,
                    output
                )
            }

            bitmap.recycle()

            if (
                !destination.exists() ||
                destination.length() == 0L
            ) {
                throw Exception(
                    "Wallpaper was not saved"
                )
            }

            getSharedPreferences(
                "nexora_settings",
                MODE_PRIVATE
            )
                .edit()
                .putString(
                    "wallpaperFile",
                    WALLPAPER_FILE
                )
                .apply()

            Toast.makeText(
                this,
                "Wallpaper selected",
                Toast.LENGTH_SHORT
            ).show()

        } catch (e: Exception) {

            Toast.makeText(
                this,
                "Could not use this image",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}