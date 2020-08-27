package com.skysam.datossegurosFirebaseFinal.ui.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.skysam.datossegurosFirebaseFinal.databinding.ActivityInicSesionBinding;
import com.skysam.datossegurosFirebaseFinal.ui.general.MainActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.skysam.datossegurosFirebaseFinal.ui.interfaces.InicSesion;

public class InicSesionActivity extends AppCompatActivity implements InicSesion {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private GoogleSignInClient mGoogleSignInClient;

    private ActivityInicSesionBinding activityInicSesionBinding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityInicSesionBinding = ActivityInicSesionBinding.inflate(getLayoutInflater());
        View view = activityInicSesionBinding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        activityInicSesionBinding.tvRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrarActivity.class));
            }
        });

        activityInicSesionBinding.tvIniciarGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });

        activityInicSesionBinding.tvRecuperarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recuperarPass();
            }
        });


        activityInicSesionBinding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //validarInciarSesion();
            }
        });

    }

    private void recuperarPass() {
        final EditText usuario = new EditText(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Ingrese correo electrónico")
                .setTitle("Recuperación de contraseña")
                .setView(usuario)
                .setPositiveButton("Recuperar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String correo = usuario.getText().toString();
                        enviarCorreo(correo);
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

    @Override
    public void onStart() {
        super.onStart();
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
            progressBarInicSesion.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Log.d("msg", "signInWithEmail:success");
                                user = mAuth.getCurrentUser();
                                progressBarInicSesion.setVisibility(View.GONE);
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            } else {
                                // If sign in fails, display a message to the user.
                                progressBarInicSesion.setVisibility(View.GONE);
                                Log.w("msg", "signInWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Error al iniciar sesión\nPor favor, verifique los datos del Usuario y su conexión a internet",
                                        Toast.LENGTH_LONG).show();

                            }

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
                autenticarFirebaseGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("msg", "Google sign in failed", e);
                // ...
            }
        }
    }

    public void autenticarFirebaseGoogle (GoogleSignInAccount acct) {
        progressBarInicSesion.setVisibility(View.VISIBLE);
        Log.d("msg", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("msg", "signInWithCredential:success");
                            user = mAuth.getCurrentUser();
                            progressBarInicSesion.setVisibility(View.GONE);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("msg", "signInWithCredential:failure", task.getException());
                            progressBarInicSesion.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), "Error al iniciar sesión\nPor favor, verifique los datos del Usuario y su conexión a internet",
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }

    public void enviarCorreo(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("msg", "Correo enviado");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void showProgress() {
        activityInicSesionBinding.progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        activityInicSesionBinding.progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showErrorEmailEmpty() {
        activityInicSesionBinding.inputLayoutEmail.setError(getString(R.string.error_campo_vacio));
    }

    @Override
    public void showErrorPassEmpty() {
        activityInicSesionBinding.inputLayoutPass.setError(getString(R.string.error_campo_vacio));
    }

    @Override
    public void showErrorEmailFormatIncorrect() {
        activityInicSesionBinding.inputLayoutEmail.setError(getString(R.string.formato_email_incorrecto));
    }

    @Override
    public void showErrorPassEmptyLongIncorrect() {
        activityInicSesionBinding.inputLayoutPass.setError(getString(R.string.error_longitud_pass));
    }

    @Override
    public void initSession() {

    }

    @Override
    public void errorSession() {

    }
}
