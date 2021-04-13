package com.skysam.datossegurosFirebaseFinal.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;

public class BloqueoActivity extends AppCompatActivity {

    private String bloqueoGuardado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            SharedPreferences sharedPreferences = getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

            bloqueoGuardado = sharedPreferences.getString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_SIN_BLOQUEO);

            String tema = sharedPreferences.getString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO);

            switch (tema){
                case Constants.PREFERENCE_AMARILLO:
                    setTheme(R.style.Theme_DatosSegurosYellow);
                    break;
                case Constants.PREFERENCE_ROJO:
                    setTheme(R.style.Theme_DatosSegurosRed);
                    break;
                case Constants.PREFERENCE_MARRON:
                    setTheme(R.style.Theme_DatosSegurosBrwon);
                    break;
                case Constants.PREFERENCE_LILA:
                    setTheme(R.style.Theme_DatosSegurosLila);
                    break;
            }

        } else {
            setTheme(R.style.Theme_DatosSegurosYellow);
        }

        setContentView(R.layout.activity_bloqueo);

        HuellaFragment huellaFragment = new HuellaFragment();
        PINFragment pinFragment = new PINFragment();


        Bundle myBundle = this.getIntent().getExtras();
        Bundle bundleFragment = new Bundle();

        if (myBundle != null) {
            String bloqueoEscogido = myBundle.getString(Constants.PREFERENCE_TIPO_BLOQUEO);
            switch (bloqueoEscogido) {
                case Constants.PREFERENCE_SIN_BLOQUEO:
                    bundleFragment.putInt("null", 0);
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado);
                    bundleFragment.putString("bloqueoEscogido", Constants.PREFERENCE_SIN_BLOQUEO);
                    pinFragment.setArguments(bundleFragment);
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit();
                    break;
                case Constants.PREFERENCE_HUELLA:
                    bundleFragment = new Bundle();
                    bundleFragment.putInt("null", 0);
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado);
                    bundleFragment.putString("bloqueoEscogido", Constants.PREFERENCE_HUELLA);
                    huellaFragment.setArguments(bundleFragment);
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit();
                    break;
                case Constants.PREFERENCE_PIN:
                    bundleFragment = new Bundle();
                    bundleFragment.putInt("null", 0);
                    bundleFragment.putString("bloqueoGuardado", bloqueoGuardado);
                    bundleFragment.putString("bloqueoEscogido", Constants.PREFERENCE_PIN);
                    pinFragment.setArguments(bundleFragment);
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, pinFragment).commit();
                    break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();

    }
}
