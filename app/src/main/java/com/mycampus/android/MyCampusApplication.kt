package com.mycampus.android

import android.app.Application
import com.mycampus.android.data.AppContainer

class MyCampusApplication : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
