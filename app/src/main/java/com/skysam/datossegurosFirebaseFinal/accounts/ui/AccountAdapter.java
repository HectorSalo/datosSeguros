package com.skysam.datossegurosFirebaseFinal.accounts.ui;

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

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.common.model.Account;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.EditarActivity;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolderBanco> {

    private ArrayList<Account> listBanco;
    private ArrayList<String> selectedCopiar;
    private ArrayList<String> selectedCompartir;
    private Context mCtx;

    public AccountAdapter(List<Account> listBanco){
        this.listBanco = new ArrayList<>(listBanco);
    }


    @NonNull
    @Override
    public ViewHolderBanco onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_banco, null, false);
        mCtx = viewGroup.getContext();
        return new ViewHolderBanco(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderBanco viewHolderBanco, final int i) {
        viewHolderBanco.titular.setText(listBanco.get(i).getUser());
        viewHolderBanco.banco.setText(listBanco.get(i).getBank());
        viewHolderBanco.numeroCuenta.setText(listBanco.get(i).getNumberAccount());
        viewHolderBanco.cedula.setText(listBanco.get(i).getNumberIdUser());
        viewHolderBanco.tipo.setText(listBanco.get(i).getTypeAccount());
        viewHolderBanco.telefono.setText(String.valueOf(listBanco.get(i).getTelph()));
        viewHolderBanco.correo.setText(listBanco.get(i).getEmail());
        viewHolderBanco.tipoDocumento.setText(listBanco.get(i).getTypeIdUSer());

        boolean isExpanded = listBanco.get(i).isExpanded();
        viewHolderBanco.constraintExpandable.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        viewHolderBanco.arrow.setImageResource(isExpanded ? R.drawable.ic_keyboard_arrow_up_24 : R.drawable.ic_keyboard_arrow_down_24);

        viewHolderBanco.menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mCtx, viewHolderBanco.menu);
            popupMenu.inflate(R.menu.menu_banco);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_copiar:
                        copiar(listBanco.get(i));
                        break;

                    case R.id.menu_compartir:
                        compartir(listBanco.get(i));
                        break;

                    case R.id.menu_editar:
                        editar(listBanco.get(i));
                        break;

                    case R.id.menu_eliminar:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
                        dialog.setTitle("Confirmar");
                        dialog.setMessage("¿Desea eliminar estos datos de manera permanente?");

                        dialog.setPositiveButton("Eliminar", (dialog1, which) -> {
                            eliminarFirebase(listBanco.get(i));
                        });
                        dialog.setNegativeButton("Cancelar", (dialog12, which) -> dialog12.dismiss());
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
        return listBanco.size();
    }

    public class ViewHolderBanco extends RecyclerView.ViewHolder {

        TextView titular, banco, numeroCuenta, cedula, tipo, telefono, menu, correo, tipoDocumento;
        CardView cardView;
        ImageButton arrow;
        ConstraintLayout constraintExpandable;

        public ViewHolderBanco(@NonNull View itemView) {
            super(itemView);

            titular = itemView.findViewById(R.id.tvTitular);
            banco = itemView.findViewById(R.id.tvBanco);
            numeroCuenta = itemView.findViewById(R.id.tvnumeroCuenta);
            cedula = itemView.findViewById(R.id.tvCedula);
            tipo = itemView.findViewById(R.id.tvTipo);
            telefono = itemView.findViewById(R.id.tvTelefono);
            correo = itemView.findViewById(R.id.tvCorreoCuenta);
            menu = itemView.findViewById(R.id.tvmenuBanco);
            tipoDocumento = itemView.findViewById(R.id.tvTipoDocumento);
            cardView = itemView.findViewById(R.id.cardviewCuenta);
            arrow = itemView.findViewById(R.id.ib_arrow);
            constraintExpandable = itemView.findViewById(R.id.expandable);

            arrow.setOnClickListener(view -> {
                Account accountModel = listBanco.get(getBindingAdapterPosition());
                accountModel.setExpanded(!accountModel.isExpanded());
                notifyItemChanged(getBindingAdapterPosition());
            });

        }
    }

    public void copiar(final Account i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarBanco, null, (dialog1, which, isChecked) -> {
            String titular = i.getUser();
            String banco = i.getBank();
            String cuenta = i.getNumberAccount();
            String documento = i.getTypeIdUSer() + ": " + i.getNumberIdUser();
            String tipo = i.getTypeAccount();
            String telefono = String.valueOf(i.getTelph());
            String correo = i.getEmail();

            String [] items = {titular, banco, cuenta, documento, tipo, telefono, correo};

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
        dialog.setNegativeButton("Cancelar", (dialog13, which) -> dialog13.dismiss());
        dialog.show();
    }

    public void compartir(final Account i) {
        selectedCompartir = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarBanco, null, (dialog1, which, isChecked) -> {
            String titular = i.getUser();
            String banco = i.getBank();
            String cuenta = i.getNumberAccount();
            String documento = i.getTypeIdUSer() + ": " + i.getNumberIdUser();
            String tipo = i.getTypeAccount();
            String telefono = String.valueOf(i.getTelph());
            String correo = i.getEmail();

            String [] items = {titular, banco, cuenta, documento, tipo, telefono, correo};

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
        dialog.setNegativeButton("Cancelar", (dialog13, which) -> dialog13.dismiss());
        dialog.show();
    }

    public void editar(Account i) {
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("id", i.getId());
        myBundle.putBoolean("isCloud", i.isSavedCloud());
        myBundle.putInt("data", 1);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminarFirebase(final Account i) {
        String doc = i.getId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS)
                .document(Auth.INSTANCE.getCurrenUser().getUid()).collection(Constants.BD_CUENTAS);

        reference.document(doc)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listBanco.remove(i);
                    notifyDataSetChanged();
                    Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> Toast.makeText(mCtx, "Error al eliminar. Intente nuevamente", Toast.LENGTH_SHORT).show());

    }

    public void updateList (List<Account> newList) {
        listBanco.clear();
        listBanco = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}
