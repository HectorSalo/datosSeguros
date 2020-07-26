package com.skysam.datossegurosFirebaseFinal.Fragments;

import android.content.ContentValues;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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


public class AddNotaFragment extends Fragment {

    private EditText etTitulo, etContenido;
    private FirebaseUser user;
    private RadioButton rbNube, rbDispositivo;
    private ProgressBar progressBar;
    private Button buttonGuardar;


    private OnFragmentInteractionListener mListener;

    public AddNotaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_add_nota, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        boolean almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        etTitulo = vista.findViewById(R.id.etTitulo);
        etContenido = vista.findViewById(R.id.etContenido);
        progressBar = vista.findViewById(R.id.progressBarAddNota);
        rbNube = vista.findViewById(R.id.radioButton_nube);
        rbDispositivo = vista.findViewById(R.id.radioButton_dispositivo);
        buttonGuardar = (Button) vista.findViewById(R.id.guardarNota);

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

        if (almacenamientoNube) {
            rbNube.setChecked(true);
        } else {
            rbDispositivo.setChecked(true);
        }

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
        void onFragmentInteraction(Uri uri);
    }

    private void validarDatos () {
        String titulo = etTitulo.getText().toString();
        String contenido = etContenido.getText().toString();

        boolean datoValido;

        if (!titulo.isEmpty() || !contenido.isEmpty()) {
            datoValido = true;
            if (titulo.isEmpty()) {
                titulo = "";
            }
            if (contenido.isEmpty()) {
                contenido = "";
            }
        } else {
            datoValido = false;
            Toast.makeText(getContext(), "No se puede guardar una nota vacía", Toast.LENGTH_SHORT).show();
        }

        if (datoValido) {
            if (rbNube.isChecked()) {
                guardarNotaFirebase(titulo, contenido);
            } else {
                guardarNotaSQLite(titulo, contenido);
            }
        }
    }

    public void guardarNotaFirebase(String titulo, String contenido) {
        String userID = user.getUid();

        progressBar.setVisibility(View.VISIBLE);
        etTitulo.setEnabled(false);
        etContenido.setEnabled(false);
        rbNube.setEnabled(false);
        rbDispositivo.setEnabled(false);
        buttonGuardar.setEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> nota = new HashMap<>();
        nota.put(Constantes.BD_TITULO_NOTAS, titulo);
        nota.put(Constantes.BD_CONTENIDO_NOTAS, contenido);

        db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_NOTAS).add(nota).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
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
                etTitulo.setEnabled(true);
                etContenido.setEnabled(true);
                rbNube.setEnabled(true);
                rbDispositivo.setEnabled(true);
                buttonGuardar.setEnabled(true);
            }
        });
    }

    public void guardarNotaSQLite(String titulo, String contenido) {
        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constantes.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constantes.BD_TITULO_NOTAS, titulo);
        values.put(Constantes.BD_CONTENIDO_NOTAS, contenido);

        db.insert(Constantes.BD_NOTAS, null, values);
        db.close();

        Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }
}
