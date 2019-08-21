package com.example.datossegurosFirebaseFinal.Fragments;

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
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.datossegurosFirebaseFinal.MainActivity;
import com.example.datossegurosFirebaseFinal.R;
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
 * {@link AddContrasenaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddContrasenaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddContrasenaFragment extends Fragment {

    private EditText etServicio, etUsuario, etContrasena, etOtroDias;
    private RadioButton rbdias30, rbdias60, rbdias90, rbdias120, rbIndeterminado, rbOtro;
    private RadioGroup radioGroup;
    private int duracionVigencia;
    private FirebaseUser user;
    private ProgressDialog progress;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddContrasenaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddContrasenaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddContrasenaFragment newInstance(String param1, String param2) {
        AddContrasenaFragment fragment = new AddContrasenaFragment();
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
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_add_contrasena, container, false);

        etServicio = (EditText) vista.findViewById(R.id.etServicio);
        etUsuario = (EditText) vista.findViewById(R.id.etUsuario);
        etContrasena = (EditText) vista.findViewById(R.id.etContrasena);
        etOtroDias = (EditText) vista.findViewById(R.id.etIngreseOtro);
        radioGroup = (RadioGroup) vista.findViewById(R.id.radioDias);
        rbdias30 = (RadioButton) vista.findViewById(R.id.radioButton30);
        rbdias60 = (RadioButton) vista.findViewById(R.id.radioButton60);
        rbdias90 = (RadioButton) vista.findViewById(R.id.radioButton90);
        rbdias120 = (RadioButton) vista.findViewById(R.id.radioButton120);
        rbIndeterminado = (RadioButton) vista.findViewById(R.id.radioButtonIndeterminado);
        rbOtro = (RadioButton) vista.findViewById(R.id.radioButtonOtro);

        user = FirebaseAuth.getInstance().getCurrentUser();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton30:
                        duracionVigencia = 30;
                        etOtroDias.setVisibility(View.GONE);
                        break;

                    case R.id.radioButton60:
                        duracionVigencia = 60;
                        etOtroDias.setVisibility(View.GONE);
                        break;

                    case R.id.radioButton90:
                        duracionVigencia = 90;
                        etOtroDias.setVisibility(View.GONE);
                        break;

                    case R.id.radioButton120:
                        duracionVigencia = 120;
                        etOtroDias.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonIndeterminado:
                        etOtroDias.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonOtro:
                        etOtroDias.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        Button buttonGuardar = (Button) vista.findViewById(R.id.guardarContrasena);
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarContrasena();
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

    public void guardarContrasena() {
        progress = new ProgressDialog(getContext());
        progress.setMessage("Guardando...");
        progress.setCancelable(false);
        progress.show();

        String servicio = etServicio.getText().toString();
        String usuario = etUsuario.getText().toString();
        String contrasena = etContrasena.getText().toString();
        String userID = user.getUid();

        String vigencia = "";
        if (rbIndeterminado.isChecked()) {
            vigencia = "";
        } else if (rbOtro.isChecked()) {
            vigencia = etOtroDias.getText().toString();
        } else if (rbdias30.isChecked() || rbdias60.isChecked() || rbdias90.isChecked() || rbdias120.isChecked()) {
            vigencia = String.valueOf(duracionVigencia);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> contrasenaM = new HashMap<>();
        contrasenaM.put(UtilidadesStatic.BD_SERVICIO, servicio);
        contrasenaM.put(UtilidadesStatic.BD_USUARIO, usuario);
        contrasenaM.put(UtilidadesStatic.BD_PASSWORD, contrasena);
        contrasenaM.put(UtilidadesStatic.BD_VIGENCIA, vigencia);

        db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_CONTRASENAS).add(contrasenaM).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                progress.dismiss();
                Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
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