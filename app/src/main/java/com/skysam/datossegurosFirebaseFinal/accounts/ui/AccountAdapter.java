package com.skysam.datossegurosFirebaseFinal.accounts.ui;

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
import com.skysam.datossegurosFirebaseFinal.common.model.AccountModel;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.EditarActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.ViewHolderBanco> {

    private ArrayList<AccountModel> listBanco;
    private ArrayList<String> selectedCopiar;
    private ArrayList<String> selectedCompartir;
    private Context mCtx;

    public AccountAdapter(List<AccountModel> listBanco){
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

        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(Auth.INSTANCE.getCurrenUser().getUid(), Context.MODE_PRIVATE);

        final boolean almacenamientoNube = sharedPreferences.getBoolean(Constants.PREFERENCE_ALMACENAMIENTO_NUBE, true);

        viewHolderBanco.titular.setText(listBanco.get(i).getTitular());
        viewHolderBanco.banco.setText(listBanco.get(i).getBanco());
        viewHolderBanco.numeroCuenta.setText(String.valueOf(listBanco.get(i).getNumeroCuenta()));
        viewHolderBanco.cedula.setText(String.valueOf(listBanco.get(i).getCedula()));
        viewHolderBanco.tipo.setText(listBanco.get(i).getTipo());
        viewHolderBanco.telefono.setText(String.valueOf(listBanco.get(i).getTelefono()));
        viewHolderBanco.correo.setText(listBanco.get(i).getCorreo());
        viewHolderBanco.tipoDocumento.setText(listBanco.get(i).getTipoDocumento());

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
                            if (almacenamientoNube) {
                                eliminarFirebase(listBanco.get(i));
                            } else {
                                eliminarSQLite(listBanco.get(i));
                            }
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

            titular = (TextView) itemView.findViewById(R.id.tvTitular);
            banco = (TextView) itemView.findViewById(R.id.tvBanco);
            numeroCuenta = (TextView) itemView.findViewById(R.id.tvnumeroCuenta);
            cedula = (TextView) itemView.findViewById(R.id.tvCedula);
            tipo = (TextView) itemView.findViewById(R.id.tvTipo);
            telefono = (TextView) itemView.findViewById(R.id.tvTelefono);
            correo = (TextView) itemView.findViewById(R.id.tvCorreoCuenta);
            menu = (TextView) itemView.findViewById(R.id.tvmenuBanco);
            tipoDocumento = (TextView) itemView.findViewById(R.id.tvTipoDocumento);
            cardView = itemView.findViewById(R.id.cardviewCuenta);
            arrow = itemView.findViewById(R.id.ib_arrow);
            constraintExpandable = itemView.findViewById(R.id.expandable);

            arrow.setOnClickListener(view -> {
                AccountModel accountModel = listBanco.get(getAdapterPosition());
                accountModel.setExpanded(!accountModel.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });

        }
    }

    public void copiar(final AccountModel i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarBanco, null, (dialog1, which, isChecked) -> {
            String titular = i.getTitular();
            String banco = i.getBanco();
            String cuenta = String.valueOf(i.getNumeroCuenta());
            String documento = i.getTipoDocumento() + ": " + i.getCedula();
            String tipo = i.getTipo();
            String telefono = String.valueOf(i.getTelefono());
            String correo = i.getCorreo();

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

    public void compartir(final AccountModel i) {
        selectedCompartir = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarBanco, null, (dialog1, which, isChecked) -> {
            String titular = i.getTitular();
            String banco = i.getBanco();
            String cuenta = String.valueOf(i.getNumeroCuenta());
            String documento = i.getTipoDocumento() + ": " + i.getCedula();
            String tipo = i.getTipo();
            String telefono = String.valueOf(i.getTelefono());
            String correo = i.getCorreo();

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

    public void editar(AccountModel i) {
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("id", i.getIdCuenta());
        myBundle.putInt("data", 1);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminarFirebase(final AccountModel i) {
        String doc = i.getIdCuenta();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid()).collection(Constants.BD_CUENTAS);

        reference.document(doc)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listBanco.remove(i);
                    notifyDataSetChanged();
                    Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> Toast.makeText(mCtx, "Error al eliminar. Intente nuevamente", Toast.LENGTH_SHORT).show());

    }

    public void eliminarSQLite(AccountModel i) {
        String idCuenta = i.getIdCuenta();

        ConexionSQLite conect = new ConexionSQLite(mCtx, Constants.BD_PROPIETARIOS, null, Constants.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        db.delete(Constants.BD_CUENTAS, "idCuenta=" + idCuenta, null);
        db.close();

        listBanco.remove(i);
        notifyDataSetChanged();
        Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();
    }

    public void updateList (List<AccountModel> newList) {
        listBanco.clear();
        listBanco = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}
