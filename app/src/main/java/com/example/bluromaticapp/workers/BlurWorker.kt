package com.example.bluromaticapp.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.bluromaticapp.KEY_BLUR_LEVEL
import com.example.bluromaticapp.KEY_IMAGE_URI

class BlurWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        val blurLevel = inputData.getInt(KEY_BLUR_LEVEL, 1)

        return try {
            if (resourceUri.isNullOrEmpty()) {
                throw IllegalArgumentException("Invalid input uri")
            }

            val resolver = applicationContext.contentResolver

            val bitmap = BitmapFactory.decodeStream(
                resolver.openInputStream(Uri.parse(resourceUri))
            )

            val output = blurBitmap(bitmap, blurLevel)

            // Write bitmap to a temp file
            val outputUri = writeBitmapToFile(applicationContext, output)

            val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())

            Result.success(outputData)
        } catch (throwable: Throwable) {
            Result.failure()
        }
    }
}
