package com.android.sensorlogger.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import com.android.sensorlogger.Utils.Config

class Gyroscope(context: Context, fileName: String) : SensorEventListener, SensorBase(context, fileName) {

    init {
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sampleRateMillis = 500

        x_threshold = Config.Sensor.GYRO_X_THRESHOLD
        y_threshold = Config.Sensor.GYRO_Y_THRESHOLD
        z_threshold = Config.Sensor.GYRO_Z_THRESHOLD
    }
}