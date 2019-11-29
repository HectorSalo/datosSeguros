package com.example.datossegurosFirebaseFinal;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class TemasActivity extends AppCompatActivity implements View.OnClickListener {

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temas);

        Button buttonAmarillo = findViewById(R.id.buttonAmarillo);
        Button buttonRojo = findViewById(R.id.buttonRojo);
        Button buttonMarron = findViewById(R.id.buttonMarron);
        Button buttonLila = findViewById(R.id.buttonLila);

        buttonAmarillo.setOnClickListener(this);
        buttonRojo.setOnClickListener(this);
        buttonMarron.setOnClickListener(this);
        buttonLila.setOnClickListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String tema = sharedPreferences.getString("tema", "Amarillo");

        switch (tema){
            case "Amarillo":
                break;
            case "Rojo":
                break;
            case "Marron":
                break;
            case "Lila":
                break;
        }

    }


    @Override
    public void onClick(View v) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        switch (v.getId()) {
            case R.id.buttonAmarillo:
                editor.putString("tema", "Amarillo");
                editor.commit();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.buttonRojo:
                editor.putString("tema", "Rojo");
                editor.commit();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.buttonMarron:
                editor.putString("tema", "Marron");
                editor.commit();
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.buttonLila:
                editor.putString("tema", "Lila");
                editor.commit();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }
    }
}
