package com.skysam.datossegurosFirebaseFinal.passwords.ui;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.database.room.Room;
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Password;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.AddViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditPasswordFragment extends Fragment {

    private TextInputLayout inputLayoutUsuario, inputLayoutPass, inputLayoutServicio;
    private TextInputEditText etServicio, etUsuario, etContrasena;
    private EditText etOtro;
    private ProgressBar progressBar;
    private Button button;
    private Spinner spinner;
    private int position;
    private String contrasenaVieja;
    private String pass1;
    private String pass2;
    private String pass3;
    private String pass4;
    private String idDoc;
    private Date fechaActual, fechaEnviar;
    private boolean isCloud;
    private Password passwordRoom;
    private ArrayList<String> labels;
    private ArrayList<String> labelsToSave;
    private ChipGroup chipGroup;


    public EditPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_editar_contrasena, container, false);
        AddViewModel addViewModel = new ViewModelProvider(requireActivity()).get(AddViewModel.class);
        addViewModel.getLabels().observe(getViewLifecycleOwner(), item -> {
            labels = new ArrayList<>();
            labels.addAll(item);
        });

        idDoc = getArguments().getString("id");
        isCloud = getArguments().getBoolean("isCloud");

        etServicio = vista.findViewById(R.id.et_servicio);
        etUsuario = vista.findViewById(R.id.et_usuario);
        etContrasena = vista.findViewById(R.id.et_pass);
        etOtro = vista.findViewById(R.id.etIngreseOtro);
        inputLayoutUsuario = vista.findViewById(R.id.outlined_usuario);
        inputLayoutPass = vista.findViewById(R.id.outlined_pass);
        inputLayoutServicio = vista.findViewById(R.id.outlined_servicio);
        progressBar = vista.findViewById(R.id.progressBar);
        spinner = vista.findViewById(R.id.spinner);
        ExtendedFloatingActionButton fab = vista.findViewById(R.id.extended_fab);
        chipGroup = vista.findViewById(R.id.chip_group);
        button = vista.findViewById(R.id.guardarContrasena);

        labelsToSave = new ArrayList<>();

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

        Calendar almanaque = Calendar.getInstance();
        int diaActual = almanaque.get(Calendar.DAY_OF_MONTH);
        int mesActual = almanaque.get(Calendar.MONTH);
        int anualActual = almanaque.get(Calendar.YEAR);
        almanaque.set(anualActual, mesActual, diaActual);
        fechaActual = almanaque.getTime();

        pass1 = null;
        pass2 = null;
        pass3 = null;
        pass4 = null;

        List<String> listaCaducidad = Arrays.asList(getResources().getStringArray(R.array.tiempo_vigencia));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_opciones, listaCaducidad);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 6) {
                    etOtro.setVisibility(View.GONE);
                } else {
                    etOtro.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isCloud) {
            fab.setVisibility(View.VISIBLE);
            chipGroup.setVisibility(View.VISIBLE);
            cargarDataFirebase();
        } else {
            fab.setVisibility(View.GONE);
            chipGroup.setVisibility(View.GONE);
            cargarDataRoom();
        }

        fab.setOnClickListener(view -> viewListLabels());
        button.setOnClickListener(v -> validarDatos());
        return vista;
    }

    public void cargarDataFirebase() {
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(Constants.BD_PROPIETARIOS)
                .document(Auth.INSTANCE.getCurrenUser().getUid()).collection(Constants.BD_CONTRASENAS);

        reference.document(idDoc).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                etServicio.setText(doc.getString(Constants.BD_SERVICIO));
                etUsuario.setText(doc.getString(Constants.BD_USUARIO));
                etContrasena.setText(doc.getString(Constants.BD_PASSWORD));

                contrasenaVieja = doc.getString(Constants.BD_PASSWORD);

                String vigencia = doc.getString(Constants.BD_VIGENCIA);

                if (vigencia != null) {
                    switch (vigencia) {
                        case "0":
                            spinner.setSelection(5);
                            break;
                        case "30":
                            spinner.setSelection(1);
                            break;
                        case "60":
                            spinner.setSelection(2);
                            break;
                        case "90":
                            spinner.setSelection(3);
                            break;
                        case "120":
                            spinner.setSelection(4);
                            break;
                        default:
                            spinner.setSelection(6);
                            etOtro.setVisibility(View.VISIBLE);
                            etOtro.setText(vigencia);
                            break;
                    }
                }

                position = spinner.getSelectedItemPosition();

                if (doc.getString(Constants.BD_ULTIMO_PASS_1) != null) {
                    pass1 = doc.getString(Constants.BD_ULTIMO_PASS_1);
                }
                if (doc.getString(Constants.BD_ULTIMO_PASS_2) != null) {
                    pass2 = doc.getString(Constants.BD_ULTIMO_PASS_2);
                }
                if (doc.getString(Constants.BD_ULTIMO_PASS_3) != null) {
                    pass3 = doc.getString(Constants.BD_ULTIMO_PASS_3);
                }
                if (doc.getString(Constants.BD_ULTIMO_PASS_4) != null) {
                    pass4 = doc.getString(Constants.BD_ULTIMO_PASS_4);
                }
                if (doc.get(Constants.ETIQUETAS) != null) {
                    labelsToSave = (ArrayList<String>) doc.get(Constants.ETIQUETAS);
                    addChips();
                }
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Error al cargar. Intente nuevamente", Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void cargarDataRoom() {
        passwordRoom = Room.INSTANCE.getPasswordById(idDoc);
        etServicio.setText(passwordRoom.getService());
        etUsuario.setText(passwordRoom.getUser());
        etContrasena.setText(passwordRoom.getPassword());
        contrasenaVieja = passwordRoom.getPassword();

        String vigencia = String.valueOf(passwordRoom.getExpiration());

        switch (vigencia) {
            case "0":
                spinner.setSelection(5);
                break;
            case "30":
                spinner.setSelection(1);
                break;
            case "60":
                spinner.setSelection(2);
                break;
            case "90":
                spinner.setSelection(3);
                break;
            case "120":
                spinner.setSelection(4);
                break;
            default:
                spinner.setSelection(6);
                etOtro.setVisibility(View.VISIBLE);
                etOtro.setText(vigencia);
                break;
        }
        position = spinner.getSelectedItemPosition();

        if (passwordRoom.getPassOld1() != null) {
            pass1 = passwordRoom.getPassOld1();
        }

        if (passwordRoom.getPassOld2() != null) {
            pass2 = passwordRoom.getPassOld2();
        }

        if (passwordRoom.getPassOld3() != null) {
            pass3 = passwordRoom.getPassOld3();
        }

        if (passwordRoom.getPassOld4() != null) {
            pass4 = passwordRoom.getPassOld4();
        }
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


    private void validarDatos () {
        inputLayoutServicio.setError(null);
        inputLayoutPass.setError(null);
        inputLayoutUsuario.setError(null);
        etOtro.setError(null);
        String servicio = etServicio.getText().toString();
        String usuario = etUsuario.getText().toString();
        String contrasena = etContrasena.getText().toString();
        String vigencia = "";

        if (usuario.isEmpty()) {
            inputLayoutUsuario.setError("El campo no puede estar vacío");
            return;
        }
        if (contrasena.isEmpty()) {
            inputLayoutPass.setError("El campo no puede estar vacío");
            return;
        }
        if (servicio.isEmpty()) {
            inputLayoutServicio.setError("El campo no puede estar vacío");
            return;
        }
        if (spinner.getSelectedItemPosition() > 0) {
            if (spinner.getSelectedItemPosition() == 6) {
                vigencia = etOtro.getText().toString();
                if (vigencia.isEmpty()) {
                    etOtro.setError("El campo no puede estar vacío");
                    return;
                }
            } else {
                switch (spinner.getSelectedItemPosition()) {
                    case 1:
                        vigencia = "30";
                        break;
                    case 2:
                        vigencia = "60";
                        break;
                    case 3:
                        vigencia = "90";
                        break;
                    case 4:
                        vigencia = "120";
                        break;
                    case 5:
                        vigencia = "0";
                        break;
                }
            }
        } else {
            Toast.makeText(getContext(), "Debe seleccionar la vigencia de la contraseña", Toast.LENGTH_SHORT).show();
            return;
        }

        if (isCloud) {
            guardarDataFirebase(servicio, contrasena, usuario, vigencia);
        } else {
            guardarDataRoom(servicio, contrasena, usuario, vigencia);
        }
    }

    public void guardarDataFirebase(String servicio, String contrasenaNueva, String usuario, String vigencia) {
        inputLayoutServicio.setEnabled(false);
        inputLayoutPass.setEnabled(false);
        inputLayoutUsuario.setEnabled(false);
        spinner.setEnabled(false);
        button.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Object> contrasenaM = new HashMap<>();


                if (!contrasenaNueva.equals(contrasenaVieja)) {
                    fechaEnviar = fechaActual;

                    contrasenaM.put(Constants.BD_SERVICIO, servicio);
                    contrasenaM.put(Constants.BD_USUARIO, usuario);
                    contrasenaM.put(Constants.BD_PASSWORD, contrasenaNueva);
                    contrasenaM.put(Constants.BD_VIGENCIA, vigencia);
                    contrasenaM.put(Constants.BD_PROPIETARIO, Auth.INSTANCE.getCurrenUser().getUid());
                    contrasenaM.put(Constants.BD_FECHA_CREACION, fechaEnviar);
                    contrasenaM.put(Constants.ETIQUETAS, labelsToSave);
                    contrasenaM.put(Constants.BD_ULTIMO_PASS_1, contrasenaVieja);
                    contrasenaM.put(Constants.BD_ULTIMO_PASS_2, pass1);
                    contrasenaM.put(Constants.BD_ULTIMO_PASS_3, pass2);
                    contrasenaM.put(Constants.BD_ULTIMO_PASS_4, pass3);
                    contrasenaM.put(Constants.BD_ULTIMO_PASS_5, pass4);
                } else {
                    if (spinner.getSelectedItemPosition() != position) {
                        fechaEnviar = fechaActual;

                        contrasenaM.put(Constants.BD_SERVICIO, servicio);
                        contrasenaM.put(Constants.BD_USUARIO, usuario);
                        contrasenaM.put(Constants.BD_PASSWORD, contrasenaNueva);
                        contrasenaM.put(Constants.BD_VIGENCIA, vigencia);
                        contrasenaM.put(Constants.BD_PROPIETARIO, Auth.INSTANCE.getCurrenUser().getUid());
                        contrasenaM.put(Constants.BD_FECHA_CREACION, fechaEnviar);
                        contrasenaM.put(Constants.ETIQUETAS, labelsToSave);
                    } else {
                        contrasenaM.put(Constants.BD_SERVICIO, servicio);
                        contrasenaM.put(Constants.BD_USUARIO, usuario);
                        contrasenaM.put(Constants.BD_VIGENCIA, vigencia);
                        contrasenaM.put(Constants.BD_PROPIETARIO, Auth.INSTANCE.getCurrenUser().getUid());
                        contrasenaM.put(Constants.ETIQUETAS, labelsToSave);
                    }
                }

        db.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid())
                .collection(Constants.BD_CONTRASENAS).document(idDoc).update(contrasenaM).addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();
                }).addOnFailureListener(e -> {
                    Log.w("msg", "Error adding document", e);
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al modificar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    inputLayoutServicio.setEnabled(true);
                    inputLayoutPass.setEnabled(true);
                    inputLayoutUsuario.setEnabled(true);
                    spinner.setEnabled(true);
                    button.setEnabled(true);
                });


    }

    public void guardarDataRoom(String servicio, String contrasenaNueva, String usuario, String vigencia) {
        if (!contrasenaNueva.equals(contrasenaVieja)) {
            passwordRoom.setService(servicio);
            passwordRoom.setUser(usuario);
            passwordRoom.setPassword(contrasenaNueva);
            passwordRoom.setExpiration(Integer.parseInt(vigencia));
            passwordRoom.setDateCreated(fechaActual.getTime());
            passwordRoom.setPassOld1(contrasenaVieja);
            passwordRoom.setPassOld2(pass1);
            passwordRoom.setPassOld3(pass2);
            passwordRoom.setPassOld4(pass3);
            passwordRoom.setPassOld5(pass4);
        } else {
            if (spinner.getSelectedItemPosition() != position) {
                fechaEnviar = fechaActual;

                passwordRoom.setService(servicio);
                passwordRoom.setUser(usuario);
                passwordRoom.setPassword(contrasenaNueva);
                passwordRoom.setExpiration(Integer.parseInt(vigencia));
                passwordRoom.setDateCreated(fechaActual.getTime());
            } else {
                passwordRoom.setService(servicio);
                passwordRoom.setUser(usuario);
                passwordRoom.setExpiration(Integer.parseInt(vigencia));
            }
        }
        Room.INSTANCE.updatePassword(passwordRoom);

        Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
        requireActivity().finish();
    }

    private int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        requireActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.resourceId;
    }
}
