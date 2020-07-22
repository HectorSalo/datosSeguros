package com.skysam.datossegurosFirebaseFinal.FragmentsBloqueo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skysam.datossegurosFirebaseFinal.InicSesionActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;

import java.util.Objects;


public class SinBloqueoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

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
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(usuario, Context.MODE_PRIVATE);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                break;
            case Constantes.PREFERENCE_ROJO:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentRojo));
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDarkRojo));
                break;
            case Constantes.PREFERENCE_MARRON:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentMarron));
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDarkMarron));
                break;
            case Constantes.PREFERENCE_LILA:
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentLila));
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDarkLila));
                break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), InicSesionActivity.class);
                Objects.requireNonNull(getActivity()).startActivity(intent);

            }
        }, 1000);
        return vista;
    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}