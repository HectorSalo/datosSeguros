package com.skysam.datossegurosFirebaseFinal.generalActivitys;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.UserInfo;
import com.skysam.datossegurosFirebaseFinal.cards.ui.CardAdapter;
import com.skysam.datossegurosFirebaseFinal.accounts.ui.AccountAdapter;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;
import com.skysam.datossegurosFirebaseFinal.passwords.ui.PasswordsAdapter;
import com.skysam.datossegurosFirebaseFinal.notes.ui.NoteAdapter;
import com.skysam.datossegurosFirebaseFinal.common.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.common.model.AccountModel;
import com.skysam.datossegurosFirebaseFinal.common.model.PasswordsModel;
import com.skysam.datossegurosFirebaseFinal.common.model.NoteModel;
import com.skysam.datossegurosFirebaseFinal.common.model.CardModel;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.settings.SettingsActivity;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.skysam.datossegurosFirebaseFinal.launcher.ui.InicSesionActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private PasswordsAdapter adapterContrasena;
    private ArrayList<PasswordsModel> listContrasena;
    private ArrayList<AccountModel> listBancos;
    private ArrayList<CardModel> listTarjetas;
    private ArrayList<NoteModel> listNota;
    private NoteAdapter adapterNota;
    private CardAdapter cardAdapter;
    private AccountAdapter adapterBanco;
    private TextView sinLista;
    private Date fechaMomento;
    private int listaBuscar, add, ultMetodo;
    private ConexionSQLite conect;
    private ProgressBar progressBarCargar;
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

        /*Calendar almanaque = Calendar.getInstance();
        int diaActual = almanaque.get(Calendar.DAY_OF_MONTH);
        int mesActual = almanaque.get(Calendar.MONTH);
        int anualActual = almanaque.get(Calendar.YEAR);
        almanaque.set(anualActual, mesActual, diaActual);
        fechaMomento = almanaque.getTime();

        conect = new ConexionSQLite(getApplicationContext(), user.getUid(), null, Constants.VERSION_SQLITE);

        validarEscogencia();*/


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


    /*public void validarEscogencia() {
        creado = true;
        boolean escogerAlmacenamiento = sharedPreferences.getBoolean(Constants.ALMACENAMIENTO_ESCOGIDO, false);
        if (!escogerAlmacenamiento) {
            //seleccionarAlmacenamiento();
        } else {
            if (almacenamientoNube) {
                //verContrasenaFirebase();
            } else {
                //verContrasenasSQLite();
            }
        }
    }

    public void seleccionarAlmacenamiento () {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Escoja lugar de almacenamiento")
                .setMessage(R.string.explicacion_escoger_almacenamiento)
                .setPositiveButton("Dispositivo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constants.PREFERENCE_ALMACENAMIENTO_NUBE, false);
                        editor.putBoolean(Constants.ALMACENAMIENTO_ESCOGIDO, true);
                        editor.commit();
                        //verContrasenasSQLite();
                    }
                }).setNegativeButton("En la nube", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constants.PREFERENCE_ALMACENAMIENTO_NUBE, true);
                editor.putBoolean(Constants.ALMACENAMIENTO_ESCOGIDO, true);
                editor.commit();
                //verContrasenaFirebase();
            }
        })
                .setCancelable(false).show();
    }*/

    public void verContrasenasSQLite() {
        progressBarCargar.setVisibility(View.VISIBLE);

        ultMetodo = 4;

        listContrasena = new ArrayList<>();
        adapterContrasena = new PasswordsAdapter(listContrasena);
        recycler.setAdapter(adapterContrasena);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        SQLiteDatabase db = conect.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + Constants.BD_CONTRASENAS, null);

        while (cursor.moveToNext()) {
            PasswordsModel pass = new PasswordsModel();
            pass.setIdContrasena(String.valueOf(cursor.getInt(0)));
            pass.setServicio(cursor.getString(1));
            pass.setUsuario(cursor.getString(2));
            pass.setContrasena(cursor.getString(3));

            String fechaCreacionS = cursor.getString(10);
            try {
                Date fechaCreacion = sdf.parse(fechaCreacionS);
                long fechaCreacionL = fechaCreacion.getTime();
                long fechaMomentoL = fechaMomento.getTime();

                long diasRestantes = fechaCreacionL - fechaMomentoL;

                long segundos = diasRestantes / 1000;
                long minutos = segundos / 60;
                long horas = minutos / 60;
                long dias = horas / 24;
                int diasTranscurridos = (int) dias;

                if (cursor.getString(4).equals("0")) {
                    pass.setVencimiento(0);
                } else {
                    int vencimiento = Integer.parseInt(cursor.getString(4));
                    int faltante = vencimiento - diasTranscurridos;
                    pass.setVencimiento(faltante);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            listContrasena.add(pass);
        }

        adapterContrasena.updateList(listContrasena);
        if (listContrasena.isEmpty()) {
            sinLista.setVisibility(View.VISIBLE);
            progressBarCargar.setVisibility(View.GONE);
        } else {
            sinLista.setVisibility(View.GONE);
            progressBarCargar.setVisibility(View.GONE);
        }

    }

    public void verCuentasBancariasSQLite() {
        progressBarCargar.setVisibility(View.VISIBLE);

        ultMetodo = 5;

        listBancos = new ArrayList<>();
        adapterBanco = new AccountAdapter(listBancos);
        recycler.setAdapter(adapterBanco);

        SQLiteDatabase db = conect.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + Constants.BD_CUENTAS, null);

        while (cursor.moveToNext()) {
            AccountModel bank = new AccountModel();
            bank.setIdCuenta(String.valueOf(cursor.getInt(0)));
            bank.setTitular(cursor.getString(1));
            bank.setBanco(cursor.getString(2));
            bank.setNumeroCuenta(cursor.getString(3));
            bank.setCedula(cursor.getString(4));
            bank.setTipo(cursor.getString(5));
            bank.setTelefono(cursor.getString(6));
            bank.setCorreo(cursor.getString(7));

            listBancos.add(bank);
        }

        adapterBanco.updateList(listBancos);
        if (listBancos.isEmpty()) {
            sinLista.setVisibility(View.VISIBLE);
            progressBarCargar.setVisibility(View.GONE);
        } else {
            sinLista.setVisibility(View.GONE);
            progressBarCargar.setVisibility(View.GONE);
        }
    }

    public void verTarjetasSQLite() {
        progressBarCargar.setVisibility(View.VISIBLE);

        ultMetodo = 6;

        listTarjetas = new ArrayList<>();
        cardAdapter = new CardAdapter(listTarjetas);
        recycler.setAdapter(cardAdapter);

        SQLiteDatabase db = conect.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + Constants.BD_TARJETAS, null);

        while (cursor.moveToNext()) {
            CardModel card = new CardModel();
            card.setIdTarjeta(String.valueOf(cursor.getInt(0)));
            card.setTitular(cursor.getString(1));
            card.setNumeroTarjeta(cursor.getString(2));
            card.setCvv(cursor.getString(3));
            card.setCedula(cursor.getString(4));
            card.setTipo(cursor.getString(5));
            card.setBanco(cursor.getString(6));
            card.setVencimiento(cursor.getString(7));
            card.setClave(cursor.getString(8));

            listTarjetas.add(card);
        }

        cardAdapter.updateList(listTarjetas);
        if (listTarjetas.isEmpty()) {
            sinLista.setVisibility(View.VISIBLE);
            progressBarCargar.setVisibility(View.GONE);
        } else {
            sinLista.setVisibility(View.GONE);
            progressBarCargar.setVisibility(View.GONE);
        }
    }

    public void verNotasSQLite() {
        progressBarCargar.setVisibility(View.VISIBLE);

        ultMetodo = 7;

        listNota = new ArrayList<>();
        adapterNota = new NoteAdapter(listNota);
        recycler.setAdapter(adapterNota);

        SQLiteDatabase db = conect.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + Constants.BD_NOTAS, null);

        while (cursor.moveToNext()) {
            NoteModel note = new NoteModel();
            note.setIdNota(String.valueOf(cursor.getInt(0)));
            note.setTitulo(cursor.getString(1));
            note.setContenido(cursor.getString(2));

            listNota.add(note);
        }

        adapterNota.updateList(listNota);
        if (listNota.isEmpty()) {
            sinLista.setVisibility(View.VISIBLE);
            progressBarCargar.setVisibility(View.GONE);
        } else {
            sinLista.setVisibility(View.GONE);
            progressBarCargar.setVisibility(View.GONE);
        }
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
        FirebaseAuth.getInstance().signOut();
        SharedPref.INSTANCE.changeToDefault();

        String providerId = "";

        for (UserInfo profile : Auth.INSTANCE.getCurrenUser().getProviderData()) {
            providerId = profile.getProviderId();
        }

        if (providerId.equals("google.com")) {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            GoogleSignInClient mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

            mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> startActivity(new Intent(getApplicationContext(), InicSesionActivity.class)));
        } else {
            startActivity(new Intent(getApplicationContext(), InicSesionActivity.class));
        }
    }
}
