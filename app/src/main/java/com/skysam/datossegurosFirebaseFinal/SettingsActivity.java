package com.skysam.datossegurosFirebaseFinal;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.skysam.datossegurosFirebaseFinal.Adpatadores.AdapterTarjeta;
import com.skysam.datossegurosFirebaseFinal.Adpatadores.BancoAdapter;
import com.skysam.datossegurosFirebaseFinal.Adpatadores.ContrasenaAdapter;
import com.skysam.datossegurosFirebaseFinal.Adpatadores.NotaAdapter;
import com.skysam.datossegurosFirebaseFinal.Clases.EliminarCuenta;
import com.skysam.datossegurosFirebaseFinal.Constructores.BancoConstructor;
import com.skysam.datossegurosFirebaseFinal.Constructores.ContrasenaConstructor;
import com.skysam.datossegurosFirebaseFinal.Constructores.NotaConstructor;
import com.skysam.datossegurosFirebaseFinal.Constructores.TarjetaConstructor;
import com.skysam.datossegurosFirebaseFinal.Variables.Constantes;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TITLE_TAG = "settingsActivityTitle";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences sharedPreferences = getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);

        String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

        switch (tema){
            case Constantes.PREFERENCE_AMARILLO:
                setTheme(R.style.AppTheme);
                break;
            case Constantes.PREFERENCE_ROJO:
                setTheme(R.style.AppThemeRojo);
                break;
            case Constantes.PREFERENCE_MARRON:
                setTheme(R.style.AppThemeMarron);
                break;
            case Constantes.PREFERENCE_LILA:
                setTheme(R.style.AppThemeLila);
                break;
        }
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HeaderFragment())
                    .commit();
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                new FragmentManager.OnBackStackChangedListener() {
                    @Override
                    public void onBackStackChanged() {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                            setTitle(R.string.title_activity_settings);
                        }
                    }
                });
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save current activity title so we can set it again after a configuration change
        outState.putCharSequence(TITLE_TAG, getTitle());
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().popBackStackImmediate()) {
            return true;
        }
        return super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        // Instantiate the new Fragment
        final Bundle args = pref.getExtras();
        final Fragment fragment = getSupportFragmentManager().getFragmentFactory().instantiate(
                getClassLoader(),
                pref.getFragment());
        fragment.setArguments(args);
        fragment.setTargetFragment(caller, 0);
        // Replace the existing Fragment with the new Fragment
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.settings, fragment)
                .addToBackStack(null)
                .commit();
        setTitle(pref.getTitle());
        return true;
    }

    public static class HeaderFragment extends PreferenceFragmentCompat {

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);
            String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

            switch (tema){
                case Constantes.PREFERENCE_AMARILLO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes));
                    break;
                case Constantes.PREFERENCE_ROJO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_rojo));
                    break;
                case Constantes.PREFERENCE_MARRON:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_marron));
                    break;
                case Constantes.PREFERENCE_LILA:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_lila));
                    break;
            }

            return view;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey);
        }
    }

    public static class PreferenciasFragment extends PreferenceFragmentCompat {

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);
            String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

            switch (tema){
                case Constantes.PREFERENCE_AMARILLO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes));
                    break;
                case Constantes.PREFERENCE_ROJO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_rojo));
                    break;
                case Constantes.PREFERENCE_MARRON:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_marron));
                    break;
                case Constantes.PREFERENCE_LILA:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_lila));
                    break;
            }

            return view;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferencias_preferences, rootKey);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            final SharedPreferences sharedPreferences = getActivity().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);
            String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

            final boolean almacenamientoNubeB = sharedPreferences.getBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);
            final String bloqueo = sharedPreferences.getString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_SIN_BLOQUEO);

            ListPreference listaBloqueo = findPreference(Constantes.PREFERENCE_TIPO_BLOQUEO);
            ListPreference listaTemas = findPreference(Constantes.PREFERENCE_TEMA);
            final SwitchPreferenceCompat almacenamientoNube = findPreference(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE);

            switch (tema){
                case Constantes.PREFERENCE_AMARILLO:
                    listaTemas.setValue(Constantes.PREFERENCE_AMARILLO);
                    break;
                case Constantes.PREFERENCE_ROJO:
                    listaTemas.setValue(Constantes.PREFERENCE_ROJO);
                    break;
                case Constantes.PREFERENCE_MARRON:
                    listaTemas.setValue(Constantes.PREFERENCE_MARRON);
                    break;
                case Constantes.PREFERENCE_LILA:
                    listaTemas.setValue(Constantes.PREFERENCE_LILA);
                    break;
            }

            switch (bloqueo){
                case Constantes.PREFERENCE_SIN_BLOQUEO:
                    listaBloqueo.setValue(Constantes.PREFERENCE_SIN_BLOQUEO);
                    break;
                case Constantes.PREFERENCE_HUELLA:
                    listaBloqueo.setValue(Constantes.PREFERENCE_HUELLA);
                    break;
                case Constantes.PREFERENCE_PIN:
                    listaBloqueo.setValue(Constantes.PREFERENCE_PIN);
                    break;
            }

            if (almacenamientoNubeB) {
                almacenamientoNube.setChecked(true);
            } else {
                almacenamientoNube.setChecked(false);
            }


            listaBloqueo.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String bloqueoEscogido = (String) newValue;

                    switch (bloqueoEscogido){
                        case Constantes.PREFERENCE_SIN_BLOQUEO:
                            if (!bloqueoEscogido.equals(bloqueo)) {
                                Bundle bundle = new Bundle();
                                bundle.putString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_SIN_BLOQUEO);
                                Intent intent = new Intent(getContext(), BloqueoActivity.class);
                                intent.putExtras(bundle);
                                requireActivity().startActivity(intent);
                            }
                            break;
                        case Constantes.PREFERENCE_HUELLA:
                            if (!bloqueoEscogido.equals(bloqueo)) {
                                Bundle bundle = new Bundle();
                                bundle.putString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_HUELLA);
                                Intent intent = new Intent(getContext(), BloqueoActivity.class);
                                intent.putExtras(bundle);
                                requireActivity().startActivity(intent);
                            }
                            break;
                        case Constantes.PREFERENCE_PIN:
                            if (!bloqueoEscogido.equals(bloqueo)) {
                                Bundle bundle = new Bundle();
                                bundle.putString(Constantes.PREFERENCE_TIPO_BLOQUEO, Constantes.PREFERENCE_PIN);
                                Intent intent = new Intent(getContext(), BloqueoActivity.class);
                                intent.putExtras(bundle);
                                requireActivity().startActivity(intent);
                            }
                            break;
                    }
                    return true;
                }
            });


            listaTemas.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    String temaEscogido = (String) newValue;
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    switch (temaEscogido){
                        case Constantes.PREFERENCE_AMARILLO:
                            editor.putString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);
                            editor.commit();
                            getActivity().recreate();
                            break;
                        case Constantes.PREFERENCE_ROJO:
                            editor.putString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_ROJO);
                            editor.commit();
                            getActivity().recreate();
                            break;
                        case Constantes.PREFERENCE_MARRON:
                            editor.putString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_MARRON);
                            editor.commit();
                            getActivity().recreate();
                            break;
                        case Constantes.PREFERENCE_LILA:
                            editor.putString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_LILA);
                            editor.commit();
                            getActivity().recreate();
                            break;
                    }
                    return true;
                }
            });


            almacenamientoNube.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean isOn = (boolean) newValue;
                    if (isOn) {
                        almacenamientoNube.setIcon(R.drawable.ic_cloud_done_24);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, true);
                        editor.commit();
                    } else {
                        almacenamientoNube.setIcon(R.drawable.ic_cloud_off_24);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(Constantes.PREFERENCE_ALMACENAMIENTO_NUBE, false);
                        editor.commit();
                    }
                    return true;
                }
            });
        }


    }

    public static class SyncFragment extends PreferenceFragmentCompat {

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);
            String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

            switch (tema){
                case Constantes.PREFERENCE_AMARILLO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes));
                    break;
                case Constantes.PREFERENCE_ROJO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_rojo));
                    break;
                case Constantes.PREFERENCE_MARRON:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_marron));
                    break;
                case Constantes.PREFERENCE_LILA:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_lila));
                    break;
            }

            return view;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.sync_preferences, rootKey);
        }
    }


    public static class BorrarDatos extends Fragment {

        private FirebaseUser user;
        private ArrayList<String> listContrasena, listBancos, listTarjetas, listNota;
        private EliminarCuenta eliminarCuenta;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_borrar_datos, container, false);

            user = FirebaseAuth.getInstance().getCurrentUser();
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences(user.getUid(), Context.MODE_PRIVATE);
            String tema = sharedPreferences.getString(Constantes.PREFERENCE_TEMA, Constantes.PREFERENCE_AMARILLO);

            switch (tema){
                case Constantes.PREFERENCE_AMARILLO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes));
                    break;
                case Constantes.PREFERENCE_ROJO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_rojo));
                    break;
                case Constantes.PREFERENCE_MARRON:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_marron));
                    break;
                case Constantes.PREFERENCE_LILA:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_lila));
                    break;
            }

            eliminarCuenta = new EliminarCuenta(getContext());

            Button buttonDispositivo = view.findViewById(R.id.button_borrar_dispositivo);
            Button buttonNube = view.findViewById(R.id.button_borrar_nube);
            Button buttonTodos = view.findViewById(R.id.button_borrar_todos);

            buttonNube.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recuperarContrasenaNube();
                }
            });

            return view;
        }

        private void recuperarContrasenaNube() {
            //progressBarCargar.setVisibility(View.VISIBLE);
            String userID = user.getUid();
            listContrasena = new ArrayList<>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference reference = db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CONTRASENAS);

            Query query = reference.orderBy(Constantes.BD_SERVICIO, Query.Direction.ASCENDING);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String id = doc.getId();

                            listContrasena.add(id);
                        }

                        recuperarCuentaNube();
                    } else {
                        Toast.makeText(getContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void recuperarCuentaNube() {
            String userID = user.getUid();
            listBancos = new ArrayList<>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference reference = db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_CUENTAS);

            Query query = reference.orderBy(Constantes.BD_BANCO, Query.Direction.ASCENDING);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                            String id = doc.getId();

                            listBancos.add(id);
                        }

                        recuperarTarjetaNube();
                    } else {
                        Toast.makeText(getContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void recuperarTarjetaNube() {
            String userID = user.getUid();
            listTarjetas = new ArrayList<>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference reference = db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_TARJETAS);

            Query query = reference.orderBy(Constantes.BD_TITULAR_TARJETA, Query.Direction.ASCENDING);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                            String id = doc.getId();

                            listTarjetas.add(id);
                        }

                        recuperarNotaNube();
                    } else {
                        Toast.makeText(getContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void recuperarNotaNube() {
            String userID = user.getUid();
            listNota = new ArrayList<>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference reference = db.collection(Constantes.BD_PROPIETARIOS).document(userID).collection(Constantes.BD_NOTAS);

            Query query = reference.orderBy(Constantes.BD_TITULO_NOTAS, Query.Direction.ASCENDING);
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                            String id = doc.getId();

                            listNota.add(id);
                        }
                        validarNube();
                    } else {
                        Toast.makeText(getContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        private void validarNube() {
            if (listContrasena.isEmpty() && listBancos.isEmpty() && listNota.isEmpty() && listTarjetas.isEmpty()) {
                Toast.makeText(getContext(), "No hay datos en la Nube para eliminar", Toast.LENGTH_SHORT).show();
            } else {
                if (!listContrasena.isEmpty()) {
                    for (int j = 0; j < listContrasena.size(); j++) {
                        String id = listContrasena.get(j);
                        eliminarCuenta.eliminarContrasenas(id);
                    }
                }
                if (!listBancos.isEmpty()) {
                    for (int j = 0; j < listBancos.size(); j++) {
                        String id = listBancos.get(j);
                        eliminarCuenta.eliminarCuentas(id);
                    }
                }
                if (!listTarjetas.isEmpty()) {
                    for (int j = 0; j < listTarjetas.size(); j++) {
                        String id = listTarjetas.get(j);
                        eliminarCuenta.eliminarTarjetas(id);
                    }
                }
                if (!listNota.isEmpty()) {
                    for (int j = 0; j < listNota.size(); j++) {
                        String id = listNota.get(j);
                        eliminarCuenta.eliminarNotas(id);
                    }
                }
            }
        }
    }
}