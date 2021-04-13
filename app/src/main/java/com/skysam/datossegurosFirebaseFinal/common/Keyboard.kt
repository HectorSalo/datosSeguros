package com.skysam.datossegurosFirebaseFinal.common

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

/**
 * Created by Hector Chirinos (Home) on 13/4/2021.
 */
object Keyboard {
    fun close(view: View) {
        val imn = DatosSeguros.DatosSeguros.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imn.hideSoftInputFromWindow(view.windowToken, 0)
    }
}