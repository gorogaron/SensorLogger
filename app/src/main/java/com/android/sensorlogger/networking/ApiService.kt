package com.android.sensorlogger.networking

import android.content.Context
import android.net.Uri
import com.android.sensorlogger.App.Companion.logFileList
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class ApiService {
    val api = SensorLoggerApi.create()

    fun uploadFiles(file: File, context: Context){
        for (file in logFileList){
            val fileUri = Uri.fromFile(file)

            val filePart = RequestBody.create(MediaType.parse(context.contentResolver.getType(fileUri)!!), file)
            val filePartRequest = MultipartBody.Part.createFormData("files", file.name, filePart)
            api.uploadFile(filePartRequest)
        }
    }
}