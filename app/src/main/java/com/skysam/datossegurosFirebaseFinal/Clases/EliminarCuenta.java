package com.skysam.datossegurosFirebaseFinal.Clases;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.skysam.datossegurosFirebaseFinal.InicSesionActivity;
import com.skysam.datossegurosFirebaseFinal.MainActivity;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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
