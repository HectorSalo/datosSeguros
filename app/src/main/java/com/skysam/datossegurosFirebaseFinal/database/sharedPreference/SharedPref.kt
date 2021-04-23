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

    fun isStorageCloud(): Boolean {
        return getInstance().getBoolean(Constants.PREFERENCE_ALMACENAMIENTO_NUBE, true)
    }

    fun getTheme(): String {
        return getInstance().getString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO)!!
    }
}