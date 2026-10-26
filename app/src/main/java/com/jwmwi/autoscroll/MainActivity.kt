package com.jwmwi.autoscroll

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    private val intervalMillis = longArrayOf(30_000L, 60_000L, 120_000L, 300_000L)
    private val prefs by lazy { getSharedPreferences("auto_scroll_settings", MODE_PRIVATE) }

    private lateinit var accessibilityStatus: TextView
    private lateinit var overlayStatus: TextView
    private lateinit var intervalSpinner: Spinner
    private lateinit var intervalSummary: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accessibilityStatus = findViewById(R.id.txtAccessibilityStatus)
        overlayStatus = findViewById(R.id.txtOverlayStatus)
        intervalSpinner = findViewById(R.id.spinnerInterval)
        intervalSummary = findViewById(R.id.txtIntervalSummary)

        findViewById<Button>(R.id.btnAccessibility).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<Button>(R.id.btnOverlay).setOnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    android.net.Uri.parse("package:$packageName")
                )
                startActivity(intent)
            } else {
                Toast.makeText(this, "Overlay permission is not required on this Android version.", Toast.LENGTH_SHORT).show()
            }
        }

        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.interval_options,
            android.R.layout.simple_spinner_item
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        intervalSpinner.adapter = adapter

        val savedInterval = prefs.getLong("interval_ms", 60_000L)
        val initialIndex = intervalMillis.indexOf(savedInterval).let { if (it < 0) 1 else it }
        intervalSpinner.setSelection(initialIndex, false)
        updateIntervalSummary(initialIndex)

        intervalSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position in intervalMillis.indices) {
                    prefs.edit().putLong("interval_ms", intervalMillis[position]).apply()
                    updateIntervalSummary(position)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) = Unit
        }
    }

    override fun onResume() {
        super.onResume()
        refreshPermissionStatuses()
    }

    private fun refreshPermissionStatuses() {
        val enabled = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ).orEmpty()

        val component = ComponentName(this, AutoScrollAccessibilityService::class.java)
            .flattenToString()

        val accessibilityEnabled = enabled.split(':').any {
            it.equals(component, ignoreCase = true)
        }

        accessibilityStatus.text = if (accessibilityEnabled) {
            getString(R.string.status_enabled)
        } else {
            getString(R.string.status_not_enabled)
        }

        val overlayEnabled = android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M ||
            Settings.canDrawOverlays(this)

        overlayStatus.text = if (overlayEnabled) {
            getString(R.string.status_enabled)
        } else {
            getString(R.string.status_not_enabled)
        }
    }

    private fun updateIntervalSummary(index: Int) {
        val option = resources.getStringArray(R.array.interval_options).getOrElse(index) {
            "1 minute"
        }
        intervalSummary.text = getString(R.string.interval_saved, option)
    }
}
