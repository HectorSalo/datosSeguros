package com.skysam.datossegurosFirebaseFinal.ui.ajustes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.skysam.datossegurosFirebaseFinal.R;

public class AcercaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
