package com.skysam.datossegurosFirebaseFinal.notes.ui;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.common.model.Note;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.AddViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


public class AddNotaFragment extends Fragment {

    private EditText etTitulo, etContenido;
    private ProgressBar progressBar;
    private Button buttonGuardar;
    private ArrayList<String> labels;
    private ArrayList<String> labelsToSave;
    private ChipGroup chipGroup;


    public AddNotaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_add_nota, container, false);
        AddViewModel addViewModel = new ViewModelProvider(requireActivity()).get(AddViewModel.class);
        addViewModel.getLabels().observe(getViewLifecycleOwner(), item -> {
            labels = new ArrayList<>();
            labels.addAll(item);
        });

        etTitulo = vista.findViewById(R.id.etTitulo);
        etContenido = vista.findViewById(R.id.etContenido);
        progressBar = vista.findViewById(R.id.progressBarAddNota);
        buttonGuardar = vista.findViewById(R.id.guardarNota);
        ExtendedFloatingActionButton fab = vista.findViewById(R.id.extended_fab);
        chipGroup = vista.findViewById(R.id.chip_group);

        labelsToSave = new ArrayList<>();

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

        fab.setOnClickListener(view -> viewListLabels());

        buttonGuardar.setOnClickListener(v -> validarDatos());
        return vista;
    }

    private void viewListLabels() {
        ArrayList<String> labelsToShow = new ArrayList<>();
        for (String lab: labels) {
            if (!labelsToSave.contains(lab)) {
                labelsToShow.add(lab);
            }
        }
        ArrayList<String> listTemporal = new ArrayList<>();
        String[] arrayLabels = labelsToShow.toArray(new String[0]);
        boolean[] array = new boolean[labelsToShow.size()];
        Arrays.fill(array, Boolean.FALSE);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.text_add_label)
                .setMultiChoiceItems(arrayLabels, array, (dialogInterface, position, isChecked) -> {
                    if (isChecked) {
                        listTemporal.add(arrayLabels[position]);
                    } else {
                        listTemporal.remove(arrayLabels[position]);
                    }
                })
                .setPositiveButton(R.string.buttonAceptar, (dialogInterface, i) -> {
                    if (!listTemporal.isEmpty()) {
                        labelsToSave.addAll(listTemporal);
                        addChips();
                    }
                });
        builder.create();
        builder.show();
    }

    private void addChips() {
        chipGroup.removeAllViews();
        for (String label: labelsToSave) {
            Chip chip = new Chip(requireContext());
            chip.setText(label);
            chip.setCloseIconVisible(true);
            chip.setChipBackgroundColorResource(getColorPrimary());
            chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.md_text_white));
            chip.setCloseIconTintResource(R.color.md_text_white);
            chip.setOnCloseIconClickListener(view -> {
                chipGroup.removeView(chip);
                labelsToSave.remove(chip.getText().toString());
            });
            chipGroup.addView(chip);
        }
    }

    private int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        requireActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.resourceId;
    }

    private void validarDatos () {
        String titulo = etTitulo.getText().toString();
        String contenido = etContenido.getText().toString();

        if (!titulo.isEmpty() || !contenido.isEmpty()) {
            if (titulo.isEmpty()) {
                titulo = "";
            }
            if (contenido.isEmpty()) {
                contenido = "";
            }
        } else {
            Toast.makeText(getContext(), "No se puede guardar una nota vacía", Toast.LENGTH_SHORT).show();
            return;
        }
        guardarNotaFirebase(titulo, contenido);
    }

    public void guardarNotaFirebase(String titulo, String contenido) {
        progressBar.setVisibility(View.VISIBLE);
        etTitulo.setEnabled(false);
        etContenido.setEnabled(false);
        buttonGuardar.setEnabled(false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> nota = new HashMap<>();
        nota.put(Constants.BD_TITULO_NOTAS, titulo);
        nota.put(Constants.BD_CONTENIDO_NOTAS, contenido);
        nota.put(Constants.ETIQUETAS, labelsToSave);

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
                    buttonGuardar.setEnabled(true);
                });
    }
}
