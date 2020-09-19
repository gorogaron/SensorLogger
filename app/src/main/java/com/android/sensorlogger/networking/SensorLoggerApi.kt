package com.android.sensorlogger.networking

import android.hardware.Sensor
import okhttp3.Interceptor
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.util.concurrent.TimeUnit

interface SensorLoggerApi {

    @Multipart
    @POST("/1")
    fun uploadFile(
        @Part("files") file : MultipartBody.Part
    )

    companion object Creator {
        fun create(): SensorLoggerApi {
            val header = Interceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader(
                        "X-API-Key",
                        "dc5daf82-f7a0-11ea-adc1-0242ac120002"
                    )
                    .build()
                chain.proceed(request)
            }

            val retrofit = Retrofit.Builder()
                .baseUrl("https://palacz.my.to/parcel")
                .build()
            return retrofit.create(SensorLoggerApi::class.java)
        }
    }
}