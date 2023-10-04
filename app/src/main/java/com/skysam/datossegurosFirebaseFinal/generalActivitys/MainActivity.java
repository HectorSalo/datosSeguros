package com.skysam.datossegurosFirebaseFinal.generalActivitys;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.UserInfo;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.settings.SettingsActivity;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.skysam.datossegurosFirebaseFinal.launcher.ui.InicSesionActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private static final int INTERVALO = 2500;
    private long tiempoPrimerClick;
    private ArrayList<String> labels;
    private MainViewModel viewModel;

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

        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.getLabels().observe(this, item -> {
            labels = new ArrayList<>();
            labels.addAll(item);
        });

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_passwords, R.id.nav_accounts, R.id.nav_cards, R.id.nav_notes
        ).build();
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
            NavigationUI.setupWithNavController(navView, navController);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_ajustes) {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        if (id == R.id.menu_cerrar_sesion) {
            confirmarCerrarSesion();
            return true;
        }
        if (id == R.id.menu_eliminar) {
            eliminarEtiquetas();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void eliminarEtiquetas() {
        ArrayList<String> listTemporal = new ArrayList<>();
        String[] arrayLabels = labels.toArray(new String[0]);
        boolean[] array = new boolean[labels.size()];
        Arrays.fill(array, Boolean.FALSE);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle(R.string.menu_eliminar_etiqueta)
                .setMultiChoiceItems(arrayLabels, array, (dialogInterface, position, isChecked) -> {
                    if (isChecked) {
                        listTemporal.add(arrayLabels[position]);
                    } else {
                        listTemporal.remove(arrayLabels[position]);
                    }
                })
                .setPositiveButton(R.string.eliminar, (dialogInterface, i) -> {
                    if (!listTemporal.isEmpty()) {
                        viewModel.deleteLabels(listTemporal);
                    }
                });
        builder.create();
        builder.show();
    }

    @Override
    public void onBackPressed() {
        if (tiempoPrimerClick + INTERVALO > System.currentTimeMillis()) {
            finishAffinity();
        } else {
            Toast.makeText(this, "Vuelve a presionar para salir", Toast.LENGTH_SHORT).show();
        }
        tiempoPrimerClick = System.currentTimeMillis();
    }

    public void confirmarCerrarSesion() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Confirmar");
        dialog.setMessage("¿Desea cerrar sesión?");
        dialog.setIcon(R.drawable.ic_advertencia);
        dialog.setPositiveButton("Si", (dialog1, which) -> cerrarSesion());
        dialog.setNegativeButton("No", (dialog12, which) -> dialog12.dismiss());
        dialog.show();
    }

    private void cerrarSesion() {
        SharedPref.INSTANCE.changeToDefault();

        String providerId = "";

        for (UserInfo profile : Auth.INSTANCE.getCurrenUser().getProviderData()) {
            providerId = profile.getProviderId();
        }

        FirebaseAuth.getInstance().signOut();
        if (providerId.equals("google.com")) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
                startActivity(new Intent(this, InicSesionActivity.class));
            });
        } else {
            startActivity(new Intent(this, InicSesionActivity.class));
        }
    }
}
