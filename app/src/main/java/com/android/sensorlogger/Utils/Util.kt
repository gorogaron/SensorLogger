package com.android.sensorlogger.Utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.InetAddress
import java.net.UnknownHostException

object Util {

    suspend fun isOnline(): Boolean {
        try {
            lateinit var addresses: Array<out InetAddress>
            GlobalScope.launch(Dispatchers.IO){
                addresses = InetAddress.getAllByName("www.google.com")
            }.join()
            return !addresses[0].hostAddress.equals("")
        } catch (e: UnknownHostException) {
            // Log error
        }
        return false
    }


}