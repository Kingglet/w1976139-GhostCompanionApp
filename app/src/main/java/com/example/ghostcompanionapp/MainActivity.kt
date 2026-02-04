package com.example.ghostcompanionapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.DatagramPacket
import java.net.DatagramSocket
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import com.example.ghostcompanionapp.ui.theme.GhostCompanionAppTheme


lateinit var cameraStatus: CameraStatus
//lateinit var cameraAPI: CameraAPI



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
            horizontalArrangement = Arrangement.spacedBy(8.dp),
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
        ){
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("placehold")

                }) {
                Text("Start Recording")
            }


            // button to navigate to movie search screen
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("placehold")
                }) {
                Text("Stop Recording")
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {

            // to add sample movies to datatbase
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("placehold")

                }) {
                Text("Connect to Camera")
            }


            // button to navigate to movie search screen
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("placehold")
                }) {
                Text("Check Connection")
            }
        }

        Spacer(modifier = Modifier.padding(4.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // button to navigate to actor search screen
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("placehold")
                }) {
                Text("blank")
            }

            // button to navigate to matching movie search screen
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    navController.navigate("settings")
                }) {
                Text("Settings")
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

suspend fun findCameraIP(connectionTimeout: Int = 5000): String? = withContext(Dispatchers.IO){
    try{
        val socket = DatagramSocket(12345).apply{
            soTimeout = connectionTimeout
        }

        val buffer = ByteArray(1024)
        val packet = DatagramPacket(buffer, buffer.size)

        socket.receive(packet)

        val received = String(packet.data, 0, packet.length)
        socket.close()

        Regex("""\b(?:\d{1,3}\.){3}\d{1,3}\b""").find(received)?.value
    }
    catch (e: Exception) {
        null
    }
}

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

/*
fun parseCameraStatus(xml: String): String{

    val battery = Regex("<battery>(.*?)</battery>").find(xml)?.groupValues?.get(1)

    val recording = Regex("<recording>(.*?)</recording>").find(xml)?.groupValues?.get(1)

    return """
        Battery: ${battery ?: "?"}%
        Recording: ${if (recording == "1") "Yes" else "No"}
    """.trimIndent()
}
*/


fun parseCameraStatus(xml: String): CameraStatus{
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

    return CameraStatus(
        battery = battery,
        recording = recording == "1",
        mode = mode
    )

}


suspend fun getCameraStatus(): String {
    val ip = findCameraIP() ?: "192.168.42.1"

    return try {
        val response = httpGetter("http://$ip/cgi-bin/foream_remote_control?get_camera_status")

        val status = parseCameraStatus(response)

        """
        Battery: ${status.battery}%
        Recording: ${if (status.recording) "Yes" else "No"}
        Mode: ${status.mode}
        """.trimIndent()

    }
    catch (e: Exception){
        "Connection error"
    }
}


/*
suspend fun getCameraStatus(): String {

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

@Preview(showBackground = true)
@Composable
fun StartPreview() {
    GhostCompanionAppTheme {
        StartPage(rememberNavController())
    }
}