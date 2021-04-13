package com.skysam.datossegurosFirebaseFinal.launcher.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.skysam.datossegurosFirebaseFinal.common.Keyboard;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainActivity;
import com.skysam.datossegurosFirebaseFinal.R;

public class RegistrarActivity extends AppCompatActivity {
    private EditText usuario, contrasena, repetirContrasena;
    private Button button;
    private ProgressBar progressBarRegistrar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        usuario = findViewById(R.id.etRegistrarUsuario);
        contrasena = findViewById(R.id.etRegistrarContrasena);
        repetirContrasena = findViewById(R.id.etRegistrarContrasenaRepetir);
        mAuth = FirebaseAuth.getInstance();
        progressBarRegistrar = findViewById(R.id.progressBarRegistrar);

        button = findViewById(R.id.buttonRegistrar);
        button.setOnClickListener(v -> registrar());
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(RegistrarActivity.this);
        dialog.setTitle("Confirmar");
        dialog.setMessage("¿Desea cancelar el Registro? Se perderá la información");
        dialog.setIcon(R.drawable.ic_advertencia);
        dialog.setPositiveButton("Si", (dialog1, which) -> finish());
        dialog.setNegativeButton("No", (dialog12, which) -> dialog12.dismiss());
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
            progressBarRegistrar.setVisibility(View.VISIBLE);
            usuario.setEnabled(false);
            contrasena.setEnabled(false);
            repetirContrasena.setEnabled(false);
            button.setEnabled(false);

            mAuth.createUserWithEmailAndPassword(usuarioS, contrasenaS)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d("msg", "createUserWithEmail:success");
                            progressBarRegistrar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            progressBarRegistrar.setVisibility(View.GONE);
                            usuario.setEnabled(true);
                            contrasena.setEnabled(true);
                            repetirContrasena.setEnabled(true);
                            button.setEnabled(true);
                            Log.w("msg", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegistrarActivity.this, "Error al Registrar\nPor favor, intente nuevamente",
                                    Toast.LENGTH_LONG).show();

                        }

                    });
        }

    }
}
