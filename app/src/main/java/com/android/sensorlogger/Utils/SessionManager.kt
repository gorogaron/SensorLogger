package com.android.sensorlogger.Utils

import android.content.Context

class SessionManager(context: Context) {

    val KEY_WIDTH = "width"
    val KEY_HEIGHT = "height"
    val KEY_FPS = "fps"
    val KEY_CAM_ID = "camId"

    val sharedPreferences = context.getSharedPreferences("SETTINGS", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()

    fun setWidth(width : Int){
        editor.putInt(KEY_WIDTH, width).apply()
    }
    fun getWidth() : Int{
        return sharedPreferences.getInt(KEY_WIDTH, 640)
    }

    fun setHeight(height : Int){
        editor.putInt(KEY_HEIGHT, height).apply()
    }
    fun getHeight() : Int{
        return sharedPreferences.getInt(KEY_HEIGHT, 480)
    }

    fun setFps(fps : Int) {
        editor.putInt(KEY_FPS, fps).apply()
    }
    fun getFps() : Int{
        return sharedPreferences.getInt(KEY_FPS, 24)
    }

    fun setCamId(id : String){
        editor.putString(KEY_CAM_ID, id).apply()
    }
    fun getCamId() : String? {
        return sharedPreferences.getString(KEY_CAM_ID, "0")
    }
}