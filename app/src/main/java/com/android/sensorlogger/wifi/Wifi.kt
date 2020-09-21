package com.android.sensorlogger.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.android.sensorlogger.Utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

    private fun scan(){
        val success = wifiManager.startScan()
        if (!success) {
            scanFailure()
        }
        scanHandler.postDelayed(scanRunnable, 15000)
    }

    fun scanFailure(){
        Toast.makeText(context, "SSIDs will not be logged.", Toast.LENGTH_LONG).show()
    }

    private fun scanSuccess() {
        val results = wifiManager.scanResults
        GlobalScope.launch(Dispatchers.IO) {
            results.forEach {
                val line = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss_SSS", Locale.US).format(Date()) + ":${it.SSID}\n"
                writeToFile(line)
            }
            closeFile()
        }
    }

    fun run(){
        if (!wifiManager.isWifiEnabled){
            Toast.makeText(context, "Wifi is turned off, SSIDs will not be logged.", Toast.LENGTH_LONG).show()
        }
        else{
            initLogFile()
            startPeriodicUpload()
            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            context.registerReceiver(wifiScanReceiver, intentFilter)
            scan()
        }
    }

    fun stop(){
        scanHandler.removeCallbacks(scanRunnable)
        stopPeriodicUpload()
    }

}