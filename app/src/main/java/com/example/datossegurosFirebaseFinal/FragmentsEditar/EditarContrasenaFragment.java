package com.example.datossegurosFirebaseFinal.FragmentsEditar;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.datossegurosFirebaseFinal.R;

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

        cargarData();

        Button button = (Button) vista.findViewById(R.id.editarContrasena);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    }
}
