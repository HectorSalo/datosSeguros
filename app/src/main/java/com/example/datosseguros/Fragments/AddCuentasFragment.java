package com.example.datosseguros.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;

import com.example.datosseguros.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddCuentasFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddCuentasFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCuentasFragment extends Fragment {

    private EditText etTitular, etBanco, etNumeroCuenta, etCedula, etTelefono;
    private RadioButton rbAhorro, rbCorriente;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddCuentasFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddCuentasFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddCuentasFragment newInstance(String param1, String param2) {
        AddCuentasFragment fragment = new AddCuentasFragment();
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
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_add_cuentas, container, false);

        etTitular = (EditText) vista.findViewById(R.id.etTitular);
        etBanco = (EditText) vista.findViewById(R.id.etBanco);
        etNumeroCuenta = (EditText) vista.findViewById(R.id.etnumeroCuenta);
        etCedula = (EditText) vista.findViewById(R.id.etCedulaCuenta);
        etTelefono = (EditText) vista.findViewById(R.id.etTelefono);
        rbAhorro = (RadioButton) vista.findViewById(R.id.radioButtonAhorro);
        rbCorriente = (RadioButton) vista.findViewById(R.id.radioButtonCorriente);

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
}
