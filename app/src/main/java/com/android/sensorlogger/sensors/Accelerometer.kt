package com.android.sensorlogger.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.Handler
import android.util.Log
import com.android.sensorlogger.App
import com.android.sensorlogger.Utils.Config
import java.lang.RuntimeException

class Accelerometer(context: Context, fileName: String) : SensorEventListener, SensorBase(context, fileName) {
    private val movementDelay : Long = 30000
    private val movementHandler = Handler()
    private val movementResetRunnable = Runnable{
        App.inMovement = false
        Log.d("ACC", "Not moved for ${movementDelay/1000} seconds. Resetting movement state.")
    }

    private var prevX : Float? = null
    private var prevY : Float? = null
    private var prevZ : Float? = null

    init {
        this.sensor = try {
            sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        } catch (e: RuntimeException) {
            Log.e("ACC", "No TYPE_LINEAR_ACCELERATION available, falling back to TYPE_ACCELEROMETER")
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        }

        sampleRateMillis = 500

        thresholdX = Config.Sensor.ACC_X_THRESHOLD
        thresholdY = Config.Sensor.ACC_Y_THRESHOLD
        thresholdZ = Config.Sensor.ACC_Z_THRESHOLD
    }

    override fun onSensorChanged(event: SensorEvent?) {
        super.onSensorChanged(event)
        if (prevX == null){
            prevX = event!!.values[0]
            prevY = event.values[1]
            prevZ = event.values[2]
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
        prevX = event!!.values[0]
        prevY = event.values[1]
        prevZ = event.values[2]

    }

    private fun thresholdExceeded(event: SensorEvent?) : Boolean {
        val x = event!!.values[0]
        val y = event.values[1]
        val z = event.values[2]

        return kotlin.math.abs(prevX!! - x) > Config.Sensor.ACC_DX_THRESHOLD ||
               kotlin.math.abs(prevY!! - y) > Config.Sensor.ACC_DY_THRESHOLD ||
               kotlin.math.abs(prevZ!! - z) > Config.Sensor.ACC_DZ_THRESHOLD
    }
}