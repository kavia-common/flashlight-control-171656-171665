package org.example.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraManager.TorchCallback
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.HapticFeedbackConstants
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import android.view.View
import android.widget.PopupMenu

// PUBLIC_INTERFACE
/**
 * MainActivity
 * This activity displays a single centered button to toggle the device flashlight using CameraManager.setTorchMode.
 * - Requests CAMERA permission at runtime (API 23+).
 * - Disables UI with a helpful message when device lacks a flash or permission denied.
 * - Applies Ocean Professional theme colors and subtle animations on toggle.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var cameraManager: CameraManager
    private var torchCameraId: String? = null
    private var isTorchOn: Boolean = false

    private lateinit var toggleButton: Button
    private lateinit var iconView: ImageView
    private lateinit var statusText: TextView

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                toggleTorch()
            } else {
                Toast.makeText(this, R.string.permission_required, Toast.LENGTH_LONG).show()
                updateUIState(enabled = false)
            }
        }

    private val torchCallback = object : TorchCallback() {
        override fun onTorchModeChanged(cameraId: String, enabled: Boolean) {
            if (cameraId == torchCameraId) {
                isTorchOn = enabled
                runOnUiThread { updateVisuals() }
            }
        }
    }

    private fun prefs() = getSharedPreferences(SettingsActivity.PREFS_NAME, MODE_PRIVATE)
    private fun isKeepScreenOnEnabled(): Boolean = prefs().getBoolean(SettingsActivity.KEY_KEEP_SCREEN_ON, false)
    private fun isHapticEnabled(): Boolean = prefs().getBoolean(SettingsActivity.KEY_HAPTIC_FEEDBACK, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Setup AppBar / Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        val overflowLeft: ImageView = findViewById(R.id.overflowLeft)
        overflowLeft.setOnClickListener { v ->
            showLeftOverflowMenu(v)
        }

        toggleButton = findViewById(R.id.toggleButton)
        iconView = findViewById(R.id.iconView)
        statusText = findViewById(R.id.statusText)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        torchCameraId = findBackCameraWithFlash(cameraManager)

        if (torchCameraId == null) {
            Toast.makeText(this, R.string.no_flash_msg, Toast.LENGTH_LONG).show()
            updateUIState(enabled = false)
        } else {
            updateUIState(enabled = true)
        }

        toggleButton.setOnClickListener {
            if (!hasCameraPermission()) {
                requestCameraPermission()
            } else {
                toggleTorch()
                if (isHapticEnabled()) {
                    toggleButton.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
            }
        }

        savedInstanceState?.let {
            isTorchOn = it.getBoolean("torch_state", false)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.registerTorchCallback(torchCallback, null)
        }

        // Apply keep-screen-on if user enabled and torch currently on
        applyKeepScreenOnFlag(isTorchOn)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("torch_state", isTorchOn)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Keep existing top-right menu if needed (Settings/About)
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_about -> {
                startActivity(android.content.Intent(this, AboutActivity::class.java))
                true
            }
            R.id.action_settings -> {
                startActivity(android.content.Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLeftOverflowMenu(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.menu_overflow_left, popup.menu)
        // Replace visible titles from resources to ensure localization
        popup.menu.findItem(R.id.menu_version)?.title = getString(R.string.menu_version)
        popup.menu.findItem(R.id.menu_about_app)?.title = getString(R.string.menu_about_flash_app)
        popup.menu.findItem(R.id.menu_privacy)?.title = getString(R.string.menu_privacy)
        popup.menu.findItem(R.id.menu_privacy_notice)?.title = getString(R.string.menu_privacy_notice)
        popup.menu.findItem(R.id.menu_oss_licenses)?.title = getString(R.string.menu_oss_licenses)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_version -> {
                    showVersionDialog()
                    true
                }
                R.id.menu_about_app -> {
                    showAboutDialog()
                    true
                }
                R.id.menu_privacy -> {
                    // Navigate to existing PrivacyActivity (WebView page)
                    startActivity(android.content.Intent(this, PrivacyActivity::class.java))
                    true
                }
                R.id.menu_privacy_notice -> {
                    // Navigate to placeholder Privacy Notice screen
                    startActivity(android.content.Intent(this, PrivacyNoticeActivity::class.java))
                    true
                }
                R.id.menu_oss_licenses -> {
                    // Navigate to placeholder OSS list screen
                    startActivity(android.content.Intent(this, OssListActivity::class.java))
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    private fun showVersionDialog() {
        val versionName = try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            pInfo.versionName ?: "1.0"
        } catch (e: Exception) {
            "1.0"
        }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_version_title))
            .setMessage(getString(R.string.dialog_version_body, versionName))
            .setPositiveButton(getString(R.string.dialog_ok), null)
            .show()
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_about_title))
            .setMessage(getString(R.string.dialog_about_body))
            .setPositiveButton(getString(R.string.dialog_ok), null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cameraManager.unregisterTorchCallback(torchCallback)
        }
        // Ensure torch off when leaving to be safe
        setTorch(false)
        // Clear keep screen on
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun hasCameraPermission(): Boolean =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED

    private fun requestCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun toggleTorch() {
        setTorch(!isTorchOn)
        val fade = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        iconView.startAnimation(fade)
        statusText.startAnimation(fade)
        toggleButton.startAnimation(fade)
    }

    private fun setTorch(enable: Boolean) {
        val id = torchCameraId ?: return
        try {
            cameraManager.setTorchMode(id, enable)
            isTorchOn = enable
            // Apply/clear keep-screen-on depending on user setting and state
            applyKeepScreenOnFlag(isTorchOn)
            updateVisuals()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.unable_control, e.message ?: ""), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUIState(enabled: Boolean) {
        toggleButton.isEnabled = enabled
        if (!enabled) {
            isTorchOn = false
            statusText.text = getString(R.string.no_flash_msg)
            iconView.setImageResource(R.drawable.ic_flash_off_24)
        }
        updateVisuals()
    }

    private fun applyKeepScreenOnFlag(torchOn: Boolean) {
        val enabledByUser = isKeepScreenOnEnabled()
        if (torchOn && enabledByUser) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    private fun updateVisuals() {
        if (!toggleButton.isEnabled) {
            toggleButton.text = getString(R.string.unavailable)
            return
        }

        if (isTorchOn) {
            iconView.setImageResource(R.drawable.ic_flash_on_24)
            toggleButton.text = getString(R.string.turn_off)
            statusText.text = getString(R.string.flash_on)
            toggleButton.setBackgroundResource(R.drawable.bg_button_ocean)
            toggleButton.isSelected = true
        } else {
            iconView.setImageResource(R.drawable.ic_flash_off_24)
            toggleButton.text = getString(R.string.turn_on)
            statusText.text = getString(R.string.flash_off)
            toggleButton.setBackgroundResource(R.drawable.bg_button_ocean)
            toggleButton.isSelected = false
        }
    }

    private fun findBackCameraWithFlash(manager: CameraManager): String? {
        return try {
            for (id in manager.cameraIdList) {
                val chars = manager.getCameraCharacteristics(id)
                val hasFlash = chars.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                val facing = chars.get(CameraCharacteristics.LENS_FACING)
                if (hasFlash && facing == CameraCharacteristics.LENS_FACING_BACK) return id
            }
            // fallback to any camera with flash
            manager.cameraIdList.firstOrNull { id ->
                manager.getCameraCharacteristics(id).get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
            }
        } catch (e: Exception) { null }
    }
}
