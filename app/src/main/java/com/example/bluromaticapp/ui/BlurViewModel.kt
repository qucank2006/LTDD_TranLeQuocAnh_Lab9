package com.example.bluromaticapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.example.bluromaticapp.KEY_IMAGE_URI
import com.example.bluromaticapp.data.BluromaticRepository
import com.example.bluromaticapp.data.blurAmountOptions
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface BlurUiState {
    object Default : BlurUiState
    object Loading : BlurUiState
    data class Complete(val outputUri: String) : BlurUiState
}

class BlurViewModel(private val bluromaticRepository: BluromaticRepository) : ViewModel() {

    internal val blurAmount = blurAmountOptions

    val blurUiState: StateFlow<BlurUiState> = bluromaticRepository.outputWorkInfo
        .map { workInfo ->
            when {
                workInfo?.state?.isFinished == true -> {
                    val outputUri = workInfo.outputData.getString(KEY_IMAGE_URI)
                    if (!outputUri.isNullOrEmpty()) {
                        BlurUiState.Complete(outputUri)
                    } else {
                        BlurUiState.Default
                    }
                }
                workInfo?.state == WorkInfo.State.RUNNING -> BlurUiState.Loading
                else -> BlurUiState.Default
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BlurUiState.Default
        )

    fun applyBlur(blurLevel: Int) {
        bluromaticRepository.applyBlur(blurLevel)
    }

    fun cancelWork() {
        bluromaticRepository.cancelWork()
    }
}
