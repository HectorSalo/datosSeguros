package com.skysam.datossegurosFirebaseFinal.accounts.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.skysam.datossegurosFirebaseFinal.common.model.Account;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddCuentasFragment extends Fragment {

    private TextInputLayout inputLayoutTitular, inputLayoutBanco, inputLayoutNumero, inputLayoutCedula, inputLayoutTelefono, inputLayoutCorreo;
    private TextInputEditText etTitular, etBanco, etNumeroCuenta, etCedula, etTelefono, etCorreo;
    private RadioButton rbAhorro, rbCorriente;
    private ProgressBar progressBar;
    private String spinnerSeleccion;
    private Spinner spinnerDocumento;
    private Button buttonGuardar;

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
        buttonGuardar = vista.findViewById(R.id.guardarCuenta);

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

        etTitular = vista.findViewById(R.id.et_titular);
        etBanco = vista.findViewById(R.id.et_banco);
        etNumeroCuenta = vista.findViewById(R.id.et_numero_cuenta);
        etCedula = vista.findViewById(R.id.et_documento);
        etTelefono = vista.findViewById(R.id.et_telefono);
        inputLayoutTitular = vista.findViewById(R.id.outlined_titular);
        inputLayoutBanco = vista.findViewById(R.id.outlined_banco);
        inputLayoutCedula = vista.findViewById(R.id.outlined_documento);
        inputLayoutNumero = vista.findViewById(R.id.outlined_numero_cuenta);
        inputLayoutTelefono = vista.findViewById(R.id.outlined_telefono);
        inputLayoutCorreo = vista.findViewById(R.id.outlined_correo);
        etCorreo = vista.findViewById(R.id.et_correo);
        rbAhorro = vista.findViewById(R.id.radioButtonAhorro);
        rbCorriente = vista.findViewById(R.id.radioButtonCorriente);
        spinnerDocumento = vista.findViewById(R.id.spinnerTipoDocumento);
        progressBar = vista.findViewById(R.id.progressBarAddCuenta);

        rbAhorro.setChecked(true);

        String [] spDocumentos = {"Cédula", "RIF", "Pasaporte"};
        ArrayAdapter<String> adapterDocumentos = new ArrayAdapter<>(requireContext(), R.layout.spinner_opciones, spDocumentos);
        spinnerDocumento.setAdapter(adapterDocumentos);

        buttonGuardar.setOnClickListener(v -> validarDatos());

        return vista;
    }

    private void validarDatos () {
        inputLayoutTitular.setError(null);
        inputLayoutBanco.setError(null);
        inputLayoutNumero.setError(null);
        inputLayoutCedula.setError(null);
        String titular = etTitular.getText().toString();
        String banco = etBanco.getText().toString();
        String documento = etCedula.getText().toString();
        String numeroCuenta = etNumeroCuenta.getText().toString();
        String telefono = etTelefono.getText().toString();
        String correo = etCorreo.getText().toString();

        boolean datoValido;

        if (!titular.isEmpty()) {
            datoValido = true;
        } else {
            inputLayoutTitular.setError("El campo no puede estar vacío");
            datoValido = false;
        }

        if (!banco.isEmpty()) {
            datoValido = true;
        } else {
            datoValido = false;
            inputLayoutBanco.setError("El campo no puede estar vacío");
        }

        if (!documento.isEmpty()) {
            datoValido = true;
        } else {
            datoValido = false;
            inputLayoutCedula.setError("El campo no puede estar vacío");
        }

        if (!numeroCuenta.isEmpty()) {
            if (numeroCuenta.length() == 20) {
                datoValido = true;
            } else {
                datoValido = false;
                inputLayoutNumero.setError("El número debe ser de 20 dígitos");
            }
        } else {
            datoValido = false;
            inputLayoutNumero.setError("El campo no puede estar vacío");
        }

        if (telefono.isEmpty()) {
            telefono = "";
        }

        if (correo.isEmpty()) {
            correo = "";
        }

        if (datoValido) {
            guardarCuentaFirebase(titular, banco, documento, numeroCuenta, telefono, correo);
        }
    }

    public void guardarCuentaFirebase(String titular, String banco, String cedula, String cuentaNumero, String telefono, String correo) {
        progressBar.setVisibility(View.VISIBLE);
        spinnerSeleccion = spinnerDocumento.getSelectedItem().toString();
        String tipo = "";

        if (rbAhorro.isChecked()) {
            tipo = "Ahorro";
        } else if (rbCorriente.isChecked()) {
            tipo = "Corriente";
        }

        inputLayoutTitular.setEnabled(false);
        inputLayoutBanco.setEnabled(false);
        inputLayoutNumero.setEnabled(false);
        inputLayoutCedula.setEnabled(false);
        inputLayoutTelefono.setEnabled(false);
        inputLayoutCorreo.setEnabled(false);
        spinnerDocumento.setEnabled(false);
        rbAhorro.setEnabled(false);
        rbCorriente.setEnabled(false);
        buttonGuardar.setEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> cuentaBancaria = new HashMap<>();
        cuentaBancaria.put(Constants.BD_TITULAR_BANCO, titular);
        cuentaBancaria.put(Constants.BD_BANCO, banco);
        cuentaBancaria.put(Constants.BD_NUMERO_CUENTA, cuentaNumero);
        cuentaBancaria.put(Constants.BD_CEDULA_BANCO, cedula);
        cuentaBancaria.put(Constants.BD_TIPO_DOCUMENTO, spinnerSeleccion);
        cuentaBancaria.put(Constants.BD_TIPO_CUENTA, tipo);
        cuentaBancaria.put(Constants.BD_TELEFONO, telefono);
        cuentaBancaria.put(Constants.BD_CORREO_CUENTA, correo);

        db.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid())
                .collection(Constants.BD_CUENTAS).add(cuentaBancaria).addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();

                }).addOnFailureListener(e -> {
                    Log.w("msg", "Error adding document", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    inputLayoutTitular.setEnabled(true);
                    inputLayoutBanco.setEnabled(true);
                    inputLayoutNumero.setEnabled(true);
                    inputLayoutCedula.setEnabled(true);
                    inputLayoutTelefono.setEnabled(true);
                    inputLayoutCorreo.setEnabled(true);
                    spinnerDocumento.setEnabled(true);
                    rbAhorro.setEnabled(true);
                    rbCorriente.setEnabled(true);
                    buttonGuardar.setEnabled(true);
                });
    }
}
