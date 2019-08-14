package com.example.datosseguros;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InicSesionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressDialog progress;
    private EditText usuario, contrasena;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inic_sesion);

        usuario = (EditText) findViewById(R.id.etInicSesionUsuario);
        contrasena = (EditText) findViewById(R.id.etInicSesionContrasena);
        TextView registrate = (TextView) findViewById(R.id.tvRegistrate);
        TextView cuentaGoogle = (TextView) findViewById(R.id.tvcuentaGoogle);

        mAuth = FirebaseAuth.getInstance();

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
                validarInciarSesion();
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();
        if (user != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    public void validarInciarSesion() {
        String email = usuario.getText().toString();
        String password = contrasena.getText().toString();
        boolean emailValido;
        boolean passwordValido;

        if (!email.isEmpty()) {
            if (email.contains("@")) {
                emailValido = true;
            } else {
                usuario.setError("Formato incorrecto para correo");
                emailValido = false;
            }
        } else {
            usuario.setError("El campo no puede estar vacío");
            emailValido = false;
        }

        if (password.isEmpty() || (password.length() < 6)) {
            passwordValido = false;
            contrasena.setError("Mínimo 6 caracteres");
        } else {
            passwordValido = true;

        }

        if (passwordValido && emailValido) {
            progress = new ProgressDialog(this);
            progress.setMessage("Iniciando sesión...");
            progress.setCancelable(false);
            progress.show();
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("msg", "signInWithEmail:success");
                                user = mAuth.getCurrentUser();
                                progress.dismiss();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                progress.dismiss();
                                Log.w("msg", "signInWithEmail:failure", task.getException());
                                Toast.makeText(InicSesionActivity.this, "Error al iniciar sesión\nPor favor, verifique los datos del Usuario y su conexión a internet",
                                        Toast.LENGTH_LONG).show();

                            }

                        }
                    });
        }
    }

}
