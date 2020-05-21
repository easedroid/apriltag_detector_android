package com.apriltagdetection.fragments


import android.graphics.Point
import android.os.Bundle
import android.util.Log
import com.apriltagdetection.callbacks.PreviewCallback


class PreviewFragment : BaseFragment() {


    companion object {
        @JvmStatic
        fun newInstance(listners: PreviewCallback) =
            PreviewFragment().apply {
                arguments = Bundle().apply {
                    // add or pass argument to do other stuff as required
                }
                this.callback = listners
            }
    }

    override fun onDetection(p00: Point, p01: Point, p11: Point, p10: Point, byteArray: ByteArray) {
        Log.e("TagsView", "${p00.x}  ${p01.x}  ${p10.x}  ${p11.x}  ${byteArray.size}")
    }
}


