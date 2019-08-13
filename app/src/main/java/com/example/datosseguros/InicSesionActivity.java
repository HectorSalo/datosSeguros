package com.example.datosseguros;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class InicSesionActivity extends AppCompatActivity {

    private EditText usuario, contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inic_sesion);

        usuario = (EditText) findViewById(R.id.etInicSesionUsuario);
        contrasena = (EditText) findViewById(R.id.etInicSesionContrasena);
        TextView registrate = (TextView) findViewById(R.id.tvRegistrate);
        TextView cuentaGoogle = (TextView) findViewById(R.id.tvcuentaGoogle);

        registrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Registro", Toast.LENGTH_SHORT).show();
            }
        });

        Button inicSesion = (Button) findViewById(R.id.buttonInicSesion);
        inicSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(InicSesionActivity.this, MainActivity.class));
            }
        });
    }
}
