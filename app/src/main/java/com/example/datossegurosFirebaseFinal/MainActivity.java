package com.example.datossegurosFirebaseFinal;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PorterDuff;
import android.os.Bundle;

import android.preference.PreferenceManager;
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

import com.example.datossegurosFirebaseFinal.Adpatadores.AdapterTarjeta;
import com.example.datossegurosFirebaseFinal.Adpatadores.BancoAdapter;
import com.example.datossegurosFirebaseFinal.Adpatadores.ContrasenaAdapter;
import com.example.datossegurosFirebaseFinal.Adpatadores.NotaAdapter;
import com.example.datossegurosFirebaseFinal.Clases.EliminarCuenta;
import com.example.datossegurosFirebaseFinal.Clases.ExportarSQLite;
import com.example.datossegurosFirebaseFinal.Clases.ImportarSQLite;
import com.example.datossegurosFirebaseFinal.Constructores.BancoConstructor;
import com.example.datossegurosFirebaseFinal.Constructores.ContrasenaConstructor;
import com.example.datossegurosFirebaseFinal.Constructores.NotaConstructor;
import com.example.datossegurosFirebaseFinal.Constructores.TarjetaConstructor;
import com.example.datossegurosFirebaseFinal.Variables.VariablesGenerales;
import com.example.datossegurosFirebaseFinal.Variables.VariablesEstaticas;
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
    private FirebaseUser user;
    private FloatingActionButton fabAdd;
    private TextView sinLista;
    private Date fechaMomento;
    private int listaBuscar;
    private ConexionSQLite conect;
    private ProgressBar progressBarCargar;
    private SharedPreferences sharedPreferences;
    private ConstraintLayout constraintLayout;
    private ExportarSQLite exportarSQLite;
    private ImportarSQLite importarSQLite;
    private boolean exportar;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_contrasena:
                    if (VariablesGenerales.almacenamientoExterno) {
                        verContrasenaFirebase();
                    } else if (VariablesGenerales.almacenamientoInterno) {
                        verContrasenasSQLite();
                    }

                    fabAdd.setImageResource(R.drawable.ic_add_contrasena);
                    listaBuscar = 0;
                    VariablesGenerales.Add = 0;
                    return true;
                case R.id.navigation_cuentas:
                    if (VariablesGenerales.almacenamientoExterno) {
                        verCuentasBancariasFirebase();
                    } else if (VariablesGenerales.almacenamientoInterno) {
                        verCuentasBancariasSQLite();
                    }

                    fabAdd.setImageResource(R.drawable.ic_add_banco);
                    listaBuscar = 1;
                    VariablesGenerales.Add = 1;
                    return true;
                case R.id.navigation_tarjetas:
                    if (VariablesGenerales.almacenamientoExterno) {
                        verTarjetasFirebase();
                    } else if (VariablesGenerales.almacenamientoInterno) {
                        verTarjetasSQLite();
                    }

                    fabAdd.setImageResource(R.drawable.ic_add_card);
                    listaBuscar = 2;
                    VariablesGenerales.Add = 2;
                    return true;
                case R.id.navigation_notas:
                    if (VariablesGenerales.almacenamientoExterno) {
                        verNotasFirebase();
                    } else if (VariablesGenerales.almacenamientoInterno) {
                        verNotasSQLite();
                    }

                    fabAdd.setImageResource(R.drawable.ic_add_nota);
                    listaBuscar = 3;
                    VariablesGenerales.Add = 3;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String tema = sharedPreferences.getString("tema", "Amarillo");

        switch (tema){
            case "Amarillo":
                setTheme(R.style.AppTheme);
                break;
            case "Rojo":
                setTheme(R.style.AppThemeRojo);
                break;
            case "Marron":
                setTheme(R.style.AppThemeMarron);
                break;
            case "Lila":
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

        user = FirebaseAuth.getInstance().getCurrentUser();
        conect = new ConexionSQLite(getApplicationContext(), VariablesGenerales.userIdSQlite, null, VariablesEstaticas.VERSION_SQLITE);

        exportarSQLite = new ExportarSQLite(this, constraintLayout);
        importarSQLite = new ImportarSQLite(this, constraintLayout);

        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        VariablesGenerales.Add = 0;

        fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
        fabAdd.setImageResource(R.drawable.ic_add_contrasena);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddActivity.class));
            }
        });

        switch (tema){
            case "Amarillo":
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccent));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDark)));
                progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDark)), PorterDuff.Mode.SRC_IN);
                break;
            case "Rojo":
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentRojo));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDarkRojo)));
                progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDarkRojo)), PorterDuff.Mode.SRC_IN);
                break;
            case "Marron":
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentMarron));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDarkMarron)));
                progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDarkMarron)), PorterDuff.Mode.SRC_IN);
                break;
            case "Lila":
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.colorAccentLila));
                fabAdd.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimaryDarkLila)));
                progressBarCargar.getIndeterminateDrawable().setColorFilter((ContextCompat.getColor(this, R.color.colorPrimaryDarkLila)), PorterDuff.Mode.SRC_IN);
                break;
        }


        escogenciaAlmacenamiento();


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

    public void seleccionAlmacenamiento () {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Escoja lugar de almacenamiento")
                .setMessage(R.string.explicacion_escoger_almacenamiento)
                .setPositiveButton("Dispositivo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressBarCargar.setVisibility(View.VISIBLE);
                        String userID = user.getUid();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String, Object> almacenar = new HashMap<>();
                        almacenar.put(VariablesEstaticas.INTERNO, true);
                        almacenar.put(VariablesEstaticas.EXTERNO, false);
                        almacenar.put(VariablesEstaticas.ALMACENAMIENTO_ESCOGIDO, true);

                        db.collection(VariablesEstaticas.BD_PROPIETARIOS).document(userID).collection(VariablesEstaticas.ALMACENAMIENTO).document(VariablesEstaticas.ALMACENAMIENTO_DOC).set(almacenar).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("msg", "Succes");
                                progressBarCargar.setVisibility(View.GONE);
                                recreate();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("msg", "Error adding document", e);
                                Toast.makeText(getApplicationContext(), "Error al configurar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                                progressBarCargar.setVisibility(View.GONE);
                                seleccionAlmacenamiento();
                            }
                        });

                    }
                }).setNegativeButton("En la nube", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressBarCargar.setVisibility(View.VISIBLE);
                String userID = user.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> almacenar = new HashMap<>();
                almacenar.put(VariablesEstaticas.INTERNO, false);
                almacenar.put(VariablesEstaticas.EXTERNO, true);
                almacenar.put(VariablesEstaticas.ALMACENAMIENTO_ESCOGIDO, true);

                db.collection(VariablesEstaticas.BD_PROPIETARIOS).document(userID).collection(VariablesEstaticas.ALMACENAMIENTO).document(VariablesEstaticas.ALMACENAMIENTO_DOC).set(almacenar).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("msg", "Succes");
                        progressBarCargar.setVisibility(View.GONE);
                        recreate();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("msg", "Error adding document", e);
                        progressBarCargar.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Error al configurar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                        seleccionAlmacenamiento();
                    }
                });

            }
        })
                .setCancelable(false).show();
    }


    public void escogenciaAlmacenamiento() {
        progressBarCargar.setVisibility(View.VISIBLE);
        String userID = user.getUid();
        FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
        CollectionReference reference = dbFirestore.collection(VariablesEstaticas.BD_PROPIETARIOS).document(userID).collection(VariablesEstaticas.ALMACENAMIENTO);

        reference.document(VariablesEstaticas.ALMACENAMIENTO_DOC).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    if(doc.exists()) {

                        VariablesGenerales.escogerAlmacenamiento = doc.getBoolean(VariablesEstaticas.ALMACENAMIENTO_ESCOGIDO);
                        VariablesGenerales.almacenamientoExterno = doc.getBoolean(VariablesEstaticas.EXTERNO);
                        VariablesGenerales.almacenamientoInterno = doc.getBoolean(VariablesEstaticas.INTERNO);
                    }

                    validarEscogencia();
                    progressBarCargar.setVisibility(View.GONE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d ("msg", "Failed", e);
                progressBarCargar.setVisibility(View.GONE);
            }
        });
    }

    public void validarEscogencia() {
        if (!VariablesGenerales.escogerAlmacenamiento) {
            seleccionAlmacenamiento();
        } else if (VariablesGenerales.almacenamientoExterno) {
            verContrasenaFirebase();
        } else if (VariablesGenerales.almacenamientoInterno) {
            verContrasenasSQLite();
        }
    }

    private void verContrasenaFirebase() {
        progressBarCargar.setVisibility(View.VISIBLE);
        String userID = user.getUid();
        listContrasena = new ArrayList<>();
        adapterContrasena = new ContrasenaAdapter(listContrasena, this);
        recycler.setAdapter(adapterContrasena);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(VariablesEstaticas.BD_PROPIETARIOS).document(userID).collection(VariablesEstaticas.BD_CONTRASENAS);

        Query query = reference.orderBy(VariablesEstaticas.BD_SERVICIO, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ContrasenaConstructor pass = new ContrasenaConstructor();
                        pass.setIdContrasena(doc.getId());
                        pass.setServicio(doc.getString(VariablesEstaticas.BD_SERVICIO));
                        pass.setUsuario(doc.getString(VariablesEstaticas.BD_USUARIO));
                        pass.setContrasena(doc.getString(VariablesEstaticas.BD_PASSWORD));

                        Date momentoCreacion = doc.getDate(VariablesEstaticas.BD_FECHA_CREACION);
                        long fechaCreacion = momentoCreacion.getTime();
                        long fechaActual = fechaMomento.getTime();

                        long diasRestantes = fechaActual - fechaCreacion;

                        long segundos = diasRestantes / 1000;
                        long minutos = segundos / 60;
                        long horas = minutos / 60;
                        long dias = horas / 24;
                        int diasTranscurridos = (int) dias;


                        if (doc.getString(VariablesEstaticas.BD_VIGENCIA).equals("0")) {
                            pass.setVencimiento(0);
                        } else {
                            int vencimiento = Integer.parseInt(doc.getString(VariablesEstaticas.BD_VIGENCIA));
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
        CollectionReference reference = db.collection(VariablesEstaticas.BD_PROPIETARIOS).document(userID).collection(VariablesEstaticas.BD_CUENTAS);

        Query query = reference.orderBy(VariablesEstaticas.BD_BANCO, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        BancoConstructor bank = new BancoConstructor();
                        bank.setIdCuenta(doc.getId());
                        bank.setTitular(doc.getString(VariablesEstaticas.BD_TITULAR_BANCO));
                        bank.setBanco(doc.getString(VariablesEstaticas.BD_BANCO));
                        bank.setNumeroCuenta(doc.getString(VariablesEstaticas.BD_NUMERO_CUENTA));
                        bank.setCedula(doc.getString(VariablesEstaticas.BD_CEDULA_BANCO));
                        bank.setTipo(doc.getString(VariablesEstaticas.BD_TIPO_CUENTA));
                        bank.setTelefono(doc.getString(VariablesEstaticas.BD_TELEFONO));
                        bank.setCorreo(doc.getString(VariablesEstaticas.BD_CORREO_CUENTA));
                        bank.setTipoDocumento(doc.getString(VariablesEstaticas.BD_TIPO_DOCUMENTO));

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
        CollectionReference reference = db.collection(VariablesEstaticas.BD_PROPIETARIOS).document(userID).collection(VariablesEstaticas.BD_TARJETAS);

        Query query = reference.orderBy(VariablesEstaticas.BD_TITULAR_TARJETA, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        TarjetaConstructor tarjeta = new TarjetaConstructor();
                        tarjeta.setIdTarjeta(doc.getId());
                        tarjeta.setTitular(doc.getString(VariablesEstaticas.BD_TITULAR_TARJETA));
                        tarjeta.setNumeroTarjeta(doc.getString(VariablesEstaticas.BD_NUMERO_TARJETA));
                        tarjeta.setCvv(doc.getString(VariablesEstaticas.BD_CVV));
                        tarjeta.setCedula(doc.getString(VariablesEstaticas.BD_CEDULA_TARJETA));
                        tarjeta.setTipo(doc.getString(VariablesEstaticas.BD_TIPO_TARJETA));
                        tarjeta.setBanco(doc.getString(VariablesEstaticas.BD_BANCO_TARJETA));
                        tarjeta.setVencimiento(doc.getString(VariablesEstaticas.BD_VENCIMIENTO_TARJETA));
                        tarjeta.setClave(doc.getString(VariablesEstaticas.BD_CLAVE_TARJETA));

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
        CollectionReference reference = db.collection(VariablesEstaticas.BD_PROPIETARIOS).document(userID).collection(VariablesEstaticas.BD_NOTAS);

        Query query = reference.orderBy(VariablesEstaticas.BD_TITULO_NOTAS, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        NotaConstructor nota = new NotaConstructor();
                        nota.setIdNota(doc.getId());
                        nota.setTitulo(doc.getString(VariablesEstaticas.BD_TITULO_NOTAS));
                        nota.setContenido(doc.getString(VariablesEstaticas.BD_CONTENIDO_NOTAS));

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

        Cursor cursor = db.rawQuery("SELECT * from " + VariablesEstaticas.BD_CONTRASENAS, null);

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

        Cursor cursor = db.rawQuery("SELECT * from " + VariablesEstaticas.BD_CUENTAS, null);

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

        Cursor cursor = db.rawQuery("SELECT * from " + VariablesEstaticas.BD_TARJETAS, null);

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

        Cursor cursor = db.rawQuery("SELECT * from " + VariablesEstaticas.BD_NOTAS, null);

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
        editor.putBoolean(VariablesEstaticas.HUELLA, false);
        editor.putBoolean(VariablesEstaticas.PIN, false);
        editor.putBoolean(VariablesEstaticas.SIN_BLOQUEO, true);
        editor.putString(VariablesEstaticas.PIN_RESPALDO, "0000");
        editor.commit();
    }


}
