package com.android.sensorlogger.sensors
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler

open class SensorBase(context: Context) : SensorEventListener  {
    //Sensor variables
    var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    lateinit var sensor : Sensor

    //Periodic handlers
    var sampleRateMillis : Long = 500   //Default sample rate, set it in implementation classes!
    var timingHandler = Handler()
    val periodicRegisterer: Runnable = object : Runnable {
        override fun run() {
            sensorManager.registerListener(this@SensorBase, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Nothing to do yet.
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //Do in implementation classes
    }

    fun run(){
        periodicRegisterer.run()
    }

    //Important! Call this on main activity destroy.
    fun stop(){
        sensorManager.unregisterListener(this)
        timingHandler.removeCallbacks(periodicRegisterer)
    }
}