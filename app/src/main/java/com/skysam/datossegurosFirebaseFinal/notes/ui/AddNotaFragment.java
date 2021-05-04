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
import com.google.firebase.firestore.FirebaseFirestore;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.database.room.Room;
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Note;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddNotaFragment extends Fragment {

    private EditText etTitulo, etContenido;
    private RadioButton rbNube, rbDispositivo;
    private ProgressBar progressBar;
    private Button buttonGuardar;


    public AddNotaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_add_nota, container, false);
        etTitulo = vista.findViewById(R.id.etTitulo);
        etContenido = vista.findViewById(R.id.etContenido);
        progressBar = vista.findViewById(R.id.progressBarAddNota);
        rbNube = vista.findViewById(R.id.radioButton_nube);
        rbDispositivo = vista.findViewById(R.id.radioButton_dispositivo);
        buttonGuardar = vista.findViewById(R.id.guardarNota);

        switch (SharedPref.INSTANCE.getTheme()){
            case Constants.PREFERENCE_AMARILLO:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.color_yellow_dark));
                break;
            case Constants.PREFERENCE_ROJO:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.color_red_ligth));
                break;
            case Constants.PREFERENCE_MARRON:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.color_camel));
                break;
            case Constants.PREFERENCE_LILA:
                buttonGuardar.setBackgroundColor(getResources().getColor(R.color.color_fucsia));
                break;
        }

        buttonGuardar.setOnClickListener(v -> validarDatos());
        return vista;
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
        progressBar.setVisibility(View.VISIBLE);
        etTitulo.setEnabled(false);
        etContenido.setEnabled(false);
        rbNube.setEnabled(false);
        rbDispositivo.setEnabled(false);
        buttonGuardar.setEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> nota = new HashMap<>();
        nota.put(Constants.BD_TITULO_NOTAS, titulo);
        nota.put(Constants.BD_CONTENIDO_NOTAS, contenido);

        db.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid())
                .collection(Constants.BD_NOTAS).add(nota).addOnSuccessListener(documentReference -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();

                }).addOnFailureListener(e -> {
                    Log.w("msg", "Error adding document", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    etTitulo.setEnabled(true);
                    etContenido.setEnabled(true);
                    rbNube.setEnabled(true);
                    rbDispositivo.setEnabled(true);
                    buttonGuardar.setEnabled(true);
                });
    }

    public void guardarNotaSQLite(String titulo, String contenido) {
        Calendar calendar = Calendar.getInstance();
        Note note = new Note(String.valueOf(calendar.getTimeInMillis()), titulo, contenido, false, false);
        Room.INSTANCE.saveNote(note);
        Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }
}
