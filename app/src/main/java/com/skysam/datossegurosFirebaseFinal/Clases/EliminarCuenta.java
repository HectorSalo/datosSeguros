package com.skysam.datossegurosFirebaseFinal.Clases;

import android.content.Context;

import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class EliminarCuenta {

    private  Context context;
    private FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


    public EliminarCuenta(Context context) {
        this.context = context;
    }

    public void eliminarContrasenas(String id) {
        CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(id).collection(Constantes.BD_CONTRASENAS);

        reference.document(id).delete();
    }

    public void eliminarCuentas(String id) {
        CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(id).collection(Constantes.BD_CUENTAS);

        reference.document(id).delete();
    }

    public void eliminarTarjetas(String id) {
        CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(id).collection(Constantes.BD_TARJETAS);

        reference.document(id).delete();
    }

    public void eliminarNotas (String id) {
        CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(id).collection(Constantes.BD_NOTAS);

        reference.document(id).delete();
    }
}
