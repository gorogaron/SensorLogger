package com.android.sensorlogger.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import com.android.sensorlogger.Utils.Config

class Magnetometer(context: Context, fileName: String) : SensorEventListener, SensorBase(context, fileName) {

    init {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sampleRateMillis = 500

        x_threshold = Config.MAG_X_THRESHOLD
        y_threshold = Config.MAG_Y_THRESHOLD
        z_threshold = Config.MAG_Z_THRESHOLD
    }
}