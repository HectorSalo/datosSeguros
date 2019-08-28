package com.example.datossegurosFirebaseFinal.FragmentsEditar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.example.datossegurosFirebaseFinal.InicSesionActivity;
import com.example.datossegurosFirebaseFinal.MainActivity;
import com.example.datossegurosFirebaseFinal.R;
import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditarContrasenaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditarContrasenaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarContrasenaFragment extends Fragment {

    private EditText etServicio, etUsuario, etContrasena, etOtro;
    private RadioGroup radioEditar;
    private RadioButton rb30, rb60, rb90, rb120, rbIndeterminado, rbOtro;
    private FirebaseUser user;
    private ProgressDialog progress;
    private int duracionVigencia;
    private String contrasenaNueva, contrasenaVieja, vigencia;
    private Date fechaActual, fechaCreacion, fechaEnviar;
    private int vigenciaAnterior, vigenciaNueva;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EditarContrasenaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarContrasenaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarContrasenaFragment newInstance(String param1, String param2) {
        EditarContrasenaFragment fragment = new EditarContrasenaFragment();
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

        View vista = inflater.inflate(R.layout.fragment_editar_contrasena, container, false);

        etServicio = (EditText) vista.findViewById(R.id.etServicioEditar);
        etUsuario = (EditText) vista.findViewById(R.id.etUsuarioEditar);
        etContrasena = (EditText) vista.findViewById(R.id.etContrasenaEditar);
        etOtro = (EditText) vista.findViewById(R.id.etIngreseOtroEditar);
        radioEditar = (RadioGroup) vista.findViewById(R.id.radioDiasEditar);
        rb30 = (RadioButton) vista.findViewById(R.id.radioButton30Editar);
        rb60 = (RadioButton) vista.findViewById(R.id.radioButton60Editar);
        rb90 = (RadioButton) vista.findViewById(R.id.radioButton90Editar);
        rb120 = (RadioButton) vista.findViewById(R.id.radioButton120Editar);
        rbIndeterminado = (RadioButton) vista.findViewById(R.id.radioButtonIndeterminadoEditar);
        rbOtro = (RadioButton) vista.findViewById(R.id.radioButtonOtroEditar);
        user = FirebaseAuth.getInstance().getCurrentUser();

        Calendar almanaque = Calendar.getInstance();
        int diaActual = almanaque.get(Calendar.DAY_OF_MONTH);
        int mesActual = almanaque.get(Calendar.MONTH);
        int anualActual = almanaque.get(Calendar.YEAR);
        almanaque.set(anualActual, mesActual, diaActual);
        fechaActual = almanaque.getTime();

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
                        etOtro.setVisibility(View.GONE);
                        break;

                    case R.id.radioButtonOtroEditar:
                        etOtro.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        cargarData();

        Button button = (Button) vista.findViewById(R.id.editarContrasena);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarData();
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

    public void cargarData() {
        progress = new ProgressDialog(getContext());
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();
        String userID = user.getUid();
        String idContrasena = Utilidades.idContrasena;

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_CONTRASENAS);

        reference.document(idContrasena).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    etServicio.setText(doc.getString(UtilidadesStatic.BD_SERVICIO));
                    etUsuario.setText(doc.getString(UtilidadesStatic.BD_USUARIO));
                    etContrasena.setText(doc.getString(UtilidadesStatic.BD_PASSWORD));
                    fechaCreacion = doc.getDate(UtilidadesStatic.BD_FECHA_CREACION);

                    contrasenaVieja = doc.getString(UtilidadesStatic.BD_PASSWORD);

                    String vigencia = doc.getString(UtilidadesStatic.BD_VIGENCIA);
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


                    progress.dismiss();

                } else {
                    progress.dismiss();
                    Toast.makeText(getContext(), "Error al cargar. Intente nuevamente", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void guardarData() {
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

                    progress = new ProgressDialog(getContext());
                    progress.setMessage("Guardando...");
                    progress.setCancelable(false);
                    progress.show();

                    fechaEnviar = fechaActual;

                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> contrasenaM = new HashMap<>();
                    contrasenaM.put(UtilidadesStatic.BD_SERVICIO, servicio);
                    contrasenaM.put(UtilidadesStatic.BD_USUARIO, usuario);
                    contrasenaM.put(UtilidadesStatic.BD_PASSWORD, contrasenaNueva);
                    contrasenaM.put(UtilidadesStatic.BD_VIGENCIA, vigencia);
                    contrasenaM.put(UtilidadesStatic.BD_PROPIETARIO, userID);
                    contrasenaM.put(UtilidadesStatic.BD_FECHA_CREACION, fechaEnviar);

                    db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_CONTRASENAS).document(Utilidades.idContrasena).set(contrasenaM).addOnSuccessListener(new OnSuccessListener<Void>() {

                        public void onSuccess(Void aVoid) {
                            progress.dismiss();
                            Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(getContext(), MainActivity.class);
                            startActivity(myIntent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("msg", "Error adding document", e);
                            progress.dismiss();
                            Toast.makeText(getContext(), "Error al modificar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else if (!contrasenaNueva.equals(contrasenaVieja) && rbIndeterminado.isChecked()) {
                    progress = new ProgressDialog(getContext());
                    progress.setMessage("Guardando...");
                    progress.setCancelable(false);
                    progress.show();

                    fechaEnviar = fechaActual;
                    vigencia = "0";


                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> contrasenaM = new HashMap<>();
                    contrasenaM.put(UtilidadesStatic.BD_SERVICIO, servicio);
                    contrasenaM.put(UtilidadesStatic.BD_USUARIO, usuario);
                    contrasenaM.put(UtilidadesStatic.BD_PASSWORD, contrasenaNueva);
                    contrasenaM.put(UtilidadesStatic.BD_VIGENCIA, vigencia);
                    contrasenaM.put(UtilidadesStatic.BD_PROPIETARIO, userID);
                    contrasenaM.put(UtilidadesStatic.BD_FECHA_CREACION, fechaEnviar);

                    db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_CONTRASENAS).document(Utilidades.idContrasena).set(contrasenaM).addOnSuccessListener(new OnSuccessListener<Void>() {

                        public void onSuccess(Void aVoid) {
                            progress.dismiss();
                            Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(getContext(), MainActivity.class);
                            startActivity(myIntent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("msg", "Error adding document", e);
                            progress.dismiss();
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
                    progress = new ProgressDialog(getContext());
                    progress.setMessage("Guardando...");
                    progress.setCancelable(false);
                    progress.show();

                    fechaEnviar = fechaActual;
                    vigencia = "0";


                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    Map<String, Object> contrasenaM = new HashMap<>();
                    contrasenaM.put(UtilidadesStatic.BD_SERVICIO, servicio);
                    contrasenaM.put(UtilidadesStatic.BD_USUARIO, usuario);
                    contrasenaM.put(UtilidadesStatic.BD_PROPIETARIO, userID);

                    db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_CONTRASENAS).document(Utilidades.idContrasena).set(contrasenaM).addOnSuccessListener(new OnSuccessListener<Void>() {

                        public void onSuccess(Void aVoid) {
                            progress.dismiss();
                            Toast.makeText(getContext(), "Modificado exitosamente", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(getContext(), MainActivity.class);
                            startActivity(myIntent);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("msg", "Error adding document", e);
                            progress.dismiss();
                            Toast.makeText(getContext(), "Error al modificar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }
}
