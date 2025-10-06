package org.example.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

// PUBLIC_INTERFACE
/**
 * PrivacyNoticeActivity
 * Displays a placeholder Privacy Notice with Ocean Professional styling.
 * Uses a simple scrollable text layout and an AppBar with back navigation.
 */
class PrivacyNoticeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privacy_notice_placeholder)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.privacy_notice_screen_title)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val content: TextView = findViewById(R.id.contentText)
        content.text = buildString {
            appendLine(getString(R.string.placeholder_section_data_collection))
            appendLine(getString(R.string.placeholder_lorem))
            appendLine()
            appendLine(getString(R.string.placeholder_section_usage))
            appendLine(getString(R.string.placeholder_lorem))
            appendLine()
            appendLine(getString(R.string.placeholder_section_contact))
            appendLine(getString(R.string.contact_email_placeholder))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_simple_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(android.content.Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
