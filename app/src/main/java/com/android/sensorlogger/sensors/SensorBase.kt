package com.android.sensorlogger.sensors
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Environment
import android.os.Handler
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.Runnable
import java.util.*


open class SensorBase(context: Context, filename_tag:String) : SensorEventListener  {
    //Sensor variables
    var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    lateinit var sensor : Sensor

    val measurementChannel = Channel<SensorEvent>(100)

    //Periodic handler for sensors
    var sampleRateMillis : Long = 500   //Default sample rate, set it in implementation classes!
    var timingHandler = Handler()
    val periodicRegisterer = Runnable { sensorManager.registerListener(this@SensorBase, sensor, SensorManager.SENSOR_DELAY_NORMAL) }

    //Periodic handler for logging
    val fileWriter = Runnable { writeToFile(context) }
    var fileName = filename_tag

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Nothing to do yet.
    }

    override fun onSensorChanged(event: SensorEvent?) {
        runBlocking {
            measurementChannel.send(event!!)
        }
        //Implement further part in children
    }

    fun writeToFile(context: Context) {
        try {
            GlobalScope.launch(Dispatchers.IO){

                val appDirectory = File(context.getExternalFilesDir(null).toString() + "/SensorLogger")
                val logDirectory = File(appDirectory.toString() + "/logs")
                val logFile = File(logDirectory, fileName)
                if (!appDirectory.exists()) {
                    appDirectory.mkdir()
                }
                if (!logDirectory.exists()) {
                    logDirectory.mkdir()
                }
                if (!logFile.exists()) {
                    logFile.createNewFile()
                }

                val outputStreamWriter = OutputStreamWriter(FileOutputStream(logFile, true))

                for (event in measurementChannel) {
                    //Loop breaks when measurementChannel.close() is called
                    val line = "${event.values[0]};${event.values[1]};${event.values[2]}\n"
                    outputStreamWriter.write(line)
                }
                outputStreamWriter.close()
            }
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: " + e.toString())
        }
    }

    fun run(){
        val calendar = Calendar.getInstance()

        //e.g. ACC_2020_09_14_13_23_22.csv
        fileName = fileName +
                "_${calendar.get(Calendar.YEAR)}_" +
                "${calendar.get(Calendar.MONTH)}_" +
                "${calendar.get(Calendar.DAY_OF_MONTH)}_" +
                "${calendar.get(Calendar.HOUR_OF_DAY)}_" +
                "${calendar.get(Calendar.MINUTE)}_" +
                "${calendar.get(Calendar.SECOND)}.txt"

        periodicRegisterer.run()
        fileWriter.run()
    }

    //Important! Call this on main activity (or parent service) destroy.
    fun stop(){
        sensorManager.unregisterListener(this)
        timingHandler.removeCallbacks(periodicRegisterer)
        measurementChannel.close()
    }
}