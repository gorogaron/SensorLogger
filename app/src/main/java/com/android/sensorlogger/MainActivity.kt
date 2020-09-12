package com.android.sensorlogger

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.sensorlogger.sensors.Accelerometer
import com.android.sensorlogger.sensors.SensorService
import kotlinx.android.synthetic.main.activity_main.*
import com.android.sensorlogger.App

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
}