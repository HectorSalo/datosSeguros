package com.example.datossegurosFirebaseFinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegistrarActivity extends AppCompatActivity {
    private EditText usuario, contrasena, repetirContrasena;
    private ProgressDialog progress;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        usuario = (EditText) findViewById(R.id.etRegistrarUsuario);
        contrasena = (EditText) findViewById(R.id.etRegistrarContrasena);
        repetirContrasena = (EditText) findViewById(R.id.etRegistrarContrasenaRepetir);
        mAuth = FirebaseAuth.getInstance();

        final Button registrar = (Button) findViewById(R.id.buttonRegistrar);
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegistrarActivity.this);
        dialog.setTitle("Confirmar");
        dialog.setMessage("¿Desea cancelar el Registro? Se perderá la información");
        dialog.setIcon(R.drawable.ic_advertencia);
        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void registrar() {
        String usuarioS = usuario.getText().toString();
        String contrasenaS = contrasena.getText().toString();
        String repetirContrasenaS = repetirContrasena.getText().toString();

        boolean emailValido;
        boolean passwordValido;

        if (!usuarioS.isEmpty()) {
            if (usuarioS.contains("@")) {
                emailValido = true;
            } else {
                usuario.setError("Formato incorrecto para correo");
                emailValido = false;
            }
        } else {
            usuario.setError("El campo no puede estar vacío");
            emailValido = false;
        }

        if (contrasenaS.isEmpty() || (contrasenaS.length() < 6)) {
            passwordValido = false;
            contrasena.setError("Mínimo 6 caracteres");
            repetirContrasena.setError("Mínimo 6 caracteres");
        } else {
            if (contrasenaS.equals(repetirContrasenaS)) {
                passwordValido = true;
            } else {
                passwordValido = false;
                contrasena.setError("Las contraseñas deben coincidir");
                repetirContrasena.setError("Las contraseñas deben coincidir");
            }

        }

        if (passwordValido && emailValido) {
            progress = new ProgressDialog(this);
            progress.setMessage("Iniciando sesión...");
            progress.setCancelable(false);
            progress.show();
            mAuth.createUserWithEmailAndPassword(usuarioS, contrasenaS)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("msg", "createUserWithEmail:success");
                                user = mAuth.getCurrentUser();
                                progress.dismiss();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                progress.dismiss();
                                Log.w("msg", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(RegistrarActivity.this, "Error al Registrar\nPor favor, intente nuevamente",
                                        Toast.LENGTH_LONG).show();

                            }

                        }
                    });
        }

    }
}
