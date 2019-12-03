package com.example.datossegurosFirebaseFinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.example.datossegurosFirebaseFinal.FragmentsBloqueo.HuellaFragment;
import com.example.datossegurosFirebaseFinal.FragmentsBloqueo.PINFragment;
import com.example.datossegurosFirebaseFinal.FragmentsBloqueo.SinBloqueoFragment;
import com.example.datossegurosFirebaseFinal.Variables.VariablesGenerales;
import com.example.datossegurosFirebaseFinal.Variables.VariablesEstaticas;

public class BloqueoActivity extends AppCompatActivity implements HuellaFragment.OnFragmentInteractionListener, SinBloqueoFragment.OnFragmentInteractionListener, PINFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String tema = sharedPreferences.getString("tema", "Amarillo");

        switch (tema){
            case "Amarillo":
                setTheme(R.style.AppTheme);
                break;
            case "Rojo":
                setTheme(R.style.AppThemeRojo);
                break;
            case "Marron":
                setTheme(R.style.AppThemeMarron);
                break;
            case "Lila":
                setTheme(R.style.AppThemeLila);
                break;
        }
        setContentView(R.layout.activity_bloqueo);

        boolean huella = sharedPreferences.getBoolean(VariablesEstaticas.HUELLA, false);
        boolean sinBloqueo = sharedPreferences.getBoolean(VariablesEstaticas.SIN_BLOQUEO, true);

        HuellaFragment huellaFragment = new HuellaFragment();
        SinBloqueoFragment sinBloqueoFragment = new SinBloqueoFragment();
        PINFragment pinFragment = new PINFragment();

        if (sinBloqueo) {
            if (VariablesGenerales.conf_bloqueo == VariablesEstaticas.HUELLA_INT) {
                getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit();
            } else if (VariablesGenerales.conf_bloqueo == VariablesEstaticas.PIN_INT) {
                getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, sinBloqueoFragment).commit();
            }
        } else if (huella){
            if (VariablesGenerales.conf_bloqueo == 1000) {
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
