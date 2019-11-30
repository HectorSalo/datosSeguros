package com.example.datossegurosFirebaseFinal.FragmentsBloqueo;

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

import com.example.datossegurosFirebaseFinal.InicSesionActivity;
import com.example.datossegurosFirebaseFinal.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SinBloqueoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SinBloqueoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SinBloqueoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public SinBloqueoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SinBloqueoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SinBloqueoFragment newInstance(String param1, String param2) {
        SinBloqueoFragment fragment = new SinBloqueoFragment();
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

        View vista = inflater.inflate(R.layout.fragment_sin_bloqueo, container, false);

        FrameLayout frameLayout = vista.findViewById(R.id.fragmentSinBloqueo);
        TextView textView = vista.findViewById(R.id.tvBienvenido);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String tema = sharedPreferences.getString("tema", "Amarillo");

        switch (tema){
            case "Amarillo":
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                break;
            case "Rojo":
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentRojo));
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDarkRojo));
                break;
            case "Marron":
                frameLayout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorAccentMarron));
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDarkMarron));
                break;
            case "Lila":
                break;
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), InicSesionActivity.class);
                getActivity().startActivity(intent);

            }
        }, 1000);
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
