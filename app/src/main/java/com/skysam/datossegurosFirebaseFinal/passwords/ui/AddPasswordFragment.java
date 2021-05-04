package com.skysam.datossegurosFirebaseFinal.passwords.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.database.room.Room;
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Password;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddPasswordFragment extends Fragment {

    private TextInputEditText etServicio, etUsuario, etContrasena;
    private TextInputLayout inputLayoutUsuario, inputLayoutPass, inputLayoutServicio;
    private EditText etOtroDias;
    private RadioButton rbNube, rbDispositivo;
    private ProgressBar progressBar;
    private Date fechaActual;
    private Spinner spinner;
    private Button buttonGuardar;

    public AddPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_add_contrasena, container, false);

        etServicio = vista.findViewById(R.id.et_servicio);
        etUsuario = vista.findViewById(R.id.et_usuario);
        etContrasena = vista.findViewById(R.id.et_pass);
        etOtroDias = vista.findViewById(R.id.etIngreseOtro);
        inputLayoutUsuario = vista.findViewById(R.id.outlined_usuario);
        inputLayoutPass = vista.findViewById(R.id.outlined_pass);
        inputLayoutServicio = vista.findViewById(R.id.outlined_servicio);
        rbNube = vista.findViewById(R.id.radioButton_nube);
        rbDispositivo = vista.findViewById(R.id.radioButton_dispositivo);
        progressBar = vista.findViewById(R.id.progressBar);
        spinner = vista.findViewById(R.id.spinner);
        buttonGuardar = vista.findViewById(R.id.guardarContrasena);

        switch (SharedPref.INSTANCE.getTheme()){
            case Constants.PREFERENCE_AMARILLO:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.color_yellow_dark));
                break;
            case Constants.PREFERENCE_ROJO:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.color_red_ligth));
                break;
            case Constants.PREFERENCE_MARRON:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.color_camel));
                break;
            case Constants.PREFERENCE_LILA:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.color_fucsia));
                break;
        }

        Calendar almanaque = Calendar.getInstance();
        int diaActual = almanaque.get(Calendar.DAY_OF_MONTH);
        int mesActual = almanaque.get(Calendar.MONTH);
        int anualActual = almanaque.get(Calendar.YEAR);
        almanaque.set(anualActual, mesActual, diaActual);
        fechaActual = almanaque.getTime();

        List<String> listaCaducidad = Arrays.asList(getResources().getStringArray(R.array.tiempo_vigencia));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_opciones, listaCaducidad);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 6) {
                    etOtroDias.setVisibility(View.GONE);
                } else {
                    etOtroDias.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        buttonGuardar.setOnClickListener(v -> validarDatos());

        return vista;
    }

    private void validarDatos () {
        inputLayoutServicio.setError(null);
        inputLayoutPass.setError(null);
        inputLayoutUsuario.setError(null);
        etOtroDias.setError(null);
        String servicio = etServicio.getText().toString();
        String usuario = etUsuario.getText().toString();
        String contrasena = etContrasena.getText().toString();
        String vigencia = "";

        boolean datoValido;

        if (!usuario.isEmpty()) {
            datoValido = true;
        } else {
            inputLayoutUsuario.setError("El campo no puede estar vacío");
            datoValido = false;
        }

        if (!contrasena.isEmpty()) {
            datoValido = true;
        } else {
            datoValido = false;
            inputLayoutPass.setError("El campo no puede estar vacío");
        }

        if (!servicio.isEmpty()) {
            datoValido = true;
        } else {
            datoValido = false;
            inputLayoutServicio.setError("El campo no puede estar vacío");
        }

        if (spinner.getSelectedItemPosition() > 0) {
            if (spinner.getSelectedItemPosition() == 6) {
                vigencia = etOtroDias.getText().toString();
                if (!vigencia.isEmpty()) {
                    datoValido = true;
                } else {
                    datoValido = false;
                    etOtroDias.setError("El campo no puede estar vacío");
                }
            } else {
                switch (spinner.getSelectedItemPosition()) {
                    case 1:
                        vigencia = "30";
                        break;
                    case 2:
                        vigencia = "60";
                        break;
                    case 3:
                        vigencia = "90";
                        break;
                    case 4:
                        vigencia = "120";
                        break;
                    case 5:
                        vigencia = "0";
                        break;
                }
                datoValido = true;
            }
        } else {
            datoValido = false;
            Toast.makeText(getContext(), "Debe seleccionar la vigencia de la contraseña", Toast.LENGTH_SHORT).show();
        }

        if (datoValido) {
            if (rbNube.isChecked()) {
                guardarContrasenaFirebase(servicio, contrasena, usuario, vigencia);
            } else {
                guardarContrasenaRoom(servicio, contrasena, usuario, vigencia);
            }
        }
    }

    public void guardarContrasenaFirebase(String servicio, String contrasena, String usuario, String vigencia) {
        progressBar.setVisibility(View.VISIBLE);
        inputLayoutServicio.setEnabled(false);
        inputLayoutUsuario.setEnabled(false);
        inputLayoutPass.setEnabled(false);
        spinner.setEnabled(false);
        rbNube.setEnabled(false);
        rbDispositivo.setEnabled(false);
        buttonGuardar.setEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> contrasenaM = new HashMap<>();
        contrasenaM.put(Constants.BD_SERVICIO, servicio);
        contrasenaM.put(Constants.BD_USUARIO, usuario);
        contrasenaM.put(Constants.BD_PASSWORD, contrasena);
        contrasenaM.put(Constants.BD_VIGENCIA, vigencia);
        contrasenaM.put(Constants.BD_PROPIETARIO, Auth.INSTANCE.getCurrenUser().getUid());
        contrasenaM.put(Constants.BD_FECHA_CREACION, fechaActual);

        db.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid())
                .collection(Constants.BD_CONTRASENAS).add(contrasenaM).addOnSuccessListener(documentReference -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
            requireActivity().finish();

        }).addOnFailureListener(e -> {
            Log.w("msg", "Error adding document", e);
            progressBar.setVisibility(View.GONE);
            inputLayoutServicio.setEnabled(true);
            inputLayoutUsuario.setEnabled(true);
            inputLayoutPass.setEnabled(true);
            spinner.setEnabled(true);
            rbNube.setEnabled(true);
            rbDispositivo.setEnabled(true);
            buttonGuardar.setEnabled(true);
            Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
        });
    }

    public void guardarContrasenaRoom(String servicio, String contrasena, String usuario, String vigencia) {
        Password password = new Password(String.valueOf(fechaActual.getTime()),
                servicio, usuario, contrasena, Integer.parseInt(vigencia), fechaActual.getTime(),
                false, null, null, null, null,
                null, false);

        Room.INSTANCE.savePassword(password);

        Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }
}
