package com.example.ghostcompanionapp

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

fun generateQRBitmap(text: String): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 600, 600)

    val width = bitMatrix.width
    val height = bitMatrix.height
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

    for (x in 0 until width) {
        for (y in 0 until height) {
            bmp.setPixel(
                x,
                y,
                if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            )
        }
    }

    return bmp
}