package com.apriltagdetection.fragments

import android.graphics.Point
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.apriltagdetection.R

/**
 * A simple [Fragment] subclass.
 */
class TestingFragment : BaseFragment() {

    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_testing, container, false)
    }*/

    override fun onDetection(p00: Point, p01: Point, p11: Point, p10: Point, byteArray: ByteArray) {

    }

}
