package com.android.sensorlogger.Utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*

open class Logger(context: Context, var fileNameTag : String) {
    private val appDirectory = File(context.getExternalFilesDir(null).toString() + "/SensorLogger")
    private val logDirectory = File("$appDirectory/logs")
    private lateinit var logFile : File
    private lateinit var outputStreamWriter: OutputStreamWriter

    fun initLogFile(){
        if (!appDirectory.exists()) {
            appDirectory.mkdir()
        }
        if (!logDirectory.exists()) {
            logDirectory.mkdir()
        }
        logFile = File(logDirectory, fileNameTag + SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US).format(Date()) + ".txt")
        if (!logFile.exists()) {
            logFile.createNewFile()
        }
        outputStreamWriter = OutputStreamWriter(FileOutputStream(logFile, true))
    }

    fun deleteFile(){
        logFile.delete()
    }

    fun writeToFile(line : String){
        outputStreamWriter.write(line)
    }

    fun closeFile(){
        outputStreamWriter.close()
    }

}