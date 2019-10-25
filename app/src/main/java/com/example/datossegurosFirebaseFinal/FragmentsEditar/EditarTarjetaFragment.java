package com.example.datossegurosFirebaseFinal.FragmentsEditar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.datossegurosFirebaseFinal.ConexionSQLite;
import com.example.datossegurosFirebaseFinal.MainActivity;
import com.example.datossegurosFirebaseFinal.R;
import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditarTarjetaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditarTarjetaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarTarjetaFragment extends Fragment {

    private EditText etTitular, etnumeroTarjeta, etCVV, etCedula, etOtro, etBanco, etVencimiento, etClave;
    private RadioButton rbMastercard, rbVisa, rbOtro, rbMaestro;
    private FirebaseUser user;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EditarTarjetaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarTarjetaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarTarjetaFragment newInstance(String param1, String param2) {
        EditarTarjetaFragment fragment = new EditarTarjetaFragment();
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
        View vista = inflater.inflate(R.layout.fragment_editar_tarjeta, container, false);

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

        user = FirebaseAuth.getInstance().getCurrentUser();

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

        if (Utilidades.almacenamientoExterno) {
            cargarDataFirebase();
        } else if (Utilidades.almacenamientoInterno) {
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
                if(Utilidades.almacenamientoExterno) {
                    guardarDataFirebase();
                } else if (Utilidades.almacenamientoInterno) {
                    guardarDataSQLite();
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

    public void cargarDataFirebase() {
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();
        String userID = user.getUid();
        String idTarjeta = Utilidades.idTarjeta;

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_TARJETAS);

        reference.document(idTarjeta).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    etTitular.setText(doc.getString(UtilidadesStatic.BD_TITULAR_TARJETA));
                    etnumeroTarjeta.setText(doc.getString(UtilidadesStatic.BD_NUMERO_TARJETA));
                    etCVV.setText(doc.getString(UtilidadesStatic.BD_CVV));
                    etCedula.setText(doc.getString(UtilidadesStatic.BD_CEDULA_TARJETA));
                    etBanco.setText(doc.getString(UtilidadesStatic.BD_BANCO_TARJETA));
                    etVencimiento.setText(doc.getString(UtilidadesStatic.BD_VENCIMIENTO_TARJETA));
                    etClave.setText(doc.getString(UtilidadesStatic.BD_CLAVE_TARJETA));

                    String tipo = doc.getString(UtilidadesStatic.BD_TIPO_TARJETA);

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

                    progress.dismiss();

                } else {
                    progress.dismiss();
                    Toast.makeText(getContext(), "Error al cargar. Intente nuevamente", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void cargarDataSQLite() {
        final ProgressDialog progress = new ProgressDialog(getContext());
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();

        String idTarjeta = Utilidades.idTarjeta;

        ConexionSQLite conect = new ConexionSQLite(getContext(), UtilidadesStatic.BD_PROPIETARIOS, null, UtilidadesStatic.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + UtilidadesStatic.BD_TARJETAS + " WHERE idTarjeta =" + idTarjeta, null);

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

            progress.dismiss();
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

                        final ProgressDialog progress = new ProgressDialog(getContext());
                        progress.setMessage("Guardando...");
                        progress.setCancelable(false);
                        progress.show();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> tarjeta = new HashMap<>();
                        tarjeta.put(UtilidadesStatic.BD_TITULAR_TARJETA, titular);
                        tarjeta.put(UtilidadesStatic.BD_NUMERO_TARJETA, numeroTarjeta);
                        tarjeta.put(UtilidadesStatic.BD_CVV, cvv);
                        tarjeta.put(UtilidadesStatic.BD_CEDULA_TARJETA, cedula);
                        tarjeta.put(UtilidadesStatic.BD_TIPO_TARJETA, tipo);
                        tarjeta.put(UtilidadesStatic.BD_VENCIMIENTO_TARJETA, vencimiento);
                        tarjeta.put(UtilidadesStatic.BD_CLAVE_TARJETA, clave);
                        tarjeta.put(UtilidadesStatic.BD_BANCO_TARJETA, banco);

                        db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_TARJETAS).document(Utilidades.idTarjeta).set(tarjeta).addOnSuccessListener(new OnSuccessListener<Void>() {

                            public void onSuccess(Void aVoid) {
                                progress.dismiss();
                                Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(getContext(), MainActivity.class);
                                startActivity(myIntent);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("msg", "Error adding document", e);
                                progress.dismiss();
                                Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        }
    }

    public void guardarDataSQLite() {
        String idTarjeta = Utilidades.idTarjeta;
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

                        ProgressDialog progress = new ProgressDialog(getContext());
                        progress.setMessage("Guardando...");
                        progress.setCancelable(false);
                        progress.show();


                        ConexionSQLite conect = new ConexionSQLite(getContext(), UtilidadesStatic.BD_PROPIETARIOS, null, UtilidadesStatic.VERSION_SQLITE);
                        SQLiteDatabase db = conect.getWritableDatabase();

                        ContentValues values = new ContentValues();
                        values.put(UtilidadesStatic.BD_TITULAR_TARJETA, titular);
                        values.put(UtilidadesStatic.BD_NUMERO_TARJETA, numeroTarjeta);
                        values.put(UtilidadesStatic.BD_CVV, cvv);
                        values.put(UtilidadesStatic.BD_CEDULA_TARJETA, cedula);
                        values.put(UtilidadesStatic.BD_TIPO_TARJETA, tipo);
                        values.put(UtilidadesStatic.BD_BANCO_TARJETA, banco);
                        values.put(UtilidadesStatic.BD_VENCIMIENTO_TARJETA, vencimiento);
                        values.put(UtilidadesStatic.BD_CLAVE_TARJETA, clave);

                        db.update(UtilidadesStatic.BD_TARJETAS, values, "idTarjeta=" + idTarjeta, null);
                        db.close();

                        progress.dismiss();
                        Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
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
