package com.skysam.datossegurosFirebaseFinal.Clases;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.ajts.androidmads.library.SQLiteToExcel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;

public class ExportarSQLite {

    private Context context;
    private View view;
    private String carpetaPath;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public ExportarSQLite(Context context, View view) {
        this.context = context;
        this.view = view;
    }

    public void crearCarpeta() {
        String NOMBRE_CARPETA = "/Datos Seguros/";
        carpetaPath = Environment.getExternalStorageDirectory() + NOMBRE_CARPETA;
        File carpeta = new File(carpetaPath);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
            exportarBD();
        } else {
            exportarBD();
            Log.d("MSG", "Carpeta creada");
        }
    }

    private void exportarBD() {

        ArrayList<String> listTablas = new ArrayList<>();
        listTablas.add(0, "contrasenas");
        listTablas.add(1, "cuentas");
        listTablas.add(2, "tarjetas");
        listTablas.add(3, "notas");


        SQLiteToExcel sqLiteToExcel = new SQLiteToExcel(context, user.getUid(), carpetaPath);

        sqLiteToExcel.exportSpecificTables(listTablas,  "DatosSeguros.xls", new SQLiteToExcel.ExportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String filePath) {
                final Snackbar snackbar = Snackbar.make(view, "Descargado en: " + filePath, Snackbar.LENGTH_LONG);
                snackbar.show();

                Log.d("MSG", filePath);

            }

            @Override
            public void onError(Exception e) {
                final Snackbar snackbar = Snackbar.make(view, "Error: " + e, Snackbar.LENGTH_LONG);
                snackbar.show();

                Log.d("MSG", " " + e);
            }
        });
    }
}
