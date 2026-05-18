package com.example.bluromaticapp.workers

import android.content.ContentValues
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bluromaticapp.KEY_IMAGE_URI
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveImageToFileWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    private val title = "Blurred Image"
    private val dateFormatter = SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault())

    override suspend fun doWork(): Result {
        val resolver = applicationContext.contentResolver
        return try {
            val resourceUri = inputData.getString(KEY_IMAGE_URI)
            val bitmap = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )

            // Modern replacement for MediaStore.Images.Media.insertImage
            val imageUrl = saveImageToMediaStore(bitmap)

            if (imageUrl != null) {
                val outputData = workDataOf(KEY_IMAGE_URI to imageUrl.toString())
                Result.success(outputData)
            } else {
                Result.failure()
            }
        } catch (exception: Exception) {
            Result.failure()
        }
    }

    private fun saveImageToMediaStore(bitmap: android.graphics.Bitmap): Uri? {
        val resolver = applicationContext.contentResolver
        val name = "blur-filter-output-${dateFormatter.format(Date())}.png"
        
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/Bluromatic")
                put(MediaStore.MediaColumns.IS_PENDING, 1)
            }
        }

        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        
        imageUri?.let { uri ->
            resolver.openOutputStream(uri)?.use { outputStream ->
                bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                contentValues.clear()
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0)
                resolver.update(uri, contentValues, null, null)
            }
        }
        return imageUri
    }
}
