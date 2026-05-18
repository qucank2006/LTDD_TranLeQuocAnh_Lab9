package com.example.bluromaticapp.data

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.bluromaticapp.IMAGE_MANIPULATION_WORK_NAME
import com.example.bluromaticapp.KEY_BLUR_LEVEL
import com.example.bluromaticapp.KEY_IMAGE_URI
import com.example.bluromaticapp.TAG_OUTPUT
import com.example.bluromaticapp.workers.BlurWorker
import com.example.bluromaticapp.workers.CleanupWorker
import com.example.bluromaticapp.workers.SaveImageToFileWorker
import com.example.bluromaticapp.workers.getImageUri
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class WorkManagerBluromaticRepository(context: Context) : BluromaticRepository {
    private val workManager = WorkManager.getInstance(context)
    private val imageUri = context.getImageUri()

    override val outputWorkInfo: Flow<WorkInfo?> =
        workManager.getWorkInfosByTagFlow(TAG_OUTPUT)
            .mapNotNull { if (it.isNotEmpty()) it[0] else null }

    override fun applyBlur(blurLevel: Int) {
        var continuation = workManager
            .beginUniqueWork(
                IMAGE_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<CleanupWorker>().build()
            )

        val blurBuilder = OneTimeWorkRequestBuilder<BlurWorker>()
        blurBuilder.setInputData(createInputDataForWorkRequest(blurLevel, imageUri))

        continuation = continuation.then(blurBuilder.build())

        val saveBuilder = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .addTag(TAG_OUTPUT)
            .build()
        
        continuation = continuation.then(saveBuilder)
        continuation.enqueue()
    }

    override fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }

    private fun createInputDataForWorkRequest(blurLevel: Int, imageUri: android.net.Uri): Data {
        val builder = Data.Builder()
        builder.putString(KEY_IMAGE_URI, imageUri.toString())
        builder.putInt(KEY_BLUR_LEVEL, blurLevel)
        return builder.build()
    }
}
