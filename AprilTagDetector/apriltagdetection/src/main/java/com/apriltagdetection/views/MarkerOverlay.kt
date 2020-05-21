package com.apriltagdetection.views

import android.R
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View


class MarkerOverlay(context: Context?, attr:AttributeSet?) : View(context, attr) {
    private var markerRect: RectF? = null
    private var isDetected = false


    fun setMarker(rect: RectF) {
        this.markerRect = rect
        //Redraw after defining circle
        isDetected = true

        postInvalidate()
    }

    fun clearView(){
        if (isDetected) {
            isDetected = false
            postInvalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        try {
            if (markerRect!=null){
                val paint = Paint(Paint.ANTI_ALIAS_FLAG)

                if (!isDetected) {
                    paint.color = resources.getColor(R.color.black)
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 2f
                    canvas?.drawPaint(paint)

                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                }else {
                    paint.color = resources.getColor(R.color.holo_green_light)
                    paint.style = Paint.Style.STROKE
                    paint.strokeWidth = 15f
                    canvas?.drawPaint(paint)

                    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
                }
                canvas?.drawRect(markerRect!!, paint)
                Log.e("IsDrawing",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}