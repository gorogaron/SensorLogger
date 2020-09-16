package com.android.sensorlogger.camera

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.media.MediaCodec
import android.media.MediaRecorder
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Range
import android.view.Surface
import android.widget.Toast
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class Camera(context: Context) {

    var mContext = context
    private val cameraManager = mContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private lateinit var camera: CameraDevice
    private lateinit var session: CameraCaptureSession

    private val appDirectory = File(context.getExternalFilesDir(null).toString() + "/SensorLogger")
    val logDirectory = File("$appDirectory/logs")
    private val outputFile = File(logDirectory, "vid.mp4") //TODO - modify file name

    /**New thread for camera operations*/
    private val cameraThread = HandlerThread("CameraThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)

    private val recorderSurface: Surface by lazy {
        // Get a persistent Surface from MediaCodec, don't forget to release when done
        val surface = MediaCodec.createPersistentInputSurface()
        // Prepare and release a dummy MediaRecorder with our new surface
        // Required to allocate an appropriately sized buffer before passing the Surface as the
        //  output target to the capture session
        createRecorder(surface).apply {
            prepare()
            release()
        }
        surface
    }

    private val recordRequest: CaptureRequest by lazy {
        session.device.createCaptureRequest(CameraDevice.TEMPLATE_RECORD).apply {
            addTarget(recorderSurface)
            set(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, Range(FPS, FPS))
        }.build()
    }

    private val recorder: MediaRecorder by lazy { createRecorder(recorderSurface) }

    private fun initializeCamera() = GlobalScope.launch(Dispatchers.Main){
            if (cameraManager.cameraIdList.isEmpty()) {
                Toast.makeText(mContext, "No cameras were found on the device.", Toast.LENGTH_SHORT).show()
                //TODO - handle error
            }
            else {
                val cameraId = cameraManager.cameraIdList[0]
                camera = openCamera(cameraId)!! //TODO - handle if camera is null
                session = createCaptureSession(camera, listOf(recorderSurface))
                Log.d("CAMTAG", "Session created.")
            }
        }

    private suspend fun openCamera(cameraId: String) : CameraDevice? = suspendCoroutine {cont ->
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(mContext, "Permission has not been granted.", Toast.LENGTH_SHORT).show()
            cont.resume(null)
        }
        cameraManager.openCamera(cameraId, object: CameraDevice.StateCallback() {
            override fun onDisconnected(cameraDevice: CameraDevice){
                //cameraThread.quitSafely()
                //recorder.release()
                //recorderSurface.release()
                cameraDevice.close()
                Log.d("CAMTAG", "Camera disconnected.")
            }
            override fun onError(p0: CameraDevice, p1: Int) = cont.resume(null)
            override fun onOpened(cameraDevice: CameraDevice){
                Log.d("CAMTAG", "Camera opened.")
                cont.resume(cameraDevice)
            }
        }, cameraHandler)
    }

    private suspend fun createCaptureSession(device: CameraDevice, targets: List<Surface>): CameraCaptureSession = suspendCoroutine { cont ->

        device.createCaptureSession(targets, object: CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                cont.resumeWithException(exc)
            }
        }, cameraHandler)
    }

    private fun createRecorder(surface: Surface) = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC) //TODO - remove this optionally, and add permission handling
        setVideoSource(MediaRecorder.VideoSource.SURFACE)
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        setOutputFile(outputFile)
        setVideoEncodingBitRate(RECORDER_VIDEO_BITRATE)
        setVideoFrameRate(FPS)
        setVideoSize(WIDTH, HEIGHT)
        setVideoEncoder(VIDEO_ENCODER)
        setAudioEncoder(AUDIO_ENCODER)
        setInputSurface(surface)
    }

    fun startRecording() = GlobalScope.launch(Dispatchers.IO){
        initializeCamera().join()
        session.setRepeatingRequest(recordRequest, null, cameraHandler)

        recorder.apply {
            // Sets output orientation based on current sensor value at start time
            //relativeOrientation.value?.let { setOrientationHint(it) }
            prepare()
            start()
        }
    }

    fun stopRecording(){
        recorder.stop()
    }

    companion object Config {
        var FPS = 24
        var VIDEO_ENCODER = MediaRecorder.VideoEncoder.H264
        var AUDIO_ENCODER = MediaRecorder.AudioEncoder.AAC
        var WIDTH = 1280
        var HEIGHT = 720
        val RECORDER_VIDEO_BITRATE: Int = 10_000_000
    }
}