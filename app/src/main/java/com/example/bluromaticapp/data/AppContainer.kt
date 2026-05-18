package com.example.bluromaticapp.data

import android.content.Context

interface AppContainer {
    val bluromaticRepository: BluromaticRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    override val bluromaticRepository: BluromaticRepository by lazy {
        WorkManagerBluromaticRepository(context)
    }
}
