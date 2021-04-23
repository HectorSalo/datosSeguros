package com.skysam.datossegurosFirebaseFinal.cards.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.skysam.datossegurosFirebaseFinal.common.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.common.model.CardModel;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.EditarActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolderTarjeta> {

    private ArrayList<CardModel> listTarjeta;
    private ArrayList<String> selectedCopiar;
    private ArrayList<String> selectedCompartir;
    private Context mCtx;

    public CardAdapter(List<CardModel> listTarjeta) {
        this.listTarjeta = new ArrayList<>(listTarjeta);
    }


    @NonNull
    @Override
    public ViewHolderTarjeta onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_tdc, null, false);
        mCtx = viewGroup.getContext();
        return new ViewHolderTarjeta(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderTarjeta viewHolderTarjeta, final int i) {

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Auth.INSTANCE.getCurrenUser().getUid(), Context.MODE_PRIVATE);

        final boolean almacenamientoNube = sharedPreferences.getBoolean(Constants.PREFERENCE_ALMACENAMIENTO_NUBE, true);


        viewHolderTarjeta.titular.setText(listTarjeta.get(i).getTitular());
        viewHolderTarjeta.numeroTarjeta.setText(String.valueOf(listTarjeta.get(i).getNumeroTarjeta()));
        viewHolderTarjeta.numeroCVV.setText(String.valueOf(listTarjeta.get(i).getCvv()));
        viewHolderTarjeta.cedula.setText(String.valueOf(listTarjeta.get(i).getCedula()));
        viewHolderTarjeta.tipoTarjeta.setText(listTarjeta.get(i).getTipo());
        viewHolderTarjeta.banco.setText(listTarjeta.get(i).getBanco());
        viewHolderTarjeta.vencimiento.setText(listTarjeta.get(i).getVencimiento());
        viewHolderTarjeta.clave.setText(listTarjeta.get(i).getClave());

        boolean isExpanded = listTarjeta.get(i).isExpanded();
        viewHolderTarjeta.constraintExpandable.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        viewHolderTarjeta.arrow.setImageResource(isExpanded ? R.drawable.ic_keyboard_arrow_up_24 : R.drawable.ic_keyboard_arrow_down_24);

        viewHolderTarjeta.menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mCtx, viewHolderTarjeta.menu);
            popupMenu.inflate(R.menu.menu_tarjeta);
            popupMenu.setOnMenuItemClickListener(item -> {
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

                        dialog.setPositiveButton("Eliminar", (dialog12, which) -> {
                            if (almacenamientoNube) {
                                eliminarFirebase(listTarjeta.get(i));
                            } else {
                                eliminarSQLite(listTarjeta.get(i));
                            }
                        });
                        dialog.setNegativeButton("Cancelar", (dialog1, which) -> dialog1.dismiss());
                        dialog.setIcon(R.drawable.ic_delete);
                        dialog.show();

                        break;

                    default:
                        break;
                }
                return false;
            });
            popupMenu.show();
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

            arrow.setOnClickListener(view -> {
                CardModel cardModel = listTarjeta.get(getAdapterPosition());
                cardModel.setExpanded(!cardModel.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }

    public void copiar(final CardModel i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarTarjeta, null, (dialog13, which, isChecked) -> {
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
        });
        dialog.setPositiveButton("Copiar", (dialog12, which) -> {
            StringBuilder selection = new StringBuilder();
            for (String item: selectedCopiar) {
                selection.append("\n").append(item);
            }

            ClipboardManager clipboardManager = (ClipboardManager) mCtx.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("text", selection.toString());
            clipboardManager.setPrimaryClip(clip);
            Toast.makeText(mCtx, "Copiado", Toast.LENGTH_SHORT).show();
        });
        dialog.setNegativeButton("Cancelar", (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }

    public void compartir(final CardModel i) {
        selectedCompartir = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarTarjeta, null, (dialog13, which, isChecked) -> {
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
        });
        dialog.setPositiveButton("Compartir", (dialog12, which) -> {
            StringBuilder selection = new StringBuilder();
            for (String item: selectedCompartir) {
                selection.append("\n").append(item);
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, selection.toString());
            mCtx.startActivity(Intent.createChooser(intent, "Compartir con"));
        });
        dialog.setNegativeButton("Cancelar", (dialog1, which) -> dialog1.dismiss());
        dialog.show();
    }

    public void editar(CardModel i) {
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("id", i.getIdTarjeta());
        myBundle.putInt("data", 2);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminarFirebase(final CardModel i) {
        String doc = i.getIdTarjeta();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid()).collection(Constants.BD_TARJETAS);

        reference.document(doc)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listTarjeta.remove(i);
                    notifyDataSetChanged();
                    Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> Toast.makeText(mCtx, "Error al eliminar. Intente nuevamente", Toast.LENGTH_SHORT).show());
    }

    public void eliminarSQLite(CardModel i) {
        String idTarjeta = i.getIdTarjeta();

        ConexionSQLite conect = new ConexionSQLite(mCtx, Constants.BD_PROPIETARIOS, null, Constants.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        db.delete(Constants.BD_TARJETAS, "idTarjeta=" + idTarjeta, null);
        db.close();

        listTarjeta.remove(i);
        notifyDataSetChanged();
        Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();
    }

    public void updateList (List<CardModel> newList) {
        listTarjeta.clear();
        listTarjeta = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}
