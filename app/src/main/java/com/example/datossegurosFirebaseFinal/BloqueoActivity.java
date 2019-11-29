package com.example.datossegurosFirebaseFinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.datossegurosFirebaseFinal.FragmentsBloqueo.HuellaFragment;
import com.example.datossegurosFirebaseFinal.FragmentsBloqueo.PINFragment;
import com.example.datossegurosFirebaseFinal.FragmentsBloqueo.SinBloqueoFragment;
import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;

public class BloqueoActivity extends AppCompatActivity implements HuellaFragment.OnFragmentInteractionListener, SinBloqueoFragment.OnFragmentInteractionListener, PINFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloqueo);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean huella = preferences.getBoolean(UtilidadesStatic.HUELLA, false);
        boolean sinBloqueo = preferences.getBoolean(UtilidadesStatic.SIN_BLOQUEO, true);

        HuellaFragment huellaFragment = new HuellaFragment();
        SinBloqueoFragment sinBloqueoFragment = new SinBloqueoFragment();
        PINFragment pinFragment = new PINFragment();

        if (sinBloqueo) {
            if (Utilidades.conf_bloqueo == UtilidadesStatic.HUELLA_INT) {
                getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit();
            } else if (Utilidades.conf_bloqueo == UtilidadesStatic.PIN_INT) {
                getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, sinBloqueoFragment).commit();
            }
        } else if (huella){
            if (Utilidades.conf_bloqueo == 1000) {
                getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit();
            }
        } else {
            getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit();
        }

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        finish();

    }
}
