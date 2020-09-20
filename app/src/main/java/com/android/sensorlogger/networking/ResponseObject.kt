package com.android.sensorlogger.networking

data class ResponseObject(
    val files: Files,
    val status: Int
)

data class Files(
    val exists: List<String>
)