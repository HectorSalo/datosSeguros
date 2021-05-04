package com.skysam.datossegurosFirebaseFinal.notes.ui;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

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


public class EditarNotaFragment extends Fragment {

    private EditText etTitulo, etContenido;
    private FirebaseUser user;
    private RadioButton rbNube, rbDispositivo;
    private ProgressBar progressBar;
    private Button button;
    private String idDoc;
    private boolean almacenamientoNube;

    public EditarNotaFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_editar_nota, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        almacenamientoNube = sharedPreferences.getBoolean(Constants.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        String tema = sharedPreferences.getString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO);

        idDoc = getArguments().getString("id");

        etTitulo = vista.findViewById(R.id.etTitulo);
        etContenido = vista.findViewById(R.id.etContenido);
        progressBar = vista.findViewById(R.id.progressBarAddNota);
        rbNube = vista.findViewById(R.id.radioButton_nube);
        rbDispositivo = vista.findViewById(R.id.radioButton_dispositivo);
        button = (Button) vista.findViewById(R.id.guardarNota);

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

        if (almacenamientoNube) {
            cargarDataFirebase();
        } else {
            cargarDataSQLite();
        }

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
        CollectionReference reference = dbFirestore.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_NOTAS);

        reference.document(idDoc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    etTitulo.setText(doc.getString(Constants.BD_TITULO_NOTAS));
                    etContenido.setText(doc.getString(Constants.BD_CONTENIDO_NOTAS));

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

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constants.BD_NOTAS + " WHERE idNota =" + idDoc, null);

        if(cursor.moveToFirst()) {
            etTitulo.setText(cursor.getString(1));
            etContenido.setText(cursor.getString(2));
        }

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
            if (almacenamientoNube) {
                guardarDataFirebase(titulo, contenido);
            } else {
                guardarDataSQLite(titulo, contenido);
            }
        }
    }

    public void guardarDataFirebase(String titulo, String contenido) {
        String userID = user.getUid();

        progressBar.setVisibility(View.VISIBLE);
        etTitulo.setEnabled(false);
        etContenido.setEnabled(false);
        button.setEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> nota = new HashMap<>();
        nota.put(Constants.BD_TITULO_NOTAS, titulo);
        nota.put(Constants.BD_CONTENIDO_NOTAS, contenido);

        db.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_NOTAS).document(idDoc).update(nota).addOnSuccessListener(new OnSuccessListener<Void>() {

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
                etTitulo.setEnabled(true);
                etContenido.setEnabled(true);
                button.setEnabled(true);
            }
        });
    }

    public void guardarDataSQLite(String titulo, String contenido) {
        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constants.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Constants.BD_TITULO_NOTAS, titulo);
        values.put(Constants.BD_CONTENIDO_NOTAS, contenido);

        db.update(Constants.BD_NOTAS, values, "idNota=" + idDoc, null);
        db.close();

        Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }
}
