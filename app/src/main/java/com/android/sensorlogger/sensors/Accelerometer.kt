package com.android.sensorlogger.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.util.Log


class Accelerometer(context: Context) : SensorEventListener {
    //Sensor variables
    var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    var accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)

    //Periodic handlers
    var sampleRateMillis : Long = 500
    var timingHandler = Handler()
    val periodicRegisterer: Runnable = object : Runnable {
        override fun run() {
            sensorManager.registerListener(this@Accelerometer, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    init {
        periodicRegisterer.run()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Nothing to do yet.
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!!.sensor.getType() != Sensor.TYPE_LINEAR_ACCELERATION) {
            return;
        }
        var x = event!!.values[0]
        var y = event!!.values[1]
        var z = event!!.values[2]
        //Log.d("ACC", "$x $y $z")
        sensorManager.unregisterListener(this)

        //Make measurement after sampleRateMillis
        timingHandler.postDelayed(periodicRegisterer, sampleRateMillis)
    }

    fun stop(){
        sensorManager.unregisterListener(this)
    }

}