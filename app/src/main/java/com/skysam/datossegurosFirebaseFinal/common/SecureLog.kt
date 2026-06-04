package com.skysam.datossegurosFirebaseFinal.common

import android.util.Log
import com.skysam.datossegurosFirebaseFinal.BuildConfig

object SecureLog {
    @JvmStatic
    @JvmOverloads
    fun d(tag: String, msg: String, t: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            if (t != null) Log.d(tag, msg, t) else Log.d(tag, msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun w(tag: String, msg: String, t: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            if (t != null) Log.w(tag, msg, t) else Log.w(tag, msg)
        }
    }

    @JvmStatic
    @JvmOverloads
    fun e(tag: String, msg: String, t: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            if (t != null) Log.e(tag, msg, t) else Log.e(tag, msg)
        }
    }
}
