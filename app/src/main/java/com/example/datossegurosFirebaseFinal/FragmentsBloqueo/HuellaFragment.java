package com.example.datossegurosFirebaseFinal.FragmentsBloqueo;

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.datossegurosFirebaseFinal.BloqueoActivity;
import com.example.datossegurosFirebaseFinal.MainActivity;
import com.example.datossegurosFirebaseFinal.R;
import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;
import com.google.android.material.textfield.TextInputLayout;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HuellaFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HuellaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HuellaFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView textViewHuella, tvAccederPin;
    private EditText etPin, etPinRepetir;
    private String pinGuardado;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public HuellaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HuellaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HuellaFragment newInstance(String param1, String param2) {
        HuellaFragment fragment = new HuellaFragment();
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
        View vista = inflater.inflate(R.layout.fragment_huella, container, false);

        textViewHuella = (TextView) vista.findViewById(R.id.textViewHuella);
        tvAccederPin = vista.findViewById(R.id.tvAccederPin);
        etPin = (EditText) vista.findViewById(R.id.etRegistrarPINRespaldo);
        etPinRepetir = (EditText) vista.findViewById(R.id.etRegistrarPINRepetirRespaldo);
        final LinearLayout linearHuella = vista.findViewById(R.id.linearHuella);
        final LinearLayout linearPin = vista.findViewById(R.id.linearPIN);
        final TextInputLayout tlPinRepetir = vista.findViewById(R.id.inputLayoutRepetirPINRespaldo);
        Button registrarPin = (Button) vista.findViewById(R.id.buttonRegistrarPINRespaldo);

        SharedPreferences preferences = getActivity().getSharedPreferences(UtilidadesStatic.BLOQUEO, Context.MODE_PRIVATE);
        pinGuardado = preferences.getString(UtilidadesStatic.PIN_RESPALDO, "0000");

        if (Utilidades.conf_bloqueo != 1000) {
            tvAccederPin.setVisibility(View.GONE);
        }

        tvAccederPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linearPin.setVisibility(View.VISIBLE);
                linearHuella.setVisibility(View.GONE);
                tvAccederPin.setVisibility(View.GONE);
                tlPinRepetir.setVisibility(View.GONE);
            }
        });

        registrarPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utilidades.conf_bloqueo != 1000) {
                    validarPinRespaldo();
                } else {
                    accederPin();
                }

            }
        });

        registrarHuella();

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


    public void registrarHuella() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
            KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);

            if (fingerprintManager.isHardwareDetected()) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                    if (keyguardManager.isKeyguardSecure()) {
                        textViewHuella.setText("Se usará la huella registrada en su Dispositivo. Coloque su huella en el lector biométrico");

                        FingerprintHandler fingerprintHandler = new FingerprintHandler(getContext());
                        fingerprintHandler.starAuth(fingerprintManager, null);
                    } else {
                        textViewHuella.setText("Debe agregar un tipo de bloqueo a su Dispositivo");
                    }
                } else {
                    textViewHuella.setText("Debe autorizar el uso del lector de huellas");
                }
            } else {
                textViewHuella.setText("Este dispositivo no cuenta con lector de huella");
            }
        } else {
            textViewHuella.setText("Para usar esta opción debe poseer una versión de Android superior");
        }
    }

    public void validarPinRespaldo() {
        String pinS = etPin.getText().toString();
        String pinSRepetir = etPinRepetir.getText().toString();

        if (pinS.isEmpty()) {
            etPin.setError("No puede estar vacío");
        } else {
            if (pinS.equals(pinSRepetir)) {
                guardarPIN(pinS);
                startActivity(new Intent(getContext(), MainActivity.class));
            } else {
                etPin.setError("Deben coincidir el PIN");
                etPinRepetir.setError("Deben coincidir el PIN");
            }
        }
    }

    public void accederPin() {
        String pinS = etPin.getText().toString();

        if (pinS.equals(pinGuardado)) {
            startActivity(new Intent(getContext(), MainActivity.class));
        } else {
            Toast.makeText(getContext(), "El PIN no coincide con el almacenado", Toast.LENGTH_LONG).show();
        }
    }

    public void guardarPIN(String pin) {
        SharedPreferences preferences = getActivity().getSharedPreferences(UtilidadesStatic.BLOQUEO, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(UtilidadesStatic.HUELLA, true);
        editor.putBoolean(UtilidadesStatic.PIN, false);
        editor.putBoolean(UtilidadesStatic.SIN_BLOQUEO, false);
        editor.putString(UtilidadesStatic.PIN_RESPALDO, pin);
        editor.commit();
    }


}
