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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.database.room.Room;
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Card;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;

import java.util.HashMap;
import java.util.Map;

public class EditarTarjetaFragment extends Fragment {

    private TextInputLayout inputLayoutTitular, inputLayoutTarjeta, inputLayoutCVV, inputLayoutCedula, inputLayoutBanco, inputLayoutVencimiento, inputLayoutClave;
    private TextInputEditText etTitular, etTarjeta, etCVV, etCedula, etBanco, etVencimiento, etClave;
    private EditText etOtroTarjeta;
    private RadioButton rbVisa, rbMastercard, rbOtro, rbMaestro;
    private ProgressBar progressBar;
    private String idDoc;
    private Button button;
    private boolean isCloud;
    private Card card;


    public EditarTarjetaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_editar_tarjeta, container, false);
        idDoc = getArguments().getString("id");
        isCloud = getArguments().getBoolean("isCloud");

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
        button = vista.findViewById(R.id.guardarTarjeta);
        progressBar = vista.findViewById(R.id.progressBarAddTarjeta);

        switch (SharedPref.INSTANCE.getTheme()){
            case Constants.PREFERENCE_AMARILLO:
                button.setBackgroundColor(getResources().getColor(R.color.color_yellow_dark));
                break;
            case Constants.PREFERENCE_ROJO:
                button.setBackgroundColor(getResources().getColor(R.color.color_red_ligth));
                break;
            case Constants.PREFERENCE_MARRON:
                button.setBackgroundColor(getResources().getColor(R.color.color_camel));
                break;
            case Constants.PREFERENCE_LILA:
                button.setBackgroundColor(getResources().getColor(R.color.color_fucsia));
                break;
        }


        radioTarjeta.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioButtonOtroTarjeta) {
                etOtroTarjeta.setVisibility(View.VISIBLE);
            } else {
                etOtroTarjeta.setVisibility(View.GONE);
            }
        });

        if (isCloud) {
            cargarDataFirebase();
        } else {
            cargarDataSQLite();
        }

        etVencimiento.setOnClickListener(v -> escogerVencimiento());

        button.setOnClickListener(v -> validarDatos());
        return vista;
    }

    public void cargarDataFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid())
                .collection(Constants.BD_TARJETAS);

        reference.document(idDoc).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                etTitular.setText(doc.getString(Constants.BD_TITULAR_TARJETA));
                etTarjeta.setText(doc.getString(Constants.BD_NUMERO_TARJETA));
                etCVV.setText(doc.getString(Constants.BD_CVV));
                etCedula.setText(doc.getString(Constants.BD_CEDULA_TARJETA));
                etBanco.setText(doc.getString(Constants.BD_BANCO_TARJETA));
                etVencimiento.setText(doc.getString(Constants.BD_VENCIMIENTO_TARJETA));
                etClave.setText(doc.getString(Constants.BD_CLAVE_TARJETA));

                String tipo = doc.getString(Constants.BD_TIPO_TARJETA);

                switch (tipo) {
                    case "Maestro":
                        rbMaestro.setChecked(true);
                        break;
                    case "Visa":
                        rbVisa.setChecked(true);
                        break;
                    case "Mastercard":
                        rbMastercard.setChecked(true);
                        break;
                    default:
                        rbOtro.setChecked(true);
                        etOtroTarjeta.setText(tipo);
                        break;
                }

                progressBar.setVisibility(View.GONE);

            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        });
    }

    public void cargarDataSQLite() {
        card = Room.INSTANCE.getCardById(idDoc);
        etTitular.setText(card.getUser());
        etTarjeta.setText(card.getNumberCard());
        etCVV.setText(card.getCvv());
        etCedula.setText(card.getNumberIdUser());
        String tipo = card.getTypeCard();
        etBanco.setText(card.getBank());
        etVencimiento.setText(card.getDateExpiration());
        etClave.setText(card.getCode());

        switch (tipo) {
            case "Maestro":
                rbMaestro.setChecked(true);
                break;
            case "Visa":
                rbVisa.setChecked(true);
                break;
            case "Mastercard":
                rbMastercard.setChecked(true);
                break;
            default:
                rbOtro.setChecked(true);
                etOtroTarjeta.setText(tipo);
                break;
        }
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

        if (!tarjeta.isEmpty()) {
            datoValido = true;
        } else {
            datoValido = false;
            inputLayoutTarjeta.setError("El campo no puede estar vacío");
        }

        if (!cvv.isEmpty()) {
            if (cvv.length() != 3) {
                datoValido = true;
            } else {
                datoValido = false;
                inputLayoutCVV.setError("La longitus debe ser 3 dígitos");
            }
        } else {
            datoValido = false;
            inputLayoutCVV.setError("El campo no puede estar vacío");
        }

        if (!vencimiento.isEmpty()) {
            datoValido = true;
        } else {
            datoValido = false;
            inputLayoutVencimiento.setError("Debe escoger la fecha de vencimiento");
        }

        if (rbOtro.isChecked()) {
            String tipo = etOtroTarjeta.getText().toString();
            if (!tipo.isEmpty()) {
                datoValido = true;
            } else {
                datoValido = false;
                etOtroTarjeta.setError("El campo no puede estar vacío");
            }
        }

        if (!cedula.isEmpty()) {
            datoValido = true;
        } else {
            datoValido = false;
            inputLayoutCedula.setError("El campo no puede estar vacío");
        }

        if (clave.isEmpty()) {
            clave = "";
        }

        if (datoValido) {
            if (isCloud) {
                guardarDataFirebase(titular, banco, tarjeta, cvv, vencimiento, cedula, clave);
            } else {
                guardarDataRoom(titular, banco, tarjeta, cvv, vencimiento, cedula, clave);
            }
        }
    }

    public void guardarDataFirebase(String titular, String banco, String numeroTarjeta, String cvv, String vencimiento, String cedula, String clave) {
        inputLayoutTitular.setEnabled(false);
        inputLayoutBanco.setEnabled(false);
        inputLayoutTarjeta.setEnabled(false);
        inputLayoutCVV.setEnabled(false);
        inputLayoutVencimiento.setEnabled(false);
        inputLayoutCedula.setEnabled(false);
        inputLayoutClave.setEnabled(false);
        rbOtro.setEnabled(false);
        rbMaestro.setEnabled(false);
        rbMastercard.setEnabled(false);
        rbVisa.setEnabled(false);
        button.setEnabled(false);
        String tipo = "";

        if (rbMaestro.isChecked()) {
            tipo = "Maestro";
        } else if (rbMastercard.isChecked()) {
            tipo = "Mastercard";
        } else if (rbVisa.isChecked()) {
            tipo = "Visa";
        }

        progressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> tarjeta = new HashMap<>();
        tarjeta.put(Constants.BD_TITULAR_TARJETA, titular);
        tarjeta.put(Constants.BD_NUMERO_TARJETA, numeroTarjeta);
        tarjeta.put(Constants.BD_CVV, cvv);
        tarjeta.put(Constants.BD_CEDULA_TARJETA, cedula);
        tarjeta.put(Constants.BD_TIPO_TARJETA, tipo);
        tarjeta.put(Constants.BD_VENCIMIENTO_TARJETA, vencimiento);
        tarjeta.put(Constants.BD_CLAVE_TARJETA, clave);
        tarjeta.put(Constants.BD_BANCO_TARJETA, banco);

        db.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid())
                .collection(Constants.BD_TARJETAS).document(idDoc).update(tarjeta).addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                }).addOnFailureListener(e -> {
                    Log.w("msg", "Error adding document", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    inputLayoutTitular.setEnabled(true);
                    inputLayoutBanco.setEnabled(true);
                    inputLayoutTarjeta.setEnabled(true);
                    inputLayoutCVV.setEnabled(true);
                    inputLayoutVencimiento.setEnabled(true);
                    inputLayoutCedula.setEnabled(true);
                    inputLayoutClave.setEnabled(true);
                    rbOtro.setEnabled(true);
                    rbMaestro.setEnabled(true);
                    rbMastercard.setEnabled(true);
                    rbVisa.setEnabled(true);
                    button.setEnabled(true);
                });
    }

    public void guardarDataRoom(String titular, String banco, String numeroTarjeta, String cvv, String vencimiento, String cedula, String clave) {
        String tipo = "";
        if (rbMaestro.isChecked()) {
            tipo = "Maestro";
        } else if (rbMastercard.isChecked()) {
            tipo = "Mastercard";
        } else if (rbVisa.isChecked()) {
            tipo = "Visa";
        }
        card.setUser(titular);
        card.setNumberCard(numeroTarjeta);
        card.setCvv(cvv);
        card.setNumberIdUser(cedula);
        card.setTypeCard(tipo);
        card.setBank(banco);
        card.setDateExpiration(vencimiento);
        card.setCode(clave);

        Room.INSTANCE.updateCard(card);

        Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
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
