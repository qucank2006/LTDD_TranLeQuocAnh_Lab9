package com.example.bluromaticapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BluromaticScreen(
    blurViewModel: BlurViewModel = viewModel()
) {
    val blurUiState by blurViewModel.blurUiState.collectAsStateWithLifecycle()
    
    BluromaticScreenContent(
        blurUiState = blurUiState,
        onApplyBlur = { blurViewModel.applyBlur(it) },
        onCancel = { blurViewModel.cancelWork() }
    )
}

@Composable
fun BluromaticScreenContent(
    blurUiState: BlurUiState,
    onApplyBlur: (Int) -> Unit,
    onCancel: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "Blur-O-Matic")
        
        when (blurUiState) {
            is BlurUiState.Default -> {
                Button(onClick = { onApplyBlur(1) }) {
                    Text("Bắt đầu")
                }
            }
            is BlurUiState.Loading -> {
                Text("Đang làm mờ...")
                Button(onClick = onCancel) {
                    Text("Hủy")
                }
            }
            is BlurUiState.Complete -> {
                Text("Hoàn tất!")
                Text("Output: ${blurUiState.outputUri}")
                Button(onClick = { onApplyBlur(1) }) {
                    Text("Bắt đầu lại")
                }
            }
        }
    }
}
