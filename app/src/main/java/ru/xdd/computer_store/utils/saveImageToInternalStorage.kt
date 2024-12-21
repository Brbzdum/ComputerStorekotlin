package ru.xdd.computer_store.utils

import android.content.Context
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun saveImageToInternalStorage(context: Context, imageBytes: ByteArray?, productId: Long): String {
    if (imageBytes == null) return "" // Обработка null

    val directory = File(context.filesDir, "product_images")
    if (!directory.exists()) {
        directory.mkdirs()
    }

    val file = File(directory, "product_$productId.jpg")
    try {
        FileOutputStream(file).use { outputStream ->
            outputStream.write(imageBytes)
        }
        return "product_images/product_$productId.jpg"
    } catch (e: IOException) { // Используем IOException
        e.printStackTrace()
    }
    return "" // Возвращаем пустую строку в случае ошибки
}