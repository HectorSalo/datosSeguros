package com.example.datossegurosFirebaseFinal.FragmentsEditar;

import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditarNotaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditarNotaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditarNotaFragment extends Fragment {

    private EditText etTitulo, etContenido;
    private FirebaseUser user;
    private ProgressDialog progress;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public EditarNotaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditarNotaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditarNotaFragment newInstance(String param1, String param2) {
        EditarNotaFragment fragment = new EditarNotaFragment();
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
        View vista = inflater.inflate(R.layout.fragment_editar_nota, container, false);

        etTitulo = (EditText) vista.findViewById(R.id.etTituloEditar);
        etContenido = (EditText) vista.findViewById(R.id.etContenidoEditar);
        user = FirebaseAuth.getInstance().getCurrentUser();

        cargarData();

        Button button = (Button) vista.findViewById(R.id.guardarNotaEditar);
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
        String idNota = Utilidades.idNota;

        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_NOTAS);

        reference.document(idNota).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    etTitulo.setText(doc.getString(UtilidadesStatic.BD_TITULO_NOTAS));
                    etContenido.setText(doc.getString(UtilidadesStatic.BD_CONTENIDO_NOTAS));

                    progress.dismiss();

                } else {
                    progress.dismiss();
                    Toast.makeText(getContext(), "Error al cargar. Intente nuevamente", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    public void guardarData() {
        String userID = user.getUid();
        String titulo = etTitulo.getText().toString();
        String contenido = etContenido.getText().toString();

        progress = new ProgressDialog(getContext());
        progress.setMessage("Guardando...");
        progress.setCancelable(false);
        progress.show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> nota = new HashMap<>();
        nota.put(UtilidadesStatic.BD_TITULO_NOTAS, titulo);
        nota.put(UtilidadesStatic.BD_CONTENIDO_NOTAS, contenido);

        db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_NOTAS).document(Utilidades.idNota).set(nota).addOnSuccessListener(new OnSuccessListener<Void>() {

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
                Toast.makeText(getContext(), "Error al guadar. Intente nuevamente", Toast.LENGTH_SHORT).show();
            }
        });
    }
}