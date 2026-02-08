package com.example.ghostcompanionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.*
import kotlinx.coroutines.launch
import com.example.ghostcompanionapp.ui.theme.GhostCompanionAppTheme


lateinit var currentCameraSettings: CameraSettings
lateinit var pendingCameraSettings: CameraSettings
lateinit var currentVideoMode: String




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
                        MainMenu(navController)
                    }

                    composable("cameraView"){
                        CameraView(navController)
                    }

                    composable("placehold") {
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
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Weclome!",
                modifier = Modifier.padding(24.dp)
            )
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    navController.navigate("mainMenu")

                }) {
                Text("Begin")
            }
        }
    }
}

// gui for the first screen the user is showm
@Composable
fun MainMenu(navController: NavController, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("") }

    var connectToCameraState by rememberSaveable { mutableStateOf(true) }
    var checkConnectionState by rememberSaveable { mutableStateOf(true) }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ){
    //Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxSize()) {


        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            // to add sample movies to database
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    checkConnectionState = false
                    connectToCameraState = false
                    scope.launch {
                        responseMessage = "Checking Connection..."
                        if (checkConnection()) {
                            navController.navigate("cameraView")
                        } else {
                            responseMessage = "Camera Not Connected"
                        }
                        checkConnectionState = true
                        connectToCameraState = true
                    }

                },
                enabled = connectToCameraState)
            {
                Text("Connect to Camera")
            }


            // button to navigate to movie search screen
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

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // button to navigate to matching movie search screen
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("settings")
                }) {
                Text("Camera Settings")
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // button to navigate to matching movie search screen
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("placeholder")
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

@Composable
fun CameraView(navController: NavController, modifier: Modifier = Modifier){
    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("")}



    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
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
                            currentVideoMode = response
                            responseMessage = "Switched to Video Mode"

                        } else {
                            responseMessage = response
                        }
                    }
                }) {
                Text("Video")
            }


            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    scope.launch {
                        responseMessage = "Switching to Video Mode..."
                        val response = switchToPhotoMode()
                        if (response == "Photo"){
                            currentVideoMode = response
                            responseMessage = "Switched to Photo Mode"

                        } else {
                            responseMessage = response
                        }
                    }
                }) {
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
                        navController.navigate("settings")
                    }
                }) {
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
                        when (currentVideoMode){
                            "Video" -> responseMessage = startRecording()
                            "Photo" -> responseMessage = takePhoto()
                            else -> responseMessage = "Button Error"

                        }
                    }
                }) {
                Text("Start Recording")
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



    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
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
                            responseMessage = getCameraStatus()
                    }}
                ) {
                    Text("Check Connection")
                }
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
        modifier = Modifier.fillMaxSize()
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