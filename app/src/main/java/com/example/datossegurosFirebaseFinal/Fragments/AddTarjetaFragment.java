package com.example.datossegurosFirebaseFinal.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.example.datossegurosFirebaseFinal.ConexionSQLite;
import com.example.datossegurosFirebaseFinal.MainActivity;
import com.example.datossegurosFirebaseFinal.R;
import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddTarjetaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddTarjetaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTarjetaFragment extends Fragment {

    private EditText etTitular, etTarjeta, etCVV, etCedula, etOtroTarjeta, etBanco, etVencimiento, etClave;
    private RadioButton rbVisa, rbMastercard, rbOtro, rbMaestro;
    private FirebaseUser user;
    private ProgressBar progressBarAdd;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddTarjetaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTarjetaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTarjetaFragment newInstance(String param1, String param2) {
        AddTarjetaFragment fragment = new AddTarjetaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_add_tarjeta, container, false);

        etTitular = (EditText) vista.findViewById(R.id.etTitularTarjeta);
        etCedula = (EditText) vista.findViewById(R.id.etCedulaTarjeta);
        etTarjeta = (EditText) vista.findViewById(R.id.etTarjeta);
        etCVV = (EditText) vista.findViewById(R.id.etnumeroCVV);
        etOtroTarjeta = (EditText) vista.findViewById(R.id.editTextOtroTarjeta);
        etBanco = (EditText) vista.findViewById(R.id.etBancoTarjeta);
        etClave = (EditText) vista.findViewById(R.id.etClaveTarjeta);
        etVencimiento = (EditText) vista.findViewById(R.id.etVencimientoTarjeta);
        rbMaestro = (RadioButton) vista.findViewById(R.id.radioButtonMaestro);
        rbMastercard = (RadioButton)vista.findViewById(R.id.radioButtonMaster);
        rbVisa = (RadioButton) vista.findViewById(R.id.radioButtonVisa);
        rbOtro = (RadioButton) vista.findViewById(R.id.radioButtonOtroTarjeta);
        RadioGroup radioTarjeta = (RadioGroup) vista.findViewById(R.id.radioTarjeta);
        user = FirebaseAuth.getInstance().getCurrentUser();
        progressBarAdd = vista.findViewById(R.id.progressBarAddTarjeta);

        radioTarjeta.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButtonMaestro:
                        etOtroTarjeta.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonMaster:
                        etOtroTarjeta.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonVisa:
                        etOtroTarjeta.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonOtroTarjeta:
                        etOtroTarjeta.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        etVencimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escogerVencimiento();
            }
        });

        Button buttonGuardar = (Button) vista.findViewById(R.id.guardarTarjeta);
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilidades.almacenamientoExterno) {
                    guardarTarjetaFirebase();
                } else if (Utilidades.almacenamientoInterno) {
                    guardarTarjetaSQLite();
                }
            }
        });

        return vista;
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void guardarTarjetaFirebase() {
        String userID = user.getUid();
        String titular = etTitular.getText().toString();
        String banco = etBanco.getText().toString();
        String numeroTarjeta = etTarjeta.getText().toString();
        String cvv = etCVV.getText().toString();
        String vencimiento = etVencimiento.getText().toString();
        String cedula = etCedula.getText().toString();
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
                            tipo = etOtroTarjeta.getText().toString();
                        }


                        progressBarAdd.setVisibility(View.VISIBLE);
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> tarjeta = new HashMap<>();
                        tarjeta.put(UtilidadesStatic.BD_TITULAR_TARJETA, titular);
                        tarjeta.put(UtilidadesStatic.BD_BANCO_TARJETA, banco);
                        tarjeta.put(UtilidadesStatic.BD_NUMERO_TARJETA, numeroTarjeta);
                        tarjeta.put(UtilidadesStatic.BD_CVV, cvv);
                        tarjeta.put(UtilidadesStatic.BD_CEDULA_TARJETA, cedula);
                        tarjeta.put(UtilidadesStatic.BD_TIPO_TARJETA, tipo);

                        if (vencimiento.isEmpty()) {
                            tarjeta.put(UtilidadesStatic.BD_VENCIMIENTO_TARJETA, "");
                        } else {
                            tarjeta.put(UtilidadesStatic.BD_VENCIMIENTO_TARJETA, vencimiento);
                        }

                        if (clave.isEmpty()) {
                            tarjeta.put(UtilidadesStatic.BD_CLAVE_TARJETA, "");
                        } else {
                            tarjeta.put(UtilidadesStatic.BD_CLAVE_TARJETA, clave);
                        }

                        db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_TARJETAS).add(tarjeta).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                progressBarAdd.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(getContext(), MainActivity.class);
                                startActivity(myIntent);

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
    }

    public void guardarTarjetaSQLite() {
        String titular = etTitular.getText().toString();
        String banco = etBanco.getText().toString();
        String numeroTarjeta = etTarjeta.getText().toString();
        String cvv = etCVV.getText().toString();
        String cedula = etCedula.getText().toString();
        String vencimiento = etVencimiento.getText().toString();
        String clave = etClave.getText().toString();
        String tipo = "";

        ConexionSQLite conect = new ConexionSQLite(getContext(), UtilidadesStatic.BD_PROPIETARIOS, null, UtilidadesStatic.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

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
                            tipo = etOtroTarjeta.getText().toString();
                        }

                        ContentValues values = new ContentValues();
                        values.put(UtilidadesStatic.BD_TITULAR_TARJETA, titular);
                        values.put(UtilidadesStatic.BD_NUMERO_TARJETA, numeroTarjeta);
                        values.put(UtilidadesStatic.BD_CVV, cvv);
                        values.put(UtilidadesStatic.BD_CEDULA_TARJETA, cedula);
                        values.put(UtilidadesStatic.BD_TIPO_TARJETA, tipo);

                        if (vencimiento.isEmpty()) {
                            values.put(UtilidadesStatic.BD_VENCIMIENTO_TARJETA, "");
                        } else {
                            values.put(UtilidadesStatic.BD_VENCIMIENTO_TARJETA, vencimiento);
                        }

                        if (clave.isEmpty()) {
                            values.put(UtilidadesStatic.BD_CLAVE_TARJETA, "");
                        } else {
                            values.put(UtilidadesStatic.BD_CLAVE_TARJETA, clave);
                        }

                        db.insert(UtilidadesStatic.BD_TARJETAS, null, values);
                        db.close();

                        Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(getContext(), MainActivity.class);
                        startActivity(myIntent);
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
