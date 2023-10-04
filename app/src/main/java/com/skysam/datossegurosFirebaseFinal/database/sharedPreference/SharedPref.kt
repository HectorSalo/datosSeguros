package com.skysam.datossegurosFirebaseFinal.database.sharedPreference

import android.content.Context
import android.content.SharedPreferences
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.common.DatosSeguros
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth

/**
 * Created by Hector Chirinos on 23/04/2021.
 */
object SharedPref {
    private fun getInstance(): SharedPreferences {
        return DatosSeguros.DatosSeguros.getContext().getSharedPreferences(Auth.getCurrenUser()!!.uid,
                Context.MODE_PRIVATE)
    }

    fun getTheme(): String {
        return getInstance().getString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO)!!
    }

    fun changeTheme(newTheme: String) {
        val editor = getInstance().edit()
        editor.putString(Constants.PREFERENCE_TEMA, newTheme)
        editor.apply()
    }

    fun getLock(): String {
        return getInstance().getString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_SIN_BLOQUEO)!!
    }

    fun changeLock(newLock: String) {
        val editor = getInstance().edit()
        editor.putString(Constants.PREFERENCE_TIPO_BLOQUEO, newLock)
        editor.apply()
    }

    fun changeToDefault() {
        val editor = getInstance().edit()
        editor.putString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_SIN_BLOQUEO)
        editor.putString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO)
        editor.putString(Constants.PREFERENCE_PIN_RESPALDO, "0000")
        editor.apply()
    }
}