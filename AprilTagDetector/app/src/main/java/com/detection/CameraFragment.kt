package com.detection

import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.otaliastudios.cameraview.frame.Frame
import kotlinx.android.synthetic.main.fragment_camera.*
import kotlinx.coroutines.delay
import java.io.ByteArrayOutputStream


class CameraFragment : Fragment() {
    private var callback: CameraCallback? = null


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
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cameraViewCamera.setLifecycleOwner(viewLifecycleOwner)
        cameraViewCamera.addFrameProcessor { frame ->

            val bitmap = getVisionImageFromFrame(frame).bitmap
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val image = stream.toByteArray()
            callback?.onPreview(image, frame.size.width,frame.size.height)
        }


        callback?.onReady()
    }

    private fun getVisionImageFromFrame(frame : Frame) : FirebaseVisionImage {
        //ByteArray for the captured frame
        val data = frame.getData<ByteArray>()

        //Metadata that gives more information on the image that is to be converted to FirebaseVisionImage
        val imageMetaData = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setRotation(frame.rotation)
            .setHeight(frame.size.height)
            .setWidth(frame.size.width)
            .build()

        val image = FirebaseVisionImage.fromByteArray(data, imageMetaData)

        return image
    }

    interface CameraCallback{
        fun onReady()
        fun onPreview(byteArray: ByteArray, width: Int, height: Int)
    }


    companion object {
        @JvmStatic
        fun newInstance(listners: CameraCallback) =
            CameraFragment().apply {
                arguments = Bundle().apply {

                }
                callback = listners
            }
    }
}
