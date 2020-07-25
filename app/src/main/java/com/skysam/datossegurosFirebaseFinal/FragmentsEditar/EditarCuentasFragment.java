package com.skysam.datossegurosFirebaseFinal.FragmentsEditar;

import android.content.ContentValues;
import android.content.Context;
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


public class EditarCuentasFragment extends Fragment {

    private TextInputLayout inputLayoutTitular, inputLayoutBanco, inputLayoutNumero, inputLayoutCedula, inputLayoutTelefono, inputLayoutCorreo;
    private TextInputEditText etTitular, etBanco, etNumeroCuenta, etCedula, etTelefono, etCorreo;
    private RadioButton rbAhorro, rbCorriente;
    private ProgressBar progressBar;
    private FirebaseUser user;
    private String spinnerSeleccion, idDoc;
    private Spinner spinnerDocumento;
    private RadioGroup radioGroup;
    private Button button;
    private boolean almacenamientoNube;


    private OnFragmentInteractionListener mListener;

    public EditarCuentasFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_editar_cuentas, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        button = vista.findViewById(R.id.guardarCuenta);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                button.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                break;
            case Constantes.PREFERENCE_ROJO:
                button.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkRojo));
                break;
            case Constantes.PREFERENCE_MARRON:
                button.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkMarron));
                break;
            case Constantes.PREFERENCE_LILA:
                button.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDarkLila));
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
        radioGroup = vista.findViewById(R.id.radioTipoCuenta);

        idDoc = getArguments().getString("id");


        String [] spDocumentos = {"Cédula", "RIF", "Pasaporte"};
        ArrayAdapter<String> adapterDocumentos = new ArrayAdapter<String>(getContext(), R.layout.spinner_opciones, spDocumentos);
        spinnerDocumento.setAdapter(adapterDocumentos);

        if (almacenamientoNube) {
            cargarDataFirebase();
        } else {
            cargarDataSQLite();
        }

        Button button = (Button) vista.findViewById(R.id.guardarCuenta);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
        progressBar.setVisibility(View.VISIBLE);
        String userID = user.getUid();

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CUENTAS);

        reference.document(idDoc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    etTitular.setText(doc.getString(Constantes.BD_TITULAR_BANCO));
                    etBanco.setText(doc.getString(Constantes.BD_BANCO));
                    etNumeroCuenta.setText(doc.getString(Constantes.BD_NUMERO_CUENTA));
                    etCedula.setText(doc.getString(Constantes.BD_CEDULA_BANCO));
                    etTelefono.setText(doc.getString(Constantes.BD_TELEFONO));
                    String tipo = doc.getString(Constantes.BD_TIPO_CUENTA);
                    etCorreo.setText(doc.getString(Constantes.BD_CORREO_CUENTA));
                    String tipoDocumento = doc.getString(Constantes.BD_TIPO_DOCUMENTO);

                    if (tipoDocumento != null) {
                        if (tipoDocumento.equals("Cédula")) {
                            spinnerDocumento.setSelection(0);
                        } else if (tipoDocumento.equals("RIF")) {
                            spinnerDocumento.setSelection(1);
                        } else if (tipoDocumento.equals("Pasaporte")) {
                            spinnerDocumento.setSelection(2);
                        }
                    }

                    if (tipo.equals("Ahorro")) {
                        rbAhorro.setChecked(true);
                    } else if (tipo.equals("Corriente")) {
                        rbCorriente.setChecked(true);
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
        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constantes.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.BD_CUENTAS + " WHERE idCuenta =" + idDoc, null);

        if (cursor.moveToFirst()) {
            etTitular.setText(cursor.getString(1));
            etBanco.setText(cursor.getString(2));
            etNumeroCuenta.setText(cursor.getString(3));
            etCedula.setText(cursor.getString(4));
            String tipo = cursor.getString(5);
            etTelefono.setText(cursor.getString(6));
            etCorreo.setText(cursor.getString(7));
            String tipoDocumento = cursor.getString(8);

            if (tipoDocumento.equals("Cédula")) {
                spinnerDocumento.setSelection(0);
            } else if (tipoDocumento.equals("RIF")) {
                spinnerDocumento.setSelection(1);
            } else if (tipoDocumento.equals("Pasaporte")) {
                spinnerDocumento.setSelection(2);
            }

            if (tipo.equals("Ahorro")) {
                rbAhorro.setChecked(true);
            } else if (tipo.equals("Corriente")) {
                rbCorriente.setChecked(true);
            }
        }

    }

    public void guardarDataFirebase () {
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
                    progressBar.setVisibility(View.VISIBLE);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> cuentaBancaria = new HashMap<>();
                    cuentaBancaria.put(Constantes.BD_TITULAR_BANCO, titular);
                    cuentaBancaria.put(Constantes.BD_BANCO, banco);
                    cuentaBancaria.put(Constantes.BD_NUMERO_CUENTA, cuentaNumero);
                    cuentaBancaria.put(Constantes.BD_CEDULA_BANCO, cedula);
                    cuentaBancaria.put(Constantes.BD_TIPO_CUENTA, tipo);
                    cuentaBancaria.put(Constantes.BD_TIPO_DOCUMENTO, spinnerSeleccion);

                    if (telefono.isEmpty()) {
                        cuentaBancaria.put(Constantes.BD_TELEFONO, "0");
                    } else {
                        cuentaBancaria.put(Constantes.BD_TELEFONO, telefono);
                    }

                    if (correo.isEmpty()) {
                        cuentaBancaria.put(Constantes.BD_CORREO_CUENTA, "");
                    } else {
                        cuentaBancaria.put(Constantes.BD_CORREO_CUENTA, correo);
                    }

                    db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CUENTAS).document(idDoc).set(cuentaBancaria).addOnSuccessListener(new OnSuccessListener<Void>() {

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
                        }
                    });
                }
            }
        }
    }

    public void guardarDataSQLite() {
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
                    ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constantes.VERSION_SQLITE);
                    SQLiteDatabase db = conect.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(Constantes.BD_TITULAR_TARJETA, titular);
                    values.put(Constantes.BD_BANCO, banco);
                    values.put(Constantes.BD_NUMERO_CUENTA, cuentaNumero);
                    values.put(Constantes.BD_CEDULA_BANCO, cedula);
                    values.put(Constantes.BD_TIPO_CUENTA, tipo);
                    values.put(Constantes.BD_TIPO_DOCUMENTO, spinnerSeleccion);

                    if (telefono.isEmpty()) {
                        values.put(Constantes.BD_TELEFONO, "0");
                    } else {
                        values.put(Constantes.BD_TELEFONO, telefono);
                    }

                    if (correo.isEmpty()) {
                        values.put(Constantes.BD_CORREO_CUENTA, "");
                    } else {
                        values.put(Constantes.BD_CORREO_CUENTA, correo);
                    }

                    db.update(Constantes.BD_CUENTAS, values, "idCuenta=" + idDoc, null);
                    db.close();

                    Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                }
            }
        }
    }
}
