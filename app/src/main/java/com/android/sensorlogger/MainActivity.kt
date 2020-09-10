package com.android.sensorlogger

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.sensorlogger.sensors.Accelerometer

class MainActivity : AppCompatActivity() {

    lateinit var accelerometer : Accelerometer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        accelerometer = Accelerometer(this)
    }

    override fun onPause() {
        super.onPause()
        accelerometer.stop()
    }
}