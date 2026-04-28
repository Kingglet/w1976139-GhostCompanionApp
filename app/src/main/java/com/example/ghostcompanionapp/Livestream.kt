package com.example.ghostcompanionapp

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun Livestream(navController: NavController, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    var responseMessage by rememberSaveable { mutableStateOf("") }

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


}