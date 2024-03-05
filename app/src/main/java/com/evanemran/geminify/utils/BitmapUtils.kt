package com.evanemran.geminify.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object BitmapUtils {
    fun compressBitmap(bitmap: Bitmap, maxImageSize: Int = 1024): Bitmap {
        val outputStream = ByteArrayOutputStream()
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        // Calculate the new dimensions while maintaining the aspect ratio
        val aspectRatio = originalWidth.toFloat() / originalHeight.toFloat()
        val newWidth = if (originalWidth > originalHeight) maxImageSize else (maxImageSize * aspectRatio).toInt()
        val newHeight = if (originalWidth > originalHeight) (maxImageSize / aspectRatio).toInt() else maxImageSize

        // Scale down the bitmap
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)

        // Compress the scaled bitmap to the output stream
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

        // Decode the compressed data back into a Bitmap
        val compressedByteArray = outputStream.toByteArray()
        return BitmapFactory.decodeByteArray(compressedByteArray, 0, compressedByteArray.size)
    }
}