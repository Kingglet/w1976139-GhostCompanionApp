package com.example.ghostcompanionapp

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import kotlinx.coroutines.isActive
import com.example.ghostcompanionapp.ui.theme.GhostCompanionAppTheme
import kotlinx.coroutines.delay
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.unit.sp
import kotlin.text.trimIndent


lateinit var currentCameraSettings: CameraSettings
lateinit var pendingCameraSettings: CameraSettings


//lateinit var currentSettings: CameraSettings

var currentSettings by mutableStateOf(
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
        hdRecord = 0))




class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GhostCompanionAppTheme {

                // sets up navigation controller
                val navController = rememberNavController()


                Column(modifier = Modifier.fillMaxSize().systemBarsPadding()){
                    // defines navigation for each page
                    NavHost(navController, "startPage") {

                        composable("startPage"){
                            StartPage(navController)
                        }

                        composable("mainMenu") {
                            MainMenu(navController, context = LocalContext.current)
                        }

                        composable("startLivestream"){
                            Livestream(navController)
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

                        composable("fileViewer"){
                            FileViewer(navController)
                        }

                        composable(
                            route = "videoPlayer/{url}",
                            arguments = listOf(navArgument("url") {type = NavType.StringType})
                        ) {backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url") ?: ""
                            VideoPlayerScreen(navController, url)
                        }
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

        Text(
            text = "Drift Ghost Companion App",
            modifier = Modifier.padding(8.dp),
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )


        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = "Remotely Control Your Drift Camera and Livestream to the World!",
            modifier = Modifier.padding(16.dp),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )


        Spacer(modifier = Modifier.padding(4.dp))


        Button(
            modifier = Modifier.padding(8.dp).width(260.dp).height(50.dp),
            onClick = {
                navController.navigate("mainMenu")

            }) {
            Text(text = "Begin",
                fontSize = 18.sp)

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
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Main Menu",
            modifier = Modifier.padding(8.dp).fillMaxWidth(),
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

    Spacer(modifier = Modifier.padding(4.dp))

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState())
    ){

        Text(
            text = "Connect to your camera's Wi-Fi via AP Mode to control your camera...",
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(4.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    checkConnectionState = false
                    connectToCameraState = false
                    scope.launch {
                        responseMessage = "Checking Connection..."
                        currentSettings = getCameraSettings()
                        if (currentSettings.status == 1) {
                            responseMessage = "Camera Connected!"
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

        Spacer(modifier = Modifier.padding(4.dp))

        Text(
            text = "Or start a Livestream via Station Mode!",
            modifier = Modifier.padding(8.dp),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.padding(4.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    navController.navigate("startLivestream")
                }
            )
            {
                Text("Start Livestream")
            }

        Spacer(modifier = Modifier.padding(16.dp))

            Text(
                text = "Enures your camera is connected to the same Wi-Fi as this device OR ensure this device is connected to the camera's Wi-Fi",
                modifier = Modifier.padding(8.dp),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                modifier = Modifier.fillMaxWidth(),
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

            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    //navController.navigate("placeholder")

                    val i = Intent(ACTION_WIFI_SETTINGS)
                    context.startActivity(i)
                }) {
                Text("Go To Wi-Fi Settings")
            }


        Spacer(modifier = Modifier.padding(10.dp))

        // displays confirmation messages for to the user after interacting with a button
            Text(text = responseMessage,
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
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
    var previewEnabled by rememberSaveable { mutableStateOf(false) }




    when (currentSettings.captureMode) {
        0 -> {videoModeState = false; responseMessage = "Video Mode Selected"}
        1 -> {photoModeState = false; responseMessage = "Photo Mode Selected"}
        2 -> {timelapseModeState = false; responseMessage = "Timelapse Mode Selected"}
        3 -> {burstModeState = false; responseMessage = "Burst Mode Selected"}
        4 -> {settingsButtonState = false; responseMessage = "Changing Settings in Camera"}
    }

    LaunchedEffect(Unit) {
        while(isActive) {
            try {
                currentSettings = getCameraSettings()

                isRecording = currentSettings.recTime != 0

                responseMessage = getCaptureModeText(currentSettings.captureMode)

                settingsButtonState = currentSettings.captureMode != 4 && !isRecording
            } catch (e: Exception) {
                Log.e("CAMERA", "Failed to get settings")
            }
            delay(2000)
        }
    }

    /*
    LaunchedEffect(Unit) {
        while(isActive){
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

            delay(2000)
        }
    }

     */






    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally) {


        Text(
            text = "Camera Preview and Control\nBattery: ${currentSettings.battery}% | Storage: " +
                    "${getStoragePercent(currentSettings.sdTotal, currentSettings.sdFree)}%",
            modifier = Modifier
                .fillMaxWidth(),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.padding(6.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally

        ) {

            /*
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(text = "Battery: ${currentSettings.battery}% | Storage: ${getStoragePercent(currentSettings.sdTotal, currentSettings.sdFree)}%",
                modifier = Modifier.padding(4.dp),
                textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.padding(10.dp))


         */
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),

                ) {


                if (previewEnabled) {
                    RtspLiveView(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    Text(
                        text = "Live Preview Off",
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        previewEnabled = !previewEnabled
                    }
                ) {
                    Text(
                        if (previewEnabled) "Diable Live Preview"
                        else "Enable Live Preview"
                    )
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "Capture Mode: ${getCaptureModeText(currentSettings.captureMode)}")
            }

            /*
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Button(
                modifier = Modifier.weight(1f),
                onClick = {

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

                },
                enabled = videoModeState
            ) {
                Text("Video")
            }


            Button(
                modifier = Modifier.weight(1f),
                onClick = {

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

                },
                enabled = timelapseModeState
            ) {
                Text("Timelapse")
            }


            Button(
                modifier = Modifier.weight(1f),
                onClick = {

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

                },
                enabled = burstModeState
            ) {
                Text("Burst")
            }
        }



        Spacer(modifier = Modifier.padding(4.dp))

         */

            Spacer(modifier = Modifier.padding(10.dp))


            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        scope.launch {
                            if (isRecording == true) {
                                responseMessage = stopRecording()
                                isRecording = false
                            } else {

                                when (currentSettings.captureMode) {
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

                        when (currentSettings.captureMode) {
                            0 -> buttonText = "Start Recording"
                            1 -> buttonText = "Take Photo"
                            2 -> buttonText = "Start Timelapse"
                            3 -> buttonText = "Take Burst"
                        }
                    }


                    Text(buttonText)
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
                        scope.launch {
                            currentSettings = getCameraSettings()
                            currentCameraSettings = currentSettings
                            pendingCameraSettings = currentCameraSettings
                            navController.navigate("settings")
                        }
                    },
                    enabled = settingsButtonState
                )
                {
                    Text("Settings")
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
                        navController.navigate("fileViewer")
                    }
                ) {
                    Text("View Files on SD Card")
                }
            }

            Spacer(modifier = Modifier.padding(10.dp))

            // displays confirmation messages for to the user after interacting with a button

        }
        Row {
            Text(text = responseMessage)
        }
    }
}

/*
@Composable
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
fun FileViewer(navController: NavController, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("Loading files...") }

    var showDeletionDialog by rememberSaveable { mutableStateOf(false) }
    var fileToDelete by rememberSaveable { mutableStateOf<CameraFile?>(null) }

    var showDeleteThumbnailDialog by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    var fileList by rememberSaveable { mutableStateOf<List<CameraFile>>(emptyList()) }


    LaunchedEffect(Unit) {
        try {
            fileList = listFiles()
            responseMessage = "Files found: ${fileList.size}"

        }
        catch (e: Exception) {
            responseMessage = "Failed to load files"
        }
    }


    if (showDeletionDialog && fileToDelete != null){
        AlertDialog(
            onDismissRequest = { showDeletionDialog = false },
            title = { Text("Delete File") },
            text = { Text("Are you sure you want to delete:\n${fileToDelete!!.fileName}?") },
            confirmButton = {
                Button(onClick = {
                    showDeletionDialog = false
                    scope.launch{
                        responseMessage = "Deleting ${fileToDelete!!.fileName}..."

                        val deleted = deleteFile(fileToDelete!!.filePath)

                        if (deleted == true) {
                            fileList = fileList.filter { it.filePath != fileToDelete!!.filePath }
                            responseMessage = "Deleted ${fileToDelete!!.fileName}"
                        } else {
                            responseMessage = "Deletion Failed"
                        }

                        fileToDelete = null

                    }
                }) {Text("Yes")}
            },
            dismissButton = {
                Button(onClick = {
                    showDeletionDialog = false
                    fileToDelete = null
                }) { Text("Cancel")}
            }
        )
    }

    if (showDeleteThumbnailDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteThumbnailDialog = false},
            title = { Text("Delete All Thumbnail Files") },
            text = { Text("Are you sure you want to delete all thumbnail files?\nDoing this helps to remove unnecessary media files and saves space.\n(Thumbnail files are smaller, lower quality version of recorded footage and follow the format of VIDxxxxx_thm.MP4)") },
            confirmButton = {
                Button(onClick = {
                    showDeleteThumbnailDialog = false

                    scope.launch {
                        responseMessage = "Deleting thumbnail files..."
                        val result = deleteThumbnailFiles(fileList)
                        responseMessage = result

                        if (result.startsWith("Deleted")) {
                            fileList = fileList.filter {
                                !(it.fileName.startsWith("VID") && it.fileName.endsWith("_thm.MP4"))
                            }
                        }
                    }

                }) {
                    Text("Yes")
                }
            }, dismissButton = {
                Button(onClick = { showDeleteThumbnailDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }


    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
            .padding(12.dp)
    ) {
        Text(responseMessage)

        Spacer(modifier = Modifier.padding(8.dp))


        LazyColumn (modifier = Modifier.weight(1f)){
            items(fileList) { file ->

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (file.fileType == "MP4"){
                        Button(
                            onClick = {
                                val videoUrl = "http://$ip/DCIM/${file.filePath}"

                                val encodedUrl = Uri.encode(videoUrl)

                                navController.navigate("videoPlayer/$encodedUrl")
                            }
                        ){
                            Text("Play")
                        }
                    } else {

                        Button(
                            onClick = {
                                val photoUrl = "http://$ip/DCIM/${file.filePath}"

                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(photoUrl))
                                context.startActivity(intent)
                            },enabled = file.fileType == "JPG"
                        ) {
                            Text("View")
                        }
                    }


                    Text(text = file.fileName,
                        modifier = Modifier.weight(1f)
                    )

                    Button(
                        onClick = {
                            fileToDelete = file
                            showDeletionDialog = true
                        }
                    ) {
                        Text("Delete File")
                    }

                }
                Spacer(modifier = Modifier.padding(6.dp))
            }
        }

        Spacer(modifier = Modifier.padding(6.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                showDeleteThumbnailDialog = true
            }
        ) {
            Text("Delete All Thumbnail Files")
        }

        Spacer(modifier = Modifier.padding(6.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                scope.launch {
                    try {
                        fileList = listFiles()
                        responseMessage = "Files found: ${fileList.size}"

                    } catch (e: Exception) {
                        responseMessage = "Failed to load files"
                    }
                }
            }
        ) {
            Text("Refresh Files from Camera SD Card")
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoPlayerScreen(navController: NavController, videoUrl: String, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("") }

    val context = LocalContext.current
    val decodedUrl = Uri.decode(videoUrl)

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            //setMediaItem(MediaItem.fromUri(videoUrl.toUri()))
            setMediaItem(MediaItem.fromUri(decodedUrl.toUri()))
            prepare()
            playWhenReady = true
        }

    }

    DisposableEffect(Unit) {
        onDispose { exoPlayer.release()}
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {

        AndroidView(
            modifier = Modifier.fillMaxWidth().weight(1f),
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = true
                }
            }
        )

        Spacer(modifier = Modifier.padding(10.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { navController.popBackStack() }
        ) {
            Text("Back")
        }

    }

    Spacer(modifier = Modifier.padding(10.dp))

    Text(responseMessage)

}

@Composable
fun Template(navController: NavController, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("") }


    //Surface(color = Color.Cyan, modifier = modifier.fillMaxSize()){

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize().padding(12.dp)
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
    //}
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