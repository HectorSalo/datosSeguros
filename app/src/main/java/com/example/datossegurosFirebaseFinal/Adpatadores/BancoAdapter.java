package com.example.datossegurosFirebaseFinal.Adpatadores;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datossegurosFirebaseFinal.Constructores.BancoConstructor;
import com.example.datossegurosFirebaseFinal.R;

import java.util.ArrayList;

public class BancoAdapter extends RecyclerView.Adapter<BancoAdapter.ViewHolderBanco> {

    private ArrayList<BancoConstructor> listBanco;
    private ArrayList<String> selectedCopiar;
    private ArrayList<String> selectedCompartir;
    private Context mCtx;

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

        viewHolderBanco.titular.setText(listBanco.get(i).getTitular());
        viewHolderBanco.banco.setText(listBanco.get(i).getBanco());
        viewHolderBanco.numeroCuenta.setText(String.valueOf(listBanco.get(i).getNumeroCuenta()));
        viewHolderBanco.cedula.setText(String.valueOf(listBanco.get(i).getCedula()));
        viewHolderBanco.tipo.setText(listBanco.get(i).getTipo());
        viewHolderBanco.telefono.setText(String.valueOf(listBanco.get(i).getTelefono()));

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
                                break;

                            case R.id.menu_eliminar:
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

        TextView titular, banco, numeroCuenta, cedula, tipo, telefono, menu;

        public ViewHolderBanco(@NonNull View itemView) {
            super(itemView);

            titular = (TextView) itemView.findViewById(R.id.tvTitular);
            banco = (TextView) itemView.findViewById(R.id.tvBanco);
            numeroCuenta = (TextView) itemView.findViewById(R.id.tvnumeroCuenta);
            cedula = (TextView) itemView.findViewById(R.id.tvCedula);
            tipo = (TextView) itemView.findViewById(R.id.tvTipo);
            telefono = (TextView) itemView.findViewById(R.id.tvTelefono);
            menu = (TextView) itemView.findViewById(R.id.tvmenuBanco);

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
                String cedula = String.valueOf(i.getCedula());
                String tipo = i.getTipo();
                String telefono = String.valueOf(i.getTelefono());

                String [] items = {titular, banco, cuenta, cedula, tipo, telefono};

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
                String cedula = String.valueOf(i.getCedula());
                String tipo = i.getTipo();
                String telefono = String.valueOf(i.getTelefono());

                String [] items = {titular, banco, cuenta, cedula, tipo, telefono};

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
}