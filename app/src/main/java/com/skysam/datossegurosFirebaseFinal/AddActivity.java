package com.skysam.datossegurosFirebaseFinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skysam.datossegurosFirebaseFinal.Fragments.AddContrasenaFragment;
import com.skysam.datossegurosFirebaseFinal.Fragments.AddCuentasFragment;
import com.skysam.datossegurosFirebaseFinal.Fragments.AddNotaFragment;
import com.skysam.datossegurosFirebaseFinal.Fragments.AddTarjetaFragment;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;

public class AddActivity extends AppCompatActivity implements AddContrasenaFragment.OnFragmentInteractionListener, AddCuentasFragment.OnFragmentInteractionListener,
        AddTarjetaFragment.OnFragmentInteractionListener, AddNotaFragment.OnFragmentInteractionListener {

    private AddContrasenaFragment addContrasenaFragment;
    private AddCuentasFragment addCuentasFragment;
    private AddTarjetaFragment addTarjetaFragment;
    private AddNotaFragment addNotaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPreferences = getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                setTheme(R.style.AppTheme);
                break;
            case Constantes.PREFERENCE_ROJO:
                setTheme(R.style.AppThemeRojo);
                break;
            case Constantes.PREFERENCE_MARRON:
                setTheme(R.style.AppThemeMarron);
                break;
            case Constantes.PREFERENCE_LILA:
                setTheme(R.style.AppThemeLila);
                break;
        }


        setContentView(R.layout.activity_add);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioButtonContrasena = findViewById(R.id.radioButton_nueva_contrasena);
        RadioButton radioButtonCuenta = findViewById(R.id.radioButton_nueva_cuenta);
        RadioButton radioButtonTarjeta = findViewById(R.id.radioButton_nueva_tarjeta);
        RadioButton radioButtonNota = findViewById(R.id.radioButton_nueva_nota);

        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.color_fondo_agregar));
                break;
            case Constantes.PREFERENCE_ROJO:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.color_fondo_agregar_rojo));
                break;
            case Constantes.PREFERENCE_MARRON:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.color_fondo_agregar_marron));
                break;
            case Constantes.PREFERENCE_LILA:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.color_fondo_agregar_lila));
                break;
        }

        addContrasenaFragment = new AddContrasenaFragment();
        addCuentasFragment = new AddCuentasFragment();
        addTarjetaFragment = new AddTarjetaFragment();
        addNotaFragment = new AddNotaFragment();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int add = bundle.getInt(Constantes.AGREGAR);

            switch (add) {
                case 0:
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, addContrasenaFragment).commit();
                    radioButtonContrasena.setChecked(true);
                    break;
                case 1:
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, addCuentasFragment).commit();
                    radioButtonCuenta.setChecked(true);
                    break;
                case 2:
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, addTarjetaFragment).commit();
                    radioButtonTarjeta.setChecked(true);
                    break;
                case 3:
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, addNotaFragment).commit();
                    radioButtonNota.setChecked(true);
                    break;
            }
        }


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (checkedId) {
                    case R.id.radioButton_nueva_contrasena:
                        transaction.replace(R.id.contenedorFragments, addContrasenaFragment);
                        break;
                    case R.id.radioButton_nueva_cuenta:
                        transaction.replace(R.id.contenedorFragments, addCuentasFragment);
                        break;
                    case R.id.radioButton_nueva_tarjeta:
                        transaction.replace(R.id.contenedorFragments, addTarjetaFragment);
                        break;
                    case R.id.radioButton_nueva_nota:
                        transaction.replace(R.id.contenedorFragments, addNotaFragment);
                        break;
                }
                transaction.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivity.this);
        dialog.setTitle("Confirmar");
        dialog.setMessage("¿Desea salir? Se perderá la información no guardada");
        dialog.setIcon(R.drawable.ic_advertencia);
        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }


}
