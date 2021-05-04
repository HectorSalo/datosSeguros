package com.skysam.datossegurosFirebaseFinal.cards.ui;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import com.skysam.datossegurosFirebaseFinal.common.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
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

public class EditarTarjetaFragment extends Fragment {

    private TextInputLayout inputLayoutTitular, inputLayoutTarjeta, inputLayoutCVV, inputLayoutCedula, inputLayoutBanco, inputLayoutVencimiento, inputLayoutClave;
    private TextInputEditText etTitular, etTarjeta, etCVV, etCedula, etBanco, etVencimiento, etClave;
    private EditText etOtroTarjeta;
    private RadioButton rbVisa, rbMastercard, rbOtro, rbMaestro;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private String idDoc;
    private Button button;
    private boolean almacenamientoNube;

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

        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        String tema = sharedPreferences.getString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO);

        almacenamientoNube = sharedPreferences.getBoolean(Constants.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        idDoc = getArguments().getString("id");

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

        switch (tema){
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

        if (almacenamientoNube) {
            cargarDataFirebase();
        } else {
            cargarDataSQLite();
        }

        etVencimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escogerVencimiento();
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validarDatos();
            }
        });
        return vista;
    }

    public void cargarDataFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        String userID = user.getUid();

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_TARJETAS);

        reference.document(idDoc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
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

                    if (tipo.equals("Maestro")) {
                        rbMaestro.setChecked(true);
                    } else if (tipo.equals("Visa")) {
                        rbVisa.setChecked(true);
                    } else if (tipo.equals("Mastercard")) {
                        rbMastercard.setChecked(true);
                    } else {
                        rbOtro.setChecked(true);
                        etOtroTarjeta.setText(tipo);
                    }

                    progressBar.setVisibility(View.GONE);

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                }
            }
        });
    }

    public void cargarDataSQLite() {
        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constants.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.BD_TARJETAS + " WHERE idTarjeta =" + idDoc, null);

        if (cursor.moveToFirst()) {
            etTitular.setText(cursor.getString(1));
            etTarjeta.setText(cursor.getString(2));
            etCVV.setText(cursor.getString(3));
            etCedula.setText(cursor.getString(4));
            String tipo = cursor.getString(5);
            etBanco.setText(cursor.getString(6));
            etVencimiento.setText(cursor.getString(7));
            etClave.setText(cursor.getString(8));

            if (tipo.equals("Maestro")) {
                rbMaestro.setChecked(true);
            } else if (tipo.equals("Visa")) {
                rbVisa.setChecked(true);
            } else if (tipo.equals("Mastercard")) {
                rbMastercard.setChecked(true);
            } else {
                rbOtro.setChecked(true);
                etOtroTarjeta.setText(tipo);
            }

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
            if (almacenamientoNube) {
                guardarDataFirebase(titular, banco, tarjeta, cvv, vencimiento, cedula, clave);
            } else {
                guardarDataSQLite(titular, banco, tarjeta, cvv, vencimiento, cedula, clave);
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
        String userID = user.getUid();
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

        db.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_TARJETAS).document(idDoc).update(tarjeta).addOnSuccessListener(new OnSuccessListener<Void>() {

            public void onSuccess(Void aVoid) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                requireActivity().finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
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
            }
        });
    }

    public void guardarDataSQLite(String titular, String banco, String numeroTarjeta, String cvv, String vencimiento, String cedula, String clave) {
        String tipo = "";

        if (rbMaestro.isChecked()) {
            tipo = "Maestro";
        } else if (rbMastercard.isChecked()) {
            tipo = "Mastercard";
        } else if (rbVisa.isChecked()) {
            tipo = "Visa";
        }

        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constants.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.BD_TITULAR_TARJETA, titular);
        values.put(Constants.BD_NUMERO_TARJETA, numeroTarjeta);
        values.put(Constants.BD_CVV, cvv);
        values.put(Constants.BD_CEDULA_TARJETA, cedula);
        values.put(Constants.BD_TIPO_TARJETA, tipo);
        values.put(Constants.BD_BANCO_TARJETA, banco);
        values.put(Constants.BD_VENCIMIENTO_TARJETA, vencimiento);
        values.put(Constants.BD_CLAVE_TARJETA, clave);

        db.update(Constants.BD_TARJETAS, values, "idTarjeta=" + idDoc, null);
        db.close();

        Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
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
