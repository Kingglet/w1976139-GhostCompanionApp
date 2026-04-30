package com.example.ghostcompanionapp

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket

suspend fun cameraListener(timeoutMs: Int = 10000): CameraInfo? {
    return withContext(Dispatchers.IO) {
        try {
            val socket = DatagramSocket(5555)
            socket.soTimeout = timeoutMs

            val buffer = ByteArray(1024)
            val packet = DatagramPacket(buffer, buffer.size)

            socket.receive(packet)

            val senderIp = packet.address.hostAddress
            val message = String(packet.data, 0, packet.length)

            Log.d("CAMERA_UDP", "Received: $message from $senderIp")

            val parts = message.split("|")

            if (parts.size >= 5 && parts[0] == "5") {
                val serial = parts[1]
                val model = parts[2]
                val status = parts[4]

                socket.close()
                return@withContext CameraInfo(senderIp, serial, model, status)
            }

            socket.close()
            null
        } catch (e: Exception) {
            Log.e("CAMERA_UDP", "UDP failed: ${e.message}")
            null
        }
    }
}