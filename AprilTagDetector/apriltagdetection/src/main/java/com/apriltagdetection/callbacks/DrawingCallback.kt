package com.apriltagdetection.callbacks

import com.apriltagdetection.config.model.ApriltagDetection

interface DrawingCallback {
    fun onMarkerDraw(marker: ApriltagDetection)
    fun clearMarkerView()
}