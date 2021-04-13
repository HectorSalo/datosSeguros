package com.skysam.datossegurosFirebaseFinal.settings;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skysam.datossegurosFirebaseFinal.common.classView.FingerprintHandler;
import com.skysam.datossegurosFirebaseFinal.launcher.ui.InicSesionActivity;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.android.material.textfield.TextInputLayout;


public class HuellaFragment extends Fragment {
    private TextView textViewHuella, tvAccederPin;
    private EditText etPin, etPinRepetir;
    private String pinGuardado, bloqueoGuardado;
    private SharedPreferences sharedPreferences;
    private int valorNull;
    private LinearLayout linearHuella, linearPin;


    public HuellaFragment() {
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
        View vista = inflater.inflate(R.layout.fragment_huella, container, false);

        textViewHuella = (TextView) vista.findViewById(R.id.textViewHuella);
        tvAccederPin = vista.findViewById(R.id.tvAccederPin);
        etPin = (EditText) vista.findViewById(R.id.etRegistrarPINRespaldo);
        etPinRepetir = (EditText) vista.findViewById(R.id.etRegistrarPINRepetirRespaldo);
        linearHuella = vista.findViewById(R.id.linearHuella);
        linearPin = vista.findViewById(R.id.linearPIN);
        final TextInputLayout tlPinRepetir = vista.findViewById(R.id.inputLayoutRepetirPINRespaldo);
        Button registrarPin = (Button) vista.findViewById(R.id.buttonRegistrarPINRespaldo);
        FrameLayout frameLayout = vista.findViewById(R.id.fragmentHuella);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        sharedPreferences = requireActivity().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        pinGuardado = sharedPreferences.getString(Constants.PREFERENCE_PIN_RESPALDO, "0000");

        String tema = sharedPreferences.getString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO);

        switch (tema){
            case Constants.PREFERENCE_AMARILLO:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_blue_grey));
                break;
            case Constants.PREFERENCE_ROJO:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_black_ligth));
                break;
            case Constants.PREFERENCE_MARRON:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_brown));
                break;
            case Constants.PREFERENCE_LILA:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.color_purple));
                break;
        }

        bloqueoGuardado = getArguments().getString("bloqueoGuardado");
        valorNull = getArguments().getInt("null");

        if (valorNull != 1) {
            tvAccederPin.setVisibility(View.GONE);
            if (bloqueoGuardado.equals(Constants.PREFERENCE_PIN)) {
                linearPin.setVisibility(View.VISIBLE);
                linearHuella.setVisibility(View.GONE);
                tvAccederPin.setVisibility(View.GONE);
                tlPinRepetir.setVisibility(View.GONE);
            }
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
                if (valorNull != 1) {
                    if (bloqueoGuardado.equals(Constants.PREFERENCE_PIN)) {
                        accederPin();
                    } else {
                        validarPinRespaldo();
                    }
                } else {
                    accederPin();
                }

            }
        });

        registrarHuella();

        return vista;
    }


    public void registrarHuella() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FingerprintManager fingerprintManager = (FingerprintManager) getActivity().getSystemService(Context.FINGERPRINT_SERVICE);
            KeyguardManager keyguardManager = (KeyguardManager) getActivity().getSystemService(Context.KEYGUARD_SERVICE);

            if (fingerprintManager.isHardwareDetected()) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.USE_FINGERPRINT) == PackageManager.PERMISSION_GRANTED) {
                    if (keyguardManager.isKeyguardSecure()) {
                        textViewHuella.setText("Se usará la huella registrada en su Dispositivo.");

                        FingerprintHandler fingerprintHandler = new FingerprintHandler(getContext(), valorNull);
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
                etPin.setError("Debe coincidir el PIN");
                etPinRepetir.setError("Debe coincidir el PIN");
            }
        }
    }

    public void accederPin() {
        etPin.setError(null);
        String pinS = etPin.getText().toString();

        if (pinS.equals(pinGuardado)) {
            if (valorNull != 1) {
                linearPin.setVisibility(View.GONE);
                linearHuella.setVisibility(View.VISIBLE);
                tvAccederPin.setVisibility(View.GONE);
                bloqueoGuardado = Constants.PREFERENCE_HUELLA;
            } else {
                startActivity(new Intent(getContext(), InicSesionActivity.class));
            }
        } else {
            etPin.setError("El PIN no coincide con el almacenado");
        }
    }

    public void guardarPIN(String pin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_HUELLA);
        editor.putString(Constants.PREFERENCE_PIN_RESPALDO, pin);
        editor.commit();
    }


}
