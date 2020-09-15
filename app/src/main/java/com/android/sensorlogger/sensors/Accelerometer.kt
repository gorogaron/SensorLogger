package com.android.sensorlogger.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log

class Accelerometer(context: Context, fileName: String) : SensorEventListener, SensorBase(context, fileName) {

    init {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        sampleRateMillis = 500
    }

    override fun onSensorChanged(event: SensorEvent?) {
        super.onSensorChanged(event)
        var x = event!!.values[0]
        var y = event!!.values[1]
        var z = event!!.values[2]
        Log.d("ACC", "$x $y $z")
        sensorManager.unregisterListener(this)

        //Make measurement after sampleRateMillis
        timingHandler.postDelayed(periodicRegisterer, sampleRateMillis)
    }

}