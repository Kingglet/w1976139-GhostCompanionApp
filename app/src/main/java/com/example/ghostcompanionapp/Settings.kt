package com.example.ghostcompanionapp

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.compose.material3.Switch
import androidx.compose.material3.Slider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.text.format.DateFormat
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign


//@Composable
/*
fun Settings(navController: NavController, modifier: Modifier = Modifier){

    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while(isActive){
            try {
                currentSettings = getCameraSettings()

            }   catch (e: Exception) {
                Log.e("CAMERA", "Failed to get settings")
            }

            delay(1000)
        }
    }



    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {

        Spacer(modifier = Modifier.padding(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        responseMessage = "Connecting..."
                        if (checkConnection() == true)  {
                            responseMessage = "Camera is Connected!"
                        } else {
                            responseMessage = "Connection Error"
                        }

                    }}
            ) {
                Text("Check Connection")
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("""
                Current Battery: ${currentSettings.battery}%
                Remaining Storage: ${getStoragePercent(currentSettings.sdTotal, currentSettings.sdFree)}%
                Firmware Version: ${currentSettings.fwVer}
                Model Name: ${currentSettings.modelName}
                Video Bitrate: ${currentSettings.bitrate} bits
                Video Framerate: ${currentSettings.framerate} FPS
            """.trimIndent())
        }

        Spacer(modifier = Modifier.padding(10.dp))




        Text(responseMessage)
    }
}

*/


@Composable
fun Settings(navController: NavController, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("") }

    var showResetDialog by rememberSaveable { mutableStateOf(false) }
    var showRebootDialog by rememberSaveable { mutableStateOf(false) }
    var showPowerOffDialog by rememberSaveable { mutableStateOf(false) }



    // Refresh camera settings
    LaunchedEffect(Unit) {
        while (isActive) {
            try {
                currentSettings = getCameraSettings()
            } catch (e: Exception) {
                Log.e("CAMERA", "Failed to get settings")
            }
            delay(1000)
        }
    }

    // ---------------- CONFIRMATION DIALOGS -------------
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Reset Settings") },
            text = { Text("Are you sure you want to reset all camera settings?\nOnce reset, camera connection will need to be re-established.") },
            confirmButton = {
                Button(onClick = {
                    showResetDialog = false
                    scope.launch {
                        responseMessage = "Resetting settings..."
                        responseMessage = resetSettings()
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                Button(onClick = { showResetDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showRebootDialog) {
        AlertDialog(
            onDismissRequest = { showRebootDialog = false },
            title = { Text("Reboot Camera") },
            text = { Text("Are you sure you want to reboot the camera?") },
            confirmButton = {
                Button(onClick = {
                    showRebootDialog = false
                    scope.launch {
                        responseMessage = "Rebooting camera..."
                        responseMessage = rebootCamera()
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                Button(onClick = { showRebootDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showPowerOffDialog) {
        AlertDialog(
            onDismissRequest = { showPowerOffDialog = false },
            title = { Text("Power Off Camera") },
            text = { Text("Are you sure you want to power off the camera?") },
            confirmButton = {
                Button(onClick = {
                    showPowerOffDialog = false
                    scope.launch {
                        responseMessage = "Powering off camera..."
                        responseMessage = shutdownCamera()
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                Button(onClick = { showPowerOffDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
        // -------------- UI ---------------

        Text("Camera Settings",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )


        Spacer(modifier = Modifier.padding(6.dp))


        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {



            // ------------------ LED -------------
            Text("Status LED")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = if (currentSettings.led == 1) {
                        ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    } else {
                        ButtonDefaults.buttonColors()},
                    onClick = {
                        scope.launch {
                            responseMessage = "Turning LED ON..."
                            responseMessage = setLed(1)
                        }
                    }
                ) { Text("ON") }

                Button(
                    modifier = Modifier.weight(1f),
                    colors = if (currentSettings.led == 0) {
                        ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    } else {
                        ButtonDefaults.buttonColors()},
                    onClick = {
                        scope.launch {
                            responseMessage = "Turning LED OFF..."
                            responseMessage = setLed(0)
                        }
                    }
                ) { Text("OFF") }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            // -------------- EXPOSURE --------
            Text("Exposure Value")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in 0..4) {
                    val displayExposure = i - 2
                    val buttonColour = if (currentSettings.exposure == i) {
                        ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    } else {
                        ButtonDefaults.buttonColors()
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = buttonColour,
                        onClick = {
                            scope.launch {
                                responseMessage = "Setting exposure..."
                                responseMessage = setExposure(i)

                            }

                        }

                    ) { Text("$displayExposure") }
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            // ------------- FILTER --------------
            Text("Image Filter")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = if (currentSettings.filter == 0) {
                        ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    } else {
                        ButtonDefaults.buttonColors()},
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting filter..."
                            responseMessage = setFilter(0)
                        }
                    }
                ) { Text("Normal") }

                Button(
                    modifier = Modifier.weight(1f),
                    colors = if (currentSettings.filter == 1) {
                        ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    } else {
                        ButtonDefaults.buttonColors()},
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting filter..."
                            responseMessage = setFilter(1)
                        }
                    }
                ) { Text("Vivid") }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    colors = if (currentSettings.filter == 2) {
                        ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    } else {
                        ButtonDefaults.buttonColors()},
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting filter..."
                            responseMessage = setFilter(2)
                        }
                    }
                ) { Text("Low Light") }

                Button(
                    modifier = Modifier.weight(1f),
                    colors = if (currentSettings.filter == 3) {
                        ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    } else {
                        ButtonDefaults.buttonColors()},
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting filter..."
                            responseMessage = setFilter(3)
                        }
                    }
                ) { Text("Water") }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            // -------------- MIC SENS------------
            Text("Mic Sensitivity")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in 0..4) {
                    val buttonColour = if (currentSettings.mic == i) {
                        ButtonDefaults.buttonColors(containerColor = Color.Gray)
                    } else {
                        ButtonDefaults.buttonColors()
                    }
                    Button(
                        modifier = Modifier.weight(1f),
                        colors = buttonColour,
                        onClick = {
                            scope.launch {
                                responseMessage = "Setting mic..."
                                responseMessage = setMicSensitivity(i)
                            }
                        }
                    ) { Text("$i") }
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            // ---------------- FOV -------------
            /*
            Text("Field of View (FOV)")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting FOV..."
                            responseMessage = setFOV(140)
                        }
                    }
                ) { Text("140") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting FOV..."
                            responseMessage = setFOV(115)
                        }
                    }
                ) { Text("115") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting FOV..."
                            responseMessage = setFOV(90)
                        }
                    }
                ) { Text("90") }
            }

            Spacer(modifier = Modifier.padding(10.dp))


             */
            // ---------- THUMBNAILS --------------
            /*
            Text("Thumbnail Generation")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Enabling thumbnails..."
                            responseMessage = setThumbnail(1)
                        }
                    }
                ) { Text("ON") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Disabling thumbnails..."
                            responseMessage = setThumbnail(0)
                        }
                    }
                ) { Text("OFF") }
            }

            Spacer(modifier = Modifier.padding(10.dp))

             */

            // -------------- LANGUAGE -------------
            /*
            Text("Language")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting language..."
                            responseMessage = setLanguage(0)
                        }
                    }
                ) { Text("English") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting language..."
                            responseMessage = setLanguage(1)
                        }
                    }
                ) { Text("Chinese") }
            }

            Spacer(modifier = Modifier.padding(10.dp))

             */

            // ---------- CAMERA TIME AND DATE ----------
            Text("Camera Time")

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    scope.launch {
                        val now = Date()
                        val formatted = DateFormat.format("yyyy-MM-dd_HH:mm:ss", now).toString()

                        responseMessage = "Setting camera time..."
                        responseMessage = setCameraDateTime(formatted)
                    }
                }
            ) {
                Text("Sync Time With Phone")
            }

            Spacer(modifier = Modifier.padding(10.dp))

            // --------- VIDEO RESOLUTION ----------
            /*
            Text("Video Resolution")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting resolution..."
                            responseMessage = setVideoResolution(0)
                        }
                    }
                ) { Text("1080P") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting resolution..."
                            responseMessage = setVideoResolution(2)
                        }
                    }
                ) { Text("720P") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting resolution..."
                            responseMessage = setVideoResolution(3)
                        }
                    }
                ) { Text("WVGA") }
            }

            Spacer(modifier = Modifier.padding(10.dp))


             */
            // ----------- VIDEO FRAMERATE ---------
            /*
            Text("Video Frame Rate")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting framerate..."
                            responseMessage = setVideoFramerate(30)
                        }
                    }
                ) { Text("30 FPS") }

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            responseMessage = "Setting framerate..."
                            responseMessage = setVideoFramerate(60)
                        }
                    }
                ) { Text("60 FPS") }
            }

            Spacer(modifier = Modifier.padding(20.dp))

             */
            // ------ MISCELLANEOUS SETTINGS ------

            Text(getCameraMiscSettings())

            Spacer(modifier = Modifier.padding(12.dp))
            // ---------- POWER AND RESET --------------
            Text("Power and Reset Functions")

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showResetDialog = true }
            ) {
                Text("Reset Settings")
            }

            Spacer(modifier = Modifier.padding(4.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showRebootDialog = true }
            ) {
                Text("Reboot Camera")
            }

            Spacer(modifier = Modifier.padding(4.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showPowerOffDialog = true }
            ) {
                Text("Power Off Camera")
            }




        }
        // ------------------ RESPONSE MESSAGE ------------------
        Spacer(modifier = Modifier.padding(12.dp))

        Text(responseMessage,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center)





    }



}
