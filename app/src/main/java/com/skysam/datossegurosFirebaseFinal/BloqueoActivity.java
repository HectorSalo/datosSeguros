package com.skysam.datossegurosFirebaseFinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skysam.datossegurosFirebaseFinal.FragmentsBloqueo.HuellaFragment;
import com.skysam.datossegurosFirebaseFinal.FragmentsBloqueo.PINFragment;
import com.skysam.datossegurosFirebaseFinal.FragmentsBloqueo.SinBloqueoFragment;
import com.skysam.datossegurosFirebaseFinal.Variables.VariablesGenerales;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;

public class BloqueoActivity extends AppCompatActivity implements HuellaFragment.OnFragmentInteractionListener, SinBloqueoFragment.OnFragmentInteractionListener, PINFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String usuario;
        if (user != null) {
            usuario = user.getUid();
        } else {
            usuario = "default";
        }
        SharedPreferences sharedPreferences = getSharedPreferences(usuario, Context.MODE_PRIVATE);

        String bloqueo = sharedPreferences.getString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_SIN_BLOQUEO);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                setTheme(R.style.AppTheme);
                break;
            case Constantes.PREFERENCE_ROJO:
                setTheme(R.style.AppThemeRojo);
                break;
            case Constantes.PREFERENCE_MARRON:
                setTheme(R.style.AppThemeMarron);
                break;
            case Constantes.PREFERENCE_LILA:
                setTheme(R.style.AppThemeLila);
                break;
        }
        setContentView(R.layout.activity_bloqueo);

        boolean huella = sharedPreferences.getBoolean(Constantes.PREFERENCE_HUELLA, false);
        boolean sinBloqueo = sharedPreferences.getBoolean(Constantes.PREFERENCE_SIN_BLOQUEO, true);

        HuellaFragment huellaFragment = new HuellaFragment();
        SinBloqueoFragment sinBloqueoFragment = new SinBloqueoFragment();
        PINFragment pinFragment = new PINFragment();

        if (sinBloqueo) {
            if (VariablesGenerales.conf_bloqueo == Constantes.HUELLA_INT) {
                getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit();
            } else if (VariablesGenerales.conf_bloqueo == Constantes.PIN_INT) {
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
