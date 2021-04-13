package com.skysam.datossegurosFirebaseFinal.common

import android.app.Application
import android.content.Context

/**
 * Created by Hector Chirinos (Home) on 13/4/2021.
 */
class DatosSeguros: Application() {
    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    object DatosSeguros {
        fun getContext(): Context {
            return appContext
        }
    }
}