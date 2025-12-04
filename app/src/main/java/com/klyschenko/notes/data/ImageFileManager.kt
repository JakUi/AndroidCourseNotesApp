package com.klyschenko.notes.data

import android.content.Context
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class ImageFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val imagesDir: File = context.filesDir // получаем путь к папке во внутреннем хранилище

    suspend fun copyImageToInternalStorage(url: String): String {
        val fileName = "IMG_${UUID.randomUUID()}.jpg" // UUID.randomUUID() генерирует уникальный ID
        val file = File(imagesDir, fileName)

//        // Записываем изображение в файл:
//        val inputStream = context.contentResolver.openInputStream(url.toUri()) // создаём поток ввода
//        val outputStream = file.outputStream() // создаём поток вывода
//
//        inputStream?.copyTo(outputStream) // копируем данные из потока ввода в поток вывода (из файла в файл)
//        // закрываем потоки (это обязательно)
//        inputStream?.close()
//        outputStream.close()

        withContext(Dispatchers.IO) {
            // Чтобы не писать обработку ошибок код выше можно переписать используя .use:
            // под капотом .use оборачивает код в блок try catch и закрывает потоки в finally
            context.contentResolver.openInputStream(url.toUri()).use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream?.copyTo(outputStream)
                }
            }
        }

        return file.absolutePath
    }

    suspend fun deleteImage(url: String) {
        withContext(Dispatchers.IO) {
            val file = File(url)
            if (file.exists() && isInternal(file.absolutePath)) file.delete()
        }
    }

    fun isInternal(url: String): Boolean {
        return url.startsWith(imagesDir.absolutePath)
    }
}
