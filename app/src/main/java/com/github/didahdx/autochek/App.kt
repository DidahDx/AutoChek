package com.github.didahdx.autochek

import android.app.Application
import com.github.didahdx.autochek.common.TimberLoggingTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * @author Daniel Didah on 6/13/22
 */

@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(TimberLoggingTree())
    }
}