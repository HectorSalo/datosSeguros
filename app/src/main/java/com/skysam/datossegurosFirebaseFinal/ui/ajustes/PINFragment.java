package com.skysam.datossegurosFirebaseFinal.ui.ajustes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.skysam.datossegurosFirebaseFinal.ui.login.InicSesionActivity;
import com.skysam.datossegurosFirebaseFinal.ui.general.MainActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;
import com.google.android.material.textfield.TextInputLayout;


public class PINFragment extends Fragment {

    private EditText etPin, etPinRepetir;
    private TextView tvPinTitle;
    private String pinGuardado, bloqueoEscogido;
    private TextInputLayout tlPinRepetir, layoutPin;
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
        tvPinTitle = (TextView) vista.findViewById(R.id.titlePIN);
        etPin = (EditText) vista.findViewById(R.id.etRegistrarPIN);
        etPinRepetir = (EditText) vista.findViewById(R.id.etRegistrarPINRepetir);
        tlPinRepetir = vista.findViewById(R.id.inputLayoutRepetirPIN);
        layoutPin = vista.findViewById(R.id.inputLayoutPIN);
        FrameLayout frameLayout = vista.findViewById(R.id.fragmentPin);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        sharedPreferences = requireActivity().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        pinGuardado = sharedPreferences.getString(Constantes.PREFERENCE_PIN_RESPALDO, "0000");

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                break;
            case Constantes.PREFERENCE_ROJO:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentRojo));
                break;
            case Constantes.PREFERENCE_MARRON:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentMarron));
                break;
            case Constantes.PREFERENCE_LILA:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentLila));
                break;
        }


        final String bloqueoGuardado = getArguments().getString("bloqueoGuardado");

        valorNull = getArguments().getInt("null");
        if (valorNull == 0) {
            bloqueoEscogido = getArguments().getString("bloqueoEscogido");
            if (!bloqueoGuardado.equals(Constantes.PREFERENCE_SIN_BLOQUEO)) {
                tvPinTitle.setText("Ingrese PIN almacenado");
                tlPinRepetir.setVisibility(View.GONE);
            }
        } else {
            if (bloqueoGuardado.equals(Constantes.PREFERENCE_PIN)) {
                tvPinTitle.setText("Ingrese PIN almacenado");
                tlPinRepetir.setVisibility(View.GONE);
            }
        }


        Button button = vista.findViewById(R.id.buttonRegistrarPIN);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (valorNull == 0) {
                    if (!bloqueoGuardado.equals(Constantes.PREFERENCE_SIN_BLOQUEO)) {
                        validarPinRespaldo();
                    } else {
                        validarPinNuevo();
                    }
                } else {
                    validarPinRespaldo();
                }

            }
        });

        return vista;
    }

    public void validarPinRespaldo() {
        layoutPin.setError("");
        String pin = etPin.getText().toString();

        if (pin.equals(pinGuardado)) {
            if (valorNull == 0) {
                if (bloqueoEscogido.equals(Constantes.PREFERENCE_SIN_BLOQUEO)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_SIN_BLOQUEO);
                    editor.putString(Constantes.PREFERENCE_PIN_RESPALDO, "0000");
                    editor.commit();
                    startActivity(new Intent(getContext(), MainActivity.class));
                }
                if (bloqueoEscogido.equals(Constantes.PREFERENCE_PIN)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_PIN);
                    editor.commit();
                }
                if (bloqueoEscogido.equals(Constantes.PREFERENCE_HUELLA)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_HUELLA);
                    editor.commit();
                }
            } else {
                startActivity(new Intent(getContext(), InicSesionActivity.class));
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
                editor.putString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_PIN);
                editor.putString(Constantes.PREFERENCE_PIN_RESPALDO, pin);
                editor.commit();
                startActivity(new Intent(getContext(), MainActivity.class));
            } else {
                etPinRepetir.setError("El PIN no coincide");
            }
        }
    }
}
