package com.skysam.datossegurosFirebaseFinal.notes.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
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
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.common.model.Note;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.EditarActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolderNota> {

    private ArrayList<Note> listNota;
    private ArrayList<String> selectedCopiar;
    private ArrayList<String> selectedCompartir;
    private Context mCtx;

    public NoteAdapter(List<Note> listNota) {
        this.listNota = new ArrayList<>(listNota);
    }

    @NonNull
    @Override
    public ViewHolderNota onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_notas, null, false);
        mCtx = viewGroup.getContext();
        return new ViewHolderNota(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderNota viewHolderNota, final int i) {
        viewHolderNota.titulo.setText(listNota.get(i).getTitle());
        viewHolderNota.contenido.setText(listNota.get(i).getContent());

        boolean isExpanded = listNota.get(i).isExpanded();
        if (isExpanded) {
            viewHolderNota.contenido.setSingleLine(false);
            viewHolderNota.contenido.setEllipsize(null);
            viewHolderNota.chipGroup.setVisibility(View.VISIBLE);
        } else {
            viewHolderNota.contenido.setSingleLine(true);
            viewHolderNota.contenido.setEllipsize(TextUtils.TruncateAt.END);
            viewHolderNota.chipGroup.setVisibility(View.GONE);
        }
        viewHolderNota.arrow.setImageResource(isExpanded ? R.drawable.ic_keyboard_arrow_up_24 : R.drawable.ic_keyboard_arrow_down_24);

        if (!listNota.get(i).getLabels().isEmpty()) {
            viewHolderNota.chipGroup.removeAllViews();
            for (String label: listNota.get(i).getLabels()) {
                Chip chip = new Chip(mCtx);
                chip.setText(label);
                chip.setChipBackgroundColorResource(getColorPrimary());
                chip.setTextColor(ContextCompat.getColor(mCtx, R.color.md_text_white));
                viewHolderNota.chipGroup.addView(chip);
            }
        } else {
            viewHolderNota.chipGroup.removeAllViews();
        }

        viewHolderNota.menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mCtx, viewHolderNota.menu);
            popupMenu.inflate(R.menu.menu_nota);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_copiar:
                        copiar(listNota.get(i));
                        break;

                    case R.id.menu_compartir:
                        compartir(listNota.get(i));
                        break;

                    case R.id.menu_editar:
                        editar(listNota.get(i));
                        break;

                    case R.id.menu_eliminar:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
                        dialog.setTitle("Confirmar");
                        dialog.setMessage("¿Desea eliminar estos datos de manera permanente?");

                        dialog.setPositiveButton("Eliminar", (dialog1, which) -> {
                            eliminarFirebase(listNota.get(i));
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
        return listNota.size();
    }

    public class ViewHolderNota extends RecyclerView.ViewHolder {

        TextView titulo, contenido, menu;
        CardView cardView;
        ChipGroup chipGroup;
        ImageButton arrow;

        public ViewHolderNota(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.tvTituloNota);
            contenido = itemView.findViewById(R.id.tvcontenidoNota);
            menu = itemView.findViewById(R.id.tvmenuNota);
            cardView = itemView.findViewById(R.id.cardviewNota);
            chipGroup = itemView.findViewById(R.id.chip_group);
            arrow = itemView.findViewById(R.id.ib_arrow);

            arrow.setOnClickListener(view -> {
                Note noteModel = listNota.get(getAdapterPosition());
                noteModel.setExpanded(!noteModel.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }

    public void copiar(final Note i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarNota, null, (dialog1, which, isChecked) -> {
            String titulo = i.getTitle();
            String contenido = i.getContent();

            String [] items = {titulo, contenido};

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

    public void compartir(final Note i) {
        selectedCompartir = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarNota, null, (dialog1, which, isChecked) -> {
            String titulo = i.getTitle();
            String contenido = i.getContent();

            String [] items = {titulo, contenido};

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

    public void editar(Note i) {
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("id", i.getId());
        myBundle.putBoolean("isCloud", i.isSavedCloud());
        myBundle.putInt("data", 3);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminarFirebase(final Note i) {
        String doc = i.getId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS)
                .document(Auth.INSTANCE.getCurrenUser().getUid()).collection(Constants.BD_NOTAS);

        reference.document(doc)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listNota.remove(i);
                    notifyDataSetChanged();
                    Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> Toast.makeText(mCtx, "Error al eliminar. Intente nuevamente", Toast.LENGTH_SHORT).show());
    }

    private int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        mCtx.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.resourceId;
    }

    public void updateList (List<Note> newList) {
        listNota.clear();
        listNota = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}
