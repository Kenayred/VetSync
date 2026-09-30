package com.example.vetsync.modelo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream

object ImagenUtils {

    // Convierte la imagen seleccionada de la galería (Uri) a un String Base64 ligero
    fun uriABase64(context: Context, uri: Uri): String {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmapOriginal = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmapOriginal == null) return ""

            val maxDimension = 500
            val ratio = minOf(
                maxDimension.toFloat() / bitmapOriginal.width,
                maxDimension.toFloat() / bitmapOriginal.height,
                1f
            )
            val anchoNuevo = (bitmapOriginal.width * ratio).toInt()
            val altoNuevo = (bitmapOriginal.height * ratio).toInt()

            val bitmapRedimensionado = Bitmap.createScaledBitmap(bitmapOriginal, anchoNuevo, altoNuevo, true)

            val outputStream = ByteArrayOutputStream()
            bitmapRedimensionado.compress(Bitmap.CompressFormat.JPEG, 75, outputStream)
            val bytes = outputStream.toByteArray()

            Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    // Convierte el String Base64 de Firebase
    fun base64ABitmap(base64Str: String): ImageBitmap? {
        if (base64Str.isBlank()) return null
        return try {
            val bytes = Base64.decode(base64Str, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
            bitmap?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }
}