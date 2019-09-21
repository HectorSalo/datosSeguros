package com.example.datossegurosFirebaseFinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.datossegurosFirebaseFinal.FragmentsBloqueo.HuellaFragment;

public class BloqueoActivity extends AppCompatActivity implements HuellaFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bloqueo);

        HuellaFragment huellaFragment = new HuellaFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsBloqueo, huellaFragment).commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
