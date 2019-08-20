package com.example.datossegurosFirebaseFinal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.view.MenuItem;
import android.view.View;

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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

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
        listContrasena = new ArrayList<>();
        testContrasena();
        adapterContrasena = new ContrasenaAdapter(listContrasena, this);
        recycler.setAdapter(adapterContrasena);
    }

    private void testContrasena() {
        listContrasena.add(new ContrasenaConstructor("Digitel", "Hector", "1234", 12));
        listContrasena.add(new ContrasenaConstructor("Google", "Salomon", "5678", 60));
    }

    private void verCuentasBancarias() {
        listBancos = new ArrayList<>();
        testBanco();
        adapterBanco = new BancoAdapter(listBancos, this);
        recycler.setAdapter(adapterBanco);
    }

    private void testBanco() {
        listBancos.add(new BancoConstructor("Luis", "Mercantil", "Ahorro", 1234567890, 654321, 04121234567));
        listBancos.add(new BancoConstructor("Yolimar", "BOD", "Corriente", 1873214560, 123789, 04147654231));
    }

    private void verTarjetas() {
        listTarjetas = new ArrayList<>();
        testTarjeta();
        adapterTarjeta = new AdapterTarjeta(listTarjetas, this);
        recycler.setAdapter(adapterTarjeta);
    }

    private void testTarjeta() {
        listTarjetas.add(new TarjetaConstructor("Franck", 123456546, 455, 1234567, "Mastercard"));
        listTarjetas.add(new TarjetaConstructor("Franck", 123456546, 123, 789456, "Visa"));
    }

    private void verNotas() {
        listNota = new ArrayList<>();
        testNota();
        adapterNota = new NotaAdapter(listNota, this);
        recycler.setAdapter(adapterNota);
    }

    private void testNota() {
        listNota.add(new NotaConstructor("Test1 Titulo", "Test1 de Contenido"));
        listNota.add(new NotaConstructor("Test2 Titulo", "Test2 de Contenido"));
    }

}
