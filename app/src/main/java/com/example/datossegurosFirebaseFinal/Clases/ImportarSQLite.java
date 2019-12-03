package com.example.datossegurosFirebaseFinal.Clases;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.ajts.androidmads.library.SQLiteToExcel;
import com.example.datossegurosFirebaseFinal.MainActivity;
import com.example.datossegurosFirebaseFinal.Variables.VariablesGenerales;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

public class ImportarSQLite {

    private Context context;
    private View view;


    public ImportarSQLite(Context context, View view) {
        this.context = context;
        this.view = view;
    }


    public void importarBD() {

        ExcelToSQLite excelToSQLite = new ExcelToSQLite(context, VariablesGenerales.userIdSQlite, true);

        String NOMBRE_CARPETA = "/Datos Seguros/";
        String carpetaPath = Environment.getExternalStorageDirectory() + NOMBRE_CARPETA + "DatosSeguros.xls";

        excelToSQLite.importFromFile(carpetaPath, new ExcelToSQLite.ImportListener() {
            @Override
            public void onStart() {

            }

            @Override
            public void onCompleted(String dbName) {
                Snackbar snackbar = Snackbar.make(view, "Carga completa", Snackbar.LENGTH_LONG);
                snackbar.show();

                context.startActivity(new Intent(context, MainActivity.class));

                Log.d("MSG", "Upload succes");

            }

            @Override
            public void onError(Exception e) {
                Snackbar snackbar = Snackbar.make(view, "Error: " + e, Snackbar.LENGTH_LONG);
                snackbar.show();

                Log.d("MSG", " " + e);
            }
        });

    }
}
