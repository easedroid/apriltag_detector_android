package com.apriltagdetection.fragments

import android.content.Context
import android.graphics.Matrix
import android.graphics.Point
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.*
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.apriltagdetection.config.model.ApriltagDetection
import com.apriltagdetection.R
import com.apriltagdetection.callbacks.DrawingCallback
import com.apriltagdetection.callbacks.PreviewCallback
import com.apriltagdetection.views.MarkerOverlay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.ByteBuffer
import java.util.concurrent.Executors
import kotlin.math.roundToInt

abstract class BaseFragment: Fragment() {

    private lateinit var viewFinder: TextureView
    private lateinit var previewView: PreviewView
    private lateinit var markerLayout: MarkerOverlay
    private var rotation = 0
    protected var callback: PreviewCallback? = null
    private var camera: Camera? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preview, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.viewFinder)
        markerLayout = view.findViewById(R.id.marker)
        callback?.setDrawingCallback(drawingCallback)
        setupCameraProvider()
    }

    private fun updateTransform() {
        val matrix = Matrix()

        //Find the center
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        //Get correct rotation
        rotation = when(viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }

        matrix.postRotate(-rotation.toFloat(), centerX, centerY)

        viewFinder.setTransform(matrix)
    }

    val drawingCallback = object : DrawingCallback {
        override fun onMarkerDraw(marker: ApriltagDetection) {

            Log.e("Points", "${marker.p[0].toFloat()} \n " +
                    "${marker.p[1].toFloat()} \n " +
                    "${marker.p[2].toFloat()} \n " +
                    "${marker.p[3].toFloat()} \n " +
                    "${marker.p[4].toFloat()} \n " +
                    "${marker.p[5].toFloat()} \n " +
                    "${marker.p[6].toFloat()} \n " +
                    "${marker.p[7].toFloat()} \n " /*+
                    "${marker.byte.size}"*/
            )

            val p00 = Point(marker.p[0].roundToInt(), marker.p[1].roundToInt())
            val p01 = Point(marker.p[2].roundToInt(), marker.p[3].roundToInt())
            val p11 = Point(marker.p[3].roundToInt(), marker.p[4].roundToInt())
            val p10  = Point(marker.p[5].roundToInt(), marker.p[6].roundToInt())
            val rectF = RectF(
                p00.x.toFloat(), p01.y.toFloat(), p10.x.toFloat(), p11.y.toFloat()
            )
            GlobalScope.launch(Dispatchers.Main) {
                markerLayout?.setMarker(rectF)
                //Log.e("TagsView", "${marker.id}  ${marker.p[0]}  ${marker.p[3]}  ${marker.p[1]}  ${marker.p[0]}")
                onDetection(p00, p01, p11, p10, ByteArray(0))



//                rootLayout.overlay.clear()
//                val box = MarkerOverlay(activity)
//                val lparams = LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
//                )
//                box.layoutParams = lparams
//                box.setMarker(rectF)
//                rootLayout.addView(box)

            }
        }

        override fun clearMarkerView() {
            GlobalScope.launch(Dispatchers.Main) {
                Log.e("ClearView", ">>>>>>>>>>>>>>><<<<<<<<<<<<<<")
                markerLayout?.clearView()
            }
        }
    }

    val imageAnalyzer = ImageAnalysis.Analyzer { proxy ->
        val buffer = proxy.planes[0].buffer
        val data = buffer.toByteArray()
        callback?.onPreview(data, proxy.width, proxy.height)

        proxy.close()
    }

    private fun setupCameraProvider() {
        ProcessCameraProvider.getInstance(activity!!).also { provider ->
            provider.addListener(Runnable {
                bindPreview(provider.get())
            }, ContextCompat.getMainExecutor(activity))
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
        val preview: Preview = Preview.Builder()
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(600, 600))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), imageAnalyzer!!)

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
        camera?.let { camera ->
            preview.setSurfaceProvider(previewView.createSurfaceProvider(camera.cameraInfo))
        }
    }

    abstract fun onDetection(p00: Point, p01: Point, p11: Point, p10: Point, byteArray: ByteArray)

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is PreviewCallback){
            callback = context
        }
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }
}