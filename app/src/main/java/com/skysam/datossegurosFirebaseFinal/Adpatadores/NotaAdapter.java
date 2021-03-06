package com.skysam.datossegurosFirebaseFinal.Adpatadores;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.skysam.datossegurosFirebaseFinal.Clases.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.Constructores.NotaConstructor;
import com.skysam.datossegurosFirebaseFinal.ui.general.EditarActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.ViewHolderNota> {

    private ArrayList<NotaConstructor> listNota;
    private ArrayList<String> selectedCopiar;
    private ArrayList<String> selectedCompartir;
    private Context mCtx;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public NotaAdapter(ArrayList<NotaConstructor> listNota, Context mCtx) {
        this.listNota = listNota;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolderNota onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_notas, null, false);
        return new ViewHolderNota(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderNota viewHolderNota, final int i) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);
        final boolean almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                viewHolderNota.cardView.setBackgroundResource(R.drawable.fondo_contrasena);
                break;
            case Constantes.PREFERENCE_ROJO:
                viewHolderNota.cardView.setBackgroundResource(R.drawable.fondo_listas_rojo);
                break;
            case Constantes.PREFERENCE_MARRON:
                viewHolderNota.cardView.setBackgroundResource(R.drawable.fondo_listas_marron);
                break;
            case Constantes.PREFERENCE_LILA:
                viewHolderNota.cardView.setBackgroundResource(R.drawable.fondo_listas_lila);
                break;
        }

        viewHolderNota.titulo.setText(listNota.get(i).getTitulo());
        viewHolderNota.contenido.setText(listNota.get(i).getContenido());

        viewHolderNota.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mCtx, viewHolderNota.menu);
                popupMenu.inflate(R.menu.menu_nota);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_copiar:
                                copiar(listNota.get(i));
                                break;

                            case R.id.menu_compartir:
                                compartir(listNota.get(i));
                                break;

                            case R.id.menu_editar:
                                editar(listNota.get(i));
                                break;

                            case R.id.menu_eliminar:
                                AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
                                dialog.setTitle("Confirmar");
                                dialog.setMessage("¿Desea eliminar estos datos de manera permanente?");

                                dialog.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (almacenamientoNube) {
                                            eliminarFirebase(listNota.get(i));
                                        } else {
                                            eliminarSQLite(listNota.get(i));
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
        return listNota.size();
    }

    public class ViewHolderNota extends RecyclerView.ViewHolder {

        TextView titulo, contenido, menu;
        CardView cardView;

        public ViewHolderNota(@NonNull View itemView) {
            super(itemView);

            titulo = (TextView) itemView.findViewById(R.id.tvTituloNota);
            contenido = (TextView) itemView.findViewById(R.id.tvcontenidoNota);
            menu = (TextView) itemView.findViewById(R.id.tvmenuNota);
            cardView = itemView.findViewById(R.id.cardviewNota);
        }
    }

    public void copiar(final NotaConstructor i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarNota, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String titulo = i.getTitulo();
                String contenido = i.getContenido();

                String [] items = {titulo, contenido};

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

    public void compartir(final NotaConstructor i) {
        selectedCompartir = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarNota, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String titulo = i.getTitulo();
                String contenido = i.getContenido();

                String [] items = {titulo, contenido};

                if (isChecked) {
                    selectedCompartir.add(items[which]);
                } else {
                    selectedCompartir.remove(items[which]);
                }
            }
        });
        dialog.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selection = "";
                for (String item: selectedCompartir) {
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

    public void editar(NotaConstructor i) {
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("id", i.getIdNota());
        myBundle.putInt("data", 3);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminarFirebase(final NotaConstructor i) {
        String userID = user.getUid();
        String doc = i.getIdNota();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_NOTAS);

        reference.document(doc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listNota.remove(i);
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

    public void eliminarSQLite(NotaConstructor i) {
        String idNota = i.getIdNota();

        ConexionSQLite conect = new ConexionSQLite(mCtx, Constantes.BD_PROPIETARIOS, null, Constantes.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        db.delete(Constantes.BD_NOTAS, "idNota=" + idNota, null);
        db.close();

        listNota.remove(i);
        notifyDataSetChanged();
        Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();
    }

    public void updateList (ArrayList<NotaConstructor> newList) {
        listNota = new ArrayList<>();
        listNota.addAll(newList);
        notifyDataSetChanged();
    }
}
