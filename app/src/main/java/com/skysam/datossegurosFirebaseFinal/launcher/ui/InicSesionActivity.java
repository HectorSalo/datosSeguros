package com.skysam.datossegurosFirebaseFinal.launcher.ui;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.skysam.datossegurosFirebaseFinal.common.Keyboard;
import com.skysam.datossegurosFirebaseFinal.databinding.ActivityInicSesionBinding;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class InicSesionActivity extends AppCompatActivity{

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    private ActivityInicSesionBinding activityInicSesionBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityInicSesionBinding = ActivityInicSesionBinding.inflate(getLayoutInflater());
        View view = activityInicSesionBinding.getRoot();
        setContentView(view);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finishAffinity();
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        activityInicSesionBinding.tvRegistrar.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), RegistrarActivity.class)));

        activityInicSesionBinding.tvIniciarGoogle.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, 101);
        });

        activityInicSesionBinding.tvRecuperarPass.setOnClickListener(v -> recuperarPass());

        activityInicSesionBinding.button.setOnClickListener(v -> validarInciarSesion());
    }

    private void recuperarPass() {
        final EditText usuario = new EditText(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Ingrese correo electrónico")
                .setTitle("Recuperación de contraseña")
                .setView(usuario)
                .setPositiveButton("Recuperar", (dialog1, which) -> {
                    String correo = usuario.getText().toString();
                    enviarCorreo(correo);
                })
                .setNegativeButton("Cancelar", (dialog12, which) -> dialog12.dismiss())
                .setCancelable(false)
                .show();
    }

    public void validarInciarSesion() {
        String email = activityInicSesionBinding.etInicSesionUsuario.getText().toString();
        String password = activityInicSesionBinding.etInicSesionContrasena.getText().toString();
        boolean emailValido;
        boolean passwordValido;

        if (!email.isEmpty()) {
            if (email.contains("@")) {
                emailValido = true;
            } else {
                activityInicSesionBinding.etInicSesionUsuario.setError("Formato incorrecto para correo");
                emailValido = false;
            }
        } else {
            activityInicSesionBinding.etInicSesionUsuario.setError("El campo no puede estar vacío");
            emailValido = false;
        }

        if (password.isEmpty() || (password.length() < 4)) {
            passwordValido = false;
            activityInicSesionBinding.etInicSesionContrasena.setError("Mínimo 4 caracteres");
        } else {
            passwordValido = true;
        }

        if (passwordValido && emailValido) {
            Keyboard.INSTANCE.close(activityInicSesionBinding.getRoot());
            activityInicSesionBinding.inputLayoutPass.setEnabled(false);
            activityInicSesionBinding.inputLayoutEmail.setEnabled(false);
            activityInicSesionBinding.button.setEnabled(false);
            activityInicSesionBinding.progressBar.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            Log.d("msg", "signInWithEmail:success");
                            activityInicSesionBinding.progressBar.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            activityInicSesionBinding.progressBar.setVisibility(View.GONE);
                            activityInicSesionBinding.inputLayoutPass.setEnabled(true);
                            activityInicSesionBinding.inputLayoutEmail.setEnabled(true);
                            activityInicSesionBinding.button.setEnabled(true);
                            Log.w("msg", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Error al iniciar sesión\nPor favor, verifique los datos del Usuario y su conexión a internet",
                                    Toast.LENGTH_LONG).show();

                        }

                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    autenticarFirebaseGoogle(account);
                }
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("msg", "Google sign in failed", e);
                // ...
            }
        }
    }

    public void autenticarFirebaseGoogle (GoogleSignInAccount acct) {
        activityInicSesionBinding.progressBar.setVisibility(View.VISIBLE);
        Log.d("msg", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("msg", "signInWithCredential:success");
                        activityInicSesionBinding.progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("msg", "signInWithCredential:failure", task.getException());
                        activityInicSesionBinding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error al iniciar sesión\nPor favor, verifique los datos del Usuario y su conexión a internet",
                                Toast.LENGTH_LONG).show();
                    }

                    // ...
                });
    }

    public void enviarCorreo(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("msg", "Correo enviado");
            }
        });
    }
}
