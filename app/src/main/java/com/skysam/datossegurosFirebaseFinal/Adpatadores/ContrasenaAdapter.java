package com.skysam.datossegurosFirebaseFinal.Adpatadores;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.skysam.datossegurosFirebaseFinal.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.Constructores.ContrasenaConstructor;
import com.skysam.datossegurosFirebaseFinal.EditarActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.Variables.VariablesGenerales;
import com.skysam.datossegurosFirebaseFinal.Variables.VariablesEstaticas;
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

import java.util.ArrayList;

public class ContrasenaAdapter extends RecyclerView.Adapter<ContrasenaAdapter.ViewHolderContrasena> {

    private ArrayList<ContrasenaConstructor> listContrasena;
    private ArrayList<String> selectedItems;
    private ArrayList <String> selectedCopiar;
    private Context mCtx;
    private FirebaseUser user;

    public ContrasenaAdapter (ArrayList<ContrasenaConstructor> listContrasena, Context mCtx) {
        this.listContrasena = listContrasena;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ContrasenaAdapter.ViewHolderContrasena onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_contrasena, null, false);

        return new ViewHolderContrasena(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContrasenaAdapter.ViewHolderContrasena viewHolderContrasena, final int i) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mCtx);

        String tema = sharedPreferences.getString("tema", "Amarillo");

        switch (tema){
            case "Amarillo":
                viewHolderContrasena.cardView.setBackgroundResource(R.drawable.fondo_contrasena);
                break;
            case "Rojo":
                viewHolderContrasena.cardView.setBackgroundResource(R.drawable.fondo_listas_rojo);
                break;
            case "Marron":
                viewHolderContrasena.cardView.setBackgroundResource(R.drawable.fondo_listas_marron);
                break;
            case "Lila":
                viewHolderContrasena.cardView.setBackgroundResource(R.drawable.fondo_listas_lila);
                break;
        }

        viewHolderContrasena.servicio.setText(listContrasena.get(i).getServicio());
        viewHolderContrasena.usuario.setText(listContrasena.get(i).getUsuario());
        viewHolderContrasena.contrasena.setText(listContrasena.get(i).getContrasena());
        if (listContrasena.get(i).getVencimiento() == 0) {
            viewHolderContrasena.vencimiento.setText("Sin fecha de vencimiento");
        } else {
            viewHolderContrasena.vencimiento.setText(String.valueOf(listContrasena.get(i).getVencimiento()) + " días");
            if (listContrasena.get(i).getVencimiento() <= 7 && listContrasena.get(i).getVencimiento() != 0) {
                viewHolderContrasena.vencimiento.setTextColor(mCtx.getResources().getColor(R.color.colorRed));

            }
        }

        viewHolderContrasena.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mCtx, viewHolderContrasena.menu);
                popupMenu.inflate(R.menu.menu_contrasena);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_copiar:
                                copiar(listContrasena.get(i));
                                break;

                            case R.id.menu_compartir:
                                compartir(listContrasena.get(i));
                                break;

                            case R.id.menu_editar:
                                editar(listContrasena.get(i));
                                break;

                            case R.id.menu_eliminar:
                                AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
                                dialog.setTitle("Confirmar");
                                dialog.setMessage("¿Desea eliminar estos datos de manera permanente?");

                                dialog.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (VariablesGenerales.almacenamientoExterno) {
                                            eliminarFirebase(listContrasena.get(i));
                                        } else if (VariablesGenerales.almacenamientoInterno) {
                                            eliminarSQLite(listContrasena.get(i));
                                        }
                                    }
                                });
                                dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.setIcon(R.drawable.ic_delete);
                                dialog.show();

                                break;

                            case R.id.menu_ultimos_pass:
                                if (VariablesGenerales.almacenamientoExterno) {
                                    verUltimosPassFirebase(listContrasena.get(i).getIdContrasena());
                                } else if (VariablesGenerales.almacenamientoInterno) {
                                    verUltimosPassSQLite(listContrasena.get(i).getIdContrasena());
                                }
                                break;

                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return listContrasena.size();
    }

    public class ViewHolderContrasena extends RecyclerView.ViewHolder {

        TextView servicio, usuario, contrasena, vencimiento, menu;
        CardView cardView;

        public ViewHolderContrasena(@NonNull View itemView) {
            super(itemView);

            servicio = (TextView) itemView.findViewById(R.id.tvServicio);
            usuario = (TextView) itemView.findViewById(R.id.tvUsuario);
            contrasena = (TextView) itemView.findViewById(R.id.tvContrasena);
            vencimiento = (TextView) itemView.findViewById(R.id.tvVencimiento);
            menu = (TextView) itemView.findViewById(R.id.tvmenuContrasena);
            cardView = itemView.findViewById(R.id.cardviewListas);
        }
    }

    public void copiar(final ContrasenaConstructor i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarContrasena, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String servicio = i.getServicio();
                String usuario = i.getUsuario();
                String contrasena = i.getContrasena();

                String [] items = {servicio, usuario, contrasena};

                if (isChecked) {
                    selectedCopiar.add(items[which]);
                } else {
                    selectedCopiar.remove(items[which]);
                }
            }
        });
        dialog.setPositiveButton("Copiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selection = "";
                for (String item: selectedCopiar) {
                    selection = selection + "\n" + item;
                }

                ClipboardManager clipboardManager = (ClipboardManager) mCtx.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("text", selection);
                clipboardManager.setPrimaryClip(clip);

                Toast.makeText(mCtx, "Copiado", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void compartir(final ContrasenaConstructor i) {
        selectedItems = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarContrasena, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                String servicio = i.getServicio();
                String usuario = i.getUsuario();
                String contrasena = i.getContrasena();

                String [] items = {servicio, usuario, contrasena};

                if (isChecked) {
                    selectedItems.add(items[which]);
                } else {
                    selectedItems.remove(items[which]);
                }
            }
        });
        dialog.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selection = "";
                for (String item: selectedItems) {
                    selection = selection + "\n" + item;
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, selection);
                mCtx.startActivity(Intent.createChooser(intent, "Compartir con"));
            }
        });
        dialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void editar(ContrasenaConstructor i) {
        VariablesGenerales.idContrasena = i.getIdContrasena();
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putInt("data", 0);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminarFirebase(final ContrasenaConstructor i) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        String doc = i.getIdContrasena();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(VariablesEstaticas.BD_PROPIETARIOS).document(userID).collection(VariablesEstaticas.BD_CONTRASENAS);

        reference.document(doc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listContrasena.remove(i);
                        notifyDataSetChanged();
                        Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mCtx, "Error al eliminar. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void eliminarSQLite(ContrasenaConstructor i) {
        String idContrasena = i.getIdContrasena();

        ConexionSQLite conect = new ConexionSQLite(mCtx, VariablesEstaticas.BD_PROPIETARIOS, null, VariablesEstaticas.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        db.delete(VariablesEstaticas.BD_CONTRASENAS, "idContrasena=" + idContrasena, null);
        db.close();

        listContrasena.remove(i);
        notifyDataSetChanged();
        Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();
    }

    public void verUltimosPassFirebase(String idPass) {
        final ProgressDialog progress = new ProgressDialog(mCtx);
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String userID = user.getUid();
        DocumentReference reference = db.collection(VariablesEstaticas.BD_PROPIETARIOS).document(userID).collection(VariablesEstaticas.BD_CONTRASENAS).document(idPass);

        LinearLayout layout = new LinearLayout(mCtx);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView textView1 = new TextView(mCtx);
        final TextView textView2 = new TextView(mCtx);
        final TextView textView3 = new TextView(mCtx);
        final TextView textView4 = new TextView(mCtx);
        final TextView textView5 = new TextView(mCtx);
        textView1.setTextSize(24);
        textView2.setTextSize(24);
        textView3.setTextSize(24);
        textView4.setTextSize(24);
        textView5.setTextSize(24);
        textView1.setPadding(50, 5, 5, 5);
        textView2.setPadding(50, 5, 5, 5);
        textView3.setPadding(50, 5, 5, 5);
        textView4.setPadding(50, 5, 5, 5);
        textView5.setPadding(50, 5, 5, 5);
        textView1.setText("");
        textView2.setText("");
        textView3.setText("");
        textView4.setText("");
        textView5.setText("");

        reference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();

                    if (doc.exists()) {
                        if (doc.getString(VariablesEstaticas.BD_ULTIMO_PASS_1) != null) {
                            String pass1 = doc.getString(VariablesEstaticas.BD_ULTIMO_PASS_1);
                            textView1.setText(pass1);
                        } else {
                            textView1.setText("Sin historial de contraseñas");
                        }

                        if (doc.getString(VariablesEstaticas.BD_ULTIMO_PASS_2) != null) {
                            String pass2 = doc.getString(VariablesEstaticas.BD_ULTIMO_PASS_2);
                            textView2.setText(pass2);
                        }

                        if (doc.getString(VariablesEstaticas.BD_ULTIMO_PASS_3) != null) {
                            String pass3 = doc.getString(VariablesEstaticas.BD_ULTIMO_PASS_3);
                            textView3.setText(pass3);
                        }

                        if (doc.getString(VariablesEstaticas.BD_ULTIMO_PASS_4) != null) {
                            String pass4 = doc.getString(VariablesEstaticas.BD_ULTIMO_PASS_4);
                            textView4.setText(pass4);
                        }

                        if (doc.getString(VariablesEstaticas.BD_ULTIMO_PASS_5) != null) {
                            String pass5 = doc.getString(VariablesEstaticas.BD_ULTIMO_PASS_5);
                            textView5.setText(pass5);
                        }
                        progress.dismiss();
                    } else {
                        Log.d("msg", "No such document");
                        progress.dismiss();
                    }

                } else {
                    Log.d("msg", "Error:", task.getException());
                    progress.dismiss();
                }
            }
        });


        layout.addView(textView1);
        layout.addView(textView2);
        layout.addView(textView3);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("Últimas contraseñas usadas")
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

    }

    public void verUltimosPassSQLite(String idContrasena) {
        final ProgressDialog progress = new ProgressDialog(mCtx);
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();

        ConexionSQLite conect = new ConexionSQLite(mCtx, VariablesEstaticas.BD_PROPIETARIOS, null, VariablesEstaticas.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + VariablesEstaticas.BD_CONTRASENAS + " WHERE idContrasena =" + idContrasena, null);

        LinearLayout layout = new LinearLayout(mCtx);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView textView1 = new TextView(mCtx);
        final TextView textView2 = new TextView(mCtx);
        final TextView textView3 = new TextView(mCtx);
        final TextView textView4 = new TextView(mCtx);
        final TextView textView5 = new TextView(mCtx);
        textView1.setTextSize(24);
        textView2.setTextSize(24);
        textView3.setTextSize(24);
        textView4.setTextSize(24);
        textView5.setTextSize(24);
        textView1.setPadding(50, 5, 5, 5);
        textView2.setPadding(50, 5, 5, 5);
        textView3.setPadding(50, 5, 5, 5);
        textView4.setPadding(50, 5, 5, 5);
        textView5.setPadding(50, 5, 5, 5);
        textView1.setText("");
        textView2.setText("");
        textView3.setText("");
        textView4.setText("");
        textView5.setText("");

        if (cursor.moveToFirst()) {
            if (cursor.getString(5) != null) {
                String pass1 = cursor.getString(5);
                textView1.setText(pass1);
            } else {
                textView1.setText("Sin historial de contraseñas");
            }

            if (cursor.getString(6) != null) {
                String pass2 = cursor.getString(6);
                textView2.setText(pass2);
            }

            if (cursor.getString(7) != null) {
                String pass3 = cursor.getString(7);
                textView3.setText(pass3);
            }

            if (cursor.getString(8) != null) {
                String pass4 = cursor.getString(8);
                textView4.setText(pass4);
            }

            if (cursor.getString(9) != null) {
                String pass5 = cursor.getString(9);
                textView5.setText(pass5);
            }
            progress.dismiss();
        }


        layout.addView(textView1);
        layout.addView(textView2);
        layout.addView(textView3);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("Últimas contraseñas usadas")
                .setView(layout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    public void updateList (ArrayList<ContrasenaConstructor> newList) {
        listContrasena = new ArrayList<>();
        listContrasena.addAll(newList);
        notifyDataSetChanged();
    }
}
