package com.example.datosseguros.Adpatadores;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.datosseguros.Constructores.ContrasenaConstructor;
import com.example.datosseguros.R;

import java.util.ArrayList;

public class ContrasenaAdapter extends RecyclerView.Adapter<ContrasenaAdapter.ViewHolderContrasena> {

    private ArrayList<ContrasenaConstructor> listContrasena;
    private Context mCtx;

    public ContrasenaAdapter (ArrayList<ContrasenaConstructor> listContrasena, Context mCtx) {
        this.listContrasena = listContrasena;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ContrasenaAdapter.ViewHolderContrasena onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_contrasena, null, false);

        return new ViewHolderContrasena(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContrasenaAdapter.ViewHolderContrasena viewHolderContrasena, int i) {

        viewHolderContrasena.servicio.setText(listContrasena.get(i).getServicio());
        viewHolderContrasena.usuario.setText(listContrasena.get(i).getUsuario());
        viewHolderContrasena.contrasena.setText(listContrasena.get(i).getContrasena());
        viewHolderContrasena.vencimiento.setText(String.valueOf(listContrasena.get(i).getVencimiento()));

    }

    @Override
    public int getItemCount() {
        return listContrasena.size();
    }

    public class ViewHolderContrasena extends RecyclerView.ViewHolder {

        TextView servicio, usuario, contrasena, vencimiento;

        public ViewHolderContrasena(@NonNull View itemView) {
            super(itemView);

            servicio = (TextView) itemView.findViewById(R.id.tvServicio);
            usuario = (TextView) itemView.findViewById(R.id.tvUsuario);
            contrasena = (TextView) itemView.findViewById(R.id.tvContrasena);
            vencimiento = (TextView) itemView.findViewById(R.id.tvVencimiento);
        }
    }
}
