package com.example.datosseguros.Adpatadores;

import android.content.Context;

import android.content.DialogInterface;
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

import com.example.datosseguros.Constructores.TarjetaConstructor;
import com.example.datosseguros.R;

import java.util.ArrayList;

public class AdapterTarjeta extends RecyclerView.Adapter<AdapterTarjeta.ViewHolderTarjeta> {

    private ArrayList<TarjetaConstructor> listTarjeta;
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
    public void onBindViewHolder(@NonNull final ViewHolderTarjeta viewHolderTarjeta, int i) {

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
                                copiar();
                                break;

                            case R.id.menu_compartir:
                                compartir();
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

    public void copiar() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarTarjeta, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });
        dialog.setPositiveButton("Copiar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

    public void compartir() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarTarjeta, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

            }
        });
        dialog.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
