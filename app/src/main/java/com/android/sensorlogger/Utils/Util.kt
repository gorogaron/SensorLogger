package com.android.sensorlogger.Utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.UnknownHostException

object Util {

    suspend fun isOnline(): Boolean {
        try {
            lateinit var addresses: Array<out InetAddress>
            GlobalScope.launch(Dispatchers.IO){
                addresses = InetAddress.getAllByName("www.google.com")
            }.join()
            return !addresses[0].hostAddress.equals("")
        } catch (e: UnknownHostException) {
            // Log error
        }
        return false
    }

    fun isSensorAvailable(sensor : Int, context: Context) : Boolean{
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        return sensorManager.getDefaultSensor(sensor) != null
    }
}

val Any.TAG: String
    get() {
        return if (!javaClass.isAnonymousClass) {
            val name = javaClass.simpleName
            if (name.length <= 23) name else name.substring(0, 23)// first 23 chars
        } else {
            val name = javaClass.name
            if (name.length <= 23) name else name.substring(name.length - 23, name.length)// last 23 chars
        }
    }
