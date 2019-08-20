package com.example.datossegurosFirebaseFinal;

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

public class InicSesionActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ProgressDialog progress;
    private EditText usuario, contrasena;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inic_sesion);

        usuario = (EditText) findViewById(R.id.etInicSesionUsuario);
        contrasena = (EditText) findViewById(R.id.etInicSesionContrasena);
        TextView registrate = (TextView) findViewById(R.id.tvRegistrate);
        TextView cuentaGoogle = (TextView) findViewById(R.id.tvcuentaGoogle);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        registrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrarActivity.class));
            }
        });

        cuentaGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
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
                Log.w("msg", "Google sign in failed", e);
                // ...
            }
        }
    }

    public void autenticarFirebaseGoogle (GoogleSignInAccount acct) {
        progress = new ProgressDialog(this);
        progress.setMessage("Iniciando sesión...");
        progress.setCancelable(false);
        progress.show();
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
                            progress.dismiss();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("msg", "signInWithCredential:failure", task.getException());
                            progress.dismiss();
                            Toast.makeText(InicSesionActivity.this, "Error al iniciar sesión\nPor favor, verifique los datos del Usuario y su conexión a internet",
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });
    }


}
