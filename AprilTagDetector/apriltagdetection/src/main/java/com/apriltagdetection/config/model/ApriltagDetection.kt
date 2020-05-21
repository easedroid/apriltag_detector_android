package com.apriltagdetection.config.model

import android.util.Size

class ApriltagDetection {
    // The decoded ID of the tag
    var id: Int = 0

    // How many error bits were corrected? Note: accepting large numbers of
    // corrected errors leads to greatly increased false positive rates.
    // NOTE: As of this implementation, the detector cannot detect tags with
    // a hamming distance greater than 2.
    var hamming: Int = 0

    // The center of the detection in image pixel coordinates.
    var c = DoubleArray(2)

    // The corners of the tag in image pixel coordinates. These always
    // wrap counter-clock wise around the tag.
    // Flattened to [x0 y0 x1 y1 ...] for JNI convenience
    var p = DoubleArray(8)


    var r = DoubleArray(9)

    //var byte: ByteArray = ByteArray(640*640)
}