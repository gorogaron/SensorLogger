package com.android.sensorlogger.camera

import android.Manifest
import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.app.PendingIntent.getActivity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/** Helper to ask camera permission.  */
object CameraPermissionHelper {
    private const val CAMERA_PERMISSION_CODE = 0
    private const val CAMERA_PERMISSION = Manifest.permission.CAMERA

    /** Check to see we have the necessary permissions for this app.  */
    fun hasCameraPermission(activity: Activity): Boolean {
        return ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED
    }

    fun hasMicPermission(activity: Activity) : Boolean {
        return ContextCompat.checkSelfPermission(activity, RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    /** Check to see we have the necessary permissions for this app, and ask for them if we don't.  */
    fun requestPermission(activity: Activity, permissions: ArrayList<String>) {
        ActivityCompat.requestPermissions(
            activity, permissions.toTypedArray(), CAMERA_PERMISSION_CODE)
    }

    /** Check to see if we need to show the rationale for this permission.  */
    fun shouldShowRequestPermissionRationale(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)
    }

    /** Launch Application Setting to grant permission.  */
    fun launchPermissionSettings(activity: Activity) {
        val intent = Intent()
        intent.action = ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", activity.packageName, null)
        activity.startActivity(intent)
    }
}