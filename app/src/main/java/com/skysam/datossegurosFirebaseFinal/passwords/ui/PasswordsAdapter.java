package com.skysam.datossegurosFirebaseFinal.passwords.ui;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.skysam.datossegurosFirebaseFinal.database.firebase.Auth;
import com.skysam.datossegurosFirebaseFinal.database.room.Room;
import com.skysam.datossegurosFirebaseFinal.database.room.entities.Password;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.EditarActivity;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PasswordsAdapter extends RecyclerView.Adapter<PasswordsAdapter.ViewHolderContrasena> {

    private ArrayList<Password> listContrasena;
    private ArrayList<String> selectedItems;
    private ArrayList <String> selectedCopiar;
    private Context mCtx;

    public PasswordsAdapter(List<Password> listContrasena) {
        this.listContrasena = new ArrayList<>(listContrasena);
    }

    @NonNull
    @Override
    public PasswordsAdapter.ViewHolderContrasena onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview_contrasena, null, false);
        mCtx = viewGroup.getContext();
        return new ViewHolderContrasena(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final PasswordsAdapter.ViewHolderContrasena viewHolderContrasena, final int i) {
        viewHolderContrasena.servicio.setText(listContrasena.get(i).getService());
        viewHolderContrasena.usuario.setText(listContrasena.get(i).getUser());
        viewHolderContrasena.contrasena.setText(listContrasena.get(i).getPassword());
        if (listContrasena.get(i).getExpiration() == 0) {
            viewHolderContrasena.vencimiento.setText("Sin fecha de vencimiento");
            viewHolderContrasena.vencimiento.setTextColor(mCtx.getResources().getColor(R.color.md_text_white));
        } else {
            viewHolderContrasena.vencimiento.setText(listContrasena.get(i).getExpiration() + " días");
            if (listContrasena.get(i).getExpiration() <= 7 && listContrasena.get(i).getExpiration() != 0) {
                viewHolderContrasena.vencimiento.setTextColor(mCtx.getResources().getColor(R.color.color_red_error));
            } else {
                viewHolderContrasena.vencimiento.setTextColor(mCtx.getResources().getColor(R.color.md_text_white));
            }
        }
        boolean isExpanded = listContrasena.get(i).isExpanded();
        viewHolderContrasena.constraintExpandable.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        viewHolderContrasena.arrow.setImageResource(isExpanded ? R.drawable.ic_keyboard_arrow_up_24 : R.drawable.ic_keyboard_arrow_down_24);

        if (!listContrasena.get(i).getLabels().isEmpty()) {
            viewHolderContrasena.chipGroup.removeAllViews();
        for (String label: listContrasena.get(i).getLabels()) {
            Chip chip = new Chip(mCtx);
            chip.setText(label);
            chip.setChipBackgroundColorResource(getColorPrimary());
            chip.setTextColor(ContextCompat.getColor(mCtx, R.color.md_text_white));
            viewHolderContrasena.chipGroup.addView(chip);
        }
        } else {
            viewHolderContrasena.chipGroup.removeAllViews();
        }

        viewHolderContrasena.menu.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mCtx, viewHolderContrasena.menu);
            popupMenu.inflate(R.menu.menu_contrasena);
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.menu_copiar:
                        copiar(listContrasena.get(i));
                        break;

                    case R.id.menu_compartir:
                        compartir(listContrasena.get(i));
                        break;

                    case R.id.menu_editar:
                        editar(listContrasena.get(i));
                        break;

                    case R.id.menu_eliminar:
                        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
                        dialog.setTitle("Confirmar");
                        dialog.setMessage("¿Desea eliminar estos datos de manera permanente?");

                        dialog.setPositiveButton("Eliminar", (dialog1, which) -> {
                            if (listContrasena.get(i).isSavedCloud()) {
                                eliminarFirebase(listContrasena.get(i));
                            } else {
                                eliminarRoom(listContrasena.get(i));
                            }
                        });
                        dialog.setNegativeButton("Cancelar", (dialog12, which) -> dialog12.dismiss());
                        dialog.setIcon(R.drawable.ic_delete);
                        dialog.show();

                        break;

                    case R.id.menu_ultimos_pass:
                        if (listContrasena.get(i).isSavedCloud()) {
                            verUltimosPassFirebase(listContrasena.get(i).getId());
                        } else {
                            verUltimosRoom(listContrasena.get(i));
                        }
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
        return listContrasena.size();
    }

    public class ViewHolderContrasena extends RecyclerView.ViewHolder {

        TextView servicio, usuario, contrasena, vencimiento, menu;
        CardView cardView;
        ImageButton arrow;
        ChipGroup chipGroup;
        ConstraintLayout constraintExpandable;

        public ViewHolderContrasena(@NonNull View itemView) {
            super(itemView);

            servicio = itemView.findViewById(R.id.tvServicio);
            usuario = itemView.findViewById(R.id.tvUsuario);
            contrasena = itemView.findViewById(R.id.tvContrasena);
            vencimiento = itemView.findViewById(R.id.tvVencimiento);
            menu = itemView.findViewById(R.id.tvmenuContrasena);
            cardView = itemView.findViewById(R.id.cardview);
            arrow = itemView.findViewById(R.id.ib_arrow);
            chipGroup = itemView.findViewById(R.id.chip_group);
            constraintExpandable = itemView.findViewById(R.id.expandable);

            arrow.setOnClickListener(view -> {
                Password passwordsModel = listContrasena.get(getAdapterPosition());
                passwordsModel.setExpanded(!passwordsModel.isExpanded());
                notifyItemChanged(getAdapterPosition());
            });
        }
    }

    public void copiar(final Password i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarContrasena, null, (dialog1, which, isChecked) -> {
            String servicio = i.getService();
            String usuario = i.getUser();
            String contrasena = i.getPassword();

            String [] items = {servicio, usuario, contrasena};

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

    public void compartir(final Password i) {
        selectedItems = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarContrasena, null, (dialog1, which, isChecked) -> {

            String servicio = i.getService();
            String usuario = i.getUser();
            String contrasena = i.getPassword();

            String [] items = {servicio, usuario, contrasena};

            if (isChecked) {
                selectedItems.add(items[which]);
            } else {
                selectedItems.remove(items[which]);
            }
        });
        dialog.setPositiveButton("Compartir", (dialog12, which) -> {
            StringBuilder selection = new StringBuilder();
            for (String item: selectedItems) {
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

    public void editar(Password i) {
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putString("id", i.getId());
        myBundle.putBoolean("isCloud", i.isSavedCloud());
        myBundle.putInt("data", 0);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminarFirebase(final Password i) {
        String doc = i.getId();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS)
                .document(Auth.INSTANCE.getCurrenUser().getUid()).collection(Constants.BD_CONTRASENAS);

        reference.document(doc)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    listContrasena.remove(i);
                    notifyDataSetChanged();
                    Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();

                })
                .addOnFailureListener(e -> Toast.makeText(mCtx, "Error al eliminar. Intente nuevamente", Toast.LENGTH_SHORT).show());
    }

    public void eliminarRoom(Password i) {
        Room.INSTANCE.deletePassword(i);
        listContrasena.remove(i);
        notifyDataSetChanged();
        Toast.makeText(mCtx,"Eliminado", Toast.LENGTH_SHORT).show();
    }

    public void verUltimosPassFirebase(String idPass) {
        final ProgressDialog progress = new ProgressDialog(mCtx);
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference reference = db.collection(Constants.BD_PROPIETARIOS).document(Auth.INSTANCE.getCurrenUser().getUid()).collection(Constants.BD_CONTRASENAS).document(idPass);

        LinearLayout layout = new LinearLayout(mCtx);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView textView1 = new TextView(mCtx);
        final TextView textView2 = new TextView(mCtx);
        final TextView textView3 = new TextView(mCtx);
        final TextView textView4 = new TextView(mCtx);
        final TextView textView5 = new TextView(mCtx);
        textView1.setTextSize(24);
        textView2.setTextSize(24);
        textView3.setTextSize(24);
        textView4.setTextSize(24);
        textView5.setTextSize(24);
        textView1.setPadding(50, 5, 5, 5);
        textView2.setPadding(50, 5, 5, 5);
        textView3.setPadding(50, 5, 5, 5);
        textView4.setPadding(50, 5, 5, 5);
        textView5.setPadding(50, 5, 5, 5);
        textView1.setText("");
        textView2.setText("");
        textView3.setText("");
        textView4.setText("");
        textView5.setText("");

        reference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();

                if (doc.exists()) {
                    if (doc.getString(Constants.BD_ULTIMO_PASS_1) != null) {
                        String pass1 = doc.getString(Constants.BD_ULTIMO_PASS_1);
                        textView1.setText(pass1);
                    } else {
                        textView1.setText("Sin historial de contraseñas");
                    }

                    if (doc.getString(Constants.BD_ULTIMO_PASS_2) != null) {
                        String pass2 = doc.getString(Constants.BD_ULTIMO_PASS_2);
                        textView2.setText(pass2);
                    }

                    if (doc.getString(Constants.BD_ULTIMO_PASS_3) != null) {
                        String pass3 = doc.getString(Constants.BD_ULTIMO_PASS_3);
                        textView3.setText(pass3);
                    }

                    if (doc.getString(Constants.BD_ULTIMO_PASS_4) != null) {
                        String pass4 = doc.getString(Constants.BD_ULTIMO_PASS_4);
                        textView4.setText(pass4);
                    }

                    if (doc.getString(Constants.BD_ULTIMO_PASS_5) != null) {
                        String pass5 = doc.getString(Constants.BD_ULTIMO_PASS_5);
                        textView5.setText(pass5);
                    }
                } else {
                    Log.d("msg", "No such document");
                }

            } else {
                Log.d("msg", "Error:", task.getException());
            }
            progress.dismiss();
        });


        layout.addView(textView1);
        layout.addView(textView2);
        layout.addView(textView3);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("Últimas contraseñas usadas")
                .setView(layout)
                .setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss()).show();

    }

    public void verUltimosRoom(Password password) {
        final ProgressDialog progress = new ProgressDialog(mCtx);
        progress.setMessage("Cargando...");
        progress.setCancelable(false);
        progress.show();

        LinearLayout layout = new LinearLayout(mCtx);
        layout.setOrientation(LinearLayout.VERTICAL);
        final TextView textView1 = new TextView(mCtx);
        final TextView textView2 = new TextView(mCtx);
        final TextView textView3 = new TextView(mCtx);
        final TextView textView4 = new TextView(mCtx);
        final TextView textView5 = new TextView(mCtx);
        textView1.setTextSize(24);
        textView2.setTextSize(24);
        textView3.setTextSize(24);
        textView4.setTextSize(24);
        textView5.setTextSize(24);
        textView1.setPadding(50, 5, 5, 5);
        textView2.setPadding(50, 5, 5, 5);
        textView3.setPadding(50, 5, 5, 5);
        textView4.setPadding(50, 5, 5, 5);
        textView5.setPadding(50, 5, 5, 5);
        textView1.setText("");
        textView2.setText("");
        textView3.setText("");
        textView4.setText("");
        textView5.setText("");

        if (password.getPassOld1() != null) {
            textView1.setText(password.getPassOld1());
        } else {
            textView1.setText("Sin historial de contraseñas");
        }
        if (password.getPassOld2() != null) {
            textView2.setText(password.getPassOld2());
        }
        if (password.getPassOld3() != null) {
            textView3.setText(password.getPassOld3());
        }
        if (password.getPassOld4() != null) {
            textView4.setText(password.getPassOld4());
        }
        if (password.getPassOld5() != null) {
            textView5.setText(password.getPassOld5());
        }
        progress.dismiss();


        layout.addView(textView1);
        layout.addView(textView2);
        layout.addView(textView3);

        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("Últimas contraseñas usadas")
                .setView(layout)
                .setPositiveButton("OK", (dialog1, which) -> dialog1.dismiss()).show();
    }

    private int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        mCtx.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.resourceId;
    }

    public void updateList (List<Password> newList) {
        listContrasena.clear();
        listContrasena = new ArrayList<>(newList);
        notifyDataSetChanged();
    }
}
