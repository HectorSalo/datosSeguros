package com.skysam.datossegurosFirebaseFinal.ui.general;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.ui.contrasena.EditarContrasenaFragment;
import com.skysam.datossegurosFirebaseFinal.ui.cuenta.EditarCuentasFragment;
import com.skysam.datossegurosFirebaseFinal.ui.tarjeta.EditarNotaFragment;
import com.skysam.datossegurosFirebaseFinal.ui.nota.EditarTarjetaFragment;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;

public class EditarActivity extends AppCompatActivity implements EditarContrasenaFragment.OnFragmentInteractionListener, EditarCuentasFragment.OnFragmentInteractionListener,
EditarTarjetaFragment.OnFragmentInteractionListener, EditarNotaFragment.OnFragmentInteractionListener{


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

        setContentView(R.layout.activity_editar);

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

        EditarContrasenaFragment editarContrasenaFragment = new EditarContrasenaFragment();
        EditarCuentasFragment editarCuentasFragment = new EditarCuentasFragment();
        EditarTarjetaFragment editarTarjetaFragment = new EditarTarjetaFragment();
        EditarNotaFragment editarNotaFragment = new EditarNotaFragment();

        Bundle myBundle = this.getIntent().getExtras();
        int data = myBundle.getInt("data");
        String id = myBundle.getString("id");

        Bundle bundleFragment = new Bundle();
        bundleFragment.putString("id", id);


        if (data == 0) {
            editarContrasenaFragment.setArguments(bundleFragment);
            getSupportFragmentManager().beginTransaction().add(R.id.contenedorFragmentsEditar, editarContrasenaFragment).commit();
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
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditarActivity.this);
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
