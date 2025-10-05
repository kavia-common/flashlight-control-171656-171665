package org.example.app

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * SettingsActivity
 * Provides user-configurable settings:
 * - Keep screen on while flashlight is ON (default off)
 * - Haptic feedback on toggle (default off)
 * And quick links to About, Privacy Policy, and Open Source Licenses.
 * All preferences are persisted via SharedPreferences using the app's default preferences.
 */
// PUBLIC_INTERFACE
class SettingsActivity : AppCompatActivity() {

    companion object {
        // Keys for SharedPreferences
        const val PREFS_NAME = "flashlight_prefs"
        const val KEY_KEEP_SCREEN_ON = "keep_screen_on"
        const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.settings_title)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        // Switches
        val keepScreenOnSwitch: Switch = findViewById(R.id.switchKeepScreenOn)
        val hapticSwitch: Switch = findViewById(R.id.switchHaptic)

        // Load persisted values (defaults to false)
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val keepOn = prefs.getBoolean(KEY_KEEP_SCREEN_ON, false)
        val haptic = prefs.getBoolean(KEY_HAPTIC_FEEDBACK, false)
        keepScreenOnSwitch.isChecked = keepOn
        hapticSwitch.isChecked = haptic

        // Toggle listeners save immediately
        keepScreenOnSwitch.setOnCheckedChangeListener { v, isChecked ->
            prefs.edit().putBoolean(KEY_KEEP_SCREEN_ON, isChecked).apply()
            maybeHaptic(v)
        }
        hapticSwitch.setOnCheckedChangeListener { v, isChecked ->
            prefs.edit().putBoolean(KEY_HAPTIC_FEEDBACK, isChecked).apply()
            maybeHaptic(v)
        }

        // Entire rows toggle the switches
        val rowKeepScreenOn: View = findViewById(R.id.rowKeepScreenOn)
        val rowHaptic: View = findViewById(R.id.rowHaptic)

        rowKeepScreenOn.setOnClickListener {
            keepScreenOnSwitch.toggle()
            maybeHaptic(it)
        }
        rowHaptic.setOnClickListener {
            hapticSwitch.toggle()
            maybeHaptic(it)
        }

        // Links
        val aboutRow: TextView = findViewById(R.id.aboutRow)
        val privacyRow: TextView = findViewById(R.id.privacyRow)
        val licensesRow: TextView = findViewById(R.id.licensesRow)

        aboutRow.setOnClickListener { startActivity(Intent(this, AboutActivity::class.java)) }
        privacyRow.setOnClickListener { startActivity(Intent(this, PrivacyActivity::class.java)) }
        licensesRow.setOnClickListener { startActivity(Intent(this, LicensesActivity::class.java)) }
    }

    private fun isHapticEnabled(): Boolean {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        return prefs.getBoolean(KEY_HAPTIC_FEEDBACK, false)
    }

    private fun maybeHaptic(view: View) {
        if (!isHapticEnabled()) return
        // Perform a simple virtual key haptic; safe across API levels
        val constant = if (Build.VERSION.SDK_INT >= 21) {
            HapticFeedbackConstants.VIRTUAL_KEY
        } else {
            HapticFeedbackConstants.KEYBOARD_TAP
        }
        view.performHapticFeedback(constant)
    }
}
