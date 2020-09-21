package com.android.sensorlogger.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.android.sensorlogger.Utils.Logger

class Wifi(context : Context) : Logger(context, "WIFI") {
    private var wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private var scanHandler = Handler()
    private var scanRunnable = Runnable { scan() }

    private val wifiScanReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val success = intent.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
            if (success) {
                scanSuccess()
            } else {
                scanFailure()
            }
        }
    }

    fun scan(){
        val success = wifiManager.startScan()
        if (!success) {
            scanFailure()
        }
        scanHandler.postDelayed(scanRunnable, 15000)
    }

    fun run(){
        if (!wifiManager.isWifiEnabled){
            Toast.makeText(context, "Wifi is turned off, SSIDs will not be logged.", Toast.LENGTH_LONG).show()
        }
        else{
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            context.registerReceiver(wifiScanReceiver, intentFilter)
            scan()
        }
    }

    fun scanFailure(){
        Toast.makeText(context, "SSIDs will not be logged.", Toast.LENGTH_LONG).show()
    }

    private fun scanSuccess() {
        val results = wifiManager.scanResults
        results.forEach { Log.d("WIFI", it.SSID) }
    }

}