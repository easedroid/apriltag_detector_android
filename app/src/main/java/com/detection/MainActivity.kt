package com.detection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import com.apriltagdetection.callbacks.DrawingCallback
import com.apriltagdetection.callbacks.PreviewCallback
import com.apriltagdetection.config.ApriltagConfig
import com.apriltagdetection.config.model.ApriltagDetection
import com.apriltagdetection.fragments.TestingFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), PreviewCallback {
    var aprilConfig: ApriltagConfig? = null
    var drawCallback: DrawingCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val builder = ApriltagConfig.Builder()
            .setSigma(1.0)
            .setDecimation(2.0)
            .setErrorBits(2)
            .setTagFamilly("tagCustom48h12")
            .setCallbacks(object : ApriltagConfig.DetectionCallback{
                override fun onDetect(item: ApriltagDetection) {
                    if (drawCallback!=null){
                        drawCallback?.onMarkerDraw(item)
                    }
                    /*Handler(Looper.getMainLooper()).post(Runnable {
                        Log.e("Detect"," Test>>>>>>>>>>>>>>>>>>>>>>>")
                    })*/
                }

                override fun clearMarkerView() {
                    if (drawCallback!=null){
                        drawCallback?.clearMarkerView()
                    }
                    /*Handler(Looper.getMainLooper()).post(Runnable {
                        Log.e("DetectFailed"," Test>>>>>>>>>>>>>>>>>>>>>>>Failed")
                    })*/
                }
            })
            .build()

        aprilConfig = builder
        //builder.initProcess()

        GlobalScope.launch(Dispatchers.IO) {
            async { configApriltag(builder) }
        }


        /*loadFragment(PreviewFragment.newInstance(object : PreviewCallback {
            override fun onPreview(byteArray: ByteArray, width: Int, height: Int) {
                //Log.e("Callback", ">>>>>>>>>>>>>>>>>> $width  $height  ${byteArray.size}")
                aprilConfig?.asyncDetection(byteArray, width, height)
            }

            override fun setDrawingCallback(callback: PreviewCallback.DrawingCallback) {
                drawingCallback = callback
            }
        }))*/

        loadFragment(TestingFragment())

        /*loadFragment(CameraFragment.newInstance(object :CameraFragment.CameraCallback{
            override fun onReady() {
                Log.e("Ready", ">>>>>>>>")
            }

            override fun onPreview(byteArray: ByteArray, width: Int, height: Int) {
                Log.e("Preview", ">>>>>>>>")
                val list = aprilConfig?.asyncDetection(byteArray, width, height)
            }
        }))*/
    }


    suspend fun configApriltag(builder: ApriltagConfig): Boolean{
        return try {
            builder.asyncConfig()
            true
        }catch (e: Exception){
            e.printStackTrace()
            false
        }
    }


    fun loadFragment(fragment: Fragment){
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.container, fragment)
        ft.commit()
    }

    override fun onPreview(byteArray: ByteArray, width: Int, height: Int) {
        Log.e("Callback", ">>>>>>>>>>>>>>>>>> $width  $height  ${byteArray.size}")
        aprilConfig?.asyncDetection(byteArray, width, height)
    }

    override fun setDrawingCallback(callback: DrawingCallback) {
        Log.e("SetCallback",">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
        drawCallback = callback
    }


}
