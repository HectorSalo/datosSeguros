package com.example.datossegurosFirebaseFinal.FragmentsBloqueo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.datossegurosFirebaseFinal.MainActivity;
import com.example.datossegurosFirebaseFinal.R;
import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PINFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PINFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PINFragment extends Fragment {

    private EditText etPin, etPinRepetir;
    private TextView textViewtitle;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public PINFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PINFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PINFragment newInstance(String param1, String param2) {
        PINFragment fragment = new PINFragment();
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
        View vista = inflater.inflate(R.layout.fragment_pin, container, false);

        etPin = (EditText) vista.findViewById(R.id.etRegistrarPIN);
        etPinRepetir = (EditText) vista.findViewById(R.id.etRegistrarPINRepetir);
        textViewtitle = (TextView) vista.findViewById(R.id.titlePIN);

        if (Utilidades.conf_bloqueo == 4) {
            textViewtitle.setText("Ingrese PIN de respaldo");
        }

        Button button = (Button) vista.findViewById(R.id.buttonRegistrarPIN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilidades.conf_bloqueo == 4) {
                    validarPinRespaldo();
                }
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
        startActivity(new Intent(getContext(), MainActivity.class));
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


    public void validarPinRespaldo() {
        String pin = etPin.getText().toString();
        String pinRepetir = etPinRepetir.getText().toString();

        boolean pin1;
        boolean pin2;

        if (!pin.isEmpty()) {
            if (pin.length() == 4) {
                pin1 = true;
            } else {
                pin1 = false;
                etPin.setError("El PIN debe ser de 4 dígitos");
            }
        } else {
            pin1 = false;
            etPin.setError("No puede estar vacío");
        }

        if (!pinRepetir.isEmpty()) {
            if (pinRepetir.length() == 4) {
                pin2 = true;
            } else {
                pin2 = false;
                etPinRepetir.setError("El PIN debe ser de 4 dígitos");
            }
        } else {
            pin2 = false;
            etPinRepetir.setError("No puede estar vacío");
        }

        if (pin1 && pin2) {
            if (pin.equals(pinRepetir)) {
                SharedPreferences preferences = getContext().getSharedPreferences(UtilidadesStatic.BLOQUEO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(UtilidadesStatic.HUELLA, true);
                editor.putBoolean(UtilidadesStatic.PIN, false);
                editor.putBoolean(UtilidadesStatic.SIN_BLOQUEO, false);
                editor.commit();
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        }
    }
}
