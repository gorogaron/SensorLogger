package com.android.sensorlogger.networking

import android.hardware.Sensor
import android.util.Log
import com.android.sensorlogger.App
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface SensorLoggerApi {

    @Multipart
    @Headers("X-API-Key: dc5daf82-f7a0-11ea-adc1-0242ac120002")
    @POST
    fun uploadFile(
        @Url endpointUrl : String,
        @Part files : MultipartBody.Part
    ) : Call<ResponseObject>

    companion object Creator {
        fun create(): SensorLoggerApi {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(App.sessionManager.getUrl()!!)
                .build()
            return retrofit.create(SensorLoggerApi::class.java)
        }
    }
}