package com.skysam.datossegurosFirebaseFinal.Fragments;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class AddTarjetaFragment extends Fragment {

    private TextInputLayout inputLayoutTitular, inputLayoutTarjeta, inputLayoutCVV, inputLayoutCedula, inputLayoutBanco, inputLayoutVencimiento;
    private TextInputEditText etTitular, etTarjeta, etCVV, etCedula, etBanco, etVencimiento, etClave;
    private EditText etOtroTarjeta;
    private RadioButton rbVisa, rbMastercard, rbOtro, rbMaestro, rbNube;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private String tipo;


    private OnFragmentInteractionListener mListener;

    public AddTarjetaFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_add_tarjeta, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        boolean almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

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
        RadioButton rbDispositivo = vista.findViewById(R.id.radioButton_dispositivo);
        Button buttonGuardar = vista.findViewById(R.id.guardarTarjeta);
        progressBar = vista.findViewById(R.id.progressBarAddTarjeta);

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

        rbMaestro.setChecked(true);

        if (almacenamientoNube) {
            rbNube.setChecked(true);
        } else {
            rbDispositivo.setChecked(true);
        }

        radioTarjeta.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonOtroTarjeta) {
                    etOtroTarjeta.setVisibility(View.VISIBLE);
                } else {
                    etOtroTarjeta.setVisibility(View.GONE);
                }
            }
        });

        etVencimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escogerVencimiento();
            }
        });

        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });

        return vista;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
            tipo = etOtroTarjeta.getText().toString();
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
            if (rbNube.isChecked()) {
                guardarTarjetaFirebase(titular, banco, tarjeta, cvv, vencimiento, cedula, clave);
            } else {
                guardarTarjetaSQLite(titular, banco, tarjeta, cvv, vencimiento, cedula, clave);
            }
        }
    }

    public void guardarTarjetaFirebase(String titular, String banco, String numeroTarjeta, String cvv, String vencimiento, String cedula, String clave) {
        progressBar.setVisibility(View.VISIBLE);
        String userID = user.getUid();

        if (rbMaestro.isChecked()) {
            tipo = "Maestro";
        } else if (rbMastercard.isChecked()) {
            tipo = "Mastercard";
        } else if (rbVisa.isChecked()) {
            tipo = "Visa";
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> tarjeta = new HashMap<>();
        tarjeta.put(Constantes.BD_TITULAR_TARJETA, titular);
        tarjeta.put(Constantes.BD_BANCO_TARJETA, banco);
        tarjeta.put(Constantes.BD_NUMERO_TARJETA, numeroTarjeta);
        tarjeta.put(Constantes.BD_CVV, cvv);
        tarjeta.put(Constantes.BD_CEDULA_TARJETA, cedula);
        tarjeta.put(Constantes.BD_TIPO_TARJETA, tipo);
        tarjeta.put(Constantes.BD_VENCIMIENTO_TARJETA, vencimiento);
        tarjeta.put(Constantes.BD_CLAVE_TARJETA, clave);

        db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_TARJETAS).add(tarjeta).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
                Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void guardarTarjetaSQLite(String titular, String banco, String numeroTarjeta, String cvv, String vencimiento, String cedula, String clave) {
        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constantes.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        if (rbMaestro.isChecked()) {
            tipo = "Maestro";
        } else if (rbMastercard.isChecked()) {
            tipo = "Mastercard";
        } else if (rbVisa.isChecked()) {
            tipo = "Visa";
        }

        ContentValues values = new ContentValues();
        values.put(Constantes.BD_TITULAR_TARJETA, titular);
        values.put(Constantes.BD_BANCO_TARJETA, banco);
        values.put(Constantes.BD_NUMERO_TARJETA, numeroTarjeta);
        values.put(Constantes.BD_CVV, cvv);
        values.put(Constantes.BD_CEDULA_TARJETA, cedula);
        values.put(Constantes.BD_TIPO_TARJETA, tipo);
        values.put(Constantes.BD_VENCIMIENTO_TARJETA, vencimiento);
        values.put(Constantes.BD_CLAVE_TARJETA, clave);

        db.insert(Constantes.BD_TARJETAS, null, values);
        db.close();

        Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }

    public void escogerVencimiento() {
        LayoutInflater inflater = getLayoutInflater();
        View vista = inflater.inflate(R.layout.vencimiento_tarjeta_picker, null);
        final NumberPicker monthPicker = (NumberPicker) vista.findViewById(R.id.mesPicker);
        final NumberPicker anualPicker = (NumberPicker) vista.findViewById(R.id.anualPicker);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        anualPicker.setMinValue(2019);
        anualPicker.setMaxValue(2030);

        dialog.setTitle("Escoja mes y año")
                .setView(vista)
                .setPositiveButton("Seleccionar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        etVencimiento.setText(monthPicker.getValue() + "/" + anualPicker.getValue());
                    }
                })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        dialog.show();
    }
}
