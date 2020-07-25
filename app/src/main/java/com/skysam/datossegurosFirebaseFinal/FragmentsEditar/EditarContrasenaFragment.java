package com.skysam.datossegurosFirebaseFinal.FragmentsEditar;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
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

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class EditarContrasenaFragment extends Fragment {

    private TextInputLayout inputLayoutUsuario, inputLayoutPass, inputLayoutServicio;
    private TextInputEditText etServicio, etUsuario, etContrasena;
    private EditText etOtro;
    private FirebaseUser user;
    private ProgressBar progressBar;
    private Button button;
    private Spinner spinner;
    private int position;
    private String contrasenaNueva, contrasenaVieja, vigencia, pass1, pass2, pass3, pass4, pass5, fechaActualS, fechaEnviarS, idDoc;
    private Date fechaActual, fechaCreacion, fechaEnviar;
    private int vigenciaAnterior, vigenciaNueva;
    private boolean almacenamientoNube;


    private OnFragmentInteractionListener mListener;

    public EditarContrasenaFragment() {
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

        user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        idDoc = getArguments().getString("id");

        etServicio = vista.findViewById(R.id.et_servicio);
        etUsuario = vista.findViewById(R.id.et_usuario);
        etContrasena = vista.findViewById(R.id.et_pass);
        etOtro = vista.findViewById(R.id.etIngreseOtro);
        inputLayoutUsuario = vista.findViewById(R.id.outlined_usuario);
        inputLayoutPass = vista.findViewById(R.id.outlined_pass);
        inputLayoutServicio = vista.findViewById(R.id.outlined_servicio);
        progressBar = vista.findViewById(R.id.progressBar);
        spinner = vista.findViewById(R.id.spinner);
        button = vista.findViewById(R.id.guardarContrasena);

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
        pass5 = null;

        List<String> listaCaducidad = Arrays.asList(getResources().getStringArray(R.array.tiempo_vigencia));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(requireContext(), R.layout.spinner_opciones, listaCaducidad);
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

        if (almacenamientoNube) {
            cargarDataFirebase();
        } else {
            cargarDataSQLite();
        }

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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void cargarDataFirebase() {
        progressBar.setVisibility(View.VISIBLE);
        String userID = user.getUid();;

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CONTRASENAS);

        reference.document(idDoc).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    etServicio.setText(doc.getString(Constantes.BD_SERVICIO));
                    etUsuario.setText(doc.getString(Constantes.BD_USUARIO));
                    etContrasena.setText(doc.getString(Constantes.BD_PASSWORD));
                    fechaCreacion = doc.getDate(Constantes.BD_FECHA_CREACION);

                    contrasenaVieja = doc.getString(Constantes.BD_PASSWORD);

                    String vigencia = doc.getString(Constantes.BD_VIGENCIA);
                    if (vigencia != null) {
                        vigenciaAnterior = Integer.parseInt(vigencia);
                    }

                    if (vigencia.equals("0")) {
                        spinner.setSelection(5);
                    } else if (vigencia.equals("30")) {
                        spinner.setSelection(1);
                    } else if (vigencia.equals("60")) {
                        spinner.setSelection(2);
                    } else if (vigencia.equals("90")) {
                        spinner.setSelection(3);
                    } else if (vigencia.equals("120")) {
                        spinner.setSelection(4);
                    } else {
                        spinner.setSelection(6);
                        etOtro.setVisibility(View.VISIBLE);
                        etOtro.setText(vigencia);
                    }

                    position = spinner.getSelectedItemPosition();

                    if (doc.getString(Constantes.BD_ULTIMO_PASS_1) != null) {
                        pass1 = doc.getString(Constantes.BD_ULTIMO_PASS_1);

                    }

                    if (doc.getString(Constantes.BD_ULTIMO_PASS_2) != null) {
                        pass2 = doc.getString(Constantes.BD_ULTIMO_PASS_2);

                    }

                    if (doc.getString(Constantes.BD_ULTIMO_PASS_3) != null) {
                        pass3 = doc.getString(Constantes.BD_ULTIMO_PASS_3);

                    }

                    if (doc.getString(Constantes.BD_ULTIMO_PASS_4) != null) {
                        pass4 = doc.getString(Constantes.BD_ULTIMO_PASS_4);

                    }
                    progressBar.setVisibility(View.GONE);

                } else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar. Intente nuevamente", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void cargarDataSQLite() {
        ConexionSQLite conect = new ConexionSQLite(getContext(), user.getUid(), null, Constantes.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Constantes.BD_CONTRASENAS + " WHERE idContrasena =" + idDoc, null);

        if (cursor.moveToFirst()) {
            etServicio.setText(cursor.getString(1));
            etUsuario.setText(cursor.getString(2));
            etContrasena.setText((cursor.getString(3)));
            contrasenaVieja = cursor.getString(3);
            vigenciaAnterior = Integer.parseInt(cursor.getString(4));

            String vigencia = cursor.getString(4);

            if (vigencia.equals("0")) {
                spinner.setSelection(5);
            } else if (vigencia.equals("30")) {
                spinner.setSelection(1);
            } else if (vigencia.equals("60")) {
                spinner.setSelection(2);
            } else if (vigencia.equals("90")) {
                spinner.setSelection(3);
            } else if (vigencia.equals("120")) {
                spinner.setSelection(4);
            } else {
                spinner.setSelection(6);
                etOtro.setVisibility(View.VISIBLE);
                etOtro.setText(vigencia);
            }
            position = spinner.getSelectedItemPosition();

            if (cursor.getString(5) != null) {
                pass1 = cursor.getString(5);
            }

            if (cursor.getString(6) != null) {
                pass2 = cursor.getString(6);
            }

            if (cursor.getString(7) != null) {
                pass3 = cursor.getString(7);
            }

            if (cursor.getString(8) != null) {
                pass4 = cursor.getString(8);
            }
            db.close();
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

        boolean datoValido;

        if (!usuario.isEmpty()) {
            datoValido = true;
        } else {
            inputLayoutUsuario.setError("El campo no puede estar vacío");
            datoValido = false;
        }

        if (!contrasena.isEmpty()) {
            datoValido = true;
        } else {
            datoValido = false;
            inputLayoutPass.setError("El campo no puede estar vacío");
        }

        if (!servicio.isEmpty()) {
            datoValido = true;
        } else {
            datoValido = false;
            inputLayoutServicio.setError("El campo no puede estar vacío");
        }

        if (spinner.getSelectedItemPosition() > 0) {
            if (spinner.getSelectedItemPosition() == 6) {
                vigencia = etOtro.getText().toString();
                if (!vigencia.isEmpty()) {
                    datoValido = true;
                } else {
                    datoValido = false;
                    etOtro.setError("El campo no puede estar vacío");
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
                datoValido = true;
            }

            if ()
        } else {
            datoValido = false;
            Toast.makeText(getContext(), "Debe seleccionar la vigencia de la contraseña", Toast.LENGTH_SHORT).show();
        }

        if (datoValido) {
            if (almacenamientoNube) {
                guardarDataFirebase(servicio, contrasena, usuario, vigencia);
            } else {
                guardarContrasenaSQLite(servicio, contrasena, usuario, vigencia);
            }
        }
    }

    public void guardarDataFirebase(String servicio, String contrasenaNueva, String usuario, String vigencia) {
        inputLayoutServicio.setEnabled(false);
        inputLayoutPass.setEnabled(false);
        inputLayoutUsuario.setEnabled(false);
        spinner.setEnabled(false);
        button.setEnabled(false);
        String userID = user.getUid();
        progressBar.setVisibility(View.VISIBLE);




                if (!contrasenaNueva.equals(contrasenaVieja) && !rbIndeterminado.isChecked()) {


                    fechaEnviar = fechaActual;

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> contrasenaM = new HashMap<>();
                    contrasenaM.put(Constantes.BD_SERVICIO, servicio);
                    contrasenaM.put(Constantes.BD_USUARIO, usuario);
                    contrasenaM.put(Constantes.BD_PASSWORD, contrasenaNueva);
                    contrasenaM.put(Constantes.BD_VIGENCIA, vigencia);
                    contrasenaM.put(Constantes.BD_PROPIETARIO, userID);
                    contrasenaM.put(Constantes.BD_FECHA_CREACION, fechaEnviar);
                    contrasenaM.put(Constantes.BD_ULTIMO_PASS_1, contrasenaVieja);
                    contrasenaM.put(Constantes.BD_ULTIMO_PASS_2, pass1);
                    contrasenaM.put(Constantes.BD_ULTIMO_PASS_3, pass2);
                    contrasenaM.put(Constantes.BD_ULTIMO_PASS_4, pass3);
                    contrasenaM.put(Constantes.BD_ULTIMO_PASS_5, pass4);

                    db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CONTRASENAS).document(idDoc).set(contrasenaM).addOnSuccessListener(new OnSuccessListener<Void>() {

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
                            Toast.makeText(getContext(), "Error al modificar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (!contrasenaNueva.equals(contrasenaVieja) && rbIndeterminado.isChecked()) {

                    fechaEnviar = fechaActual;


                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> contrasenaM = new HashMap<>();
                    contrasenaM.put(Constantes.BD_SERVICIO, servicio);
                    contrasenaM.put(Constantes.BD_USUARIO, usuario);
                    contrasenaM.put(Constantes.BD_PASSWORD, contrasenaNueva);
                    contrasenaM.put(Constantes.BD_VIGENCIA, vigencia);
                    contrasenaM.put(Constantes.BD_PROPIETARIO, userID);
                    contrasenaM.put(Constantes.BD_FECHA_CREACION, fechaEnviar);
                    contrasenaM.put(Constantes.BD_ULTIMO_PASS_1, contrasenaVieja);
                    contrasenaM.put(Constantes.BD_ULTIMO_PASS_2, pass1);
                    contrasenaM.put(Constantes.BD_ULTIMO_PASS_3, pass2);
                    contrasenaM.put(Constantes.BD_ULTIMO_PASS_4, pass3);
                    contrasenaM.put(Constantes.BD_ULTIMO_PASS_5, pass4);

                    db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CONTRASENAS).document(idDoc).set(contrasenaM).addOnSuccessListener(new OnSuccessListener<Void>() {

                        public void onSuccess(Void aVoid) {
                            progressBarEditar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                            requireActivity().finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("msg", "Error adding document", e);
                            progressBarEditar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error al modificar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else if (contrasenaNueva.equals(contrasenaVieja) && vigenciaAnterior != duracionVigencia){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Aviso");
                    dialog.setMessage("No puede cambiar la vigencia si no se ha cambiado la contraseña");
                    dialog.setIcon(R.drawable.ic_advertencia);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    progressBarEditar.setVisibility(View.VISIBLE);

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> contrasenaM = new HashMap<>();
                    contrasenaM.put(Constantes.BD_SERVICIO, servicio);
                    contrasenaM.put(Constantes.BD_USUARIO, usuario);
                    contrasenaM.put(Constantes.BD_PROPIETARIO, userID);

                    db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CONTRASENAS).document(idDoc).update(contrasenaM).addOnSuccessListener(new OnSuccessListener<Void>() {

                        public void onSuccess(Void aVoid) {
                            progressBarEditar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                            requireActivity().finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("msg", "Error adding document", e);
                            progressBarEditar.setVisibility(View.GONE);
                            Toast.makeText(getContext(), "Error al modificar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


    }

    public void guardarDataSQLite() {
        String servicio = etServicio.getText().toString();
        String usuario = etUsuario.getText().toString();
        contrasenaNueva = etContrasena.getText().toString();
        vigencia = "";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        fechaActualS = sdf.format(fechaActual);


        if (servicio.isEmpty() || usuario.isEmpty() || contrasenaNueva.isEmpty()) {
            Toast.makeText(getContext(), "Hay campos vacíos", Toast.LENGTH_SHORT).show();
        } else {
            if (!rb30.isChecked() && !rb60.isChecked() && !rb90.isChecked() && !rb120.isChecked() && !rbIndeterminado.isChecked() && !rbOtro.isChecked()) {
                Toast.makeText(getContext(), "Debe seleccionar la vigencia de la contraseña", Toast.LENGTH_SHORT).show();
            } else {

                if (!contrasenaNueva.equals(contrasenaVieja) && !rbIndeterminado.isChecked()) {
                    if (rbOtro.isChecked()) {
                        vigencia = etOtro.getText().toString();

                    } else if (rb30.isChecked() || rb60.isChecked() || rb90.isChecked() || rb120.isChecked()) {
                        vigencia = String.valueOf(duracionVigencia);

                    }

                    fechaEnviarS = fechaActualS;

                    ConexionSQLite conect = new ConexionSQLite(getContext(), idDoc, null, Constantes.VERSION_SQLITE);
                    SQLiteDatabase db = conect.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(Constantes.BD_SERVICIO, servicio);
                    values.put(Constantes.BD_USUARIO, usuario);
                    values.put(Constantes.BD_PASSWORD, contrasenaNueva);
                    values.put(Constantes.BD_VIGENCIA, vigencia);
                    values.put(Constantes.BD_FECHA_CREACION, fechaEnviarS);
                    values.put(Constantes.BD_ULTIMO_PASS_1, contrasenaVieja);
                    values.put(Constantes.BD_ULTIMO_PASS_2, pass1);
                    values.put(Constantes.BD_ULTIMO_PASS_3, pass2);
                    values.put(Constantes.BD_ULTIMO_PASS_4, pass3);
                    values.put(Constantes.BD_ULTIMO_PASS_5, pass4);

                    db.update(Constantes.BD_CONTRASENAS, values, "idContrasena=" + idDoc, null);
                    db.close();

                    Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();

                } else if (!contrasenaNueva.equals(contrasenaVieja) && rbIndeterminado.isChecked()) {

                    fechaEnviarS = fechaActualS;
                    vigencia = "0";

                    ConexionSQLite conect = new ConexionSQLite(getContext(), Constantes.BD_PROPIETARIOS, null, Constantes.VERSION_SQLITE);
                    SQLiteDatabase db = conect.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(Constantes.BD_SERVICIO, servicio);
                    values.put(Constantes.BD_USUARIO, usuario);
                    values.put(Constantes.BD_PASSWORD, contrasenaNueva);
                    values.put(Constantes.BD_VIGENCIA, vigencia);
                    values.put(Constantes.BD_FECHA_CREACION, fechaEnviarS);
                    values.put(Constantes.BD_ULTIMO_PASS_1, contrasenaVieja);
                    values.put(Constantes.BD_ULTIMO_PASS_2, pass1);
                    values.put(Constantes.BD_ULTIMO_PASS_3, pass2);
                    values.put(Constantes.BD_ULTIMO_PASS_4, pass3);
                    values.put(Constantes.BD_ULTIMO_PASS_5, pass4);

                    db.update(Constantes.BD_CONTRASENAS, values, "idContrasena=" + idDoc, null);
                    db.close();

                    Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();

                } else if (contrasenaNueva.equals(contrasenaVieja) && vigenciaAnterior != duracionVigencia){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                    dialog.setTitle("Aviso");
                    dialog.setMessage("No puede cambiar la vigencia si no se ha cambiado la contraseña");
                    dialog.setIcon(R.drawable.ic_advertencia);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                } else {
                    ConexionSQLite conect = new ConexionSQLite(getContext(), Constantes.BD_PROPIETARIOS, null, Constantes.VERSION_SQLITE);
                    SQLiteDatabase db = conect.getWritableDatabase();

                    ContentValues values = new ContentValues();
                    values.put(Constantes.BD_SERVICIO, servicio);
                    values.put(Constantes.BD_USUARIO, usuario);

                    db.update(Constantes.BD_CONTRASENAS, values, "idContrasena=" + idDoc, null);
                    db.close();

                    Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish();

                }
            }
        }
    }
}
