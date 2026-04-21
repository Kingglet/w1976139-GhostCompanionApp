package com.example.ghostcompanionapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.HttpURLConnection
import java.net.URL
import android.util.Log
import kotlin.math.round
import kotlin.math.roundToInt
import org.json.JSONArray

var ip = "192.168.42.1"

/*
suspend fun findCameraIP(connectionTimeout: Int = 5000): String = withContext(Dispatchers.IO){
    try {
        val socket = DatagramSocket(5555).apply{
            broadcast = true
            soTimeout = connectionTimeout
        }

        val buffer = ByteArray(1024)
        val packet = DatagramPacket(buffer, buffer.size)

        socket.receive(packet)

        ip = packet.address.hostAddress

        val received = String(packet.data, 0, packet.length)

        socket.close()

        Log.d("CAMERA", "Packet: $received")
        Log.d("CAMERA", "Camera IP: $ip")

        ip
    }

    catch (e: Exception) {
        Log.d("CAMERA","UDP receiving failed. Default IP:  192.168.42.1")
        "192.168.42.1"
    }
}
*/

suspend fun httpGetter(urlString: String): String = withContext(Dispatchers.IO) {
    val url = URL(urlString)
    val con: HttpURLConnection = url.openConnection() as HttpURLConnection

    try {
        con.requestMethod = "GET"
        con.connectTimeout = 5000
        con.readTimeout = 5000

        val bf = BufferedReader(InputStreamReader(con.inputStream))
        bf.use { it.readText() }
    }
    finally {
        con.disconnect()
    }
}


fun parseResponse(xml: String): Int{
    val parserFactory = XmlPullParserFactory.newInstance()
    val parser = parserFactory.newPullParser()

    parser.setInput(xml.reader())

    var event = parser.eventType

    while (event != XmlPullParser.END_DOCUMENT) {
        if (event == XmlPullParser.START_TAG && parser.name == "Status") {
            return parser.nextText().toIntOrNull() ?: -1
        }
        event = parser.next()
    }

    return -1
}

/*
fun parseCameraSettings(xml: String): CameraSettings{
    val parserFactory = XmlPullParserFactory.newInstance()
    val parser = parserFactory.newPullParser()

    parser.setInput(xml.reader())

    var event = parser.eventType

    var battery = ""
    var recording = ""
    var mode = ""

    while (event != XmlPullParser.END_DOCUMENT) {
        if (event == XmlPullParser.START_TAG) {
            when (parser.name) {
                "battery" -> battery = parser.nextText()
                "recording" -> recording = parser.nextText()
                "mode" -> mode = parser.nextText()
            }
        }

        event = parser.next()
    }

    return CameraSettings(
        battery = battery,
        recording = recording == "1",
        mode = mode
    )

}
*/

fun parseCameraSettings(xml: String): CameraSettings{
    val parserFactory = XmlPullParserFactory.newInstance()
    val parser = parserFactory.newPullParser()

    parser.setInput(xml.reader())

    var event = parser.eventType

    var status = ""
    var captureMode = ""
    var battery = ""
    var sdFree = ""
    var sdTotal = ""
    var recTime = ""
    var fwVer = ""
    var modelName = ""
    var res = ""
    var framerate = ""
    var bitrate = ""
    var quality = ""
    var streamRes = ""
    var streamFramerate = ""
    var streamBitrate = ""
    var dzoom = ""
    var filter = ""
    var exposure = ""
    var mic = ""
    var led = ""
    var hdRecord = ""

    while (event != XmlPullParser.END_DOCUMENT) {
        if (event == XmlPullParser.START_TAG) {
            when (parser.name) {
                "Status" -> status = parser.nextText()
                "capture_mode" -> captureMode = parser.nextText()
                "battery" -> battery = parser.nextText()
                "sd_free" -> sdFree = parser.nextText()
                "sd_total" -> sdTotal = parser.nextText()
                "rec_time" -> recTime = parser.nextText()
                "fw_ver" -> fwVer = parser.nextText()
                "model_name" -> modelName = parser.nextText()
                "res" -> res = parser.nextText()
                "framerate" -> framerate = parser.nextText()
                "bitrate" -> bitrate = parser.nextText()
                "quality" -> quality = parser.nextText()
                "stream_res" -> streamRes = parser.nextText()
                "stream_framerate" -> streamFramerate = parser.nextText()
                "stream_bitrate" -> streamBitrate = parser.nextText()
                "dzoom" -> dzoom = parser.nextText()
                "filter" -> filter = parser.nextText()
                "exposure" -> exposure = parser.nextText()
                "mic" -> mic = parser.nextText()
                "led" -> led = parser.nextText()
                "hd_record" -> hdRecord = parser.nextText()


            }
        }

        event = parser.next()
    }

    return CameraSettings(
        status = status.toInt(),
        captureMode = captureMode.toInt(),
        battery = battery.toInt(),
        sdFree = sdFree.toInt(),
        sdTotal = sdTotal.toInt(),
        recTime = recTime.toInt(),
        fwVer = fwVer.toInt(),
        modelName = modelName,
        res = res.toInt(),
        framerate = framerate.toInt(),
        bitrate = bitrate.toInt(),
        quality = quality.toInt(),
        streamRes = streamRes.toInt(),
        streamFramerate = streamFramerate.toInt(),
        streamBitrate = streamBitrate.toInt(),
        dzoom = dzoom.toInt(),
        filter = filter.toInt(),
        exposure = exposure.toInt(),
        mic = mic.toInt(),
        led = led.toInt(),
        hdRecord = hdRecord.toInt()
    )

}

/*
fun parseFileList(xml: String): List<CameraFile> {
    val files = mutableListOf<CameraFile>()

    val factory = XmlPullParserFactory.newInstance()
    val parser = factory.newPullParser()
    parser.setInput(xml.reader())

    var event = parser.eventType

    while (event != XmlPullParser.END_DOCUMENT) {
        if (event == XmlPullParser.START_TAG && parser.name == "file") {
            val fileName = parser.nextText()

            val ext = fileName.substringAfterLast(".", "").uppercase()

            files.add(
                CameraFile(
                    fileName = fileName,
                    filePath = fileName,
                    fileType = ext
                )
            )
        }

        event = parser.next()
    }

    return files

}
*/

fun parseFileList(xml: String): List<CameraFile> {
    val files = mutableListOf<CameraFile>()

    val filesText = Regex("<Files>(.*?)</Files>")
        .find(xml)
        ?.groupValues
        ?.get(1)
        ?: return emptyList()

    val cleaned = filesText.trim().removeSuffix(",")
    val jsonArrayText = "[$cleaned]"

    try {
        val jsonArray = JSONArray(jsonArrayText)

        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)

            val path = obj.getString("Path")
            val ext = path.substringAfterLast(".", "").uppercase()

            files.add(
                CameraFile(
                    fileName = path.substringAfterLast("/"),
                    filePath = path,
                    fileType = ext
                )
            )
        }

    } catch (e: Exception) {
        Log.e("FILE_PARSE", "Parse error", e)
    }

    return files
}

fun getStoragePercent(sdTotal: Int, sdFree: Int): Int {
    try {
        val calculationFactor = 100 / sdTotal.toDouble()
        val remainingStorage =  sdFree.toDouble() * calculationFactor
        //val roundedStorage = round(remainingStorage)
        return remainingStorage.roundToInt()
    }
    catch (e: Exception){
        return 0
    }

}

suspend fun deleteFile(filePath: String): Boolean {
    val url = "http://$ip/cgi-bin/foream_remote_control?delete_media_file=$filePath"
    val xml = httpGetter(url)

    return parseResponse(xml) == 1
}

suspend fun checkConnection(): Boolean{
    try {
        val status = getCameraSettings()

        if (status.status == 1) {
            Log.d("CAMERA", "Camera Connected. Default IP:  192.168.42.1")
            return true
        } else {
            Log.d("CAMERA","Connection Failed")
            return false
        }


    } catch (e: Exception) {
        Log.d("CAMERA","Connection Failed")
        return false
    }
}


suspend fun getCameraStatus(): String {
    //val ip = findCameraIP()
    //val ip = findCameraIP() ?: "192.168.42.1"

    return try {
        val status = getCameraSettings()

        if (status.status == 1){
            Log.d("CAMERA","Camera Connected. Default IP:  192.168.42.1")

            """
            Battery: ${status.battery}%
            Recording: ${if (status.recTime == 0) "No" else "Yes"}
            Remaining Storage: ${getStoragePercent(status.sdTotal, status.sdFree)}%
            """.trimIndent()
        } else {
            Log.e("CAMERA","Camera Not Connected")
            "Settings Not Received"
        }


    }
    catch (e: Exception){
        Log.d("CAMERA","Connection Failed")
        "Connection error - Check phone is connected to camera Wi-Fi"
    }
}

suspend fun getCameraSettings(): CameraSettings {

    return try {
        val response = httpGetter("http://$ip/cgi-bin/foream_remote_control?get_camera_status")

        val cameraSetting = parseCameraSettings(response)


        Log.d("CAMERA","Settings Received")
        Log.d("CAMERA",response)

        cameraSetting

    }
    catch (e: Exception){
        Log.e("CAMERA","Settings Not Received")
        CameraSettings(
            status = 0,
            captureMode = 0,
            battery = 0,
            sdFree = 0,
            sdTotal = 0,
            recTime = 0,
            fwVer = 0,
            modelName = "",
            res = 0,
            framerate = 0,
            bitrate = 0,
            quality = 0,
            streamRes = 0,
            streamFramerate = 0,
            streamBitrate = 0,
            dzoom = 0,
            filter = 0,
            exposure = 0,
            mic = 0,
            led = 0,
            hdRecord = 0)
    }
}

suspend fun startRecording(): String {
    //val ip = findCameraIP() ?: "192.168.42.1"

    try {
        val response = httpGetter("http://$ip/cgi-bin/foream_remote_control?start_record")

        return if (parseResponse(response) == 1){
            Log.d("CAMERA", response)
            "Recording Started"
        } else {
            Log.d("CAMERA", response)
            "Recording Couldn't Be Started"

        }
    }

    catch (e: Exception){
        return "Connection Error"
    }
}

suspend fun stopRecording(): String {
    //val ip = findCameraIP() ?: "192.168.42.1"

    try {
        val response = httpGetter("http://$ip/cgi-bin/foream_remote_control?stop_record")

        return if (parseResponse(response) == 1){
            Log.d("CAMERA", response)
            "Recording Stopped"
        } else {
            Log.d("CAMERA", response)
            "Recording Couldn't Be Stopped"
        }
    }

    catch (e: Exception){
        return "Connection Error"
    }
}

suspend fun takePhoto(): String {
    val apiCall = "http://$ip/cgi-bin/foream_remote_control?take_photo"

    try{
        val response = httpGetter(apiCall)

        return if (parseResponse(response) == 1){
            Log.d("CAMERA", response)
            "Photo Taken"
        } else {
            Log.d("CAMERA", response)
            "Photo Couldn't Be Taken"
        }
    }

    catch (e: Exception){
        return "Connection Error"
    }
}

suspend fun switchToVideoMode(): String {
    val apiCall = "http://$ip/cgi-bin/foream_remote_control?switch_video_mode"

    try{
        val response = httpGetter(apiCall)

        return if (parseResponse(response) == 1){
            Log.d("CAMERA", response)
            Log.d("CAMERA", "Switched to Video Mode")
            "Video"

        } else {
            Log.d("CAMERA", response)
            "Failed to Switch to Video Mode"
        }
    }

    catch (e: Exception){
        return "Connection Error"
    }
}

suspend fun switchToPhotoMode(): String {
    val apiCall = "http://$ip/cgi-bin/foream_remote_control?switch_photo_mode"

    try{
        val response = httpGetter(apiCall)

        return if (parseResponse(response) == 1){
            Log.d("CAMERA", response)
            "Photo"
        } else {
            Log.d("CAMERA", response)
            "Failed to Switch to Photo Mode"
        }
    }

    catch (e: Exception){
        return "Connection Error"
    }
}

suspend fun switchToTimelapseMode(): String {
    val apiCall = "http://$ip/cgi-bin/foream_remote_control?switch_timelapse_mode"

    try{
        val response = httpGetter(apiCall)

        return if (parseResponse(response) == 1){
            Log.d("CAMERA", response)
            "Timelapse"
        } else {
            Log.d("CAMERA", response)
            "Failed to Switch to Timelapse Mode"
        }
    }

    catch (e: Exception){
        return "Connection Error"
    }
}

suspend fun switchToBurstMode(): String {
    val apiCall = "http://$ip/cgi-bin/foream_remote_control?switch_photo_mode"

    try{
        val response = httpGetter(apiCall)

        return if (parseResponse(response) == 1){
            Log.d("CAMERA", response)
            "Burst"
        } else {
            Log.d("CAMERA", response)
            "Failed to Switch to Burst Mode"
        }
    }

    catch (e: Exception){
        return "Connection Error"
    }
}

suspend fun setZoom(zoomLevel: Int): String {
    val apiCall = "http://$ip/cgi-bin/foream_remote_control?dzoom=$zoomLevel"

    try{
        val response = httpGetter(apiCall)

        return if (parseResponse(response) == 1){
            Log.d("CAMERA", response)
            Log.d("CAMERA", "Zoom Level set to $zoomLevel")
            "Zoom Level set to $zoomLevel"
        } else {
            Log.d("CAMERA", response)
            Log.d("CAMERA", "Zoom level $zoomLevel may be invalid")
            "Zoom Level Not Set"
        }
    }

    catch (e: Exception){
        return "Connection Error"
    }
}

suspend fun listFiles(): List<CameraFile> {
    val apiCall = "http://$ip/cgi-bin/foream_remote_control?list_files=/tmp/SD0/DCIM"


    val response = httpGetter(apiCall)


    Log.d("CAMERA", response)

    return parseFileList(response)
}

suspend fun APITest(): String {
    //val apiCall = "http://$ip/cgi-bin/foream_remote_control?2"
    //val apiCall = "http://$ip/setting/cgi-bin/fd_control_client?func=fd_set_camera_off"
    //val apiCall = "http://$ip/cgi-bin/foream_remote_control?reboot"

    //val apiCall = "http://$ip/cgi-bin/fd_control_client?func="
    val apiCall = "http://$ip/cgi-bin/foream_remote_control?switch_photo_mode"

    try{
        val response = httpGetter(apiCall)

        return if (parseResponse(response) == 1){
            Log.d("CAMERA", response)
            ""
        } else {
            Log.d("CAMERA", response)
            ""
        }
    }

    catch (e: Exception){
        return "Connection Error"
    }
}
suspend fun APItemplate(): String {
    val apiCall = "http://$ip/cgi-bin/foream_remote_control?"

    try{
        val response = httpGetter(apiCall)

        return if (parseResponse(response) == 1){
            Log.d("CAMERA", response)
            ""
        } else {
            Log.d("CAMERA", response)
            ""
        }
    }

    catch (e: Exception){
        return "Connection Error"
    }
}

/*
//for testing connection
suspend fun getCameraSettings(): String {

    val ip = findCameraIP() ?: "192.168.42.1"

    return try {
        val url =
            "http://$ip/cgi-bin/foream_remote_control?get_camera_status"

        val response = httpGetter(url)

        "RAW RESPONSE:\n$response"

    } catch (e: Exception) {
        "ERROR: ${e.message}"
    }
}
 */


