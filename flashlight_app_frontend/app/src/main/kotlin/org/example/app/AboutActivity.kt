package org.example.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

// PUBLIC_INTERFACE
/**
 * AboutActivity
 * Displays app name, short description, app version, and quick navigation to Privacy Policy
 * and Open Source Licenses screens. Uses the Ocean Professional theme and a simple toolbar with Up navigation.
 */
class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.about_title)

        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val appIcon: ImageView = findViewById(R.id.appIcon)
        val appTitle: TextView = findViewById(R.id.appTitle)
        val appDesc: TextView = findViewById(R.id.appDesc)
        val versionText: TextView = findViewById(R.id.versionText)
        val privacyRow: TextView = findViewById(R.id.privacyRow)
        val licensesRow: TextView = findViewById(R.id.licensesRow)

        appIcon.setImageResource(R.drawable.ic_flash_on_24)
        appTitle.text = getString(R.string.app_name)
        appDesc.text = getString(R.string.about_description)

        // Show versionName retrieved from PackageManager to avoid build-time dependency on BuildConfig
        val versionName = try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
        val versionDisplay = getString(R.string.version_label, versionName)
        versionText.text = versionDisplay

        privacyRow.setOnClickListener {
            startActivity(Intent(this, PrivacyActivity::class.java))
        }

        licensesRow.setOnClickListener {
            startActivity(Intent(this, LicensesActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_about, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
