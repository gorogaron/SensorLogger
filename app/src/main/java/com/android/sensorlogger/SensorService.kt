package com.android.sensorlogger

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.agrolytics.agrolytics_android.utils.Gps
import com.android.sensorlogger.Utils.Util
import com.android.sensorlogger.camera.Camera
import com.android.sensorlogger.networking.UploadWatcher
import com.android.sensorlogger.sensors.Accelerometer
import com.android.sensorlogger.sensors.Gyroscope
import com.android.sensorlogger.sensors.Magnetometer
import com.android.sensorlogger.wifi.Wifi
import java.lang.RuntimeException


class SensorService : Service(){
    private var accelerometer : Accelerometer? = null
    private var gyroscope : Gyroscope? = null
    private var magnetometer: Magnetometer? = null
    private var camera : Camera? = null
    private var gps : Gps? = null
    private var wifi : Wifi? = null
    private var uploadWatcher : UploadWatcher? = null

    override fun onCreate() {
        //Will be called only the first time the service is created. We can stop and start it,
        //onCreate will be called only once.
        super.onCreate()
        fun <T> tryOrNull(f: () -> T) =
            try {
                f()
            } catch (e: Exception) {
                Log.e("SEN", "Could not initialize: ${e.localizedMessage}")
                null
            }
        accelerometer = tryOrNull { Accelerometer(this, "ACC") }
        gyroscope = tryOrNull { Gyroscope(this, "GYRO") }
        magnetometer = tryOrNull { Magnetometer(this, "MAG") }
        gps = tryOrNull { Gps(this) }
        camera = tryOrNull { Camera(this) }
        wifi = tryOrNull { Wifi(this) }
        uploadWatcher = UploadWatcher(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.extras != null){
            uploadWatcher?.triggerManualUpload()
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

            wifi?.run()
            accelerometer?.run()
            gyroscope?.run()
            magnetometer?.run()
            camera?.start()
            gps?.run()
            uploadWatcher?.run()
        }
        //When system kills the service, restart it automatically with intent = null
        return START_STICKY
    }

    override fun onDestroy() {
        wifi?.stop()
        accelerometer?.stop()
        gyroscope?.stop()
        magnetometer?.stop()
        gps?.stop()
        camera?.stop()
        uploadWatcher?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

}