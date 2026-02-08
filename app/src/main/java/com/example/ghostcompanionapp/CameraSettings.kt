package com.example.ghostcompanionapp

data class CameraSettings(
    val status: Int,
    val captureMode: Int,
    val battery: Int,
    val sdFree: Int,
    val sdTotal: Int,
    val recTime: Int,
    val fwVer: Int,
    val modelName: String,
    val res: Int,
    val framerate: Int,
    val bitrate: Int,
    val quality: Int,
    val streamRes: Int,
    val streamFramerate: Int,
    val streamBitrate: Int,
    val dzoom: Int,
    val filter: Int,
    val exposure: Int,
    val mic: Int,
    val led: Int,
    val hdRecord: Int
)