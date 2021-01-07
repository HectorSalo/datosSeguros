package com.skysam.datossegurosFirebaseFinal.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.skysam.datossegurosFirebaseFinal.R
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes
import com.skysam.datossegurosFirebaseFinal.ui.ajustes.HuellaFragment
import com.skysam.datossegurosFirebaseFinal.ui.ajustes.PINFragment
import com.skysam.datossegurosFirebaseFinal.ui.ajustes.SinBloqueoFragment

class LaunchActivity : AppCompatActivity() {

    private var bloqueoGuardado: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = FirebaseAuth.getInstance().currentUser

        if (user != null) {
            val sharedPreferences = getSharedPreferences(user.uid, MODE_PRIVATE)
            bloqueoGuardado = sharedPreferences.getString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_SIN_BLOQUEO)
            when (sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO)) {
                Constantes.PREFERENCE_AMARILLO -> setTheme(R.style.AppTheme)
                Constantes.PREFERENCE_ROJO -> setTheme(R.style.AppThemeRojo)
                Constantes.PREFERENCE_MARRON -> setTheme(R.style.AppThemeMarron)
                Constantes.PREFERENCE_LILA -> setTheme(R.style.AppThemeLila)
            }
        } else {
            setTheme(R.style.AppTheme)
        }
        setContentView(R.layout.activity_launch)

        val huellaFragment = HuellaFragment()
        val sinBloqueoFragment = SinBloqueoFragment()
        val pinFragment = PINFragment()

        val bundleFragment = Bundle()

        if (user != null) {
            when (bloqueoGuardado) {
                Constantes.PREFERENCE_SIN_BLOQUEO -> {
                    supportFragmentManager.beginTransaction().add(R.id.contenedorFragmentsBloqueo, sinBloqueoFragment).commit()
                }
                Constantes.PREFERENCE_HUELLA -> {
                    bundleFragment.putInt("null", 1)
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado)
                    huellaFragment.arguments = bundleFragment
                    supportFragmentManager.beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit()
                }
                Constantes.PREFERENCE_PIN -> {
                    bundleFragment.putInt("null", 1)
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado)
                    pinFragment.arguments = bundleFragment
                    supportFragmentManager.beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit()
                }
            }
        } else {
            supportFragmentManager.beginTransaction().add(R.id.contenedorFragmentsBloqueo, sinBloqueoFragment).commit()
        }
    }
}