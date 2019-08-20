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

import com.example.datossegurosFirebaseFinal.Constructores.TarjetaConstructor;
import com.example.datossegurosFirebaseFinal.R;

import java.util.ArrayList;

public class AdapterTarjeta extends RecyclerView.Adapter<AdapterTarjeta.ViewHolderTarjeta> {

    private ArrayList<TarjetaConstructor> listTarjeta;
    private ArrayList<String> selectedCopiar;
    private ArrayList<String> selectedCompartir;
    private Context mCtx;

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

        viewHolderTarjeta.titular.setText(listTarjeta.get(i).getTitular());
        viewHolderTarjeta.numeroTarjeta.setText(String.valueOf(listTarjeta.get(i).getNumeroTarjeta()));
        viewHolderTarjeta.numeroCVV.setText(String.valueOf(listTarjeta.get(i).getCvv()));
        viewHolderTarjeta.cedula.setText(String.valueOf(listTarjeta.get(i).getCedula()));
        viewHolderTarjeta.tipoTarjeta.setText(listTarjeta.get(i).getTipo());

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
        return listTarjeta.size();
    }

    public class ViewHolderTarjeta extends RecyclerView.ViewHolder {

        TextView titular, numeroTarjeta, numeroCVV, cedula, tipoTarjeta, menu;

        public ViewHolderTarjeta(@NonNull View itemView) {
            super(itemView);

            titular = (TextView) itemView.findViewById(R.id.tvTitularTarjeta);
            numeroTarjeta = (TextView) itemView.findViewById(R.id.tvnumeroTarjeta);
            numeroCVV = (TextView) itemView.findViewById(R.id.tvnumeroCVV);
            cedula = (TextView) itemView.findViewById(R.id.tvCedulaTarjeta);
            tipoTarjeta = (TextView) itemView.findViewById(R.id.tvTipoTarjeta);
            menu = (TextView) itemView.findViewById(R.id.tvmenuTarjeta);
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
                String tarjeta = String.valueOf(i.getNumeroTarjeta());
                String cvv = String.valueOf(i.getCvv());
                String cedula = String.valueOf(i.getCedula());
                String tipo = i.getTipo();

                String [] items = {titular, tarjeta, cvv, cedula, tipo};

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
                String tarjeta = String.valueOf(i.getNumeroTarjeta());
                String cvv = String.valueOf(i.getCvv());
                String cedula = String.valueOf(i.getCedula());
                String tipo = i.getTipo();

                String [] items = {titular, tarjeta, cvv, cedula, tipo};

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
