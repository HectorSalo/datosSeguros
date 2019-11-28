package com.example.datossegurosFirebaseFinal.Fragments;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.ProgressBar;
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
 * {@link AddNotaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddNotaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNotaFragment extends Fragment {

    private EditText titulo, contenido;
    private FirebaseUser user;
    private ProgressBar progressBarAdd;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddNotaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNotaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNotaFragment newInstance(String param1, String param2) {
        AddNotaFragment fragment = new AddNotaFragment();
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

        View vista = inflater.inflate(R.layout.fragment_add_nota, container, false);

        titulo = (EditText) vista.findViewById(R.id.etTitulo);
        contenido = (EditText) vista.findViewById(R.id.etContenido);
        user = FirebaseAuth.getInstance().getCurrentUser();
        progressBarAdd = vista.findViewById(R.id.progressBarAddNota);

        Button buttonGuardar = (Button) vista.findViewById(R.id.guardarNota);
        buttonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilidades.almacenamientoExterno) {
                    guardarNotaFirebase();
                } else if (Utilidades.almacenamientoInterno) {
                    guardarNotaSQLite();
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

    public void guardarNotaFirebase() {
        String userID = user.getUid();
        String tituloS = titulo.getText().toString();
        String contenidoS = contenido.getText().toString();

        progressBarAdd.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> nota = new HashMap<>();
        nota.put(UtilidadesStatic.BD_TITULO_NOTAS, tituloS);
        nota.put(UtilidadesStatic.BD_CONTENIDO_NOTAS, contenidoS);

        db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_NOTAS).add(nota).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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

    public void guardarNotaSQLite() {
        String tituloS = titulo.getText().toString();
        String contenidoS = contenido.getText().toString();

        ConexionSQLite conect = new ConexionSQLite(getContext(), UtilidadesStatic.BD_PROPIETARIOS, null, UtilidadesStatic.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(UtilidadesStatic.BD_TITULO_NOTAS, tituloS);
        values.put(UtilidadesStatic.BD_CONTENIDO_NOTAS, contenidoS);

        db.insert(UtilidadesStatic.BD_NOTAS, null, values);
        db.close();

        Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
        Intent myIntent = new Intent(getContext(), MainActivity.class);
        startActivity(myIntent);
    }
}
