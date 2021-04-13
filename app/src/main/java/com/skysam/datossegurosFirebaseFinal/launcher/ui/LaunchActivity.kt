package com.skysam.datossegurosFirebaseFinal.launcher.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.common.Constants
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainActivity
import com.skysam.datossegurosFirebaseFinal.settings.HuellaFragment
import com.skysam.datossegurosFirebaseFinal.settings.PINFragment

class LaunchActivity : AppCompatActivity() {

    private var bloqueoGuardado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val sharedPreferences = getSharedPreferences(user.uid, MODE_PRIVATE)
            bloqueoGuardado = sharedPreferences.getString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_SIN_BLOQUEO)
            when (sharedPreferences.getString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO)) {
                Constants.PREFERENCE_AMARILLO -> setTheme(R.style.Theme_DatosSegurosYellow)
                Constants.PREFERENCE_ROJO -> setTheme(R.style.Theme_DatosSegurosRed)
                Constants.PREFERENCE_MARRON -> setTheme(R.style.Theme_DatosSegurosBrwon)
                Constants.PREFERENCE_LILA -> setTheme(R.style.Theme_DatosSegurosLila)
            }
        } else {
            setTheme(R.style.Theme_DatosSegurosYellow)
        }
        setContentView(R.layout.activity_launch)

        val huellaFragment = HuellaFragment()
        val pinFragment = PINFragment()

        val bundleFragment = Bundle()

        if (user != null) {
            when (bloqueoGuardado) {
                Constants.PREFERENCE_SIN_BLOQUEO -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                Constants.PREFERENCE_HUELLA -> {
                    bundleFragment.putInt("null", 1)
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado)
                    huellaFragment.arguments = bundleFragment
                    supportFragmentManager.beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit()
                }
                Constants.PREFERENCE_PIN -> {
                    bundleFragment.putInt("null", 1)
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado)
                    pinFragment.arguments = bundleFragment
                    supportFragmentManager.beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit()
                }
            }
        } else {
            startActivity(Intent(this, InicSesionActivity::class.java))
            finish()
        }
    }
}