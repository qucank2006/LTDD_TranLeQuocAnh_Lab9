package com.example.bluromaticapp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.work.Configuration
import androidx.work.WorkInfo
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.bluromaticapp.data.WorkManagerBluromaticRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WorkManagerBluromaticRepositoryTest {

    private lateinit var context: Context
    private lateinit var repository: WorkManagerBluromaticRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.DEBUG)
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
        repository = WorkManagerBluromaticRepository(context)
    }

    @Test
    fun applyBlur_enqueuesWork() = runBlocking {
        repository.applyBlur(1)
        
        // Check if work is enqueued
        val workInfo = repository.outputWorkInfo.first()
        assertNotNull(workInfo)
        // Since it's a new unique work with REPLACE, it should be ENQUEUED or RUNNING
        assertTrue(
            workInfo!!.state == WorkInfo.State.ENQUEUED || 
            workInfo.state == WorkInfo.State.RUNNING ||
            workInfo.state == WorkInfo.State.BLOCKED
        )
    }
    
    // Helper to use assertTrue in runBlocking
    private fun assertTrue(condition: Boolean) {
        org.junit.Assert.assertTrue(condition)
    }
}
