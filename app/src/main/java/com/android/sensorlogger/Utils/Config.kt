package com.android.sensorlogger.Utils

import android.media.MediaRecorder

object Config {

    object Sensor {
        var ACC_X_THRESHOLD = 0.5
        var ACC_Y_THRESHOLD = 0.5
        var ACC_Z_THRESHOLD = 0.5

        var GYRO_X_THRESHOLD = 0.1
        var GYRO_Y_THRESHOLD = 0.1
        var GYRO_Z_THRESHOLD = 0.1

        var MAG_X_THRESHOLD = 0.1
        var MAG_Y_THRESHOLD = 0.1
        var MAG_Z_THRESHOLD = 0.1
    }

    object Camera {
        var FPS = 24
        var VIDEO_ENCODER = MediaRecorder.VideoEncoder.H264
        var AUDIO_ENCODER = MediaRecorder.AudioEncoder.AAC
        var WIDTH = 1280
        var HEIGHT = 720
        val RECORDER_VIDEO_BITRATE: Int = 10_000_000
    }
}