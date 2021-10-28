package com.skysam.datossegurosFirebaseFinal.generalActivitys;

import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.classView.AddLabelDialog;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;
import com.skysam.datossegurosFirebaseFinal.passwords.ui.AddPasswordFragment;
import com.skysam.datossegurosFirebaseFinal.accounts.ui.AddCuentasFragment;
import com.skysam.datossegurosFirebaseFinal.notes.ui.AddNotaFragment;
import com.skysam.datossegurosFirebaseFinal.cards.ui.AddTarjetaFragment;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.skysam.datossegurosFirebaseFinal.settings.SettingsActivity;

public class AddActivity extends AppCompatActivity {

    private AddPasswordFragment addPasswordFragment;
    private AddCuentasFragment addCuentasFragment;
    private AddTarjetaFragment addTarjetaFragment;
    private AddNotaFragment addNotaFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        switch (SharedPref.INSTANCE.getTheme()){
            case Constants.PREFERENCE_AMARILLO:
                setTheme(R.style.Theme_DatosSegurosYellow);
                break;
            case Constants.PREFERENCE_ROJO:
                setTheme(R.style.Theme_DatosSegurosRed);
                break;
            case Constants.PREFERENCE_MARRON:
                setTheme(R.style.Theme_DatosSegurosBrwon);
                break;
            case Constants.PREFERENCE_LILA:
                setTheme(R.style.Theme_DatosSegurosLila);
                break;
        }


        setContentView(R.layout.activity_add);

        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        RadioButton radioButtonContrasena = findViewById(R.id.radioButton_nueva_contrasena);
        RadioButton radioButtonCuenta = findViewById(R.id.radioButton_nueva_cuenta);
        RadioButton radioButtonTarjeta = findViewById(R.id.radioButton_nueva_tarjeta);
        RadioButton radioButtonNota = findViewById(R.id.radioButton_nueva_nota);

        ConstraintLayout constraintLayout = findViewById(R.id.constraint_layout);

        switch (SharedPref.INSTANCE.getTheme()){
            case Constants.PREFERENCE_AMARILLO:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.color_fondo_agregar));
                break;
            case Constants.PREFERENCE_ROJO:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.color_fondo_agregar_rojo));
                break;
            case Constants.PREFERENCE_MARRON:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.color_fondo_agregar_marron));
                break;
            case Constants.PREFERENCE_LILA:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.color_fondo_agregar_lila));
                break;
        }

        addPasswordFragment = new AddPasswordFragment();
        addCuentasFragment = new AddCuentasFragment();
        addTarjetaFragment = new AddTarjetaFragment();
        addNotaFragment = new AddNotaFragment();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            int add = bundle.getInt(Constants.AGREGAR);

            switch (add) {
                case 0:
                    getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragments, addPasswordFragment).commit();
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


        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            switch (checkedId) {
                case R.id.radioButton_nueva_contrasena:
                    transaction.replace(R.id.contenedorFragments, addPasswordFragment);
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
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add_label) {
            AddLabelDialog addLabelDialog = new AddLabelDialog();
            addLabelDialog.show(getSupportFragmentManager(), getString(R.string.text_label));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddActivity.this);
        dialog.setTitle("Confirmar");
        dialog.setMessage("¿Desea salir? Se perderá la información no guardada");
        dialog.setIcon(R.drawable.ic_advertencia);
        dialog.setPositiveButton("Si", (dialog12, which) -> finish());
        dialog.setNegativeButton("No", (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }
}
