package com.android.sensorlogger.networking

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.core.content.FileProvider
import com.android.sensorlogger.App.Companion.logFileList
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.await
import retrofit2.awaitResponse
import java.io.File

class ApiService {
    val api = SensorLoggerApi.create()

    suspend fun uploadFile(file: File, context: Context){
        Log.d("API", "Started uploading file: ${file.name}")
        val fileUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)

        val filePart = file.asRequestBody(context.contentResolver.getType(fileUri)!!.toMediaTypeOrNull())
        val filePartRequest = MultipartBody.Part.createFormData("files", file.name, filePart)
        var response = api.uploadFile(filePartRequest).awaitResponse()
        if (response.isSuccessful){
            Log.d("API", "Uploading successful: ${file.name}")
        }
        else {
            Log.d("API", "Failed to upload file: ${file.name}")
        }
    }
}