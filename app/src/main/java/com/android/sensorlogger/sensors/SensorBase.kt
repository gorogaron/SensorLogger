package com.android.sensorlogger.sensors
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.lang.Runnable

open class SensorBase(context: Context) : SensorEventListener  {
    //Sensor variables
    var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    lateinit var sensor : Sensor

    private val measurementChannel = Channel<SensorEvent>(100)

    //Periodic handlers
    var sampleRateMillis : Long = 500   //Default sample rate, set it in implementation classes!
    var timingHandler = Handler()
    val periodicRegisterer = Runnable { sensorManager.registerListener(this@SensorBase, sensor, SensorManager.SENSOR_DELAY_NORMAL) }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Nothing to do yet.
    }

    override fun onSensorChanged(event: SensorEvent?) {
        runBlocking {
            measurementChannel.send(event!!)
        }
        //Implement further part in children
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