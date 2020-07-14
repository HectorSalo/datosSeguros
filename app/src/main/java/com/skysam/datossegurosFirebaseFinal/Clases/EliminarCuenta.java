package com.skysam.datossegurosFirebaseFinal.Clases;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.skysam.datossegurosFirebaseFinal.InicSesionActivity;
import com.skysam.datossegurosFirebaseFinal.MainActivity;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;
import com.skysam.datossegurosFirebaseFinal.Variables.VariablesGenerales;
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

    public void eliminarAlmacenamiento(final String id) {
        //progressBarCargar.setVisibility(View.VISIBLE);

        final CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(id).collection(Constantes.ALMACENAMIENTO);

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
        final CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(id).collection(Constantes.BD_CONTRASENAS);

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
        final CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(id).collection(Constantes.BD_CUENTAS);

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
        final CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(id).collection(Constantes.BD_TARJETAS);

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
        final CollectionReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(id).collection(Constantes.BD_NOTAS);

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
        final DocumentReference reference = dbFirestore.collection(Constantes.BD_PROPIETARIOS).document(id);

        reference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Status", "DocumentSnapshot successfully deleted!");
                context.deleteDatabase(user.getUid());
                context.startActivity(new Intent(context, MainActivity.class));
                //eliminarUsuario();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("Status", "Error deleting propietario", e);
            }
        });

    }


    public void eliminarUsuario() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        if (user != null) {

            AuthCredential credential = EmailAuthProvider
                    .getCredential("test@gmail.com", "123456");


// Prompt the user to re-provide their sign-in credentials
            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Log.d("MSG", "User re-authenticated.");
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
                    });
        }

    }
}
