package org.example.app

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

// PUBLIC_INTERFACE
/**
 * OssListActivity
 * Displays a simple placeholder list of open source libraries. This is a temporary screen
 * that will be replaced with a dynamic list later.
 */
class OssListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oss_list_placeholder)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.oss_licenses_screen_title)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val content: TextView = findViewById(R.id.contentText)
        content.text = """
            Kotlin - Apache 2.0
            AndroidX AppCompat - Apache 2.0
            AndroidX Core KTX - Apache 2.0
            ConstraintLayout - Apache 2.0

            ${getString(R.string.placeholder_oss_note)}
        """.trimIndent()
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
