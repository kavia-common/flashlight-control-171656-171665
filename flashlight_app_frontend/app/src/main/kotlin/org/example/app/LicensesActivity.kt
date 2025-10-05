package org.example.app

import android.os.Bundle
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

// PUBLIC_INTERFACE
/**
 * LicensesActivity
 * Displays a static list of open source licenses bundled with the application. Content is loaded from
 * res/raw/open_source_licenses.html via WebView with restricted settings for local-only rendering.
 */
class LicensesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_licenses)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.licenses_title)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val webView: WebView = findViewById(R.id.webView)
        val settings: WebSettings = webView.settings
        settings.javaScriptEnabled = false
        settings.allowContentAccess = false
        settings.allowFileAccess = false
        settings.domStorageEnabled = false
        settings.cacheMode = WebSettings.LOAD_NO_CACHE

        val html = resources.openRawResource(R.raw.open_source_licenses).bufferedReader().use { it.readText() }
        webView.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)
    }
}
