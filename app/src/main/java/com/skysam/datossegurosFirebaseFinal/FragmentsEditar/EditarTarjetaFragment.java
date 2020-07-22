package com.skysam.datossegurosFirebaseFinal.FragmentsEditar;

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

import com.skysam.datossegurosFirebaseFinal.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;
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
import java.util.Objects;

public class EditarTarjetaFragment extends Fragment {

    private EditText etTitular, etnumeroTarjeta, etCVV, etCedula, etOtro, etBanco, etVencimiento, etClave;
    private RadioButton rbMastercard, rbVisa, rbOtro, rbMaestro;
    private FirebaseUser user;
    private ProgressBar progressBarEditar;
    private String idDoc;


    private OnFragmentInteractionListener mListener;

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

        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        final boolean almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        idDoc = getArguments().getString("id");

        etTitular = (EditText) vista.findViewById(R.id.etTitularTarjetaEditar);
        etnumeroTarjeta = (EditText) vista.findViewById(R.id.etTarjetaEditar);
        etCedula = (EditText) vista.findViewById(R.id.etCedulaTarjetaEditar);
        etCVV = (EditText) vista.findViewById(R.id.etnumeroCVVEditar);
        etOtro = (EditText) vista.findViewById(R.id.editTextOtroTarjetaEditar);
        etBanco = (EditText) vista.findViewById(R.id.etBancoTarjetaEditar);
        etVencimiento = (EditText) vista.findViewById(R.id.etVencimientoEditar);
        etClave = (EditText) vista.findViewById(R.id.etClaveTarjetaEditar);
        rbMastercard = (RadioButton) vista.findViewById(R.id.radioButtonMasterEditar);
        rbVisa = (RadioButton) vista.findViewById(R.id.radioButtonVisaEditar);
        rbOtro = (RadioButton) vista.findViewById(R.id.radioButtonOtroTarjetaEditar);
        rbMaestro = (RadioButton) vista.findViewById(R.id.radioButtonMaestroEditar);
        RadioGroup radioEditar = (RadioGroup) vista.findViewById(R.id.radioTarjetaEditar);
        progressBarEditar = vista.findViewById(R.id.progressBarEditarTarjeta);


        radioEditar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonVisaEditar:
                        etOtro.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonMaestroEditar:
                        etOtro.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonMasterEditar:
                        etOtro.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonOtroTarjetaEditar:
                        etOtro.setVisibility(View.VISIBLE);
                        break;
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

        Button button = (Button) vista.findViewById(R.id.guardarTarjetaEditar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(almacenamientoNube) {
                    guardarDataFirebase();
                } else {
                    guardarDataSQLite();
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

    public void cargarDataFirebase() {
        progressBarEditar.setVisibility(View.VISIBLE);
        String userID = user.getUid();

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_TARJETAS);

        reference.document(idDoc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    etTitular.setText(doc.getString(Constantes.BD_TITULAR_TARJETA));
                    etnumeroTarjeta.setText(doc.getString(Constantes.BD_NUMERO_TARJETA));
                    etCVV.setText(doc.getString(Constantes.BD_CVV));
                    etCedula.setText(doc.getString(Constantes.BD_CEDULA_TARJETA));
                    etBanco.setText(doc.getString(Constantes.BD_BANCO_TARJETA));
                    etVencimiento.setText(doc.getString(Constantes.BD_VENCIMIENTO_TARJETA));
                    etClave.setText(doc.getString(Constantes.BD_CLAVE_TARJETA));

                    String tipo = doc.getString(Constantes.BD_TIPO_TARJETA);

                    if (tipo.equals("Maestro")) {
                        rbMaestro.setChecked(true);
                    } else if (tipo.equals("Visa")) {
                        rbVisa.setChecked(true);
                    } else if (tipo.equals("Mastercard")) {
                        rbMastercard.setChecked(true);
                    } else {
                        rbOtro.setChecked(true);
                        etOtro.setText(tipo);
                    }

                    progressBarEditar.setVisibility(View.GONE);

                } else {
                    progressBarEditar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void cargarDataSQLite() {
        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constantes.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.BD_TARJETAS + " WHERE idTarjeta =" + idDoc, null);

        if (cursor.moveToFirst()) {
            etTitular.setText(cursor.getString(1));
            etnumeroTarjeta.setText(cursor.getString(2));
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
                etOtro.setText(tipo);
            }

        }

    }

    public void guardarDataFirebase() {
        String userID = user.getUid();
        String titular = etTitular.getText().toString();
        String numeroTarjeta = etnumeroTarjeta.getText().toString();
        String cvv = etCVV.getText().toString();
        String cedula = etCedula.getText().toString();
        String banco = etBanco.getText().toString();
        String vencimiento = etVencimiento.getText().toString();
        String clave = etClave.getText().toString();
        String tipo = "";

        if (titular.isEmpty() || numeroTarjeta.isEmpty() || cvv.isEmpty() || cedula.isEmpty() || banco.isEmpty()) {
            Toast.makeText(getContext(), "Hay campos importantes vacíos", Toast.LENGTH_SHORT).show();
        } else {
            if (!rbMastercard.isChecked() && !rbVisa.isChecked() && !rbOtro.isChecked() && !rbMaestro.isChecked()) {
                Toast.makeText(getContext(), "Debe seleccionar un tipo de tarjeta", Toast.LENGTH_SHORT).show();
            } else {
                if (numeroTarjeta.length() > 24) {
                    Toast.makeText(getContext(), "La longitud del número de tarjeta no debe ser mayor a 24 dígitos", Toast.LENGTH_LONG).show();
                } else {
                    if (cvv.length() != 3) {
                        Toast.makeText(getContext(), "La longitud del número de CVV debe ser 3 dígitos", Toast.LENGTH_LONG).show();
                    } else {
                        if (rbMaestro.isChecked()) {
                            tipo = "Maestro";
                        } else if (rbMastercard.isChecked()) {
                            tipo = "Mastercard";
                        } else if (rbVisa.isChecked()) {
                            tipo = "Visa";
                        } else if (rbOtro.isChecked()) {
                            tipo = etOtro.getText().toString();
                        }
                        progressBarEditar.setVisibility(View.VISIBLE);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> tarjeta = new HashMap<>();
                        tarjeta.put(Constantes.BD_TITULAR_TARJETA, titular);
                        tarjeta.put(Constantes.BD_NUMERO_TARJETA, numeroTarjeta);
                        tarjeta.put(Constantes.BD_CVV, cvv);
                        tarjeta.put(Constantes.BD_CEDULA_TARJETA, cedula);
                        tarjeta.put(Constantes.BD_TIPO_TARJETA, tipo);
                        tarjeta.put(Constantes.BD_VENCIMIENTO_TARJETA, vencimiento);
                        tarjeta.put(Constantes.BD_CLAVE_TARJETA, clave);
                        tarjeta.put(Constantes.BD_BANCO_TARJETA, banco);

                        db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_TARJETAS).document(idDoc).set(tarjeta).addOnSuccessListener(new OnSuccessListener<Void>() {

                            public void onSuccess(Void aVoid) {
                                progressBarEditar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                                requireActivity().finish();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("msg", "Error adding document", e);
                                progressBarEditar.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }
    }

    public void guardarDataSQLite() {
        String titular = etTitular.getText().toString();
        String numeroTarjeta = etnumeroTarjeta.getText().toString();
        String cvv = etCVV.getText().toString();
        String cedula = etCedula.getText().toString();
        String banco = etBanco.getText().toString();
        String vencimiento = etVencimiento.getText().toString();
        String clave = etClave.getText().toString();
        String tipo = "";

        if (titular.isEmpty() || numeroTarjeta.isEmpty() || cvv.isEmpty() || cedula.isEmpty() || banco.isEmpty()) {
            Toast.makeText(getContext(), "Hay campos importantes vacíos", Toast.LENGTH_SHORT).show();
        } else {
            if (!rbMastercard.isChecked() && !rbVisa.isChecked() && !rbOtro.isChecked() && !rbMaestro.isChecked()) {
                Toast.makeText(getContext(), "Debe seleccionar un tipo de tarjeta", Toast.LENGTH_SHORT).show();
            } else {
                if (numeroTarjeta.length() > 24) {
                    Toast.makeText(getContext(), "La longitud del número de tarjeta no debe ser mayor a 24 dígitos", Toast.LENGTH_LONG).show();
                } else {
                    if (cvv.length() != 3) {
                        Toast.makeText(getContext(), "La longitud del número de CVV debe ser 3 dígitos", Toast.LENGTH_LONG).show();
                    } else {
                        if (rbMaestro.isChecked()) {
                            tipo = "Maestro";
                        } else if (rbMastercard.isChecked()) {
                            tipo = "Mastercard";
                        } else if (rbVisa.isChecked()) {
                            tipo = "Visa";
                        } else if (rbOtro.isChecked()) {
                            tipo = etOtro.getText().toString();
                        }

                        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constantes.VERSION_SQLITE);
                        SQLiteDatabase db = conect.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put(Constantes.BD_TITULAR_TARJETA, titular);
                        values.put(Constantes.BD_NUMERO_TARJETA, numeroTarjeta);
                        values.put(Constantes.BD_CVV, cvv);
                        values.put(Constantes.BD_CEDULA_TARJETA, cedula);
                        values.put(Constantes.BD_TIPO_TARJETA, tipo);
                        values.put(Constantes.BD_BANCO_TARJETA, banco);
                        values.put(Constantes.BD_VENCIMIENTO_TARJETA, vencimiento);
                        values.put(Constantes.BD_CLAVE_TARJETA, clave);

                        db.update(Constantes.BD_TARJETAS, values, "idTarjeta=" + idDoc, null);
                        db.close();

                        Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                        requireActivity().finish();

                    }
                }
            }
        }
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