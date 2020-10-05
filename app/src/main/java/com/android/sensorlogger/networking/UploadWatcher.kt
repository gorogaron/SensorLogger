package com.android.sensorlogger.networking

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.sensorlogger.App
import com.android.sensorlogger.Utils.Util
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.RandomAccessFile
import java.nio.channels.OverlappingFileLockException
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

class UploadWatcher(val context: Context) {
    private val appDirectory = File(context.getExternalFilesDir(null).toString() + "/SensorLogger")
    private val logDirectory = File("$appDirectory/logs")
    private val uploadRate : Long = com.android.sensorlogger.App.sessionManager.getUploadRate().toLong() * 1000
    private var timer = Timer("UploadTask", false)

    fun run () {
        timer.scheduleAtFixedRate(0, 5000) {
            uploadFiles()
        }
    }
    private fun uploadFiles() {
        Log.d("UW", "Run!")
        GlobalScope.launch(Dispatchers.IO) {
            if (Util.isOnline()) {
                logDirectory.walk().forEach {
                    if(!it.isFile) return@forEach
                    if(isLocked(it)) {
                        Log.e("UW", "locked ${it}")
                        return@forEach
                    }
                    if(App.apiService.uploadFile(it, context)) {
                        Log.e("UW", "uploaded ${it}")
                        it.delete()
                    }
                }
            }
            else {
                Log.d("LOGGER", "No internet, postponed upload")
                Toast.makeText(
                    context,
                    "No network connection, postponed uploading.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun isLocked(file: File): Boolean {
        val channel = RandomAccessFile(file, "rw").channel

        try {
            channel.tryLock()
        } catch (e: OverlappingFileLockException) {
            return true
        }
        return false
    }



    fun triggerManualUpload() {
        uploadFiles()
    }
    fun stop() {
        timer.cancel()
    }
}