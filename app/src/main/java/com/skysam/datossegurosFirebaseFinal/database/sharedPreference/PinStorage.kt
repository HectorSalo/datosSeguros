package com.skysam.datossegurosFirebaseFinal.database.sharedPreference

import android.content.Context
import android.content.SharedPreferences
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.DatosSeguros
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth

object PinStorage {
    private const val DEFAULT_PIN = "0000"

    private fun prefs(): SharedPreferences =
        DatosSeguros.DatosSeguros.getContext().getSharedPreferences(
            Auth.getCurrenUser()!!.uid,
            Context.MODE_PRIVATE
        )

    @JvmStatic
    fun verify(pin: String): Boolean {
        val stored = prefs().getString(Constants.PREFERENCE_PIN_RESPALDO, DEFAULT_PIN)
        return pin == stored
    }

    @JvmStatic
    fun save(pin: String) {
        prefs().edit().putString(Constants.PREFERENCE_PIN_RESPALDO, pin).apply()
    }

    @JvmStatic
    fun reset() {
        prefs().edit().putString(Constants.PREFERENCE_PIN_RESPALDO, DEFAULT_PIN).apply()
    }
}
