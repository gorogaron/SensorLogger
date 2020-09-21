package com.android.sensorlogger

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.agrolytics.agrolytics_android.utils.Gps
import com.android.sensorlogger.camera.Camera
import com.android.sensorlogger.sensors.Accelerometer
import com.android.sensorlogger.sensors.Gyroscope
import com.android.sensorlogger.sensors.Magnetometer

class SensorService : Service(){

    lateinit var accelerometer : Accelerometer
    lateinit var gyroscope : Gyroscope
    lateinit var magnetometer: Magnetometer
    lateinit var camera : Camera
    lateinit var gps : Gps

    override fun onCreate() {
        //Will be called only the first time the service is created. We can stop and start it,
        //onCreate will be called only once.
        super.onCreate()

        //accelerometer = Accelerometer(this, "ACC")
        //gyroscope = Gyroscope(this, "GYRO")
        //magnetometer = Magnetometer(this, "MAG")
        gps = Gps(this)
        //camera = Camera(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setContentTitle("Sensor Logger")
            .setContentText("Measurement is running in the background.")
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        //accelerometer.run()
        //gyroscope.run()
        //magnetometer.run()
        //camera.startRecording()
        gps.run()
        //When system kills the service, restart it automatically with intent = null
        return START_STICKY
    }

    override fun onDestroy() {
        //accelerometer.stop()
        //gyroscope.stop()
        //magnetometer.stop()
        gps.stop()
        //camera.stopRecording()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}