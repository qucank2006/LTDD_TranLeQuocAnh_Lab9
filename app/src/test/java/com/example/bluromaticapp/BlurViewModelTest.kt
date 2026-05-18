package com.example.bluromaticapp

import androidx.work.Data
import androidx.work.WorkInfo
import com.example.bluromaticapp.data.BluromaticRepository
import com.example.bluromaticapp.ui.BlurUiState
import com.example.bluromaticapp.ui.BlurViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BlurViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: BluromaticRepository
    private lateinit var viewModel: BlurViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun blurUiState_whenWorkIsRunning_is_Loading() = runTest {
        val workInfo = mock<WorkInfo>()
        whenever(workInfo.state).thenReturn(WorkInfo.State.RUNNING)
        val flow = MutableStateFlow<WorkInfo?>(workInfo)
        whenever(repository.outputWorkInfo).thenReturn(flow)

        viewModel = BlurViewModel(repository)
        
        // Subscriber to trigger stateIn
        val job = launch { viewModel.blurUiState.collect() }
        
        testDispatcher.scheduler.runCurrent()
        
        val state = viewModel.blurUiState.value
        assertEquals(BlurUiState.Loading, state)
        job.cancel()
    }

    @Test
    fun blurUiState_whenWorkIsFinished_with_Uri_is_Complete() = runTest {
        val workInfo = mock<WorkInfo>()
        val outputUri = "test_uri"
        val outputData = Data.Builder().putString(KEY_IMAGE_URI, outputUri).build()
        
        whenever(workInfo.state).thenReturn(WorkInfo.State.SUCCEEDED)
        whenever(workInfo.outputData).thenReturn(outputData)
        
        val flow = MutableStateFlow<WorkInfo?>(workInfo)
        whenever(repository.outputWorkInfo).thenReturn(flow)

        viewModel = BlurViewModel(repository)
        
        val job = launch { viewModel.blurUiState.collect() }
        
        testDispatcher.scheduler.runCurrent()
        
        val state = viewModel.blurUiState.value
        assertTrue(state is BlurUiState.Complete)
        assertEquals(outputUri, (state as BlurUiState.Complete).outputUri)
        job.cancel()
    }
}
