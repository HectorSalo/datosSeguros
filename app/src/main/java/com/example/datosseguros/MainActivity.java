package com.example.datosseguros;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.datosseguros.Adpatadores.ContrasenaAdapter;
import com.example.datosseguros.Constructores.ContrasenaConstructor;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private TextView mTextMessage;
    private FloatingActionButton fabAdd;
    private RecyclerView recycler;
    private ContrasenaAdapter adapter;
    private ArrayList<ContrasenaConstructor> listContrasena;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_contrasena:
                    fabAdd.setImageResource(R.drawable.ic_add_contrasena);
                    return true;
                case R.id.navigation_cuentas:
                    fabAdd.setImageResource(R.drawable.ic_add_banco);
                    return true;
                case R.id.navigation_tarjetas:
                    fabAdd.setImageResource(R.drawable.ic_add_card);
                    return true;
                case R.id.navigation_notas:
                    fabAdd.setImageResource(R.drawable.ic_add_nota);
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
        mTextMessage = findViewById(R.id.message);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);



        recycler = (RecyclerView) findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setHasFixedSize(true);
        listContrasena = new ArrayList<>();

        testContrasena();
        adapter = new ContrasenaAdapter(listContrasena, this);
        recycler.setAdapter(adapter);

        fabAdd = (FloatingActionButton) findViewById(R.id.fab_agregar);

    }

    private void testContrasena() {
        listContrasena.add(new ContrasenaConstructor("Digitel", "Hector", "1234", 12));
        listContrasena.add(new ContrasenaConstructor("Google", "Salomon", "5678", 60));
    }

}
