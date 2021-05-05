package com.skysam.datossegurosFirebaseFinal.generalActivitys;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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


public class MainActivity extends AppCompatActivity {

    private static final int INTERVALO = 2500;
    private long tiempoPrimerClick;

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
        }else if (id == R.id.menu_cerrar_sesion) {
            confirmarCerrarSesion();
            return true;
        } else if (id == R.id.menu_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                //FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, InicSesionActivity.class));
            });
        } else {
            startActivity(new Intent(this, InicSesionActivity.class));
        }
    }
}
