package com.skysam.datossegurosFirebaseFinal.settings;

import androidx.appcompat.app.AppCompatActivity;

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