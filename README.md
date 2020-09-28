# SensorLogger application for Android
## Basic informations
**Requirements**
* Minimum Android 8.0
* Permissions:
  * Camera
  * Internet
  * Location
  * Write rights to external storage
  
  
**Logged informations**
* Accelerometer signals
* Gyroscope signals
* Magnetometer signals
* Lost and found Wi-Fi access point SSIDs and MAC addresses
* Video and audio recording
* GPS coordinates

## Logging
### Logic of logging
Logging of accelerometer signals, GPS coordinates, Wi-Fi informations, and video/audio recording is only done, if the device is in movement.
The device is defined to be moving if the measured acceleration on any axis is greater than a pre-defined value. When the threshold is exceeded, the device will
be in movement for 30 seconds. When the threshold value is exceeded again within this 30 seconds, the timer is reset. The amount of time for which the
device is defined to be moving after threshold exceeding can be configured in `Accelerometer.kt` via `movementDelay`.

The logging of gyroscope and magnetometer signals is only done, if the measurement on any axis is greater than a pre-defined value.

The configuration of the previously mentioned threshold values can be done in `Config.kt`. Video and audio encoder, and video bitrate can be also set in this file.

### Configuration
* The sampling frequency can be changed for each sensors/loggers. For the gyro, magnetometer and accelerometer, `sampleRateMillis` has to be overwritten. The default value is 
500, which means the sample rate will be 0.5 seconds for these sensors. Since writing and opening the logfile during runtime can be expensive when the logging is done
at a very high rate, the file is not saved after each line. The frequency of file saving for these 3 sensors can be set via `fileSavingRate` in `SensorBase.kt`.

* Basic camera parameters can be set via user interface, and in `Config.kt`. The format can be changed from `.mp4` to any kind via overwriting `fileName` in `Camera.kt`.

* GPS signal is logged whenever the location changes. As it's frequency is not too big, the corresponding logfile is saved in each iteration.

* Wifi networks are only logged if a network is lost or found. The coloumn seperated logfile has 4 coloumns:
  * Found network SSID
  * Found network MAC address
  * Lost network SSID
  * Lost network MAC address
  
## Uploading

The uploading of logfiles and video is done periodically in the background. The files are uploaded with seperate POST requests. The total amount of uploaded files in MByte and the last
uploaded file is logged on the main screen of the app. The period can be set from the UI. Uploaded files are immediately deleted from the device.

The URL of the backend can be defined from the UI. **Important:** The URL MUST be defined in the same format as described in the popup window (starts with protocol, ends with `/`)
By default the requests will go to `parcel/1` endpoint, this can be changed in `SensorLogger.Api`.

Besides the periodic uploading, the files will be posted for the server when the the measurement is stopeed manually. Uploading can be triggered from the UI as well during measurement.

## How to start

You can configure which sensors you'd like to use in `SensorService.kt`. I have just commented out some of them for testing purposes, you can turn them on by
removing the comment from `onCreate`, `onStartCommand` and `onDestroy` functions.
