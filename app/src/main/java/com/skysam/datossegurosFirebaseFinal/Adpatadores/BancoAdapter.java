package com.skysam.datossegurosFirebaseFinal.Adpatadores;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.skysam.datossegurosFirebaseFinal.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.Constructores.BancoConstructor;
import com.skysam.datossegurosFirebaseFinal.EditarActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.Variables.VariablesGenerales;
import com.skysam.datossegurosFirebaseFinal.Variables.VariablesEstaticas;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BancoAdapter extends RecyclerView.Adapter<BancoAdapter.ViewHolderBanco> {

    private ArrayList<BancoConstructor> listBanco;
    private ArrayList<String> selectedCopiar;
    private ArrayList<String> selectedCompartir;
    private Context mCtx;
    private FirebaseUser user;

    public BancoAdapter (ArrayList<BancoConstructor> listBanco, Context mCtx){
        this.listBanco = listBanco;
        this.mCtx = mCtx;
    }


    @NonNull
    @Override
    public ViewHolderBanco onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_banco, null, false);
        return new ViewHolderBanco(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderBanco viewHolderBanco, final int i) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mCtx);

        String tema = sharedPreferences.getString("tema", "Amarillo");

        switch (tema){
            case "Amarillo":
                viewHolderBanco.cardView.setBackgroundResource(R.drawable.fondo_contrasena);
                break;
            case "Rojo":
                viewHolderBanco.cardView.setBackgroundResource(R.drawable.fondo_listas_rojo);
                break;
            case "Marron":
                viewHolderBanco.cardView.setBackgroundResource(R.drawable.fondo_listas_marron);
                break;
            case "Lila":
                viewHolderBanco.cardView.setBackgroundResource(R.drawable.fondo_listas_lila);
                break;
        }

        viewHolderBanco.titular.setText(listBanco.get(i).getTitular());
        viewHolderBanco.banco.setText(listBanco.get(i).getBanco());
        viewHolderBanco.numeroCuenta.setText(String.valueOf(listBanco.get(i).getNumeroCuenta()));
        viewHolderBanco.cedula.setText(String.valueOf(listBanco.get(i).getCedula()));
        viewHolderBanco.tipo.setText(listBanco.get(i).getTipo());
        viewHolderBanco.telefono.setText(String.valueOf(listBanco.get(i).getTelefono()));
        viewHolderBanco.correo.setText(listBanco.get(i).getCorreo());
        viewHolderBanco.tipoDocumento.setText(listBanco.get(i).getTipoDocumento());

        viewHolderBanco.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mCtx, viewHolderBanco.menu);
                popupMenu.inflate(R.menu.menu_banco);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
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

                                dialog.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (VariablesGenerales.almacenamientoExterno) {
                                            eliminarFirebase(listBanco.get(i));
                                        } else if (VariablesGenerales.almacenamientoInterno) {
                                            eliminarSQLite(listBanco.get(i));
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
        return listBanco.size();
    }

    public class ViewHolderBanco extends RecyclerView.ViewHolder {

        TextView titular, banco, numeroCuenta, cedula, tipo, telefono, menu, correo, tipoDocumento;
        CardView cardView;

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

        }
    }

    public void copiar(final BancoConstructor i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarBanco, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
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

    public void compartir(final BancoConstructor i) {
        selectedCompartir = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarBanco, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
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

    public void editar(BancoConstructor i) {
        VariablesGenerales.idCuenta = i.getIdCuenta();
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putInt("data", 1);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminarFirebase(final BancoConstructor i) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        String doc = i.getIdCuenta();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(VariablesEstaticas.BD_PROPIETARIOS).document(userID).collection(VariablesEstaticas.BD_CUENTAS);

        reference.document(doc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listBanco.remove(i);
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

    public void eliminarSQLite(BancoConstructor i) {
        String idCuenta = i.getIdCuenta();

        ConexionSQLite conect = new ConexionSQLite(mCtx, VariablesEstaticas.BD_PROPIETARIOS, null, VariablesEstaticas.VERSION_SQLITE);
        SQLiteDatabase db = conect.getWritableDatabase();

        db.delete(VariablesEstaticas.BD_CUENTAS, "idCuenta=" + idCuenta, null);
        db.close();

        listBanco.remove(i);
        notifyDataSetChanged();
        Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();
    }

    public void updateList (ArrayList<BancoConstructor> newList) {
        listBanco = new ArrayList<>();
        listBanco.addAll(newList);
        notifyDataSetChanged();
    }
}
