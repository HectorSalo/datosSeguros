package com.skysam.datossegurosFirebaseFinal.ui.ajustes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skysam.datossegurosFirebaseFinal.ui.login.InicSesionActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;

import java.util.Objects;


public class SinBloqueoFragment extends Fragment {

    public SinBloqueoFragment() {
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

        View vista = inflater.inflate(R.layout.fragment_sin_bloqueo, container, false);

        FrameLayout frameLayout = vista.findViewById(R.id.fragmentSinBloqueo);
        TextView textView = vista.findViewById(R.id.tvBienvenido);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String usuario;
        if (user != null) {
            usuario = user.getUid();
        } else {
            usuario = "default";
        }
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(usuario, Context.MODE_PRIVATE);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                frameLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccent));
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark));
                break;
            case Constantes.PREFERENCE_ROJO:
                frameLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccentRojo));
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarkRojo));
                break;
            case Constantes.PREFERENCE_MARRON:
                frameLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccentMarron));
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarkMarron));
                break;
            case Constantes.PREFERENCE_LILA:
                frameLayout.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.colorAccentLila));
                textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDarkLila));
                break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(requireContext(), InicSesionActivity.class);
                requireActivity().startActivity(intent);

            }
        }, 1000);
        return vista;
    }
}
