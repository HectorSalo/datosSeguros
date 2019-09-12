package com.example.datossegurosFirebaseFinal;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        Button buttonEmail = (Button) findViewById(R.id.buttonUpdateEmail);
        Button buttonPass = (Button) findViewById(R.id.buttonUpdatePass);

        buttonEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarEmail();
            }
        });

        buttonPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarPass();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void actualizarEmail() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText email = new EditText(this);
        final EditText repetirEmail = new EditText(this);
        email.setHint("Ingrese nueva dirección de correo");
        repetirEmail.setHint("Repita la dirección de correo");

        layout.addView(email);
        layout.addView(repetirEmail);

        androidx.appcompat.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Cambio de Correo")
                .setView(layout)
                .setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String correo = email.getText().toString();
                        String repetirCorreo = repetirEmail.getText().toString();

                        if (!correo.isEmpty()) {
                            if (correo.contains("@")) {
                                if (correo.equals(repetirCorreo)) {
                                    dialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "True", Toast.LENGTH_SHORT).show();
                                } else {
                                    email.setError("Las contraseñas deben coincidir");
                                }
                            } else {
                                email.setError("Formato incorrecto para correo");

                            }
                        } else {
                            email.setError("El campo no puede estar vacío");

                        }


                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //dialog.dismiss();

                    }
                })
                .setCancelable(false)
                .show();
    }

    public void actualizarPass() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText pass = new EditText(this);
        EditText repetirPass = new EditText(this);
        pass.setHint("Ingrese nueva contraseña");
        repetirPass.setHint("Repita la contraseña");

        layout.addView(pass);
        layout.addView(repetirPass);

        androidx.appcompat.app.AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Cambio de contraseña")
                .setView(layout)
                .setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String passw = pass.getText().toString();
                        //enviarCorreo(correo);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }
}
