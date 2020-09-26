package com.android.sensorlogger.Utils

object Util {

    fun isOnline(): Boolean {
        try {
            val p1 =
                Runtime.getRuntime().exec("ping -c 1 www.google.com")
            val returnVal = p1.waitFor()
            return returnVal == 0
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}