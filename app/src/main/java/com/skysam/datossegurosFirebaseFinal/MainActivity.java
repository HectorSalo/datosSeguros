package com.skysam.datossegurosFirebaseFinal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.skysam.datossegurosFirebaseFinal.Adpatadores.AdapterTarjeta;
import com.skysam.datossegurosFirebaseFinal.Adpatadores.BancoAdapter;
import com.skysam.datossegurosFirebaseFinal.Adpatadores.ContrasenaAdapter;
import com.skysam.datossegurosFirebaseFinal.Adpatadores.NotaAdapter;
import com.skysam.datossegurosFirebaseFinal.Clases.EliminarCuenta;
import com.skysam.datossegurosFirebaseFinal.Clases.ExportarSQLite;
import com.skysam.datossegurosFirebaseFinal.Clases.ImportarSQLite;
import com.skysam.datossegurosFirebaseFinal.Constructores.BancoConstructor;
import com.skysam.datossegurosFirebaseFinal.Constructores.ContrasenaConstructor;
import com.skysam.datossegurosFirebaseFinal.Constructores.NotaConstructor;
import com.skysam.datossegurosFirebaseFinal.Constructores.TarjetaConstructor;
import com.skysam.datossegurosFirebaseFinal.Variables.VariablesGenerales;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerView recycler;
    private ContrasenaAdapter adapterContrasena;
    private ArrayList<ContrasenaConstructor> listContrasena;
    private ArrayList<BancoConstructor> listBancos;
    private ArrayList<TarjetaConstructor> listTarjetas;
    private ArrayList<NotaConstructor> listNota;
    private NotaAdapter adapterNota;
    private AdapterTarjeta adapterTarjeta;
    private BancoAdapter adapterBanco;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FloatingActionButton fabAdd;
    private TextView sinLista;
    private Date fechaMomento;
    private int listaBuscar, add;
    private ConexionSQLite conect;
    private ProgressBar progressBarCargar;
    private SharedPreferences sharedPreferences = getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);
    private ConstraintLayout constraintLayout;
    private ExportarSQLite exportarSQLite;
    private ImportarSQLite importarSQLite;
    private boolean exportar;
    private boolean almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_contrasena:
                    if (almacenamientoNube) {
                        verContrasenaFirebase();
                    } else {
                        verContrasenasSQLite();
                    }

                    fabAdd.setImageResource(R.drawable.ic_add_contrasena);
                    listaBuscar = 0;
                    add = 0;
                    return true;
                case R.id.navigation_cuentas:
                    if (almacenamientoNube) {
                        verCuentasBancariasFirebase();
                    } else {
                        verCuentasBancariasSQLite();
                    }

                    fabAdd.setImageResource(R.drawable.ic_add_banco);
                    listaBuscar = 1;
                    add = 1;
                    return true;
                case R.id.navigation_tarjetas:
                    if (almacenamientoNube) {
                        verTarjetasFirebase();
                    } else {
                        verTarjetasSQLite();
                    }

                    fabAdd.setImageResource(R.drawable.ic_add_card);
                    listaBuscar = 2;
                    add = 2;
                    return true;
                case R.id.navigation_notas:
                    if (almacenamientoNube) {
                        verNotasFirebase();
                    } else {
                        verNotasSQLite();
                    }

                    fabAdd.setImageResource(R.drawable.ic_add_nota);
                    listaBuscar = 3;
                    add = 3;
                    return true;
            }
            return false;
        }
    };

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

        setContentView(R.layout.activity_main);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        Calendar almanaque = Calendar.getInstance();
        int diaActual = almanaque.get(Calendar.DAY_OF_MONTH);
        int mesActual = almanaque.get(Calendar.MONTH);
        int anualActual = almanaque.get(Calendar.YEAR);
        almanaque.set(anualActual, mesActual, diaActual);
        fechaMomento = almanaque.getTime();

        sinLista = (TextView) findViewById(R.id.tvSinLista);
        progressBarCargar = findViewById(R.id.progressBarCargar);
        constraintLayout = findViewById(R.id.containerMain);

        conect = new ConexionSQLite(getApplicationContext(), user.getUid(), null, Constantes.VERSION_SQLITE);

        exportarSQLite = new ExportarSQLite(this, constraintLayout);
        importarSQLite = new ImportarSQLite(this, constraintLayout);

        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        add = 0;

        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setImageResource(R.drawable.ic_add_contrasena);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putInt(Constantes.AGREGAR, add);
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
                progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDark)), PorterDuff.Mode.SRC_IN);
                break;
            case Constantes.PREFERENCE_ROJO:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentRojo));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDarkRojo)));
                progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDarkRojo)), PorterDuff.Mode.SRC_IN);
                break;
            case Constantes.PREFERENCE_MARRON:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentMarron));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDarkMarron)));
                progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDarkMarron)), PorterDuff.Mode.SRC_IN);
                break;
            case Constantes.PREFERENCE_LILA:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentLila));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDarkLila)));
                progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDarkLila)), PorterDuff.Mode.SRC_IN);
                break;
        }


        validarEscogencia();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem menuItem = menu.findItem(R.id.menu_buscar);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_acerca) {
            startActivity(new Intent(getApplicationContext(), AcercaActivity.class));
            return true;
        } else if (id == R.id.menu_perfil) {
            startActivity(new Intent(getApplicationContext(), PerfilActivity.class));
            return  true;
        } else if (id == R.id.menu_exportar_importar) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("¿Qué desea hacer?")
                    .setMessage(R.string.explicacion_importar_exportar)
                    .setPositiveButton("Importar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            verificarPermisos();
                            exportar = false;
                        }
                    })
                    .setNegativeButton("Exportar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            verificarPermisos();
                            exportar = true;
                        }
                    })
                    .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return true;
        } else if (id == R.id.menu_tema) {
            startActivity(new Intent(this, TemasActivity.class));
            return true;
        } else if (id == R.id.menu_eliminar_cuenta) {
            final EliminarCuenta eliminarCuenta = new EliminarCuenta(this);
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("¡Advertencia!")
                    .setMessage(R.string.explicacion_eliminar_cuenta)
                    .setIcon(R.drawable.ic_advertencia)
                    .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            eliminarCuenta.eliminarAlmacenamiento(user.getUid());

                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
            return true;
        } else if (id == R.id.menu_cerrar_sesion) {
            cerrarSesion();
            return true;
        } else if (id == R.id.menu_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        cerrarSesion();
    }


    public void validarEscogencia() {
        boolean escogerAlmacenamiento = sharedPreferences.getBoolean(Constantes.ALMACENAMIENTO_ESCOGIDO, false);
        if (!escogerAlmacenamiento) {
            seleccionarAlmacenamiento();
        } else {
            if (almacenamientoNube) {
                verContrasenaFirebase();
            } else {
                verContrasenasSQLite();
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
                        editor.putBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, false);
                        editor.putBoolean(Constantes.ALMACENAMIENTO_ESCOGIDO, true);
                        editor.commit();
                    }
                }).setNegativeButton("En la nube", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);
                editor.putBoolean(Constantes.ALMACENAMIENTO_ESCOGIDO, true);
                editor.commit();
            }
        })
                .setCancelable(false).show();
    }


    private void verContrasenaFirebase() {
        progressBarCargar.setVisibility(View.VISIBLE);
        String userID = user.getUid();
        listContrasena = new ArrayList<>();
        adapterContrasena = new ContrasenaAdapter(listContrasena, this);
        recycler.setAdapter(adapterContrasena);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CONTRASENAS);

        Query query = reference.orderBy(Constantes.BD_SERVICIO, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ContrasenaConstructor pass = new ContrasenaConstructor();
                        pass.setIdContrasena(doc.getId());
                        pass.setServicio(doc.getString(Constantes.BD_SERVICIO));
                        pass.setUsuario(doc.getString(Constantes.BD_USUARIO));
                        pass.setContrasena(doc.getString(Constantes.BD_PASSWORD));

                        Date momentoCreacion = doc.getDate(Constantes.BD_FECHA_CREACION);
                        long fechaCreacion = momentoCreacion.getTime();
                        long fechaActual = fechaMomento.getTime();

                        long diasRestantes = fechaActual - fechaCreacion;

                        long segundos = diasRestantes / 1000;
                        long minutos = segundos / 60;
                        long horas = minutos / 60;
                        long dias = horas / 24;
                        int diasTranscurridos = (int) dias;


                        if (doc.getString(Constantes.BD_VIGENCIA).equals("0")) {
                            pass.setVencimiento(0);
                        } else {
                            int vencimiento = Integer.parseInt(doc.getString(Constantes.BD_VIGENCIA));
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
        adapterBanco = new BancoAdapter(listBancos, this);
        recycler.setAdapter(adapterBanco);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CUENTAS);

        Query query = reference.orderBy(Constantes.BD_BANCO, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        BancoConstructor bank = new BancoConstructor();
                        bank.setIdCuenta(doc.getId());
                        bank.setTitular(doc.getString(Constantes.BD_TITULAR_BANCO));
                        bank.setBanco(doc.getString(Constantes.BD_BANCO));
                        bank.setNumeroCuenta(doc.getString(Constantes.BD_NUMERO_CUENTA));
                        bank.setCedula(doc.getString(Constantes.BD_CEDULA_BANCO));
                        bank.setTipo(doc.getString(Constantes.BD_TIPO_CUENTA));
                        bank.setTelefono(doc.getString(Constantes.BD_TELEFONO));
                        bank.setCorreo(doc.getString(Constantes.BD_CORREO_CUENTA));
                        bank.setTipoDocumento(doc.getString(Constantes.BD_TIPO_DOCUMENTO));

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
        adapterTarjeta = new AdapterTarjeta(listTarjetas, this);
        recycler.setAdapter(adapterTarjeta);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_TARJETAS);

        Query query = reference.orderBy(Constantes.BD_TITULAR_TARJETA, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        TarjetaConstructor tarjeta = new TarjetaConstructor();
                        tarjeta.setIdTarjeta(doc.getId());
                        tarjeta.setTitular(doc.getString(Constantes.BD_TITULAR_TARJETA));
                        tarjeta.setNumeroTarjeta(doc.getString(Constantes.BD_NUMERO_TARJETA));
                        tarjeta.setCvv(doc.getString(Constantes.BD_CVV));
                        tarjeta.setCedula(doc.getString(Constantes.BD_CEDULA_TARJETA));
                        tarjeta.setTipo(doc.getString(Constantes.BD_TIPO_TARJETA));
                        tarjeta.setBanco(doc.getString(Constantes.BD_BANCO_TARJETA));
                        tarjeta.setVencimiento(doc.getString(Constantes.BD_VENCIMIENTO_TARJETA));
                        tarjeta.setClave(doc.getString(Constantes.BD_CLAVE_TARJETA));

                        listTarjetas.add(tarjeta);

                    }
                    adapterTarjeta.updateList(listTarjetas);
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
        adapterNota = new NotaAdapter(listNota, this);
        recycler.setAdapter(adapterNota);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_NOTAS);

        Query query = reference.orderBy(Constantes.BD_TITULO_NOTAS, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        NotaConstructor nota = new NotaConstructor();
                        nota.setIdNota(doc.getId());
                        nota.setTitulo(doc.getString(Constantes.BD_TITULO_NOTAS));
                        nota.setContenido(doc.getString(Constantes.BD_CONTENIDO_NOTAS));

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

        listContrasena = new ArrayList<>();
        adapterContrasena = new ContrasenaAdapter(listContrasena, this);
        recycler.setAdapter(adapterContrasena);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        SQLiteDatabase db = conect.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + Constantes.BD_CONTRASENAS, null);

        while (cursor.moveToNext()) {
            ContrasenaConstructor pass = new ContrasenaConstructor();
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

        listBancos = new ArrayList<>();
        adapterBanco = new BancoAdapter(listBancos, this);
        recycler.setAdapter(adapterBanco);

        SQLiteDatabase db = conect.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + Constantes.BD_CUENTAS, null);

        while (cursor.moveToNext()) {
            BancoConstructor bank = new BancoConstructor();
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

        listTarjetas = new ArrayList<>();
        adapterTarjeta = new AdapterTarjeta(listTarjetas, this);
        recycler.setAdapter(adapterTarjeta);

        SQLiteDatabase db = conect.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + Constantes.BD_TARJETAS, null);

        while (cursor.moveToNext()) {
            TarjetaConstructor card = new TarjetaConstructor();
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

        adapterTarjeta.updateList(listTarjetas);
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

        listNota = new ArrayList<>();
        adapterNota = new NotaAdapter(listNota, this);
        recycler.setAdapter(adapterNota);

        SQLiteDatabase db = conect.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * from " + Constantes.BD_NOTAS, null);

        while (cursor.moveToNext()) {
            NotaConstructor note = new NotaConstructor();
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

    public void cerrarSesion() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Confirmar");
        dialog.setMessage("¿Desea cerrar sesión?");
        dialog.setIcon(R.drawable.ic_advertencia);
        dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                configurarSinBloqueo();
                startActivity(new Intent(getApplicationContext(), InicSesionActivity.class));
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
                final ArrayList<ContrasenaConstructor> newList = new ArrayList<>();

                for (ContrasenaConstructor name : listContrasena) {

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
                final ArrayList<BancoConstructor> newList = new ArrayList<>();

                for (BancoConstructor name : listBancos) {

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
                final ArrayList<TarjetaConstructor> newList = new ArrayList<>();

                for (TarjetaConstructor name : listTarjetas) {

                    if (name.getTitular().toLowerCase().contains(userInput) || name.getBanco().toLowerCase().contains(userInput)) {

                        newList.add(name);
                    }
                }
                adapterTarjeta.updateList(newList);
            }
        } else if (listaBuscar == 3) {
            if (listNota.isEmpty()) {
                Toast.makeText(this, "No hay lista cargada", Toast.LENGTH_SHORT).show();
            } else {
                String userInput = newText.toLowerCase();
                final ArrayList<NotaConstructor> newList = new ArrayList<>();

                for (NotaConstructor name : listNota) {

                    if (name.getTitulo().toLowerCase().contains(userInput) || name.getContenido().toLowerCase().contains(userInput)) {

                        newList.add(name);
                    }
                }
                adapterNota.updateList(newList);
            }
        }
        return false;
    }

    public void verificarPermisos() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            if (exportar) {
                exportarSQLite.crearCarpeta();
            } else {
                importarSQLite.importarBD();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (exportar) {
                        exportarSQLite.crearCarpeta();
                    } else {
                        importarSQLite.importarBD();
                    }
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    final Snackbar snackbar = Snackbar.make(constraintLayout, "Permiso Denegado. No se puede completar la tarea", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }



    public void configurarSinBloqueo() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constantes.HUELLA, false);
        editor.putBoolean(Constantes.PIN, false);
        editor.putBoolean(Constantes.SIN_BLOQUEO, true);
        editor.putString(Constantes.PIN_RESPALDO, "0000");
        editor.commit();
    }


}
