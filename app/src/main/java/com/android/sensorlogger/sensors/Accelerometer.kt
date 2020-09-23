package com.android.sensorlogger.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Handler
import android.util.Log
import com.android.sensorlogger.App
import com.android.sensorlogger.Utils.Config
import java.text.SimpleDateFormat
import java.util.*

class Accelerometer(context: Context, fileName: String) : SensorEventListener, SensorBase(context, fileName) {
    private val movementHandler = Handler()
    private val movementResetRunnable = Runnable{ App.inMovement = false }

    init {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sampleRateMillis = 500

        x_threshold = Config.ACC_X_THRESHOLD
        y_threshold = Config.ACC_Y_THRESHOLD
        z_threshold = Config.ACC_Z_THRESHOLD
    }

    override fun onSensorChanged(event: SensorEvent?) {
        super.onSensorChanged(event)
        if(thresholdExceeded(event)){
            if (App.inMovement){
                movementHandler.removeCallbacks(movementResetRunnable)
                movementHandler.postDelayed(movementResetRunnable, 30000)
                Log.d("ACC", "Threshold exceeded, reset runnable.")
            }
            else{
                App.inMovement = true
                movementHandler.postDelayed(movementResetRunnable, 30000)
                Log.d("ACC", "Threshold exceeded, started runnable")
            }
        }
        else{
            Log.d("ACC", "Threshold not exceeded.")
        }

    }

    private fun thresholdExceeded(event: SensorEvent?) : Boolean {
        val x = kotlin.math.abs(event!!.values[0])
        val y = kotlin.math.abs(event!!.values[1])
        val z = kotlin.math.abs(event!!.values[2])

        return x > x_threshold || y > y_threshold || z > z_threshold
    }
}