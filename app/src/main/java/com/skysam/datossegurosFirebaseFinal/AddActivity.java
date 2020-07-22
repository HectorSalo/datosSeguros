package com.skysam.datossegurosFirebaseFinal;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.FragmentTransaction;


import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private Spinner spinnerOpciones;
    private AddContrasenaFragment addContrasenaFragment;
    private AddCuentasFragment addCuentasFragment;
    private AddTarjetaFragment addTarjetaFragment;
    private AddNotaFragment addNotaFragment;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private SharedPreferences sharedPreferences = getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


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

        spinnerOpciones = (Spinner) findViewById(R.id.spinnerOpciones);

        String [] spOpciones = {"Cambiar opción", "Contraseña", "Cuenta Bancaria", "Tarjeta Bancaria", "Nota"};
        ArrayAdapter<String> adapterOpciones = new ArrayAdapter<String>(this, R.layout.spinner_opciones, spOpciones);
        spinnerOpciones.setAdapter(adapterOpciones);

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
                    break;
                case 1:
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, addCuentasFragment).commit();
                    break;
                case 2:
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, addTarjetaFragment).commit();
                    break;
                case 3:
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, addNotaFragment).commit();
                    break;
            }
        }


        spinnerOpciones.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                int seleccion = spinnerOpciones.getSelectedItemPosition();

                switch (seleccion) {
                    case 1:
                        transaction.replace(R.id.contenedorFragments, addContrasenaFragment);
                        break;
                    case 2:
                        transaction.replace(R.id.contenedorFragments, addCuentasFragment);
                        break;
                    case 3:
                        transaction.replace(R.id.contenedorFragments, addTarjetaFragment);
                        break;
                    case 4:
                        transaction.replace(R.id.contenedorFragments, addNotaFragment);
                        break;
                }
                transaction.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
