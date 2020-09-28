package com.android.sensorlogger.networking

import android.app.AlertDialog
import android.content.Context
import android.text.InputFilter
import android.view.LayoutInflater
import android.widget.Toast
import com.android.sensorlogger.App
import com.android.sensorlogger.R
import com.android.sensorlogger.Utils.SessionManager
import com.android.sensorlogger.camera.InputFilterMinMax
import kotlinx.android.synthetic.main.camera_settings.view.*
import kotlinx.android.synthetic.main.upload_settings.view.*

class UploadSettings(var context : Context) {

    /**View items */
    val view = LayoutInflater.from(context).inflate(R.layout.upload_settings, null)
    val api_url_edittext = view.api_url_edittext
    val upload_rate_edittext = view.upload_rate_edittext
    val api_endpoint_edittext = view.api_endpoint_edittext

    fun OpenUploadSettings(){
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Upload settings")
        builder.setView(view)
        builder.setPositiveButton("OK") { dialog, which ->
            saveConfiguration()
            Toast.makeText(context, "Configuration saved.", Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
            .setCancelable(true)

        builder.show()

        loadConfiguration()
    }

    private fun saveConfiguration(){
        App.sessionManager.setUrl(api_url_edittext.text.toString())
        App.sessionManager.setUploadRate(upload_rate_edittext.text.toString().toInt())
        App.sessionManager.setEndpoint(api_endpoint_edittext.text.toString())
    }

    private fun loadConfiguration(){
        api_url_edittext.setText(App.sessionManager.getUrl())
        upload_rate_edittext.setText(App.sessionManager.getUploadRate().toString())
        api_endpoint_edittext.setText(App.sessionManager.getEndpoint())
    }
}