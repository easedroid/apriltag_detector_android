package com.apriltagdetection.callbacks

import com.apriltagdetection.config.model.ApriltagDetection

interface PreviewCallback {
    fun onPreview(byteArray: ByteArray, width: Int, height: Int)
    //fun onPreview(byteArray: ByteArray, width: Int, height: Int, callback: DrawingCallback)
    fun setDrawingCallback(callback: DrawingCallback)


}