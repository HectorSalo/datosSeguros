package com.example.datossegurosFirebaseFinal.Adpatadores;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.example.datossegurosFirebaseFinal.Constructores.ContrasenaConstructor;
import com.example.datossegurosFirebaseFinal.EditarActivity;
import com.example.datossegurosFirebaseFinal.R;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ContrasenaAdapter extends RecyclerView.Adapter<ContrasenaAdapter.ViewHolderContrasena> {

    private ArrayList<ContrasenaConstructor> listContrasena;
    private ArrayList<String> selectedItems;
    private ArrayList <String> selectedCopiar;
    private Context mCtx;
    private FirebaseUser user;

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
    public void onBindViewHolder(@NonNull final ContrasenaAdapter.ViewHolderContrasena viewHolderContrasena, final int i) {

        viewHolderContrasena.servicio.setText(listContrasena.get(i).getServicio());
        viewHolderContrasena.usuario.setText(listContrasena.get(i).getUsuario());
        viewHolderContrasena.contrasena.setText(listContrasena.get(i).getContrasena());
        if (listContrasena.get(i).getVencimiento() == 0) {
            viewHolderContrasena.vencimiento.setText("Sin fecha de vencimiento");
        } else {
            viewHolderContrasena.vencimiento.setText(String.valueOf(listContrasena.get(i).getVencimiento()));
        }

        viewHolderContrasena.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mCtx, viewHolderContrasena.menu);
                popupMenu.inflate(R.menu.menu_contrasena);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
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

                                dialog.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        eliminar(listContrasena.get(i));
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
        return listContrasena.size();
    }

    public class ViewHolderContrasena extends RecyclerView.ViewHolder {

        TextView servicio, usuario, contrasena, vencimiento, menu;

        public ViewHolderContrasena(@NonNull View itemView) {
            super(itemView);

            servicio = (TextView) itemView.findViewById(R.id.tvServicio);
            usuario = (TextView) itemView.findViewById(R.id.tvUsuario);
            contrasena = (TextView) itemView.findViewById(R.id.tvContrasena);
            vencimiento = (TextView) itemView.findViewById(R.id.tvVencimiento);
            menu = (TextView) itemView.findViewById(R.id.tvmenuContrasena);
        }
    }

    public void copiar(final ContrasenaConstructor i) {
        selectedCopiar = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea copiar?");
        dialog.setMultiChoiceItems(R.array.copiarContrasena, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                String servicio = i.getServicio();
                String usuario = i.getUsuario();
                String contrasena = i.getContrasena();

                String [] items = {servicio, usuario, contrasena};

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

    public void compartir(final ContrasenaConstructor i) {
        selectedItems = new ArrayList<>();
        AlertDialog.Builder dialog = new AlertDialog.Builder(mCtx);
        dialog.setTitle("¿Qué desea compartir?");
        dialog.setMultiChoiceItems(R.array.copiarContrasena, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                String servicio = i.getServicio();
                String usuario = i.getUsuario();
                String contrasena = i.getContrasena();

                String [] items = {servicio, usuario, contrasena};

                if (isChecked) {
                    selectedItems.add(items[which]);
                } else {
                    selectedItems.remove(items[which]);
                }
            }
        });
        dialog.setPositiveButton("Compartir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selection = "";
                for (String item: selectedItems) {
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

    public void editar(ContrasenaConstructor i) {
        String id = i.getIdContrasena();
        Intent myIntent = new Intent(mCtx, EditarActivity.class);
        Bundle myBundle = new Bundle();
        myBundle.putInt("data", 0);
        myBundle.putString("idEditar", id);
        myIntent.putExtras(myBundle);
        mCtx.startActivity(myIntent);
    }

    public void eliminar(final ContrasenaConstructor i) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        String userID = user.getUid();
        String doc = i.getIdContrasena();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection(UtilidadesStatic.BD_PROPIETARIOS).document(userID).collection(UtilidadesStatic.BD_CONTRASENAS);

        reference.document(doc)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listContrasena.remove(i);
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

    public void updateList (ArrayList<ContrasenaConstructor> newList) {
        listContrasena = new ArrayList<>();
        listContrasena.addAll(newList);
        notifyDataSetChanged();
    }
}
