package com.example.datossegurosFirebaseFinal;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datossegurosFirebaseFinal.Adpatadores.AdapterTarjeta;
import com.example.datossegurosFirebaseFinal.Adpatadores.BancoAdapter;
import com.example.datossegurosFirebaseFinal.Adpatadores.ContrasenaAdapter;
import com.example.datossegurosFirebaseFinal.Adpatadores.NotaAdapter;
import com.example.datossegurosFirebaseFinal.Constructores.BancoConstructor;
import com.example.datossegurosFirebaseFinal.Constructores.ContrasenaConstructor;
import com.example.datossegurosFirebaseFinal.Constructores.NotaConstructor;
import com.example.datossegurosFirebaseFinal.Constructores.TarjetaConstructor;
import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;
import com.getbase.floatingactionbutton.FloatingActionsMenu;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
    private ProgressDialog progress;
    private FloatingActionsMenu fabGrupo;
    private com.getbase.floatingactionbutton.FloatingActionButton fabContrasena, fabCuenta, fabNota, fabTarjeta;
    private TextView sinLista;
    private Date fechaMomento;
    private int listaBuscar;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_contrasena:
                    verContrasena();
                    listaBuscar = 0;
                    return true;
                case R.id.navigation_cuentas:
                    verCuentasBancarias();
                    listaBuscar = 1;
                    return true;
                case R.id.navigation_tarjetas:
                    verTarjetas();
                    listaBuscar = 2;
                    return true;
                case R.id.navigation_notas:
                    verNotas();
                    listaBuscar = 3;
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        user = FirebaseAuth.getInstance().getCurrentUser();

        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        verContrasena();

        fabGrupo = (FloatingActionsMenu) findViewById(R.id.fabGrupo);
        fabContrasena = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fabContrasena);
        fabCuenta = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fabCuenta);
        fabTarjeta = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fabTarjeta);
        fabNota = (com.getbase.floatingactionbutton.FloatingActionButton) findViewById(R.id.fabNota);

        fabContrasena.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.Add = 0;
                startActivity(new Intent(MainActivity.this, AddActivity.class));
                fabGrupo.collapse();
            }
        });

        fabCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.Add = 1;
                startActivity(new Intent(MainActivity.this, AddActivity.class));
                fabGrupo.collapse();
            }
        });

        fabTarjeta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.Add = 2;
                startActivity(new Intent(MainActivity.this, AddActivity.class));
                fabGrupo.collapse();
            }
        });

        fabNota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utilidades.Add = 3;
                startActivity(new Intent(MainActivity.this, AddActivity.class));
                fabGrupo.collapse();
            }
        });

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

    private void verContrasena() {
        progress = new ProgressDialog(this);
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();
        String userID = user.getUid();
        listContrasena = new ArrayList<>();
        adapterContrasena = new ContrasenaAdapter(listContrasena, this);
        recycler.setAdapter(adapterContrasena);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_CONTRASENAS);

        Query query = reference.orderBy(UtilidadesStatic.BD_SERVICIO, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        ContrasenaConstructor pass = new ContrasenaConstructor();
                        pass.setIdContrasena(doc.getId());
                        pass.setServicio(doc.getString(UtilidadesStatic.BD_SERVICIO));
                        pass.setUsuario(doc.getString(UtilidadesStatic.BD_USUARIO));
                        pass.setContrasena(doc.getString(UtilidadesStatic.BD_PASSWORD));

                        Date momentoCreacion = doc.getDate(UtilidadesStatic.BD_FECHA_CREACION);
                        long fechaCreacion = momentoCreacion.getTime();
                        long fechaActual = fechaMomento.getTime();

                        long diasRestantes = fechaActual - fechaCreacion;

                        long segundos = diasRestantes / 1000;
                        long minutos = segundos / 60;
                        long horas = minutos / 60;
                        long dias = horas / 24;
                        int diasTranscurridos = (int) dias;


                        if (doc.getString(UtilidadesStatic.BD_VIGENCIA).equals("0")) {
                            pass.setVencimiento(0);
                        } else {
                            int vencimiento = Integer.parseInt(doc.getString(UtilidadesStatic.BD_VIGENCIA));
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
                    progress.dismiss();
                } else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void verCuentasBancarias() {
        progress = new ProgressDialog(this);
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();
        String userID = user.getUid();
        listBancos = new ArrayList<>();
        adapterBanco = new BancoAdapter(listBancos, this);
        recycler.setAdapter(adapterBanco);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_CUENTAS);

        Query query = reference.orderBy(UtilidadesStatic.BD_BANCO, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        BancoConstructor bank = new BancoConstructor();
                        bank.setIdCuenta(doc.getId());
                        bank.setTitular(doc.getString(UtilidadesStatic.BD_TITULAR_BANCO));
                        bank.setBanco(doc.getString(UtilidadesStatic.BD_BANCO));
                        bank.setNumeroCuenta(doc.getString(UtilidadesStatic.BD_NUMERO_CUENTA));
                        bank.setCedula(doc.getString(UtilidadesStatic.BD_CEDULA_BANCO));
                        bank.setTipo(doc.getString(UtilidadesStatic.BD_TIPO_CUENTA));
                        bank.setTelefono(doc.getString(UtilidadesStatic.BD_TELEFONO));

                        listBancos.add(bank);

                    }
                    adapterBanco.updateList(listBancos);
                    if (listBancos.isEmpty()) {
                        sinLista.setVisibility(View.VISIBLE);
                    } else {
                        sinLista.setVisibility(View.GONE);
                    }
                    progress.dismiss();

                } else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void verTarjetas() {
        progress = new ProgressDialog(this);
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();
        String userID = user.getUid();
        listTarjetas = new ArrayList<>();
        adapterTarjeta = new AdapterTarjeta(listTarjetas, this);
        recycler.setAdapter(adapterTarjeta);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_TARJETAS);

        Query query = reference.orderBy(UtilidadesStatic.BD_TITULAR_TARJETA, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        TarjetaConstructor tarjeta = new TarjetaConstructor();
                        tarjeta.setIdTarjeta(doc.getId());
                        tarjeta.setTitular(doc.getString(UtilidadesStatic.BD_TITULAR_TARJETA));
                        tarjeta.setNumeroTarjeta(doc.getString(UtilidadesStatic.BD_NUMERO_TARJETA));
                        tarjeta.setCvv(doc.getString(UtilidadesStatic.BD_CVV));
                        tarjeta.setCedula(doc.getString(UtilidadesStatic.BD_CEDULA_TARJETA));
                        tarjeta.setTipo(doc.getString(UtilidadesStatic.BD_TIPO_TARJETA));

                        listTarjetas.add(tarjeta);

                    }
                    adapterTarjeta.updateList(listTarjetas);
                    if (listTarjetas.isEmpty()) {
                        sinLista.setVisibility(View.VISIBLE);
                    } else {
                        sinLista.setVisibility(View.GONE);
                    }
                    progress.dismiss();
                } else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void verNotas() {
        progress = new ProgressDialog(this);
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();
        String userID = user.getUid();
        listNota = new ArrayList<>();
        adapterNota = new NotaAdapter(listNota, this);
        recycler.setAdapter(adapterNota);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_NOTAS);

        Query query = reference.orderBy(UtilidadesStatic.BD_TITULO_NOTAS, Query.Direction.ASCENDING);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        NotaConstructor nota = new NotaConstructor();
                        nota.setIdNota(doc.getId());
                        nota.setTitulo(doc.getString(UtilidadesStatic.BD_TITULO_NOTAS));
                        nota.setContenido(doc.getString(UtilidadesStatic.BD_CONTENIDO_NOTAS));

                        listNota.add(nota);

                    }
                    adapterNota.updateList(listNota);
                    if (listNota.isEmpty()) {
                        sinLista.setVisibility(View.VISIBLE);
                    } else {
                        sinLista.setVisibility(View.GONE);
                    }
                    progress.dismiss();

                } else {
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
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

                    if (name.getTitular().toLowerCase().contains(userInput)) {

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
}
