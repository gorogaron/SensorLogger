package com.android.sensorlogger

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.agrolytics.agrolytics_android.utils.Gps
import com.android.sensorlogger.Utils.Util
import com.android.sensorlogger.camera.Camera
import com.android.sensorlogger.sensors.Accelerometer
import com.android.sensorlogger.sensors.Gyroscope
import com.android.sensorlogger.sensors.Magnetometer
import com.android.sensorlogger.wifi.Wifi


class SensorService : Service(){


    lateinit var accelerometer : Accelerometer
    lateinit var gyroscope : Gyroscope
    lateinit var magnetometer: Magnetometer
    lateinit var camera : Camera
    lateinit var gps : Gps
    lateinit var wifi : Wifi

    override fun onCreate() {
        //Will be called only the first time the service is created. We can stop and start it,
        //onCreate will be called only once.
        super.onCreate()

        if (Util.isSensorAvailable(Sensor.TYPE_LINEAR_ACCELERATION,this) || Util.isSensorAvailable(Sensor.TYPE_LINEAR_ACCELERATION,this)){
            accelerometer = Accelerometer(this, "ACC")
        }
        if (Util.isSensorAvailable(Sensor.TYPE_GYROSCOPE,this)){
            gyroscope = Gyroscope(this, "GYRO")
        }
        if (Util.isSensorAvailable(Sensor.TYPE_MAGNETIC_FIELD,this)){
            magnetometer = Magnetometer(this, "MAG")
        }

        gps = Gps(this)
        camera = Camera(this)
        wifi = Wifi(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent!!.extras != null){
            wifi.triggerManualUpload()

            if (Util.isSensorAvailable(Sensor.TYPE_ACCELEROMETER, this) || Util.isSensorAvailable(Sensor.TYPE_LINEAR_ACCELERATION, this)) {
                accelerometer.triggerManualUpload()
            }
            if (Util.isSensorAvailable(Sensor.TYPE_GYROSCOPE,this)) {
                gyroscope.triggerManualUpload()
            }
            if (Util.isSensorAvailable(Sensor.TYPE_MAGNETIC_FIELD,this)) {
                magnetometer.triggerManualUpload()
            }

            camera.triggerManualUpload()
            gps.triggerManualUpload()
        }
        else {
            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

            val notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
                .setContentTitle("Sensor Logger")
                .setContentText("Measurement is running in the background.")
                .setSmallIcon(R.drawable.icon)
                .setContentIntent(pendingIntent)
                .build()
            startForeground(1, notification)

            wifi.run()

            if (Util.isSensorAvailable(Sensor.TYPE_LINEAR_ACCELERATION,this) ||Util.isSensorAvailable(Sensor.TYPE_ACCELEROMETER,this)) {
                accelerometer.run()
            }
            if (Util.isSensorAvailable(Sensor.TYPE_GYROSCOPE,this)) {
                gyroscope.run()
            }
            if (Util.isSensorAvailable(Sensor.TYPE_MAGNETIC_FIELD,this)) {
                magnetometer.run()
            }

            camera.start()
            gps.run()
        }
        //When system kills the service, restart it automatically with intent = null
        return START_STICKY
    }

    override fun onDestroy() {
        wifi.stop()

        if (Util.isSensorAvailable(Sensor.TYPE_LINEAR_ACCELERATION,this) ||Util.isSensorAvailable(Sensor.TYPE_ACCELEROMETER,this)) {
            accelerometer.stop()
        }
        if (Util.isSensorAvailable(Sensor.TYPE_GYROSCOPE,this)) {
            gyroscope.stop()
        }
        if (Util.isSensorAvailable(Sensor.TYPE_MAGNETIC_FIELD,this)) {
            magnetometer.stop()
        }

        gps.stop()
        camera.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}