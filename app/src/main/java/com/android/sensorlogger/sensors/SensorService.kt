package com.android.sensorlogger.sensors

import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.android.sensorlogger.App
import com.android.sensorlogger.MainActivity
import com.android.sensorlogger.R

class SensorService : Service(){

    lateinit var accelerometer : Accelerometer

    override fun onCreate() {
        super.onCreate()
        accelerometer = Accelerometer(this, "ACC")
        //Will be called only the first time the service is created. We can stop and start it,
        //onCreate will be called only once.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        var notificationIntent = Intent(this, MainActivity::class.java)
        var pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0)

        var notification = NotificationCompat.Builder(this, App.CHANNEL_ID)
            .setContentTitle("Sensor Logger")
            .setContentText("Measurement is running in the background.")
            .setSmallIcon(R.drawable.icon)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(1, notification)
        accelerometer.run()
        //When system kills the service, restart it automatically with intent = null
        return START_STICKY
    }

    override fun onDestroy() {
        accelerometer.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}