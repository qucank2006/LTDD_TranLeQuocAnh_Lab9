package com.example.bluromaticapp

import android.app.Application
import com.example.bluromaticapp.data.AppContainer
import com.example.bluromaticapp.data.DefaultAppContainer

class BluromaticApplication : Application() {
    /** AppContainer instance used by the rest of the classes to obtain dependencies */
    lateinit var container: AppContainer
    
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}
