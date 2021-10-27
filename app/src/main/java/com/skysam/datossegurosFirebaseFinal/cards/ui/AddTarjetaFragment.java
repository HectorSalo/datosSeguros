package com.skysam.datossegurosFirebaseFinal.cards.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.database.room.Room;
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Card;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddTarjetaFragment extends Fragment {

    private TextInputLayout inputLayoutTitular, inputLayoutTarjeta, inputLayoutCVV, inputLayoutCedula, inputLayoutBanco, inputLayoutVencimiento, inputLayoutClave;
    private TextInputEditText etTitular, etTarjeta, etCVV, etCedula, etBanco, etVencimiento, etClave;
    private EditText etOtroTarjeta;
    private RadioButton rbVisa, rbMastercard, rbOtro, rbMaestro, rbNube, rbDispositivo;
    private ProgressBar progressBar;
    private String tipo;
    private Button buttonGuardar;

    public AddTarjetaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_add_tarjeta, container, false);
        etTitular = vista.findViewById(R.id.et_titular);
        etCedula = vista.findViewById(R.id.et_cedula);
        etTarjeta = vista.findViewById(R.id.et_tarjeta);
        etCVV = vista.findViewById(R.id.et_cvv);
        inputLayoutTitular = vista.findViewById(R.id.outlined_titular);
        inputLayoutTarjeta = vista.findViewById(R.id.outlined_tarjeta);
        inputLayoutCVV = vista.findViewById(R.id.outlined_cvv);
        inputLayoutCedula = vista.findViewById(R.id.outlined_cedula);
        inputLayoutBanco = vista.findViewById(R.id.outlined_banco);
        inputLayoutVencimiento = vista.findViewById(R.id.outlined_vencimiento);
        inputLayoutClave = vista.findViewById(R.id.outlined_clave);
        etOtroTarjeta = vista.findViewById(R.id.editTextOtroTarjeta);
        etBanco = vista.findViewById(R.id.et_banco);
        etClave = vista.findViewById(R.id.et_clave);
        etVencimiento = vista.findViewById(R.id.et_vencimiento);
        rbMaestro = vista.findViewById(R.id.radioButtonMaestro);
        rbMastercard = vista.findViewById(R.id.radioButtonMaster);
        rbVisa = vista.findViewById(R.id.radioButtonVisa);
        rbOtro = vista.findViewById(R.id.radioButtonOtroTarjeta);
        RadioGroup radioTarjeta = vista.findViewById(R.id.radioTarjeta);
        rbNube = vista.findViewById(R.id.radioButton_nube);
        rbDispositivo = vista.findViewById(R.id.radioButton_dispositivo);
        buttonGuardar = vista.findViewById(R.id.guardarTarjeta);
        progressBar = vista.findViewById(R.id.progressBarAddTarjeta);

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

        rbMaestro.setChecked(true);

        radioTarjeta.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonOtroTarjeta) {
                etOtroTarjeta.setVisibility(View.VISIBLE);
            } else {
                etOtroTarjeta.setVisibility(View.GONE);
            }
        });

        etVencimiento.setOnClickListener(v -> escogerVencimiento());

        buttonGuardar.setOnClickListener(v -> validarDatos());

        return vista;
    }

    private void validarDatos () {
        inputLayoutTitular.setError(null);
        inputLayoutBanco.setError(null);
        inputLayoutTarjeta.setError(null);
        inputLayoutCVV.setError(null);
        inputLayoutVencimiento.setError(null);
        inputLayoutCedula.setError(null);
        String titular = etTitular.getText().toString();
        String banco = etBanco.getText().toString();
        String tarjeta = etTarjeta.getText().toString();
        String cvv = etCVV.getText().toString();
        String vencimiento = etVencimiento.getText().toString();
        String cedula = etCedula.getText().toString();
        String clave = etClave.getText().toString();

        if (titular.isEmpty()) {
            inputLayoutTitular.setError("El campo no puede estar vacío");
            return;
        }
        if (banco.isEmpty()) {
            inputLayoutBanco.setError("El campo no puede estar vacío");
            return;
        }
        if (tarjeta.isEmpty()) {
            inputLayoutTarjeta.setError("El campo no puede estar vacío");
            return;
        }
        if (cvv.isEmpty()) {
            inputLayoutCVV.setError("El campo no puede estar vacío");
            return;
        }
        if (cvv.length() != 3) {
            inputLayoutCVV.setError("La longitud debe ser 3 dígitos");
            return;
        }
        if (vencimiento.isEmpty()) {
            inputLayoutVencimiento.setError("Debe escoger la fecha de vencimiento");
            return;
        }
        if (rbOtro.isChecked()) {
            tipo = etOtroTarjeta.getText().toString();
            if (tipo.isEmpty()) {
                etOtroTarjeta.setError("El campo no puede estar vacío");
                return;
            }
        }
        if (cedula.isEmpty()) {
            inputLayoutCedula.setError("El campo no puede estar vacío");
            return;
        }
        if (clave.isEmpty()) {
            clave = "";
        }
        if (rbNube.isChecked()) {
            guardarTarjetaFirebase(titular, banco, tarjeta, cvv, vencimiento, cedula, clave);
        } else {
            guardarTarjetaRoom(titular, banco, tarjeta, cvv, vencimiento, cedula, clave);
        }
    }

    public void guardarTarjetaFirebase(String titular, String banco, String numeroTarjeta, String cvv, String vencimiento, String cedula, String clave) {
        progressBar.setVisibility(View.VISIBLE);
        inputLayoutTitular.setEnabled(false);
        inputLayoutBanco.setEnabled(false);
        inputLayoutTarjeta.setEnabled(false);
        inputLayoutCVV.setEnabled(false);
        inputLayoutVencimiento.setEnabled(false);
        inputLayoutCedula.setEnabled(false);
        inputLayoutClave.setEnabled(false);
        rbOtro.setEnabled(false);
        rbMaestro.setEnabled(false);
        rbNube.setEnabled(false);
        rbDispositivo.setEnabled(false);
        rbMastercard.setEnabled(false);
        rbVisa.setEnabled(false);
        buttonGuardar.setEnabled(false);

        if (rbMaestro.isChecked()) {
            tipo = "Maestro";
        } else if (rbMastercard.isChecked()) {
            tipo = "Mastercard";
        } else if (rbVisa.isChecked()) {
            tipo = "Visa";
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> tarjeta = new HashMap<>();
        tarjeta.put(Constants.BD_TITULAR_TARJETA, titular);
        tarjeta.put(Constants.BD_BANCO_TARJETA, banco);
        tarjeta.put(Constants.BD_NUMERO_TARJETA, numeroTarjeta);
        tarjeta.put(Constants.BD_CVV, cvv);
        tarjeta.put(Constants.BD_CEDULA_TARJETA, cedula);
        tarjeta.put(Constants.BD_TIPO_TARJETA, tipo);
        tarjeta.put(Constants.BD_VENCIMIENTO_TARJETA, vencimiento);
        tarjeta.put(Constants.BD_CLAVE_TARJETA, clave);

        db.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid())
                .collection(Constants.BD_TARJETAS).add(tarjeta).addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();

                }).addOnFailureListener(e -> {
                    Log.w("msg", "Error adding document", e);
                    progressBar.setVisibility(View.GONE);
                    inputLayoutTitular.setEnabled(true);
                    inputLayoutBanco.setEnabled(true);
                    inputLayoutTarjeta.setEnabled(true);
                    inputLayoutCVV.setEnabled(true);
                    inputLayoutVencimiento.setEnabled(true);
                    inputLayoutCedula.setEnabled(true);
                    inputLayoutClave.setEnabled(true);
                    rbOtro.setEnabled(true);
                    rbMaestro.setEnabled(true);
                    rbNube.setEnabled(true);
                    rbDispositivo.setEnabled(true);
                    rbMastercard.setEnabled(true);
                    rbVisa.setEnabled(true);
                    buttonGuardar.setEnabled(true);
                    Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                });
    }

    public void guardarTarjetaRoom(String titular, String banco, String numeroTarjeta, String cvv, String vencimiento, String cedula, String clave) {
        Calendar calendar = Calendar.getInstance();
        if (rbMaestro.isChecked()) {
            tipo = "Maestro";
        } else if (rbMastercard.isChecked()) {
            tipo = "Mastercard";
        } else if (rbVisa.isChecked()) {
            tipo = "Visa";
        }
        Card card = new Card(String.valueOf(calendar.getTimeInMillis()),
                titular, banco, numeroTarjeta, cedula, tipo, cvv, vencimiento, clave, false, false);
        Room.INSTANCE.saveCard(card);

        Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }

    public void escogerVencimiento() {
        LayoutInflater inflater = getLayoutInflater();
        View vista = inflater.inflate(R.layout.vencimiento_tarjeta_picker, null);
        final NumberPicker monthPicker = vista.findViewById(R.id.mesPicker);
        final NumberPicker anualPicker = vista.findViewById(R.id.anualPicker);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        anualPicker.setMinValue(2019);
        anualPicker.setMaxValue(2030);

        dialog.setTitle("Escoja mes y año")
                .setView(vista)
                .setPositiveButton("Seleccionar", (dialog1, which) -> etVencimiento.setText(monthPicker.getValue() + "/" + anualPicker.getValue()))
                .setNegativeButton("Cancelar", (dialog12, which) -> dialog12.dismiss());
        dialog.show();
    }
}
