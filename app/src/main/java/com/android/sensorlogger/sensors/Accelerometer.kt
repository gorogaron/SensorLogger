package com.android.sensorlogger.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.util.Log
import com.android.sensorlogger.App
import com.android.sensorlogger.Utils.Config
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*

class Accelerometer(context: Context, fileName: String) : SensorEventListener, SensorBase(context, fileName) {
    private val movementDelay : Long = 30000
    private val movementHandler = Handler()
    private val movementResetRunnable = Runnable{
        App.inMovement = false
        Log.d("ACC", "Not moved for ${movementDelay/1000} seconds. Resetting movement state.")
    }

    var prev_x : Float? = null
    var prev_y : Float? = null
    var prev_z : Float? = null

    init {

        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) == null){
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }
        else{
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        }

        sampleRateMillis = 500

        x_threshold = Config.Sensor.ACC_X_THRESHOLD
        y_threshold = Config.Sensor.ACC_Y_THRESHOLD
        z_threshold = Config.Sensor.ACC_Z_THRESHOLD
    }

    override fun onSensorChanged(event: SensorEvent?) {
        super.onSensorChanged(event)
        if (prev_x == null){
            prev_x = event!!.values[0]
            prev_y = event!!.values[1]
            prev_z = event!!.values[2]
        }
        if(thresholdExceeded(event)){
            if (App.inMovement){
                movementHandler.removeCallbacks(movementResetRunnable)
                movementHandler.postDelayed(movementResetRunnable, movementDelay)
                Log.d("ACC", "Threshold exceeded, reset runnable.")
            }
            else{
                App.inMovement = true
                movementHandler.postDelayed(movementResetRunnable, movementDelay)
                Log.d("ACC", "Threshold exceeded, started runnable")
            }
        }
        prev_x = event!!.values[0]
        prev_y = event!!.values[1]
        prev_z = event!!.values[2]

    }

    private fun thresholdExceeded(event: SensorEvent?) : Boolean {
        val x = kotlin.math.abs(event!!.values[0])
        val y = kotlin.math.abs(event!!.values[1])
        val z = kotlin.math.abs(event!!.values[2])

        return kotlin.math.abs(prev_x!! - x) > Config.Sensor.ACC_DX_THRESHOLD ||
               kotlin.math.abs(prev_y!! - y) > Config.Sensor.ACC_DY_THRESHOLD ||
               kotlin.math.abs(prev_z!! - z) > Config.Sensor.ACC_DZ_THRESHOLD
    }
}