package com.skysam.datossegurosFirebaseFinal.notes.ui;

import android.os.Bundle;

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

import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.database.room.Room;
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Note;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;

import java.util.HashMap;
import java.util.Map;


public class EditarNotaFragment extends Fragment {

    private EditText etTitulo, etContenido;
    private RadioButton rbNube, rbDispositivo;
    private ProgressBar progressBar;
    private Button button;
    private String idDoc;
    private boolean isCloud;
    private Note note;

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
        idDoc = getArguments().getString("id");
        isCloud = getArguments().getBoolean("isCloud");

        etTitulo = vista.findViewById(R.id.etTitulo);
        etContenido = vista.findViewById(R.id.etContenido);
        progressBar = vista.findViewById(R.id.progressBarAddNota);
        rbNube = vista.findViewById(R.id.radioButton_nube);
        rbDispositivo = vista.findViewById(R.id.radioButton_dispositivo);
        button = vista.findViewById(R.id.guardarNota);

        switch (SharedPref.INSTANCE.getTheme()){
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

        if (isCloud) {
            cargarDataFirebase();
        } else {
            cargarDataSQLite();
        }

        button.setOnClickListener(v -> validarDatos());

        return vista;
    }

    public void cargarDataFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(Constants.BD_PROPIETARIOS)
                .document(Auth.INSTANCE.getCurrenUser().getUid()).collection(Constants.BD_NOTAS);

        reference.document(idDoc).get().addOnCompleteListener(task -> {
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
        });
    }

    public void cargarDataSQLite() {
        note = Room.INSTANCE.getNoteById(idDoc);
        etTitulo.setText(note.getTitle());
        etContenido.setText(note.getContent());
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
            if (isCloud) {
                guardarDataFirebase(titulo, contenido);
            } else {
                guardarDataRoom(titulo, contenido);
            }
        }
    }

    public void guardarDataFirebase(String titulo, String contenido) {
        progressBar.setVisibility(View.VISIBLE);
        etTitulo.setEnabled(false);
        etContenido.setEnabled(false);
        button.setEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> nota = new HashMap<>();
        nota.put(Constants.BD_TITULO_NOTAS, titulo);
        nota.put(Constants.BD_CONTENIDO_NOTAS, contenido);

        db.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid())
                .collection(Constants.BD_NOTAS).document(idDoc).update(nota).addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                }).addOnFailureListener(e -> {
                    Log.w("msg", "Error adding document", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    etTitulo.setEnabled(true);
                    etContenido.setEnabled(true);
                    button.setEnabled(true);
                });
    }

    public void guardarDataRoom(String titulo, String contenido) {
        note.setTitle(titulo);
        note.setContent(contenido);
        Room.INSTANCE.updateNote(note);
        Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }
}
