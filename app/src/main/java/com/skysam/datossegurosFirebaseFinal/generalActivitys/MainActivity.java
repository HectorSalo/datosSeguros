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

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recycler;
    private PasswordsAdapter adapterContrasena;
    private ArrayList<PasswordsModel> listContrasena;
    private ArrayList<AccountModel> listBancos;
    private ArrayList<CardModel> listTarjetas;
    private ArrayList<NoteModel> listNota;
    private NoteAdapter adapterNota;
    private CardAdapter cardAdapter;
    private AccountAdapter adapterBanco;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private TextView sinLista;
    private Date fechaMomento;
    private int listaBuscar, add, ultMetodo;
    private ConexionSQLite conect;
    private ProgressBar progressBarCargar;
    private SharedPreferences sharedPreferences;
    private boolean almacenamientoNube, creado;
    private static final int INTERVALO = 2500;
    private long tiempoPrimerClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        almacenamientoNube = sharedPreferences.getBoolean(Constants.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        String tema = sharedPreferences.getString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO);

        switch (tema){
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

        /*switch (tema){
            case Constants.PREFERENCE_AMARILLO:
                //progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDark)), PorterDuff.Mode.SRC_IN);
                break;
            case Constants.PREFERENCE_ROJO:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentRojo));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDarkRojo)));
                //progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDarkRojo)), PorterDuff.Mode.SRC_IN);
                break;
            case Constants.PREFERENCE_MARRON:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentMarron));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDarkMarron)));
                //progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDarkMarron)), PorterDuff.Mode.SRC_IN);
                break;
            case Constants.PREFERENCE_LILA:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentLila));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDarkLila)));
                //progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDarkLila)), PorterDuff.Mode.SRC_IN);
                break;
        }*/

        /*Calendar almanaque = Calendar.getInstance();
        int diaActual = almanaque.get(Calendar.DAY_OF_MONTH);
        int mesActual = almanaque.get(Calendar.MONTH);
        int anualActual = almanaque.get(Calendar.YEAR);
        almanaque.set(anualActual, mesActual, diaActual);
        fechaMomento = almanaque.getTime();

        sinLista = (TextView) findViewById(R.id.tvSinLista);
        progressBarCargar = findViewById(R.id.progressBarCargar);

        conect = new ConexionSQLite(getApplicationContext(), user.getUid(), null, Constants.VERSION_SQLITE);

        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        add = 0;


        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constants.AGREGAR, add);
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });




        validarEscogencia();*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_buscar);
       /* SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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


    public void validarEscogencia() {
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
    }


    private void verContrasenaFirebase() {
        progressBarCargar.setVisibility(View.VISIBLE);
        String userID = user.getUid();
        listContrasena = new ArrayList<>();
        adapterContrasena = new PasswordsAdapter(listContrasena);
        recycler.setAdapter(adapterContrasena);

        ultMetodo = 0;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_CONTRASENAS);

        Query query = reference.orderBy(Constants.BD_SERVICIO, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        PasswordsModel pass = new PasswordsModel();
                        pass.setIdContrasena(doc.getId());
                        pass.setServicio(doc.getString(Constants.BD_SERVICIO));
                        pass.setUsuario(doc.getString(Constants.BD_USUARIO));
                        pass.setContrasena(doc.getString(Constants.BD_PASSWORD));

                        Date momentoCreacion = doc.getDate(Constants.BD_FECHA_CREACION);
                        long fechaCreacion = momentoCreacion.getTime();
                        long fechaActual = fechaMomento.getTime();

                        long diasRestantes = fechaActual - fechaCreacion;

                        long segundos = diasRestantes / 1000;
                        long minutos = segundos / 60;
                        long horas = minutos / 60;
                        long dias = horas / 24;
                        int diasTranscurridos = (int) dias;


                        if (doc.getString(Constants.BD_VIGENCIA).equals("0")) {
                            pass.setVencimiento(0);
                        } else {
                            int vencimiento = Integer.parseInt(doc.getString(Constants.BD_VIGENCIA));
                            int faltante = vencimiento - diasTranscurridos;
                            pass.setVencimiento(faltante);
                        }

                        listContrasena.add(pass);

                    }
                    adapterContrasena.updateList(listContrasena);
                    if (listContrasena.isEmpty()) {
                        sinLista.setVisibility(View.VISIBLE);
                    } else {
                        sinLista.setVisibility(View.GONE);
                    }
                    progressBarCargar.setVisibility(View.GONE);
                } else {
                    progressBarCargar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void verCuentasBancariasFirebase() {
        progressBarCargar.setVisibility(View.VISIBLE);
        String userID = user.getUid();
        listBancos = new ArrayList<>();
        adapterBanco = new AccountAdapter(listBancos, this);
        recycler.setAdapter(adapterBanco);

        ultMetodo = 1;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_CUENTAS);

        Query query = reference.orderBy(Constants.BD_BANCO, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        AccountModel bank = new AccountModel();
                        bank.setIdCuenta(doc.getId());
                        bank.setTitular(doc.getString(Constants.BD_TITULAR_BANCO));
                        bank.setBanco(doc.getString(Constants.BD_BANCO));
                        bank.setNumeroCuenta(doc.getString(Constants.BD_NUMERO_CUENTA));
                        bank.setCedula(doc.getString(Constants.BD_CEDULA_BANCO));
                        bank.setTipo(doc.getString(Constants.BD_TIPO_CUENTA));
                        bank.setTelefono(doc.getString(Constants.BD_TELEFONO));
                        bank.setCorreo(doc.getString(Constants.BD_CORREO_CUENTA));
                        bank.setTipoDocumento(doc.getString(Constants.BD_TIPO_DOCUMENTO));

                        listBancos.add(bank);

                    }
                    adapterBanco.updateList(listBancos);
                    if (listBancos.isEmpty()) {
                        sinLista.setVisibility(View.VISIBLE);
                    } else {
                        sinLista.setVisibility(View.GONE);
                    }
                    progressBarCargar.setVisibility(View.GONE);

                } else {
                    progressBarCargar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void verTarjetasFirebase() {
        progressBarCargar.setVisibility(View.VISIBLE);
        String userID = user.getUid();
        listTarjetas = new ArrayList<>();
        cardAdapter = new CardAdapter(listTarjetas, this);
        recycler.setAdapter(cardAdapter);

        ultMetodo = 2;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_TARJETAS);

        Query query = reference.orderBy(Constants.BD_TITULAR_TARJETA, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        CardModel tarjeta = new CardModel();
                        tarjeta.setIdTarjeta(doc.getId());
                        tarjeta.setTitular(doc.getString(Constants.BD_TITULAR_TARJETA));
                        tarjeta.setNumeroTarjeta(doc.getString(Constants.BD_NUMERO_TARJETA));
                        tarjeta.setCvv(doc.getString(Constants.BD_CVV));
                        tarjeta.setCedula(doc.getString(Constants.BD_CEDULA_TARJETA));
                        tarjeta.setTipo(doc.getString(Constants.BD_TIPO_TARJETA));
                        tarjeta.setBanco(doc.getString(Constants.BD_BANCO_TARJETA));
                        tarjeta.setVencimiento(doc.getString(Constants.BD_VENCIMIENTO_TARJETA));
                        tarjeta.setClave(doc.getString(Constants.BD_CLAVE_TARJETA));

                        listTarjetas.add(tarjeta);

                    }
                    cardAdapter.updateList(listTarjetas);
                    if (listTarjetas.isEmpty()) {
                        sinLista.setVisibility(View.VISIBLE);
                    } else {
                        sinLista.setVisibility(View.GONE);
                    }
                    progressBarCargar.setVisibility(View.GONE);
                } else {
                    progressBarCargar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void verNotasFirebase() {
        progressBarCargar.setVisibility(View.VISIBLE);
        String userID = user.getUid();
        listNota = new ArrayList<>();
        adapterNota = new NoteAdapter(listNota, this);
        recycler.setAdapter(adapterNota);

        ultMetodo = 3;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_NOTAS);

        Query query = reference.orderBy(Constants.BD_TITULO_NOTAS, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        NoteModel nota = new NoteModel();
                        nota.setIdNota(doc.getId());
                        nota.setTitulo(doc.getString(Constants.BD_TITULO_NOTAS));
                        nota.setContenido(doc.getString(Constants.BD_CONTENIDO_NOTAS));

                        listNota.add(nota);

                    }
                    adapterNota.updateList(listNota);
                    if (listNota.isEmpty()) {
                        sinLista.setVisibility(View.VISIBLE);
                    } else {
                        sinLista.setVisibility(View.GONE);
                    }
                    progressBarCargar.setVisibility(View.GONE);

                } else {
                    progressBarCargar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

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
        adapterBanco = new AccountAdapter(listBancos, this);
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
        cardAdapter = new CardAdapter(listTarjetas, this);
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
        adapterNota = new NoteAdapter(listNota, this);
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
        configurarPreferenciasDeafault();

        String providerId = "";

        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                providerId = profile.getProviderId();
            }
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (listaBuscar == 0) {
            if (listContrasena.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
            } else {
                String userInput = newText.toLowerCase();
                final ArrayList<PasswordsModel> newList = new ArrayList<>();

                for (PasswordsModel name : listContrasena) {

                    if (name.getServicio().toLowerCase().contains(userInput) || name.getUsuario().toLowerCase().contains(userInput)) {

                        newList.add(name);
                    }
                }

                adapterContrasena.updateList(newList);

            }
        } else if (listaBuscar == 1) {
            if (listBancos.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
            } else {
                String userInput = newText.toLowerCase();
                final ArrayList<AccountModel> newList = new ArrayList<>();

                for (AccountModel name : listBancos) {

                    if (name.getBanco().toLowerCase().contains(userInput) || name.getTitular().toLowerCase().contains(userInput)) {

                        newList.add(name);
                    }
                }
                adapterBanco.updateList(newList);
            }

        } else if (listaBuscar == 2) {
            if (listTarjetas.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
            } else {
                String userInput = newText.toLowerCase();
                final ArrayList<CardModel> newList = new ArrayList<>();

                for (CardModel name : listTarjetas) {

                    if (name.getTitular().toLowerCase().contains(userInput) || name.getBanco().toLowerCase().contains(userInput)) {

                        newList.add(name);
                    }
                }
                cardAdapter.updateList(newList);
            }
        } else if (listaBuscar == 3) {
            if (listNota.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
            } else {
                String userInput = newText.toLowerCase();
                final ArrayList<NoteModel> newList = new ArrayList<>();

                for (NoteModel name : listNota) {

                    if (name.getTitulo().toLowerCase().contains(userInput) || name.getContenido().toLowerCase().contains(userInput)) {

                        newList.add(name);
                    }
                }
                adapterNota.updateList(newList);
            }
        }
        return false;
    }

    public void configurarPreferenciasDeafault() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_SIN_BLOQUEO);
        editor.putString(Constants.PREFERENCE_TEMA, Constants.PREFERENCE_AMARILLO);
        editor.putBoolean(Constants.ALMACENAMIENTO_ESCOGIDO, false);
        editor.putString(Constants.PREFERENCE_PIN_RESPALDO, "0000");
        editor.apply();
    }
}
