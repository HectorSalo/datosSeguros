package com.example.datossegurosFirebaseFinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
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

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String tema = sharedPreferences.getString("tema", "Amarillo");

        switch (tema){
            case "Amarillo":
                setTheme(R.style.Theme_Dialog_MiPerfil);
                break;
            case "Rojo":
                setTheme(R.style.Theme_Dialog_MiPerfilRojo);
                break;
            case "Marron":
                break;
            case "Lila":
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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        final boolean huella = preferences.getBoolean(UtilidadesStatic.HUELLA, false);
        boolean pin = preferences.getBoolean(UtilidadesStatic.PIN, false);
        final boolean sinBloqueo = preferences.getBoolean(UtilidadesStatic.SIN_BLOQUEO, true);

        if (huella) {
            rbHuella.setChecked(true);
        } else if (pin) {
            rbPIN.setChecked(true);
        } else if (sinBloqueo) {
            rbSinBloqueo.setChecked(true);
        }


        if (Utilidades.almacenamientoInterno) {
            rbDispositivo.setChecked(true);
        } else if (Utilidades.almacenamientoExterno) {
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
                } else if (rbHuella.isChecked()) {
                    Utilidades.conf_bloqueo = UtilidadesStatic.HUELLA_INT;
                    startActivity(new Intent(PerfilActivity.this, BloqueoActivity.class));
                } else if (rbPIN.isChecked()) {
                    Utilidades.conf_bloqueo = UtilidadesStatic.PIN_INT;
                    startActivity(new Intent(PerfilActivity.this, BloqueoActivity.class));
                } else if (rbSinBloqueo.isChecked()) {
                    if (sinBloqueo) {
                        finish();
                    } else {
                        Utilidades.conf_bloqueo = UtilidadesStatic.SIN_BLOQUEO_INT;
                        startActivity(new Intent(PerfilActivity.this, BloqueoActivity.class));
                    }
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

            almacenar.put(UtilidadesStatic.INTERNO, false);
            almacenar.put(UtilidadesStatic.EXTERNO, true);
            almacenar.put(UtilidadesStatic.ALMACENAMIENTO_ESCOGIDO, true);
        } else if (rbDispositivo.isChecked()) {
            almacenar.put(UtilidadesStatic.INTERNO, true);
            almacenar.put(UtilidadesStatic.EXTERNO, false);
            almacenar.put(UtilidadesStatic.ALMACENAMIENTO_ESCOGIDO, true);
        }



        db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.ALMACENAMIENTO).document(UtilidadesStatic.ALMACENAMIENTO_DOC).set(almacenar).addOnSuccessListener(new OnSuccessListener<Void>() {
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
