package com.sys.sync

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.LinearLayout
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        val ll = LinearLayout(this); ll.orientation = LinearLayout.VERTICAL
        fun btn(label: String, action: () -> Unit) {
            val t = TextView(this); t.text = label; t.textSize = 18f; t.setPadding(40,40,40,40)
            t.setOnClickListener { action() }; ll.addView(t)
        }
        btn("1. Accessibility") { startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) }
        btn("2. Notification Access") { startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) }
        btn("3. Usage Access") { startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
        btn("4. Battery exemption") {
            val i = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS)
            i.data = Uri.parse("package:$packageName"); startActivity(i)
        }
        btn("5. Runtime perms") { CoreService.requestRuntime(this) }
        btn("6. Start service") { CoreService.start(this) }
        setContentView(ll); CoreService.start(this)
    }
}
