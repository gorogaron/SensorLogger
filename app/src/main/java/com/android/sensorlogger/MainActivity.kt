package com.android.sensorlogger

import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.sensorlogger.sensors.Accelerometer
import com.android.sensorlogger.sensors.SensorService
import kotlinx.android.synthetic.main.activity_main.*
import com.android.sensorlogger.App
import com.android.sensorlogger.camera.CameraPermissionHelper

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        

        if (isMeasurementRunning()) {
            animationView.visibility = View.VISIBLE
            startStopButton.text = "STOP"
        } else {
            animationView.visibility = View.GONE
        }

        startStopButton.setOnClickListener {if (!isMeasurementRunning()) startMeasurement() else stopMeasurement()}

        val permissions = arrayListOf<String>()
        if (!CameraPermissionHelper.hasCameraPermission(this)) {
            permissions.add(CAMERA)
        }
        if(!CameraPermissionHelper.hasMicPermission(this)){
            permissions.add(RECORD_AUDIO)
        }

        if (permissions.isNotEmpty()){
            CameraPermissionHelper.requestPermission(this, permissions)
        }
    }

    private fun startMeasurement(){
        startStopButton.text = "STOP"
        animationView.visibility = View.VISIBLE
        var serviceIntent = Intent(this, SensorService::class.java)
        startService(serviceIntent)
    }

    private fun stopMeasurement(){
        startStopButton.text = "START"
        animationView.visibility = View.GONE

        var serviceIntent = Intent(this, SensorService::class.java)
        stopService(serviceIntent)
    }

    private fun isMeasurementRunning(): Boolean {
        val manager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (SensorService::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (!CameraPermissionHelper.hasCameraPermission(this) or !CameraPermissionHelper.hasMicPermission(this)) {
            Toast.makeText(this, "Camera permission is needed to run this application", Toast.LENGTH_LONG)
                .show()
            if (!CameraPermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                CameraPermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
        recreate()
    }
}