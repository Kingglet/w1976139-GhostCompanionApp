package com.example.ghostcompanionapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings.ACTION_WIFI_SETTINGS
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.example.ghostcompanionapp.ui.theme.GhostCompanionAppTheme
import kotlinx.coroutines.delay
import kotlin.text.trimIndent


lateinit var currentCameraSettings: CameraSettings
lateinit var pendingCameraSettings: CameraSettings


lateinit var currentSettings: CameraSettings




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GhostCompanionAppTheme {

                // sets up navigation controller
                val navController = rememberNavController()


                // defines navigation for each page
                NavHost(navController, "startPage") {

                    composable("startPage"){
                        StartPage(navController)
                    }

                    composable("mainMenu") {
                        MainMenu(navController, context = LocalContext.current)
                    }

                    composable("cameraView"){
                        CameraView(navController)
                    }

                    composable("placeholder") {
                        Placeholder(navController)
                    }

                    composable("settings"){
                        Settings(navController)
                    }



                }


                /*
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ){
                    Greeting(
                        "Android"
                    )
                }
                */

            }
        }
    }
}

@Composable
fun StartPage(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Drift Ghost Companion App",
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                modifier = Modifier.weight(1f).width(300.dp),
                onClick = {
                    navController.navigate("mainMenu")

                }) {
                Text("Begin")
            }
        }
    }
}

// gui for the first screen the user is shown
@Composable
fun MainMenu(navController: NavController, modifier: Modifier = Modifier, context: Context) {


    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("") }

    var connectToCameraState by rememberSaveable { mutableStateOf(true) }
    var checkConnectionState by rememberSaveable { mutableStateOf(true) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ){
    //Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {


        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {


            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    checkConnectionState = false
                    connectToCameraState = false
                    scope.launch {
                        responseMessage = "Checking Connection..."
                        currentSettings = getCameraSettings()
                        if (currentSettings.status == 1) {
                            navController.navigate("cameraView")
                        } else {
                            responseMessage = "Camera Not Connected"
                        }
                        checkConnectionState = true
                        connectToCameraState = true
                    }

                },
                enabled = connectToCameraState
            )
            {
                Text("Connect to Camera")
            }
        }
        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    checkConnectionState = false
                    connectToCameraState = false
                    scope.launch {
                        responseMessage = "Checking connection..."
                        responseMessage = getCameraStatus()
                        checkConnectionState = true
                        connectToCameraState = true
                    }
                },
                enabled = checkConnectionState)
            {
                Text("Check Connection")
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))

        /*
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("settings")
                }) {
                Text("Camera Settings")
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))

         */

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    //navController.navigate("placeholder")

                    val i = Intent(ACTION_WIFI_SETTINGS)
                    context.startActivity(i)
                }) {
                Text("Go To Wi-Fi Settings")
            }
        }


        Spacer(modifier = Modifier.padding(10.dp))

        // displays confirmation messages for to the user after interacting with a button
        Row {
            Text(text = responseMessage)
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun CameraView(navController: NavController, modifier: Modifier = Modifier){
    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("")}
    var buttonText by rememberSaveable { mutableStateOf ("")}
    var videoModeState by rememberSaveable { mutableStateOf(false) }
    var photoModeState by rememberSaveable { mutableStateOf(false) }
    var timelapseModeState by rememberSaveable { mutableStateOf(false) }
    var burstModeState by rememberSaveable { mutableStateOf(false) }
    var settingsButtonState by rememberSaveable { mutableStateOf(false) }
    var isRecording by rememberSaveable { mutableStateOf(false) }



    when (currentSettings.captureMode) {
        0 -> {videoModeState = false; responseMessage = "Video Mode Selected"}
        1 -> {photoModeState = false; responseMessage = "Photo Mode Selected"}
        2 -> {timelapseModeState = false; responseMessage = "Timelapse Mode Selected"}
        3 -> {burstModeState = false; responseMessage = "Burst Mode Selected"}
        4 -> {settingsButtonState = false; responseMessage = "Changing Settings in Camera"}
    }


    LaunchedEffect(Unit) {
        while(true){
            try {
                currentSettings = getCameraSettings()


                if (currentSettings.recTime != 0 || currentSettings.captureMode == 4)   {
                    videoModeState = false
                    photoModeState = false
                    timelapseModeState = false
                    burstModeState = false
                    settingsButtonState = false
                    isRecording = true
                } else {

                    videoModeState = true
                    photoModeState = true
                    timelapseModeState = true
                    burstModeState = true
                    settingsButtonState = true
                    isRecording = false

                    when (currentSettings.captureMode) {
                        0 -> {videoModeState = false; responseMessage = "Video Mode Selected"}
                        1 -> {photoModeState = false; responseMessage = "Photo Mode Selected"}
                        2 -> {timelapseModeState = false; responseMessage = "Timelapse Mode Selected"}
                        3 -> {burstModeState = false; responseMessage = "Burst Mode Selected"}
                        4 -> {settingsButtonState = false; responseMessage = "Changing Settings in Camera"}
                    }
                }



            }   catch (e: Exception) {
                Log.e("CAMERA", "Failed to get settings")
            }

            delay(100)
        }
    }







    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Battery: ${currentSettings.battery}% | Storage: ${getStoragePercent(currentSettings.sdTotal, currentSettings.sdFree)}%")
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {



            RtspLiveView(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }

        Spacer(modifier = Modifier.padding(10.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    /*
                    scope.launch {
                        responseMessage = "Switching to Video Mode..."
                        val response = switchToVideoMode()
                        if (response == "Video"){
                            currentSettings = getCameraSettings()
                            responseMessage = "Switched to Video Mode"

                        } else {
                            responseMessage = response
                        }
                    }
                     */
                },
                enabled = videoModeState
            ) {
                Text("Video")
            }


            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    /*
                    scope.launch {
                        responseMessage = "Switching to Video Mode..."
                        val response = switchToPhotoMode()
                        if (response == "Photo"){
                            currentSettings = getCameraSettings()
                            responseMessage = "Switched to Photo Mode"

                        } else {
                            responseMessage = response
                        }
                    }
                     */
                },
                enabled = photoModeState
            ) {
                Text("Photo")
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    /*
                    scope.launch {
                        responseMessage = "Switching to Timelapse Mode..."
                        val response = switchToTimelapseMode()
                        if (response == "Timelapse"){
                            currentSettings = getCameraSettings()
                            responseMessage = "Switched to Timelapse Mode"

                        } else {
                            responseMessage = response
                        }
                    }
                    */
                },
                enabled = timelapseModeState
            ) {
                Text("Timelapse")
            }


            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    /*
                    scope.launch {
                        responseMessage = "Switching to Burst Mode..."
                        val response = switchToBurstMode()
                        if (response == "Burst"){
                            currentSettings = getCameraSettings()
                            responseMessage = "Switched to Burst Mode"

                        } else {
                            responseMessage = response
                        }
                    }
                    */
                },
                enabled = burstModeState
            ) {
                Text("Burst")
            }
        }



        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        currentSettings = getCameraSettings()
                        currentCameraSettings = currentSettings
                        pendingCameraSettings = currentCameraSettings
                        navController.navigate("settings")
                    }
                },
                enabled = settingsButtonState)
            {
                Text("Settings")
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(16.dp),
                onClick = {
                    scope.launch {
                        if (isRecording == true) {
                            responseMessage = stopRecording()
                            isRecording = false
                        } else {

                            when (currentSettings.captureMode){
                                0 -> responseMessage = startRecording()
                                1 -> responseMessage = takePhoto()
                                2 -> responseMessage = takePhoto()
                                3 -> responseMessage = takePhoto()
                            }
                        }
                    }
                },
                enabled = settingsButtonState || (isRecording && currentSettings.captureMode == 0)
            ) {

                if (isRecording != false) {
                    buttonText = "Stop Recording"
                } else {

                    when (currentSettings.captureMode){
                        0 -> buttonText = "Start Recording"
                        1 -> buttonText = "Take Photo"
                        2 -> buttonText = "Start Timelapse"
                        3 -> buttonText = "Take Burst"
                    }
                }


                Text(buttonText)
            }

        }

        Spacer(modifier = Modifier.padding(10.dp))

        // displays confirmation messages for to the user after interacting with a button
        Row {
            Text(text = responseMessage)
        }
    }
}

@Composable
fun Settings(navController: NavController, modifier: Modifier = Modifier){

    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while(true){
            try {
                currentSettings = getCameraSettings()

            }   catch (e: Exception) {
                Log.e("CAMERA", "Failed to get settings")
            }

            delay(100)
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



@Composable
fun Template(navController: NavController, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("") }

    /*Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ){
    */

    Surface(color = Color.Cyan, modifier = modifier.fillMaxSize()){

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Spacer(modifier = Modifier.padding(6.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {


                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {}
                ) {
                    Text("Blank Button")
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Text(responseMessage)
        }
    }
}


@Composable
fun Placeholder(navController: NavController, modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {
        Column{
            Text(
                text = "Placeholder",
                modifier = Modifier.padding(24.dp)
            )

            Button(
                modifier = Modifier.weight(1f),
                onClick = {}
            ) {
                Text("No Function")
            }
        }}

}


@Preview(showBackground = true)
@Composable
fun StartPreview() {
    GhostCompanionAppTheme {
        StartPage(rememberNavController())
    }
}