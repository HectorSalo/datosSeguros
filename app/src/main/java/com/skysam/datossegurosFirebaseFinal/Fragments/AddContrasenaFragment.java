package com.skysam.datossegurosFirebaseFinal.Fragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.skysam.datossegurosFirebaseFinal.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AddContrasenaFragment extends Fragment {

    private TextInputEditText etServicio, etUsuario, etContrasena;
    private TextInputLayout inputLayoutUsuario, inputLayoutPass, inputLayoutServicio;
    private EditText etOtroDias;
    private RadioButton rbNube;
    private RadioGroup radioGroup;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private Date fechaActual;
    private Spinner spinner;
    private Button buttonGuardar;


    private OnFragmentInteractionListener mListener;

    public AddContrasenaFragment() {
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

        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        boolean almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        etServicio = vista.findViewById(R.id.et_servicio);
        etUsuario = vista.findViewById(R.id.et_usuario);
        etContrasena = vista.findViewById(R.id.et_pass);
        etOtroDias = vista.findViewById(R.id.etIngreseOtro);
        inputLayoutUsuario = vista.findViewById(R.id.outlined_usuario);
        inputLayoutPass = vista.findViewById(R.id.outlined_pass);
        inputLayoutServicio = vista.findViewById(R.id.outlined_servicio);
        rbNube = vista.findViewById(R.id.radioButton_nube);
        RadioButton rbDispositivo = vista.findViewById(R.id.radioButton_dispositivo);
        progressBar = vista.findViewById(R.id.progressBar);
        spinner = vista.findViewById(R.id.spinner);
        radioGroup = vista.findViewById(R.id.radio_almacenamiento);
        buttonGuardar = vista.findViewById(R.id.guardarContrasena);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case Constantes.PREFERENCE_ROJO:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkRojo));
                break;
            case Constantes.PREFERENCE_MARRON:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkMarron));
                break;
            case Constantes.PREFERENCE_LILA:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkLila));
                break;
        }

        Calendar almanaque = Calendar.getInstance();
        int diaActual = almanaque.get(Calendar.DAY_OF_MONTH);
        int mesActual = almanaque.get(Calendar.MONTH);
        int anualActual = almanaque.get(Calendar.YEAR);
        almanaque.set(anualActual, mesActual, diaActual);
        fechaActual = almanaque.getTime();

        List<String> listaCaducidad = Arrays.asList(getResources().getStringArray(R.array.tiempo_vigencia));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_opciones, listaCaducidad);
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

        if (almacenamientoNube) {
            rbNube.setChecked(true);
        } else {
            rbDispositivo.setChecked(true);
        }

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });

        return vista;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
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
                guardarContrasenaSQLite(servicio, contrasena, usuario, vigencia);
            }
        }
    }

    public void guardarContrasenaFirebase(String servicio, String contrasena, String usuario, String vigencia) {
        progressBar.setVisibility(View.VISIBLE);
        inputLayoutServicio.setEnabled(false);
        inputLayoutUsuario.setEnabled(false);
        inputLayoutPass.setEnabled(false);
        spinner.setEnabled(false);
        radioGroup.setEnabled(false);
        buttonGuardar.setEnabled(false);

        String userID = user.getUid();


        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> contrasenaM = new HashMap<>();
        contrasenaM.put(Constantes.BD_SERVICIO, servicio);
        contrasenaM.put(Constantes.BD_USUARIO, usuario);
        contrasenaM.put(Constantes.BD_PASSWORD, contrasena);
        contrasenaM.put(Constantes.BD_VIGENCIA, vigencia);
        contrasenaM.put(Constantes.BD_PROPIETARIO, userID);
        contrasenaM.put(Constantes.BD_FECHA_CREACION, fechaActual);

        db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CONTRASENAS).add(contrasenaM).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                requireActivity().finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("msg", "Error adding document", e);
                progressBar.setVisibility(View.GONE);
                inputLayoutServicio.setEnabled(true);
                inputLayoutUsuario.setEnabled(true);
                inputLayoutPass.setEnabled(true);
                spinner.setEnabled(true);
                radioGroup.setEnabled(true);
                buttonGuardar.setEnabled(true);
                Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void guardarContrasenaSQLite(String servicio, String contrasena, String usuario, String vigencia) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String fechaS = sdf.format(fechaActual);

        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constantes.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constantes.BD_SERVICIO, servicio);
        values.put(Constantes.BD_USUARIO, usuario);
        values.put(Constantes.BD_PASSWORD, contrasena);
        values.put(Constantes.BD_VIGENCIA, vigencia);
        values.put(Constantes.BD_FECHA_CREACION, fechaS);

        db.insert(Constantes.BD_CONTRASENAS, null, values);
        db.close();

        Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }

}
