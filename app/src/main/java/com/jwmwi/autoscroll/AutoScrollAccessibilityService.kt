package com.jwmwi.autoscroll

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

/**
 * Starter AccessibilityService.
 *
 * The service is declared with gesture capability so the timed swipe engine can be added
 * here next. Android requires the user to enable the service manually in system settings.
 */
class AutoScrollAccessibilityService : AccessibilityService() {

    override fun onServiceConnected() {
        super.onServiceConnected()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Intentionally empty in the project scaffold. The swipe engine is not wired yet.
    }

    override fun onInterrupt() {
        // No ongoing work to interrupt yet.
    }
}
