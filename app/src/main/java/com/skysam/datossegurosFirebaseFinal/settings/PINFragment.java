package com.skysam.datossegurosFirebaseFinal.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skysam.datossegurosFirebaseFinal.launcher.ui.InicSesionActivity;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.android.material.textfield.TextInputLayout;


public class PINFragment extends Fragment {

    private EditText etPin, etPinRepetir;
    private String pinGuardado, bloqueoEscogido;
    private TextInputLayout layoutPin;
    private int valorNull;
    private SharedPreferences sharedPreferences;


    public PINFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_pin, container, false);
        TextView tvPinTitle = (TextView) vista.findViewById(R.id.titlePIN);
        etPin = (EditText) vista.findViewById(R.id.etRegistrarPIN);
        etPinRepetir = (EditText) vista.findViewById(R.id.etRegistrarPINRepetir);
        TextInputLayout tlPinRepetir = vista.findViewById(R.id.inputLayoutRepetirPIN);
        layoutPin = vista.findViewById(R.id.inputLayoutPIN);
        FrameLayout frameLayout = vista.findViewById(R.id.fragmentPin);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        sharedPreferences = requireActivity().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        pinGuardado = sharedPreferences.getString(Constants.PREFERENCE_PIN_RESPALDO, "0000");

        String tema = sharedPreferences.getString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO);

        switch (tema){
            case Constants.PREFERENCE_AMARILLO:
                frameLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_blue_grey));
                break;
            case Constants.PREFERENCE_ROJO:
                frameLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_black_ligth));
                break;
            case Constants.PREFERENCE_MARRON:
                frameLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_brown));
                break;
            case Constants.PREFERENCE_LILA:
                frameLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.color_purple));
                break;
        }


        final String bloqueoGuardado = getArguments().getString("bloqueoGuardado");

        valorNull = getArguments().getInt("null");
        if (valorNull == 0) {
            bloqueoEscogido = getArguments().getString("bloqueoEscogido");
            if (!bloqueoGuardado.equals(Constants.PREFERENCE_SIN_BLOQUEO)) {
                tvPinTitle.setText("Ingrese PIN almacenado");
                tlPinRepetir.setVisibility(View.GONE);
            }
        } else {
            if (bloqueoGuardado.equals(Constants.PREFERENCE_PIN)) {
                tvPinTitle.setText("Ingrese PIN almacenado");
                tlPinRepetir.setVisibility(View.GONE);
            }
        }


        Button button = vista.findViewById(R.id.buttonRegistrarPIN);
        button.setOnClickListener(v -> {
            if (valorNull == 0) {
                if (!bloqueoGuardado.equals(Constants.PREFERENCE_SIN_BLOQUEO)) {
                    validarPinRespaldo();
                } else {
                    validarPinNuevo();
                }
            } else {
                validarPinRespaldo();
            }
        });

        return vista;
    }

    public void validarPinRespaldo() {
        layoutPin.setError("");
        String pin = etPin.getText().toString();

        if (pin.equals(pinGuardado)) {
            if (valorNull == 0) {
                if (bloqueoEscogido.equals(Constants.PREFERENCE_SIN_BLOQUEO)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_SIN_BLOQUEO);
                    editor.putString(Constants.PREFERENCE_PIN_RESPALDO, "0000");
                    editor.apply();
                    startActivity(new Intent(getContext(), MainActivity.class));
                }
                if (bloqueoEscogido.equals(Constants.PREFERENCE_PIN)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_PIN);
                    editor.apply();
                }
                if (bloqueoEscogido.equals(Constants.PREFERENCE_HUELLA)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_HUELLA);
                    editor.apply();
                }
            } else {
                startActivity(new Intent(getContext(), MainActivity.class));
            }
        } else {
            layoutPin.setError("El PIN no coincide con el almacenado");
        }
    }

    public void validarPinNuevo() {
        etPin.setError(null);
        etPinRepetir.setError(null);
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
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_PIN);
                editor.putString(Constants.PREFERENCE_PIN_RESPALDO, pin);
                editor.apply();
                startActivity(new Intent(getContext(), MainActivity.class));
            } else {
                etPinRepetir.setError("El PIN no coincide");
            }
        }
    }
}
