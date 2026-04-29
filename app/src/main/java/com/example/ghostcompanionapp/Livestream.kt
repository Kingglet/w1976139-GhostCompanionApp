package com.example.ghostcompanionapp

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@Composable
fun Livestream(navController: NavController, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("") }


    var ssid by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var rtmpUrl by rememberSaveable { mutableStateOf("") }
    //var rtmpUrl by rememberSaveable { mutableStateOf("") }

    var qrBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var cameraIP by rememberSaveable { mutableStateOf("") }

    //ssid = "BT-62CJN2"
    //password = "uC4TG6deRHRQtQ"
    //rtmpUrl = "192.168.3.133:1935/live/5"
    //cameraIP = "192.168.1.251"

    var isDiscovering by rememberSaveable { mutableStateOf(false) }
    var isStreaming by rememberSaveable { mutableStateOf(false) }

    val inputFieldColours = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.Black,
        unfocusedTextColor = Color.Black,
        focusedLabelColor = Color.Black,
        unfocusedLabelColor = Color.Black,
        focusedBorderColor = Color.Black,
        unfocusedBorderColor = Color.Gray,
        cursorColor = Color.Black
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.padding(12.dp))

        Text("Livestream Setup")

        Spacer(modifier = Modifier.padding(6.dp))

        OutlinedTextField(
            value = ssid,
            onValueChange = { ssid = it },
            label = { Text("Router SSID") },
            modifier = Modifier.fillMaxWidth(),
            colors = inputFieldColours
        )

        Spacer(modifier = Modifier.padding(6.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Router Password") },
            modifier = Modifier.fillMaxWidth(),
            colors = inputFieldColours
        )

        Spacer(modifier = Modifier.padding(6.dp))

        OutlinedTextField(
            value = rtmpUrl,
            onValueChange = { rtmpUrl = it },
            label = { Text("RTMP Server (no rtmp:// prefix)") },
            modifier = Modifier.fillMaxWidth(),
            colors = inputFieldColours
        )

        Spacer(modifier = Modifier.padding(6.dp))

        OutlinedTextField(
            value = cameraIP,
            onValueChange = { cameraIP = it },
            label = { Text("Camera IP (Enter Manually or Use Auto Detect)") },
            modifier = Modifier.fillMaxWidth(),
            colors = inputFieldColours
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (ssid.isBlank()) {
                    responseMessage = "SSID cannot be empty"
                    return@Button
                }

                val qrText = generateQRText(ssid, password)
                qrBitmap = generateQRBitmap(qrText)
                responseMessage = "Show QR code to camera to connect"
            }
        ) {
            Text("Generate QR Code")
        }

        Spacer(modifier = Modifier.padding(10.dp))

        qrBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(250.dp)
            )
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isDiscovering && !isStreaming,
            onClick = {
                scope.launch {
                    responseMessage = "Listening for camera broadcast..."
                    isDiscovering = true

                    val cameraInfo = cameraListener()

                    if (cameraInfo != null) {
                        cameraIP = cameraInfo.cameraIP
                        responseMessage = "Camera detected: ${cameraInfo.cameraModel} @ $cameraIP"
                        isDiscovering = false
                        return@launch
                    }

                    val detectedIP = findDriftOnNetwork()

                    if (detectedIP != null) {
                        cameraIP = detectedIP
                        responseMessage = "Camera detected: @ $cameraIP"
                        isDiscovering = false
                        return@launch
                    }

                    responseMessage = "No camera broadcast or IP detected"
                    isDiscovering = false
                    return@launch
                }
            }
        ) {
            Text("Detect Camera IP")
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = cameraIP.isNotEmpty() && !isStreaming && !isDiscovering,
            onClick = {
                scope.launch {
                    responseMessage = "Starting RTMP stream..."
                    val result = startRTMPStream(cameraIP, rtmpUrl)
                    responseMessage = result

                    if (result == "Stream Started") {
                        isStreaming = true
                    } else {
                        isStreaming = false
                    }
                }
            }
        ) {
            Text("Start Livestream (RTMP)")
        }

        Spacer(modifier = Modifier.padding(6.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = isStreaming && !isDiscovering,
            onClick = {
                scope.launch {
                    responseMessage = "Stopping RTMP stream..."
                    val result = stopRTMPStream(cameraIP)
                    responseMessage = result

                    if (result == "Stream Stopped") {
                        isStreaming = false
                    }
                }
            }
        ) {
            Text("Stop Livestream")
        }

        Spacer(modifier = Modifier.padding(6.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            onClick = {
                scope.launch {
                    responseMessage = "Starting RTMP stream..."
                    val result = startRTMPStream("192.168.1.251", rtmpUrl)
                    responseMessage = result

                    if (result == "Stream Started") {
                        isStreaming = true
                    } else {
                        isStreaming = false
                    }
                }
            }
        ) {
            Text("[Override] Start Livestream (RTMP)")
        }
        Spacer(modifier = Modifier.padding(6.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = true,
            onClick = {
                scope.launch {
                    responseMessage = "Stopping RTMP stream..."
                    val result = stopRTMPStream("192.168.1.251")
                    responseMessage = result

                    if (result == "Stream Stopped") {
                        isStreaming = false
                    }
                }
            }
        ) {
            Text("[Override] Stop Livestream")
        }



        Spacer(modifier = Modifier.padding(10.dp))

        Text(responseMessage)

        Spacer(modifier = Modifier.padding(6.dp))

        if (cameraIP.isNotEmpty()) {
            Text("RTSP Preview URL: rtsp://$cameraIP/live")
        }
    }


}