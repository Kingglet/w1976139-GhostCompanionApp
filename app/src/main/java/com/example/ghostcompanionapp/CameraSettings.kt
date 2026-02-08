package com.example.ghostcompanionapp

data class CameraSettings(
    var status: Int,
    var captureMode: Int,
    var battery: Int,
    var sdFree: Int,
    var sdTotal: Int,
    var recTime: Int,
    var fwVer: Int,
    var modelName: String,
    var res: Int,
    var framerate: Int,
    var bitrate: Int,
    var quality: Int,
    var streamRes: Int,
    var streamFramerate: Int,
    var streamBitrate: Int,
    var dzoom: Int,
    var filter: Int,
    var exposure: Int,
    var mic: Int,
    var led: Int,
    var hdRecord: Int
)