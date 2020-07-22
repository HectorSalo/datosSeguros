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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditarContrasenaFragment extends Fragment {

    private EditText etServicio, etUsuario, etContrasena, etOtro;
    private RadioButton rb30, rb60, rb90, rb120, rbIndeterminado, rbOtro;
    private FirebaseUser user;
    private ProgressBar progressBarEditar;
    private int duracionVigencia;
    private String contrasenaNueva, contrasenaVieja, vigencia, pass1, pass2, pass3, pass4, pass5, fechaActualS, fechaEnviarS, idDoc;
    private Date fechaActual, fechaCreacion, fechaEnviar;
    private int vigenciaAnterior, vigenciaNueva;


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

        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        final boolean almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        idDoc = getArguments().getString("id");

        etServicio = (EditText) vista.findViewById(R.id.etServicioEditar);
        etUsuario = (EditText) vista.findViewById(R.id.etUsuarioEditar);
        etContrasena = (EditText) vista.findViewById(R.id.etContrasenaEditar);
        etOtro = (EditText) vista.findViewById(R.id.etIngreseOtroEditar);
        RadioGroup radioEditar = (RadioGroup) vista.findViewById(R.id.radioDiasEditar);
        rb30 = (RadioButton) vista.findViewById(R.id.radioButton30Editar);
        rb60 = (RadioButton) vista.findViewById(R.id.radioButton60Editar);
        rb90 = (RadioButton) vista.findViewById(R.id.radioButton90Editar);
        rb120 = (RadioButton) vista.findViewById(R.id.radioButton120Editar);
        rbIndeterminado = (RadioButton) vista.findViewById(R.id.radioButtonIndeterminadoEditar);
        rbOtro = (RadioButton) vista.findViewById(R.id.radioButtonOtroEditar);
        progressBarEditar = vista.findViewById(R.id.progressBarEditarContrasena);

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

        radioEditar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton30Editar:
                        duracionVigencia = 30;
                        etOtro.setVisibility(View.GONE);
                        break;

                    case R.id.radioButton60Editar:
                        duracionVigencia = 60;
                        etOtro.setVisibility(View.GONE);
                        break;

                    case R.id.radioButton90Editar:
                        duracionVigencia = 90;
                        etOtro.setVisibility(View.GONE);
                        break;

                    case R.id.radioButton120Editar:
                        duracionVigencia = 120;
                        etOtro.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonIndeterminadoEditar:
                        duracionVigencia = 0;
                        etOtro.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonOtroEditar:
                        etOtro.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        if (almacenamientoNube) {
            cargarDataFirebase();
        } else {
            cargarDataSQLite();
        }

        Button button = (Button) vista.findViewById(R.id.editarContrasena);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (almacenamientoNube) {
                    guardarDataFirebase();
                } else {
                    guardarDataSQLite();
                }
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
        progressBarEditar.setVisibility(View.VISIBLE);
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
                    vigenciaAnterior = Integer.parseInt(vigencia);

                    if (vigencia.equals("0")) {
                        rbIndeterminado.setChecked(true);
                    } else if (vigencia.equals("30")) {
                        rb30.setChecked(true);
                    } else if (vigencia.equals("60")) {
                        rb60.setChecked(true);
                    } else if (vigencia.equals("90")) {
                        rb90.setChecked(true);
                    } else if (vigencia.equals("120")) {
                        rb120.setChecked(true);
                    } else {
                        rbOtro.setChecked(true);
                        etOtro.setVisibility(View.VISIBLE);
                        etOtro.setText(vigencia);
                    }

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

                    progressBarEditar.setVisibility(View.GONE);

                } else {
                    progressBarEditar.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Error al cargar. Intente nuevamente", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void cargarDataSQLite() {
        progressBarEditar.setVisibility(View.VISIBLE);

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
                rbIndeterminado.setChecked(true);
            } else if (vigencia.equals("30")) {
                rb30.setChecked(true);
            } else if (vigencia.equals("60")) {
                rb60.setChecked(true);
            } else if (vigencia.equals("90")) {
                rb90.setChecked(true);
            } else if (vigencia.equals("120")) {
                rb120.setChecked(true);
            } else {
                rbOtro.setChecked(true);
                etOtro.setVisibility(View.VISIBLE);
                etOtro.setText(vigencia);
            }

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

            progressBarEditar.setVisibility(View.GONE);
            db.close();

        }
    }

    public void guardarDataFirebase() {
        String servicio = etServicio.getText().toString();
        String usuario = etUsuario.getText().toString();
        contrasenaNueva = etContrasena.getText().toString();
        String userID = user.getUid();
        vigencia = "";

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

                    progressBarEditar.setVisibility(View.VISIBLE);

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

                } else if (!contrasenaNueva.equals(contrasenaVieja) && rbIndeterminado.isChecked()) {
                    progressBarEditar.setVisibility(View.VISIBLE);

                    fechaEnviar = fechaActual;
                    vigencia = "0";


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
