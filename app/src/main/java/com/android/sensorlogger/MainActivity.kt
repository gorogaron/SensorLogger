package com.android.sensorlogger

import android.animation.Animator
import android.animation.ValueAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.sensorlogger.sensors.Accelerometer
import com.android.sensorlogger.sensors.Gyroscope
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var measurementRunning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startStopButton.setOnClickListener {if (measurementRunning) startMeasurement() else stopMeasurement()}
    }

    private fun startMeasurement(){
        startStopButton.text = "STOP"
        animationView.progress = 0.65f
        animationView.pauseAnimation()
    }

    private fun stopMeasurement(){
        startStopButton.text = "START"
        animationView.removeAllAnimatorListeners()
        animationView.playAnimation()
    }

    override fun onPause() {
        super.onPause()
    }
}