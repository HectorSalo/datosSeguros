package com.example.datossegurosFirebaseFinal.FragmentsEditar;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.RadioButton;
import android.widget.Toast;

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
 * {@link EditarCuentasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditarCuentasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarCuentasFragment extends Fragment {

    private EditText etTitular, etBanco, etnumeroCuenta, etCedula, etTelefono;
    private RadioButton rbAhorro, rbCorriente;
    private ProgressDialog progress;
    private FirebaseUser user;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EditarCuentasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarCuentasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarCuentasFragment newInstance(String param1, String param2) {
        EditarCuentasFragment fragment = new EditarCuentasFragment();
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
        View vista = inflater.inflate(R.layout.fragment_editar_cuentas, container, false);

        etTitular = (EditText) vista.findViewById(R.id.etTitularEditar);
        etBanco = (EditText) vista.findViewById(R.id.etBancoEditar);
        etnumeroCuenta = (EditText) vista.findViewById(R.id.etnumeroCuentaEditar);
        etCedula = (EditText) vista.findViewById(R.id.etCedulaCuentaEditar);
        etTelefono = (EditText) vista.findViewById(R.id.etTelefonoEditar);
        rbAhorro = (RadioButton) vista.findViewById(R.id.radioButtonAhorroEditar);
        rbCorriente = (RadioButton) vista.findViewById(R.id.radioButtonCorrienteEditar);
        user = FirebaseAuth.getInstance().getCurrentUser();

        cargarData();

        Button button = (Button) vista.findViewById(R.id.guardarCuentaEditar);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarData();
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

    public void cargarData() {
        progress = new ProgressDialog(getContext());
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();
        String userID = user.getUid();
        String idCuenta = Utilidades.idCuenta;

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_CUENTAS);

        reference.document(idCuenta).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    etTitular.setText(doc.getString(UtilidadesStatic.BD_TITULAR_BANCO));
                    etBanco.setText(doc.getString(UtilidadesStatic.BD_BANCO));
                    etnumeroCuenta.setText(doc.getString(UtilidadesStatic.BD_NUMERO_CUENTA));
                    etCedula.setText(doc.getString(UtilidadesStatic.BD_CEDULA_BANCO));
                    String telefono = doc.getString(UtilidadesStatic.BD_TELEFONO);
                    String tipo = doc.getString(UtilidadesStatic.BD_TIPO_CUENTA);

                    if (telefono.equals("0")) {
                        etTelefono.setText("");
                    } else {
                        etTelefono.setText(telefono);
                    }

                    if (tipo.equals("Ahorro")) {
                        rbAhorro.setChecked(true);
                    } else if (tipo.equals("Corriente")) {
                        rbCorriente.setChecked(true);
                    }

                    progress.dismiss();

                } else {
                    progress.dismiss();
                    Toast.makeText(getContext(), "Error al cargar. Intente nuevamente", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void guardarData () {
        String userID = user.getUid();
        String titular = etTitular.getText().toString();
        String banco = etBanco.getText().toString();
        String cuentaNumero = etnumeroCuenta.getText().toString();
        String cedula = etCedula.getText().toString();
        String telefono = etTelefono.getText().toString();
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
                    progress = new ProgressDialog(getContext());
                    progress.setMessage("Guardando...");
                    progress.setCancelable(false);
                    progress.show();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> cuentaBancaria = new HashMap<>();
                    cuentaBancaria.put(UtilidadesStatic.BD_TITULAR_BANCO, titular);
                    cuentaBancaria.put(UtilidadesStatic.BD_BANCO, banco);
                    cuentaBancaria.put(UtilidadesStatic.BD_NUMERO_CUENTA, cuentaNumero);
                    cuentaBancaria.put(UtilidadesStatic.BD_CEDULA_BANCO, cedula);
                    cuentaBancaria.put(UtilidadesStatic.BD_TIPO_CUENTA, tipo);

                    if (telefono.isEmpty()) {
                        cuentaBancaria.put(UtilidadesStatic.BD_TELEFONO, "0");
                    } else {
                        cuentaBancaria.put(UtilidadesStatic.BD_TELEFONO, telefono);
                    }

                    db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_CUENTAS).document(Utilidades.idCuenta).set(cuentaBancaria).addOnSuccessListener(new OnSuccessListener<Void>() {

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
