package com.android.sensorlogger.camera

import android.Manifest
import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService

class Camera(context: Context) {

    var mContext = context
    lateinit var mCamera: CameraDevice

    private fun startCameraSession() {

        val cameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        if (cameraManager.cameraIdList.isEmpty()) {
            Toast.makeText(mContext, "No cameras were found on the device.", Toast.LENGTH_SHORT).show()
            return
        }
        val firstCamera = "0"
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Permission has not been granted.", Toast.LENGTH_SHORT).show()
            return
        }
        cameraManager.openCamera(firstCamera, object: CameraDevice.StateCallback() {
            override fun onDisconnected(p0: CameraDevice) { }
            override fun onError(p0: CameraDevice, p1: Int) { }

            override fun onOpened(cameraDevice: CameraDevice) {
                //TODO
                val cameraCharacteristics =    cameraManager.getCameraCharacteristics(cameraDevice.id)

            }
        }, Handler { true })
    }
}