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
import java.text.SimpleDateFormat
import java.util.*


open class SensorBase(context: Context, filename_tag:String) : SensorEventListener  {
    //Sensor variables
    var sensorManager: SensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    lateinit var sensor : Sensor

    //Periodic handler for sensors
    var sampleRateMillis : Long = 500   //Default sample rate, set it in implementation classes!
    var timingHandler = Handler()

    private val fakeListener = FakeListener()
    private val periodicRegisterer = Runnable { sensorManager.registerListener(this@SensorBase, sensor, SensorManager.SENSOR_DELAY_NORMAL) }

    //Periodic handler for logging
    private val fileWriter = Runnable { writeToFile(context) }
    private var fileName = filename_tag
    private val fileSavingRate = 10000  //Period time of file saving in milliseconds
    private val measurementChannel = Channel<SensorEvent>(100)

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        //Nothing to do yet.
    }

    override fun onSensorChanged(event: SensorEvent?) {
        runBlocking {
            measurementChannel.send(event!!)
        }
        sensorManager.unregisterListener(this)

        //Make measurement after sampleRateMillis
        timingHandler.postDelayed(periodicRegisterer, sampleRateMillis)
    }

    private fun writeToFile(context: Context) {
        try {
            GlobalScope.launch(Dispatchers.IO){

                val appDirectory = File(context.getExternalFilesDir(null).toString() + "/SensorLogger")
                val logDirectory = File("$appDirectory/logs")
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

                var outputStreamWriter = OutputStreamWriter(FileOutputStream(logFile, true))
                var iterationCounter = 0

                for (event in measurementChannel) {
                    //Loop breaks when measurementChannel.close() is called
                    val line = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US).format(Date()) +
                               ":${event.values[0]};${event.values[1]};${event.values[2]}\n"
                    Log.d("Sensor", line)
                    outputStreamWriter.write(line)

                    iterationCounter += 1
                    if (iterationCounter > (fileSavingRate/sampleRateMillis).toInt()){
                        Log.d("Sensor", "File saved.")
                        iterationCounter = 0
                        outputStreamWriter.close() //Save file
                        outputStreamWriter = OutputStreamWriter(FileOutputStream(logFile, true))
                    }
                }
                outputStreamWriter.close()
            }
        } catch (e: IOException) {
            Log.e("Exception", "File write failed: $e")
        }
    }

    fun run(){
        //e.g. ACC_2020_09_14_13_23_22.csv
        fileName = fileName + "_" + SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(Date()) + ".txt"

        //FakeListener is needed to keep virtual sensors awake. This is a workaround to
        //maintain the desired sampling rate.
        sensorManager.registerListener(fakeListener, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        periodicRegisterer.run()
        fileWriter.run()
    }

    //Important! Call this on main activity (or parent service) destroy.
    fun stop(){
        sensorManager.unregisterListener(fakeListener)
        sensorManager.unregisterListener(this)
        timingHandler.removeCallbacks(periodicRegisterer)
        measurementChannel.close()
    }
}