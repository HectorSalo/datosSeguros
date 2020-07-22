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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.skysam.datossegurosFirebaseFinal.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AddCuentasFragment extends Fragment {

    private EditText etTitular, etBanco, etNumeroCuenta, etCedula, etTelefono, etCorreo;
    private RadioButton rbAhorro, rbCorriente;
    private FirebaseUser user;
    private ProgressBar progressBarAdd;
    private String spinnerSeleccion;
    private Spinner spinnerDocumento;


    private OnFragmentInteractionListener mListener;

    public AddCuentasFragment() {
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
        View vista = inflater.inflate(R.layout.fragment_add_cuentas, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        final boolean almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        etTitular = (EditText) vista.findViewById(R.id.etTitular);
        etBanco = (EditText) vista.findViewById(R.id.etBanco);
        etNumeroCuenta = (EditText) vista.findViewById(R.id.etnumeroCuenta);
        etCedula = (EditText) vista.findViewById(R.id.etCedulaCuenta);
        etTelefono = (EditText) vista.findViewById(R.id.etTelefono);
        etCorreo = (EditText) vista.findViewById(R.id.etCorreoCuenta);
        rbAhorro = (RadioButton) vista.findViewById(R.id.radioButtonAhorro);
        rbCorriente = (RadioButton) vista.findViewById(R.id.radioButtonCorriente);
        spinnerDocumento = (Spinner) vista.findViewById(R.id.spinnerTipoDocumento);
        user = FirebaseAuth.getInstance().getCurrentUser();
        progressBarAdd = vista.findViewById(R.id.progressBarAddCuenta);


        String [] spDocumentos = {"Cédula", "RIF", "Pasaporte"};
        ArrayAdapter<String> adapterDocumentos = new ArrayAdapter<String>(getContext(), R.layout.spinner_opciones, spDocumentos);
        spinnerDocumento.setAdapter(adapterDocumentos);



        Button buttonGuardar = (Button) vista.findViewById(R.id.guardarCuenta);
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (almacenamientoNube) {
                    guardarCuentaFirebase();
                } else {
                    guardarCuentaSQLite();
                }
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

    public void guardarCuentaFirebase() {
        String userID = user.getUid();
        String titular = etTitular.getText().toString();
        String banco = etBanco.getText().toString();
        String cuentaNumero = etNumeroCuenta.getText().toString();
        String cedula = etCedula.getText().toString();
        String telefono = etTelefono.getText().toString();
        String correo = etCorreo.getText().toString();
        spinnerSeleccion = spinnerDocumento.getSelectedItem().toString();
        String tipo = "";


        if (rbAhorro.isChecked()) {
            tipo = "Ahorro";
        } else if (rbCorriente.isChecked()) {
            tipo = "Corriente";
        }

        if (titular.isEmpty() || banco.isEmpty() || cuentaNumero.isEmpty() || cedula.isEmpty()) {
            Toast.makeText(getContext(), "Hay campos vacíos", Toast.LENGTH_SHORT).show();
        } else {
            if (!rbCorriente.isChecked() && !rbAhorro.isChecked()) {
                Toast.makeText(getContext(), "Debe seleccionar un tipo de cuenta", Toast.LENGTH_SHORT).show();
            } else {
                if (cuentaNumero.length() != 20) {
                    Toast.makeText(getContext(), "La longitud del número de cuenta debe ser 20 dígitos", Toast.LENGTH_LONG).show();
                } else {
                    progressBarAdd.setVisibility(View.VISIBLE);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> cuentaBancaria = new HashMap<>();
                    cuentaBancaria.put(Constantes.BD_TITULAR_BANCO, titular);
                    cuentaBancaria.put(Constantes.BD_BANCO, banco);
                    cuentaBancaria.put(Constantes.BD_NUMERO_CUENTA, cuentaNumero);
                    cuentaBancaria.put(Constantes.BD_CEDULA_BANCO, cedula);
                    cuentaBancaria.put(Constantes.BD_TIPO_DOCUMENTO, spinnerSeleccion);
                    cuentaBancaria.put(Constantes.BD_TIPO_CUENTA, tipo);

                    if (telefono.isEmpty()) {
                        cuentaBancaria.put(Constantes.BD_TELEFONO, "");
                    } else {
                        cuentaBancaria.put(Constantes.BD_TELEFONO, telefono);
                    }

                    if (correo.isEmpty()) {
                        cuentaBancaria.put(Constantes.BD_CORREO_CUENTA,"");
                    } else {
                        cuentaBancaria.put(Constantes.BD_CORREO_CUENTA, correo);
                    }

                    db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CUENTAS).add(cuentaBancaria).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressBarAdd.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                            requireActivity().finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("msg", "Error adding document", e);
                            progressBarAdd.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    public void guardarCuentaSQLite() {

        String titular = etTitular.getText().toString();
        String banco = etBanco.getText().toString();
        String cuentaNumero = etNumeroCuenta.getText().toString();
        String cedula = etCedula.getText().toString();
        String telefono = etTelefono.getText().toString();
        String correo = etCorreo.getText().toString();
        spinnerSeleccion = spinnerDocumento.getSelectedItem().toString();
        String tipo = "";

        if (rbAhorro.isChecked()) {
            tipo = "Ahorro";
        } else if (rbCorriente.isChecked()) {
            tipo = "Corriente";
        }

        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constantes.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        if (titular.isEmpty() || banco.isEmpty() || cuentaNumero.isEmpty() || cedula.isEmpty()) {
            Toast.makeText(getContext(), "Hay campos vacíos", Toast.LENGTH_SHORT).show();
        } else {
            if (!rbCorriente.isChecked() && !rbAhorro.isChecked()) {
                Toast.makeText(getContext(), "Debe seleccionar un tipo de cuenta", Toast.LENGTH_SHORT).show();
            } else {
                if (cuentaNumero.length() != 20) {
                    Toast.makeText(getContext(), "La longitud del número de cuenta debe ser 20 dígitos", Toast.LENGTH_LONG).show();
                } else {

                    ContentValues values = new ContentValues();
                    values.put(Constantes.BD_TITULAR_BANCO, titular);
                    values.put(Constantes.BD_BANCO, banco);
                    values.put(Constantes.BD_NUMERO_CUENTA, cuentaNumero);
                    values.put(Constantes.BD_CEDULA_BANCO, cedula);
                    values.put(Constantes.BD_TIPO_CUENTA, tipo);
                    values.put(Constantes.BD_TIPO_DOCUMENTO, spinnerSeleccion);

                    if (telefono.isEmpty()) {
                        values.put(Constantes.BD_TELEFONO, "");
                    } else {
                        values.put(Constantes.BD_TELEFONO, telefono);
                    }

                    if (correo.isEmpty()) {
                        values.put(Constantes.BD_CORREO_CUENTA, "");
                    } else {
                        values.put(Constantes.BD_CORREO_CUENTA, correo);
                    }

                    db.insert(Constantes.BD_CUENTAS, null, values);
                    db.close();

                    Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                }
            }
        }
    }
}
