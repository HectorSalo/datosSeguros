package com.example.datosseguros.Adpatadores;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.datosseguros.Constructores.NotaConstructor;
import com.example.datosseguros.R;

import java.util.ArrayList;

public class NotaAdapter extends RecyclerView.Adapter<NotaAdapter.ViewHolderNota> {

    private ArrayList<NotaConstructor> listNota;
    private Context mCtx;

    public NotaAdapter(ArrayList<NotaConstructor> listNota, Context mCtx) {
        this.listNota = listNota;
        this.mCtx = mCtx;
    }

    @NonNull
    @Override
    public ViewHolderNota onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_notas, null, false);
        return new ViewHolderNota(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderNota viewHolderNota, int i) {

        viewHolderNota.titulo.setText(listNota.get(i).getTitulo());
        viewHolderNota.contenido.setText(listNota.get(i).getContenido());

    }

    @Override
    public int getItemCount() {
        return listNota.size();
    }

    public class ViewHolderNota extends RecyclerView.ViewHolder {

        TextView titulo, contenido;

        public ViewHolderNota(@NonNull View itemView) {
            super(itemView);

            titulo = (TextView) itemView.findViewById(R.id.tvTituloNota);
            contenido = (TextView) itemView.findViewById(R.id.tvcontenidoNota);
        }
    }
}
