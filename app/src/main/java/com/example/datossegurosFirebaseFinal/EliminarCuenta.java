package com.example.datossegurosFirebaseFinal;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.datossegurosFirebaseFinal.Constructores.ContrasenaConstructor;
import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;

public class EliminarCuenta {

    private  Context context;
    private FirebaseFirestore dbFirestore = FirebaseFirestore.getInstance();


    public EliminarCuenta(Context context) {
        this.context = context;
    }

    public void eliminarAlmacenamiento(final String id) {
        //progressBarCargar.setVisibility(View.VISIBLE);

        final CollectionReference reference = dbFirestore.collection(UtilidadesStatic.BD_PROPIETARIOS).document(id).collection(UtilidadesStatic.ALMACENAMIENTO);

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        reference.document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Status", "DocumentSnapshot successfully deleted!");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Status", "Error deleting almacenamiento", e);
                            }
                        });
                    }
                    eliminarTarjetas(id);
                    //progressBarCargar.setVisibility(View.GONE);
                } else {
                    //progressBarCargar.setVisibility(View.GONE);
                    Toast.makeText(context, "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Status", "Error querying almacenamiento", e);
            }
        });
    }

    public void eliminarContrasenas(final String id) {
        final CollectionReference reference = dbFirestore.collection(UtilidadesStatic.BD_PROPIETARIOS).document(id).collection(UtilidadesStatic.BD_CONTRASENAS);

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        reference.document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Status", "DocumentSnapshot successfully deleted!");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Status", "Error deleting contraseñas", e);
                            }
                        });
                    }
                    eliminarCuentas(id);
                    Log.d("Status", "test");
                    //progressBarCargar.setVisibility(View.GONE);
                } else {
                    //progressBarCargar.setVisibility(View.GONE);
                    Toast.makeText(context, "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Status", "Error query contraseñas", e);
            }
        });

    }

    public void eliminarCuentas(final String id) {
        final CollectionReference reference = dbFirestore.collection(UtilidadesStatic.BD_PROPIETARIOS).document(id).collection(UtilidadesStatic.BD_CUENTAS);

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        reference.document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Status", "DocumentSnapshot successfully deleted!");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Status", "Error deleting cuentas", e);
                            }
                        });
                    }
                    eliminarTarjetas(id);
                    //progressBarCargar.setVisibility(View.GONE);
                } else {
                    //progressBarCargar.setVisibility(View.GONE);
                    Toast.makeText(context, "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Status", "Error querying cuentas", e);
            }
        });
    }

    public void eliminarTarjetas(final String id) {
        final CollectionReference reference = dbFirestore.collection(UtilidadesStatic.BD_PROPIETARIOS).document(id).collection(UtilidadesStatic.BD_TARJETAS);

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        reference.document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Status", "DocumentSnapshot successfully deleted!");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Status", "Error deleting tarjetas", e);
                            }
                        });
                    }
                    eliminarNotas(id);
                    //progressBarCargar.setVisibility(View.GONE);
                } else {
                    //progressBarCargar.setVisibility(View.GONE);
                    Toast.makeText(context, "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Status", "Error querying tarjetas", e);
            }
        });
    }

    public void eliminarNotas (final String id) {
        final CollectionReference reference = dbFirestore.collection(UtilidadesStatic.BD_PROPIETARIOS).document(id).collection(UtilidadesStatic.BD_NOTAS);

        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        reference.document(doc.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d("Status", "DocumentSnapshot successfully deleted!");

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w("Status", "Error deleting notas", e);
                            }
                        });
                    }
                    eliminarPropietario(id);
                    //progressBarCargar.setVisibility(View.GONE);
                } else {
                    //progressBarCargar.setVisibility(View.GONE);
                    Toast.makeText(context, "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Status", "Error querying notas", e);
            }
        });
    }

    public void eliminarPropietario(String id) {
        final DocumentReference reference = dbFirestore.collection(UtilidadesStatic.BD_PROPIETARIOS).document(id);

        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Status", "DocumentSnapshot successfully deleted!");
                eliminarUsuario();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Status", "Error deleting propietario", e);
            }
        });

    }


    public void eliminarUsuario() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d("MSG", "User account deleted.");
                                context.startActivity(new Intent(context, InicSesionActivity.class));
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("MSG", "User account Error.");
                }
            });
        }
    }
}
