package com.android.sensorlogger.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log

class Gyroscope(context: Context, fileName: String) : SensorEventListener, SensorBase(context, fileName) {

    init {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sampleRateMillis = 500
    }

    override fun onSensorChanged(event: SensorEvent?) {
        var x = event!!.values[0]
        var y = event!!.values[1]
        var z = event!!.values[2]
        //Log.d("GYRO", "$x $y $z")
        sensorManager.unregisterListener(this)

        //Make measurement after sampleRateMillis
        timingHandler.postDelayed(periodicRegisterer, sampleRateMillis)
    }

}