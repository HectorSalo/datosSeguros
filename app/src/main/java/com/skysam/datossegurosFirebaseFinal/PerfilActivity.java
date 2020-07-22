package com.skysam.datossegurosFirebaseFinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private EditText etEmail, etRepetirEmail, etPass, etRepetirPass;
    private RadioButton rbEmail, rbPass, rbNube, rbDispositivo, rbAlmacenamiento, rbHuella, rbPIN, rbSinBloqueo;
    private ProgressBar progressBarPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);
        boolean almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                setTheme(R.style.AppTheme);
                break;
            case Constantes.PREFERENCE_ROJO:
                setTheme(R.style.AppThemeRojo);
                break;
            case Constantes.PREFERENCE_MARRON:
                setTheme(R.style.AppThemeMarron);
                break;
            case Constantes.PREFERENCE_LILA:
                setTheme(R.style.AppThemeLila);
                break;
        }

        setContentView(R.layout.activity_perfil);

        etEmail = (EditText) findViewById(R.id.editTextUpdateEmail);
        etRepetirEmail = (EditText) findViewById(R.id.editTextRepetirUpdateEmail);
        etPass = (EditText) findViewById(R.id.editTextUpdatePass);
        etRepetirPass = (EditText) findViewById(R.id.editTextRepetirUpdatePass);
        final RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupUpdate);
        final RadioGroup radioGroupAlmacenamiento = (RadioGroup) findViewById(R.id.radioGroupAlmacenamiento);
        final RadioGroup radioGroupBloqueo = (RadioGroup) findViewById(R.id.radioGruopBloqueo);
        rbEmail = (RadioButton) findViewById(R.id.radioButtonUpdateEmail);
        rbAlmacenamiento = (RadioButton) findViewById(R.id.radioButtonUpdateAlmacenamiento);
        rbPass = (RadioButton) findViewById(R.id.radioButtonUpdatePass);
        rbNube = (RadioButton) findViewById(R.id.radioButtonNube);
        rbDispositivo = (RadioButton) findViewById(R.id.radioButtonDispositivo);
        rbHuella = (RadioButton) findViewById(R.id.radioButtonHuella);
        rbPIN = (RadioButton) findViewById(R.id.radioButtonPIN);
        rbSinBloqueo = (RadioButton) findViewById(R.id.radioButtonSinBloqueo);
        progressBarPerfil = findViewById(R.id.progressBarPerfil);
        final LinearLayout layoutEmail = (LinearLayout) findViewById(R.id.layoutEmail);
        final LinearLayout layoutPass = (LinearLayout) findViewById(R.id.layoutPass);


        final boolean huella = sharedPreferences.getBoolean(Constantes.PREFERENCE_HUELLA, false);
        boolean pin = sharedPreferences.getBoolean(Constantes.PREFERENCE_PIN, false);
        final boolean sinBloqueo = sharedPreferences.getBoolean(Constantes.PREFERENCE_SIN_BLOQUEO, true);

        if (huella) {
            rbHuella.setChecked(true);
        } else if (pin) {
            rbPIN.setChecked(true);
        } else if (sinBloqueo) {
            rbSinBloqueo.setChecked(true);
        }


        if (almacenamientoNube) {
            rbDispositivo.setChecked(true);
        } else {
            rbNube.setChecked(true);
        }

        rbEmail.setChecked(true);
        layoutPass.setVisibility(View.GONE);
        layoutEmail.setVisibility(View.VISIBLE);
        radioGroupAlmacenamiento.setVisibility(View.GONE);
        radioGroupBloqueo.setVisibility(View.GONE);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonUpdateEmail:
                        layoutEmail.setVisibility(View.VISIBLE);
                        layoutPass.setVisibility(View.GONE);
                        radioGroupAlmacenamiento.setVisibility(View.GONE);
                        radioGroupBloqueo.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonUpdatePass:
                        layoutEmail.setVisibility(View.GONE);
                        layoutPass.setVisibility(View.VISIBLE);
                        radioGroupAlmacenamiento.setVisibility(View.GONE);
                        radioGroupBloqueo.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonUpdateAlmacenamiento:
                        layoutEmail.setVisibility(View.GONE);
                        layoutPass.setVisibility(View.GONE);
                        radioGroupAlmacenamiento.setVisibility(View.VISIBLE);
                        radioGroupBloqueo.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonUpdateBloqueo:
                        layoutEmail.setVisibility(View.GONE);
                        layoutPass.setVisibility(View.GONE);
                        radioGroupAlmacenamiento.setVisibility(View.GONE);
                        radioGroupBloqueo.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });

        Button button = (Button) findViewById(R.id.buttonUpdateEmail);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rbEmail.isChecked()) {
                    validarEmail();
                } else if (rbPass.isChecked()) {
                    validarPass();
                } else if (rbAlmacenamiento.isChecked()) {
                    actualizarAlmacenamiento();
                }
            }
        });


    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    public void validarEmail() {
        String email = etEmail.getText().toString();
        String emailRepetir = etRepetirEmail.getText().toString();

        if (!email.isEmpty()) {
            if (email.contains("@")) {
                if (email.equals(emailRepetir)) {
                    actualizarEmail(email);
                } else {
                    etEmail.setError("Las correos deben coincidir");
                    etRepetirEmail.setError("Las correos deben coincidir");
                }
            } else {
                etEmail.setError("Formato incorrecto para correo");

            }
        } else {
            etEmail.setError("El campo no puede estar vacío");
            etRepetirEmail.setError("El campo no puede estar vacío");
        }
    }

    public void validarPass() {
        String pass = etPass.getText().toString();
        String passRepetir = etRepetirPass.getText().toString();

        if (pass.isEmpty() || (pass.length() < 6)) {
            etPass.setError("Mínimo 6 caracteres");
            etRepetirPass.setError("Mínimo 6 caracteres");
        } else {
            if (pass.equals(passRepetir)) {
                actualizarPass(pass);
            } else {
                etPass.setError("Las contraseñas deben coincidir");
                etRepetirPass.setError("Las contraseñas deben coincidir");
            }

        }

    }

    public void actualizarEmail(String email) {
        progressBarPerfil.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("msg", "User email address updated.");
                                finish();
                                progressBarPerfil.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getApplicationContext(), "Error al actualizar. Intene nuevamente", Toast.LENGTH_SHORT).show();
                                progressBarPerfil.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    public void actualizarPass(String pass) {
        progressBarPerfil.setVisibility(View.VISIBLE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updatePassword(pass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("msg", "User password updated.");
                                finish();
                                progressBarPerfil.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getApplicationContext(), "Error al actualizar. Intene nuevamente", Toast.LENGTH_SHORT).show();
                                progressBarPerfil.setVisibility(View.GONE);
                            }
                        }
                    });
        }
    }

    public void actualizarAlmacenamiento() {
        progressBarPerfil.setVisibility(View.VISIBLE);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> almacenar = new HashMap<>();


        if (rbNube.isChecked()) {
            //almacenar.put(Constantes.INTERNO, false);
            //almacenar.put(Constantes.EXTERNO, true);
            almacenar.put(Constantes.ALMACENAMIENTO_ESCOGIDO, true);
        } else if (rbDispositivo.isChecked()) {
            //almacenar.put(Constantes.INTERNO, true);
            //almacenar.put(Constantes.EXTERNO, false);
            almacenar.put(Constantes.ALMACENAMIENTO_ESCOGIDO, true);
        }



        db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.ALMACENAMIENTO).document(Constantes.ALMACENAMIENTO_DOC).set(almacenar).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("msg", "Succes");
                progressBarPerfil.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("msg", "Error adding document", e);
                Toast.makeText(getApplicationContext(), "Error al configurar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                progressBarPerfil.setVisibility(View.GONE);
            }
        });
    }
}
