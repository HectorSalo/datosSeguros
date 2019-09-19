package com.example.datossegurosFirebaseFinal;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class ConexionSQLite extends SQLiteOpenHelper {
    public ConexionSQLite(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contrasenas (idContrasena integer primary key autoincrement, servicio text, usuario text, password text, vigencia text, passviejo1 text, passviejo2 text, passviejo3 text, passviejo4 text, passviejo5 text, fechacreacion text)");
        db.execSQL("CREATE TABLE cuentas (idCuentas integer primary key autoincrement, titularbanco text, banco text, numerocuenta text, cedulabanco text, tipocuenta text, telefono text)");
        db.execSQL("CREATE TABLE tarjetas (idTarjeta integer primary key autoincrement, titulartarjeta text, numerotarjeta text, cvv text, cedulatarjeta text, tipotarjeta text)");
        db.execSQL("CREATE TABLE notas(idNota integer primary key autoincrement, titulonotas text, contenidonotas text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contrasenas");
        db.execSQL("DROP TABLE IF EXISTS cuentas");
        db.execSQL("DROP TABLE IF EXISTS tarjetas");
        db.execSQL("DROP TABLE IF EXISTS notas");
        onCreate(db);
    }
}
