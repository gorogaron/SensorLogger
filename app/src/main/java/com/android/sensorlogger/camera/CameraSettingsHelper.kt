package com.android.sensorlogger.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.RecommendedStreamConfigurationMap.USECASE_RECORD
import android.hardware.camera2.params.StreamConfigurationMap
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.core.app.ActivityCompat
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class CameraSettingsHelper(var mContext: Context) {

    private val cameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private lateinit var camera: CameraDevice

    fun  getCameraIDs() : Array<String> {
        return cameraManager.cameraIdList
    }

    fun getPossibleVideoSizes(cameraId : String): Array<out Size>? {
            val char = cameraManager.getCameraCharacteristics(cameraId)
            val map = char.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            return map!!.getOutputSizes(SurfaceTexture::class.java)
    }
}