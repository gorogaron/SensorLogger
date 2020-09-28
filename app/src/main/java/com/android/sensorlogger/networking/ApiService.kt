package com.android.sensorlogger.networking

import android.content.Context
import android.util.Log
import androidx.core.content.FileProvider
import com.android.sensorlogger.App
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.awaitResponse
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ApiService {
    val api = SensorLoggerApi.create()

    suspend fun uploadFile(file: File, context: Context){
        Log.d("API", "Started uploading file: ${file.name}")
        val fileUri = FileProvider.getUriForFile(context, context.packageName + ".provider", file)

        val filePart = file.asRequestBody(context.contentResolver.getType(fileUri)!!.toMediaTypeOrNull())
        val filePartRequest = MultipartBody.Part.createFormData("files", file.name, filePart)
        var response = api.uploadFile(App.sessionManager.getEndpoint()!!,filePartRequest).awaitResponse()
        if (response.isSuccessful){
            Log.d("API", "Uploading successful: ${file.name}")

            var date = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
            App.lastUpload = "${file.name} ($date)"
            App.networkTraffic = (App.networkTraffic + getFileSize(file)).round(2)

        }
        else {
            Log.d("API", "Failed to upload file: ${file.name}")
        }
    }
}

//TODO: Move this method to appropriate place
private fun Double.round(i: Int): Double {
    var multiplier = 1.0
    repeat(i) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}

private fun getFileSize(file: File) : Double{
    return (file.length().toDouble()/(1024*1024)).round(2)
}