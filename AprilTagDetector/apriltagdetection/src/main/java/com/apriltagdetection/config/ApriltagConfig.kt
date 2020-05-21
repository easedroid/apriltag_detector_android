package com.apriltagdetection.config

import android.util.Log
import com.apriltagdetection.config.model.ApriltagDetection
import kotlinx.coroutines.*
import java.util.ArrayList
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

data class ApriltagConfig private constructor(
    var deciMation: Double?,
    var sigMa: Double?,
    var errorBits: Int?,
    var family: String?,
    var markerCallback: DetectionCallback?){


    var nThread: Int = 8
    var callback: DetectionCallback? = null


    init {
        System.loadLibrary("apriltag")
        asyncInit()
    }


    data class Builder(var tagFamily: String? = null, var sigma:Double? =null, var decimation:Double?=null, var error: Int?=null, var thread:Int?=null){
        private var deciMation: Double = 0.0
        private var sigMa: Double = 0.0
        private var errorBits: Int = 0
        private var family: String = "tagCustom48h12"
        var callback: DetectionCallback? = null

        fun setDecimation(decimation: Double) = apply {
            deciMation = decimation
        }

        fun setSigma(sig: Double) = apply {
            sigMa = sig
        }

        fun setErrorBits(error: Int) = apply {
            errorBits = error
        }

        fun setTagFamilly(family: String) = apply {
            this.family = family
        }

        fun setCallbacks(listners: DetectionCallback) = apply {
            callback = listners
        }

        fun build() = ApriltagConfig(
            deciMation,
            sigMa,
            errorBits,
            family,
            callback
        )
    }


    fun asyncInit() = runBlocking {
        val customDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
        launch(customDispatcher){
            initProcess()
        }
        (customDispatcher.executor as ExecutorService).shutdown()
    }

    fun asyncConfig() = runBlocking {
        apriltagInitialize(family!!, errorBits!!, deciMation!!, sigMa!!, nThread)
    }

    fun asyncDetection(byte: ByteArray, width: Int, height: Int) = runBlocking {
        val customDispatcher = Executors.newFixedThreadPool(4).asCoroutineDispatcher()
        launch(customDispatcher){
            //Log.e(">>>>>>>>>", "$byte  $width  $height ")
            val list = startDetection(byte, width, height)
            if (list.size>0) {
                for (item in list) {
                    //Log.e("Tags", "${item.id.toString()}")
                    //Log.e("Tags", "${item.id.toString()}  ${item.c[0]}  ${item.c[1]}  ${item.p[0]}  ${item.p[0]}")
                    markerCallback?.onDetect(item)
                }
            }else {
                markerCallback?.clearMarkerView()
            }
        }
        (customDispatcher.executor as ExecutorService).shutdown()
    }




    private external fun initProcess()
    private external fun apriltagInitialize(family: String, error: Int, decimation: Double, sigma: Double, nThread: Int)
    private external fun startDetection(byte: ByteArray, width: Int, height: Int): ArrayList<ApriltagDetection>


    interface DetectionCallback{
        fun onDetect(item: ApriltagDetection)
        fun clearMarkerView()
    }


    /*companion object{
        init {
            System.loadLibrary("apriltag")
            //initProcess()
        }
    }*/

}