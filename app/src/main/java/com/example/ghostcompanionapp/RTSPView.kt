package com.example.ghostcompanionapp

import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.core.net.toUri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.media3.common.Player
import androidx.media3.common.PlaybackException
import androidx.media3.ui.AspectRatioFrameLayout


@androidx.media3.common.util.UnstableApi
@Composable
fun RtspLiveView(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val rtspUrl = "rtsp://192.168.42.1/live"

    var isConnecting by rememberSaveable { mutableStateOf(true)}

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {

            addListener(object : Player.Listener {

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    isConnecting = !isPlaying
                }

                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_READY) {
                        isConnecting = false
                    }
                }


                override fun onPlayerError(error: PlaybackException) {
                    isConnecting = true
                    Log.e("RTSP", "Playback error: ${error.message}", error)
                }
            })



            //val mediaItem = MediaItem.fromUri(rtspUrl.toUri())
            //setMediaItem(mediaItem)

            setMediaItem(MediaItem.fromUri(rtspUrl.toUri()))
            prepare()
            playWhenReady = true
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }


    Box(
        modifier = modifier.fillMaxWidth()
            .aspectRatio(16f/9f)
    ){
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            }
        )

        if (isConnecting) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text("Connecting to Camera...")
            }
        }
    }

}