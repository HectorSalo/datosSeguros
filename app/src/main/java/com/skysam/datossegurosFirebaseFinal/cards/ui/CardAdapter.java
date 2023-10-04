package com.skysam.datossegurosFirebaseFinal.cards.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.Intent;
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

import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.common.model.Card;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.EditarActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolderTarjeta> {

    private ArrayList<Card> listTarjeta;
    private ArrayList<String> selectedCopiar;
    private ArrayList<String> selectedCompartir;
    private Context mCtx;

    public CardAdapter(List<Card> listTarjeta) {
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
        viewHolderTarjeta.titular.setText(listTarjeta.get(i).getUser());
        viewHolderTarjeta.numeroTarjeta.setText(listTarjeta.get(i).getNumberCard());
        viewHolderTarjeta.numeroCVV.setText(listTarjeta.get(i).getCvv());
        viewHolderTarjeta.cedula.setText(listTarjeta.get(i).getNumberIdUser());
        viewHolderTarjeta.tipoTarjeta.setText(listTarjeta.get(i).getTypeCard());
        viewHolderTarjeta.banco.setText(listTarjeta.get(i).getBank());
        viewHolderTarjeta.vencimiento.setText(listTarjeta.get(i).getDateExpiration());
        viewHolderTarjeta.clave.setText(listTarjeta.get(i).getCode());

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
                            eliminarFirebase(listTarjeta.get(i));
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

            titular = itemView.findViewById(R.id.tvTitularTarjeta);
            numeroTarjeta = itemView.findViewById(R.id.tvnumeroTarjeta);
            numeroCVV = itemView.findViewById(R.id.tvnumeroCVV);
            cedula = itemView.findViewById(R.id.tvCedulaTarjeta);
            tipoTarjeta = itemView.findViewById(R.id.tvTipoTarjeta);
            menu = itemView.findViewById(R.id.tvmenuTarjeta);
            banco = itemView.findViewById(R.id.tvBancoTarjeta);
            vencimiento = itemView.findViewById(R.id.tvVencimientoTarjeta);
            clave = itemView.findViewById(R.id.tvClaveTarjeta);
            cardView = itemView.findViewById(R.id.cardviewTarjeta);
            arrow = itemView.findViewById(R.id.ib_arrow);
            constraintExpandable = itemView.findViewById(R.id.expandable);

            arrow.setOnClickListener(view -> {
                Card cardModel = listTarjeta.get(getAdapterPosition());
                cardModel.setExpanded(!cardModel.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }

    public void copiar(final Card i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarTarjeta, null, (dialog13, which, isChecked) -> {
            String titular = i.getUser();
            String banco = i.getBank();
            String tarjeta = i.getNumberCard();
            String cvv = i.getCvv();
            String vencimiento = i.getDateExpiration();
            String cedula = i.getNumberIdUser();
            String clave = i.getCode();
            String tipo = i.getTypeCard();

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

    public void compartir(final Card i) {
        selectedCompartir = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarTarjeta, null, (dialog13, which, isChecked) -> {
            String titular = i.getUser();
            String banco = i.getBank();
            String tarjeta = i.getNumberCard();
            String cvv = i.getCvv();
            String vencimiento = i.getDateExpiration();
            String cedula = i.getNumberIdUser();
            String clave = i.getCode();
            String tipo = i.getTypeCard();

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

    public void editar(Card i) {
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("id", i.getId());
        myBundle.putBoolean("isCloud", i.isSavedCloud());
        myBundle.putInt("data", 2);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminarFirebase(final Card i) {
        String doc = i.getId();
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

    public void updateList (List<Card> newList) {
        listTarjeta.clear();
        listTarjeta = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}
