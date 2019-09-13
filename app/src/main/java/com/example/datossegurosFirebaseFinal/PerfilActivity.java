package com.example.datossegurosFirebaseFinal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PerfilActivity extends AppCompatActivity {

    private EditText etEmail, etRepetirEmail, etPass, etRepetirPass;
    private RadioButton rbEmail, rbPass;
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        etEmail = (EditText) findViewById(R.id.editTextUpdateEmail);
        etRepetirEmail = (EditText) findViewById(R.id.editTextRepetirUpdateEmail);
        etPass = (EditText) findViewById(R.id.editTextUpdatePass);
        etRepetirPass = (EditText) findViewById(R.id.editTextRepetirUpdatePass);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioGroupUpdate);
        rbEmail = (RadioButton) findViewById(R.id.radioButtonUpdateEmail);
        rbPass = (RadioButton) findViewById(R.id.radioButtonUpdatePass);
        final LinearLayout layoutEmail = (LinearLayout) findViewById(R.id.layoutEmail);
        final LinearLayout layoutPass = (LinearLayout) findViewById(R.id.layoutPass);

        rbEmail.setChecked(true);
        layoutPass.setVisibility(View.GONE);
        layoutEmail.setVisibility(View.VISIBLE);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonUpdateEmail:
                        layoutEmail.setVisibility(View.VISIBLE);
                        layoutPass.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonUpdatePass:
                        layoutEmail.setVisibility(View.GONE);
                        layoutPass.setVisibility(View.VISIBLE);
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
        progress = new ProgressDialog(this);
        progress.setMessage("Actualizando...");
        progress.setCancelable(false);
        progress.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updateEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("msg", "User email address updated.");
                                finish();
                                progress.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error al actualizar. Intene nuevamente", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                            }
                        }
                    });
        }
    }

    public void actualizarPass(String pass) {
        progress = new ProgressDialog(this);
        progress.setMessage("Actualizando...");
        progress.setCancelable(false);
        progress.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updatePassword(pass)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("msg", "User password updated.");
                                finish();
                                progress.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error al actualizar. Intene nuevamente", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                            }
                        }
                    });
        }
    }
}
