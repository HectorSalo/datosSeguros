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
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;

import com.skysam.datossegurosFirebaseFinal.Clases.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.Constructores.TarjetaConstructor;
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

public class AdapterTarjeta extends RecyclerView.Adapter<AdapterTarjeta.ViewHolderTarjeta> {

    private ArrayList<TarjetaConstructor> listTarjeta;
    private ArrayList<String> selectedCopiar;
    private ArrayList<String> selectedCompartir;
    private Context mCtx;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public AdapterTarjeta(ArrayList<TarjetaConstructor> listTarjeta, Context mCtx) {
        this.listTarjeta = listTarjeta;
        this.mCtx = mCtx;
    }


    @NonNull
    @Override
    public ViewHolderTarjeta onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_tdc, null, false);
        return new ViewHolderTarjeta(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderTarjeta viewHolderTarjeta, final int i) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);
        final boolean almacenamientoNube = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                viewHolderTarjeta.cardView.setBackgroundResource(R.drawable.fondo_contrasena);
                break;
            case Constantes.PREFERENCE_ROJO:
                viewHolderTarjeta.cardView.setBackgroundResource(R.drawable.fondo_listas_rojo);
                break;
            case Constantes.PREFERENCE_MARRON:
                viewHolderTarjeta.cardView.setBackgroundResource(R.drawable.fondo_listas_marron);
                break;
            case Constantes.PREFERENCE_LILA:
                viewHolderTarjeta.cardView.setBackgroundResource(R.drawable.fondo_listas_lila);
                break;
        }

        viewHolderTarjeta.titular.setText(listTarjeta.get(i).getTitular());
        viewHolderTarjeta.numeroTarjeta.setText(String.valueOf(listTarjeta.get(i).getNumeroTarjeta()));
        viewHolderTarjeta.numeroCVV.setText(String.valueOf(listTarjeta.get(i).getCvv()));
        viewHolderTarjeta.cedula.setText(String.valueOf(listTarjeta.get(i).getCedula()));
        viewHolderTarjeta.tipoTarjeta.setText(listTarjeta.get(i).getTipo());
        viewHolderTarjeta.banco.setText(listTarjeta.get(i).getBanco());
        viewHolderTarjeta.vencimiento.setText(listTarjeta.get(i).getVencimiento());
        viewHolderTarjeta.clave.setText(listTarjeta.get(i).getClave());

        viewHolderTarjeta.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mCtx, viewHolderTarjeta.menu);
                popupMenu.inflate(R.menu.menu_tarjeta);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu_copiar:
                                copiar(listTarjeta.get(i));
                                break;

                            case R.id.menu_compartir:
                                compartir(listTarjeta.get(i));
                                break;

                            case R.id.menu_editar:
                                editar(listTarjeta.get(i));
                                break;

                            case R.id.menu_eliminar:
                                AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
                                dialog.setTitle("Confirmar");
                                dialog.setMessage("¿Desea eliminar estos datos de manera permanente?");

                                dialog.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (almacenamientoNube) {
                                            eliminarFirebase(listTarjeta.get(i));
                                        } else {
                                            eliminarSQLite(listTarjeta.get(i));
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
        return listTarjeta.size();
    }

    public class ViewHolderTarjeta extends RecyclerView.ViewHolder {

        TextView titular, numeroTarjeta, numeroCVV, cedula, tipoTarjeta, menu, banco, vencimiento, clave;
        CardView cardView;
        ImageButton arrow;
        ConstraintLayout constraintExpandable;

        public ViewHolderTarjeta(@NonNull View itemView) {
            super(itemView);

            titular = (TextView) itemView.findViewById(R.id.tvTitularTarjeta);
            numeroTarjeta = (TextView) itemView.findViewById(R.id.tvnumeroTarjeta);
            numeroCVV = (TextView) itemView.findViewById(R.id.tvnumeroCVV);
            cedula = (TextView) itemView.findViewById(R.id.tvCedulaTarjeta);
            tipoTarjeta = (TextView) itemView.findViewById(R.id.tvTipoTarjeta);
            menu = (TextView) itemView.findViewById(R.id.tvmenuTarjeta);
            banco = (TextView) itemView.findViewById(R.id.tvBancoTarjeta);
            vencimiento = (TextView) itemView.findViewById(R.id.tvVencimientoTarjeta);
            clave = (TextView) itemView.findViewById(R.id.tvClaveTarjeta);
            cardView = itemView.findViewById(R.id.cardviewTarjeta);
            arrow = itemView.findViewById(R.id.ib_arrow);
            constraintExpandable = itemView.findViewById(R.id.expandable);

            arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (constraintExpandable.getVisibility() == View.GONE) {
                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                        constraintExpandable.setVisibility(View.VISIBLE);
                        arrow.setImageResource(R.drawable.ic_keyboard_arrow_up_24);
                    } else {
                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                        constraintExpandable.setVisibility(View.GONE);
                        arrow.setImageResource(R.drawable.ic_keyboard_arrow_down_24);
                    }
                }
            });
        }
    }

    public void copiar(final TarjetaConstructor i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarTarjeta, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String titular = i.getTitular();
                String banco = i.getBanco();
                String tarjeta = String.valueOf(i.getNumeroTarjeta());
                String cvv = String.valueOf(i.getCvv());
                String vencimiento = i.getVencimiento();
                String cedula = String.valueOf(i.getCedula());
                String clave = i.getClave();
                String tipo = i.getTipo();

                String [] items = {titular, banco, tarjeta, cvv, vencimiento, cedula, clave, tipo};

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

    public void compartir(final TarjetaConstructor i) {
        selectedCompartir = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarTarjeta, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String titular = i.getTitular();
                String banco = i.getBanco();
                String tarjeta = String.valueOf(i.getNumeroTarjeta());
                String cvv = String.valueOf(i.getCvv());
                String vencimiento = i.getVencimiento();
                String cedula = String.valueOf(i.getCedula());
                String clave = i.getClave();
                String tipo = i.getTipo();

                String [] items = {titular, banco, tarjeta, cvv, vencimiento, cedula, clave, tipo};

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

    public void editar(TarjetaConstructor i) {
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("id", i.getIdTarjeta());
        myBundle.putInt("data", 2);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminarFirebase(final TarjetaConstructor i) {
        String userID = user.getUid();
        String doc = i.getIdTarjeta();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_TARJETAS);

        reference.document(doc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listTarjeta.remove(i);
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

    public void eliminarSQLite(TarjetaConstructor i) {
        String idTarjeta = i.getIdTarjeta();

        ConexionSQLite conect = new ConexionSQLite(mCtx, Constantes.BD_PROPIETARIOS, null, Constantes.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        db.delete(Constantes.BD_TARJETAS, "idTarjeta=" + idTarjeta, null);
        db.close();

        listTarjeta.remove(i);
        notifyDataSetChanged();
        Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();
    }

    public void updateList (ArrayList<TarjetaConstructor> newList) {
        listTarjeta = new ArrayList<>();
        listTarjeta.addAll(newList);
        notifyDataSetChanged();
    }
}
