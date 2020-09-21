package com.android.sensorlogger

import android.Manifest.permission.*
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import com.android.sensorlogger.Utils.PermissionHelper

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
        if (!PermissionHelper.hasCameraPermission(this)) {
            permissions.add(CAMERA)
        }
        if(!PermissionHelper.hasMicPermission(this)){
            permissions.add(RECORD_AUDIO)
        }
        if(!PermissionHelper.hasGpsPermission(this)){
            permissions.add(ACCESS_FINE_LOCATION)
            permissions.add(ACCESS_COARSE_LOCATION)
        }

        if (permissions.isNotEmpty()){
            PermissionHelper.requestPermission(this, permissions)
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
        if (!PermissionHelper.hasAllPermissions(this)) {
            Toast.makeText(this, "All permissions are needed to run this application", Toast.LENGTH_LONG)
                .show()
            if (!PermissionHelper.shouldShowRequestPermissionRationale(this)) {
                // Permission denied with checking "Do not ask again".
                PermissionHelper.launchPermissionSettings(this)
            }
            finish()
        }
        recreate()
    }
}