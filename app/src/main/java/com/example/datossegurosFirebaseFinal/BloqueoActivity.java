package com.example.datossegurosFirebaseFinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.example.datossegurosFirebaseFinal.FragmentsBloqueo.HuellaFragment;
import com.example.datossegurosFirebaseFinal.FragmentsBloqueo.SinBloqueoFragment;
import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;

public class BloqueoActivity extends AppCompatActivity implements HuellaFragment.OnFragmentInteractionListener, SinBloqueoFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloqueo);

        SharedPreferences preferences = getSharedPreferences(UtilidadesStatic.BLOQUEO, Context.MODE_PRIVATE);
        boolean huella = preferences.getBoolean(UtilidadesStatic.HUELLA, false);
        boolean patron = preferences.getBoolean(UtilidadesStatic.PATRON, false);
        boolean pin = preferences.getBoolean(UtilidadesStatic.PIN, false);
        boolean sinBloqueo = preferences.getBoolean(UtilidadesStatic.SIN_BLOQUEO, true);

        HuellaFragment huellaFragment = new HuellaFragment();
        SinBloqueoFragment sinBloqueoFragment = new SinBloqueoFragment();

        if (huella || Utilidades.uso_huella == 2) {
            getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit();
        } else if (sinBloqueo) {
            getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, sinBloqueoFragment).commit();
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
