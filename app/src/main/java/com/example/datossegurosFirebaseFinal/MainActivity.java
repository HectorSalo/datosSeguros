package com.example.datossegurosFirebaseFinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;
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
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;
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

public class MainActivity extends AppCompatActivity {

    private FloatingActionButton fabAdd;
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


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_contrasena:
                    verContrasena();
                    return true;
                case R.id.navigation_cuentas:
                    verCuentasBancarias();
                    return true;
                case R.id.navigation_tarjetas:
                    verTarjetas();
                    return true;
                case R.id.navigation_notas:
                    verNotas();
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

        user = FirebaseAuth.getInstance().getCurrentUser();

        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);

        verContrasena();

        fabAdd = (FloatingActionButton) findViewById(R.id.fab_agregar);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddActivity.class));
            }
        });

    }

    @Override
    public void onBackPressed() {
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

    private void verContrasena() {
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
                        pass.setServicio(doc.getString(UtilidadesStatic.BD_SERVICIO));
                        pass.setUsuario(doc.getString(UtilidadesStatic.BD_USUARIO));
                        pass.setContrasena(doc.getString(UtilidadesStatic.BD_PASSWORD));
                        if (doc.getString(UtilidadesStatic.BD_PASSWORD).equals("0")) {
                            pass.setVencimiento(0);
                        } else {
                            pass.setVencimiento(Integer.parseInt(doc.getString(UtilidadesStatic.BD_VIGENCIA)));
                        }

                        listContrasena.add(pass);

                    }
                    adapterContrasena.updateList(listContrasena);
                } else {
                    Toast.makeText(getApplicationContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void verCuentasBancarias() {
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
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        BancoConstructor bank = new BancoConstructor();
                        bank.setTitular(doc.getString(UtilidadesStatic.BD_TITULAR_BANCO));
                        bank.setBanco(doc.getString(UtilidadesStatic.BD_BANCO));
                        bank.setNumeroCuenta(Integer.parseInt(doc.getString(UtilidadesStatic.BD_NUMERO_CUENTA)));
                        //bank.setCedula();

                        listBancos.add(bank);

                    }
                    adapterBanco.updateList(listBancos);
                } else {
                    Toast.makeText(getApplicationContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }



    private void verTarjetas() {
        listTarjetas = new ArrayList<>();

        adapterTarjeta = new AdapterTarjeta(listTarjetas, this);
        recycler.setAdapter(adapterTarjeta);
    }



    private void verNotas() {
        listNota = new ArrayList<>();

        adapterNota = new NotaAdapter(listNota, this);
        recycler.setAdapter(adapterNota);
    }



}
