package com.skysam.datossegurosFirebaseFinal.generalActivitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.classView.AddLabelDialog;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;
import com.skysam.datossegurosFirebaseFinal.passwords.ui.EditPasswordFragment;
import com.skysam.datossegurosFirebaseFinal.accounts.ui.EditarCuentasFragment;
import com.skysam.datossegurosFirebaseFinal.notes.ui.EditarNotaFragment;
import com.skysam.datossegurosFirebaseFinal.cards.ui.EditarTarjetaFragment;
import com.skysam.datossegurosFirebaseFinal.common.Constants;

public class EditarActivity extends AppCompatActivity{


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

        setContentView(R.layout.activity_editar);

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

        EditPasswordFragment editPasswordFragment = new EditPasswordFragment();
        EditarCuentasFragment editarCuentasFragment = new EditarCuentasFragment();
        EditarTarjetaFragment editarTarjetaFragment = new EditarTarjetaFragment();
        EditarNotaFragment editarNotaFragment = new EditarNotaFragment();

        Bundle myBundle = this.getIntent().getExtras();
        int data = myBundle.getInt("data");
        String id = myBundle.getString("id");
        boolean isCloud = myBundle.getBoolean("isCloud");

        Bundle bundleFragment = new Bundle();
        bundleFragment.putString("id", id);
        bundleFragment.putBoolean("isCloud", isCloud);


        if (data == 0) {
            editPasswordFragment.setArguments(bundleFragment);
            getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsEditar, editPasswordFragment).commit();
        } else if (data == 1) {
            editarCuentasFragment.setArguments(bundleFragment);
            getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsEditar, editarCuentasFragment).commit();
        } else if (data == 2) {
            editarTarjetaFragment.setArguments(bundleFragment);
            getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsEditar, editarTarjetaFragment).commit();
        } else if (data == 3) {
            editarNotaFragment.setArguments(bundleFragment);
            getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsEditar, editarNotaFragment).commit();
        }
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
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditarActivity.this);
        dialog.setTitle("Confirmar");
        dialog.setMessage("¿Desea salir? Se perderá la información no guardada");
        dialog.setIcon(R.drawable.ic_advertencia);
        dialog.setPositiveButton("Si", (dialog12, which) -> finish());
        dialog.setNegativeButton("No", (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }
}
