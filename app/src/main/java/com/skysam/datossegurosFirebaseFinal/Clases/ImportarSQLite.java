package com.skysam.datossegurosFirebaseFinal.Clases;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.ajts.androidmads.library.ExcelToSQLite;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.skysam.datossegurosFirebaseFinal.MainActivity;
import com.skysam.datossegurosFirebaseFinal.Variables.VariablesGenerales;
import com.google.android.material.snackbar.Snackbar;

public class ImportarSQLite {

    private Context context;
    private View view;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public ImportarSQLite(Context context, View view) {
        this.context = context;
        this.view = view;
    }


    public void importarBD() {

        ExcelToSQLite excelToSQLite = new ExcelToSQLite(context, user.getUid(), true);

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
