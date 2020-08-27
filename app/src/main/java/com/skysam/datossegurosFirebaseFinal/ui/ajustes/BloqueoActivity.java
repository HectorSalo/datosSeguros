package com.skysam.datossegurosFirebaseFinal.ui.ajustes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.ui.ajustes.HuellaFragment;
import com.skysam.datossegurosFirebaseFinal.ui.ajustes.PINFragment;
import com.skysam.datossegurosFirebaseFinal.ui.ajustes.SinBloqueoFragment;
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

        String bloqueoGuardado = sharedPreferences.getString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_SIN_BLOQUEO);

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

        HuellaFragment huellaFragment = new HuellaFragment();
        SinBloqueoFragment sinBloqueoFragment = new SinBloqueoFragment();
        PINFragment pinFragment = new PINFragment();

        Bundle myBundle = this.getIntent().getExtras();
        Bundle bundleFragment = new Bundle();

        if (myBundle != null) {
            String bloqueoEscogido = myBundle.getString(Constantes.PREFERENCE_TIPO_BLOQUEO);
            switch (bloqueoEscogido) {
                case Constantes.PREFERENCE_SIN_BLOQUEO:
                    bundleFragment.putInt("null", 0);
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado);
                    bundleFragment.putString("bloqueoEscogido", Constantes.PREFERENCE_SIN_BLOQUEO);
                    pinFragment.setArguments(bundleFragment);
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit();
                    break;
                case Constantes.PREFERENCE_HUELLA:
                    bundleFragment = new Bundle();
                    bundleFragment.putInt("null", 0);
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado);
                    bundleFragment.putString("bloqueoEscogido", Constantes.PREFERENCE_HUELLA);
                    huellaFragment.setArguments(bundleFragment);
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit();
                    break;
                case Constantes.PREFERENCE_PIN:
                    bundleFragment = new Bundle();
                    bundleFragment.putInt("null", 0);
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado);
                    bundleFragment.putString("bloqueoEscogido", Constantes.PREFERENCE_PIN);
                    pinFragment.setArguments(bundleFragment);
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit();
                    break;
            }
        } else {
            switch (bloqueoGuardado) {
                case Constantes.PREFERENCE_SIN_BLOQUEO:
                    bundleFragment.putInt("null", 1);
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado);
                    sinBloqueoFragment.setArguments(bundleFragment);
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, sinBloqueoFragment).commit();
                    break;
                case Constantes.PREFERENCE_HUELLA:
                    bundleFragment.putInt("null", 1);
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado);
                    huellaFragment.setArguments(bundleFragment);
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit();
                    break;
                case Constantes.PREFERENCE_PIN:
                    bundleFragment.putInt("null", 1);
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado);
                    pinFragment.setArguments(bundleFragment);
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit();
                    break;
            }
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
