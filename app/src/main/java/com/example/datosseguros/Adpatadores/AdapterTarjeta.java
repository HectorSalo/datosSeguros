package com.example.datosseguros.Adpatadores;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
    public void onBindViewHolder(@NonNull ViewHolderTarjeta viewHolderTarjeta, int i) {

        viewHolderTarjeta.titular.setText(listTarjeta.get(i).getTitular());
        viewHolderTarjeta.numeroTarjeta.setText(String.valueOf(listTarjeta.get(i).getNumeroTarjeta()));
        viewHolderTarjeta.numeroCVV.setText(String.valueOf(listTarjeta.get(i).getCvv()));
        viewHolderTarjeta.cedula.setText(String.valueOf(listTarjeta.get(i).getCedula()));
        viewHolderTarjeta.tipoTarjeta.setText(listTarjeta.get(i).getTipo());

    }

    @Override
    public int getItemCount() {
        return listTarjeta.size();
    }

    public class ViewHolderTarjeta extends RecyclerView.ViewHolder {

        TextView titular, numeroTarjeta, numeroCVV, cedula, tipoTarjeta;

        public ViewHolderTarjeta(@NonNull View itemView) {
            super(itemView);

            titular = (TextView) itemView.findViewById(R.id.tvTitularTarjeta);
            numeroTarjeta = (TextView) itemView.findViewById(R.id.tvnumeroTarjeta);
            numeroCVV = (TextView) itemView.findViewById(R.id.tvnumeroCVV);
            cedula = (TextView) itemView.findViewById(R.id.tvCedulaTarjeta);
            tipoTarjeta = (TextView) itemView.findViewById(R.id.tvTipoTarjeta);
        }
    }
}