package com.skysam.datossegurosFirebaseFinal.settings;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.skysam.datossegurosFirebaseFinal.common.EliminarCuenta;
import com.skysam.datossegurosFirebaseFinal.common.ConexionSQLite;
import com.skysam.datossegurosFirebaseFinal.R;
import com.skysam.datossegurosFirebaseFinal.common.Constants;
import com.skysam.datossegurosFirebaseFinal.database.room.Room;
import com.skysam.datossegurosFirebaseFinal.database.sharedPreference.SharedPref;
import com.skysam.datossegurosFirebaseFinal.generalActivitys.MainActivity;

import java.util.ArrayList;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TITLE_TAG = "settingsActivityTitle";

    private HeaderFragment headerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (SharedPref.INSTANCE.getTheme()){
            case Constants.PREFERENCE_AMARILLO:
                setTheme(R.style.Theme_DatosSegurosYellow);
                break;
            case Constants.PREFERENCE_ROJO:
                setTheme(R.style.Theme_DatosSegurosRed);
                break;
            case Constants.PREFERENCE_MARRON:
                setTheme(R.style.Theme_DatosSegurosBrwon);
                break;
            case Constants.PREFERENCE_LILA:
                setTheme(R.style.Theme_DatosSegurosLila);
                break;
        }
        setContentView(R.layout.settings_activity);

        headerFragment = new HeaderFragment();
        PreferenciasFragment preferenciasFragment = new PreferenciasFragment();

        Bundle bundle = this.getIntent().getExtras();

        if (savedInstanceState == null) {
            if (bundle == null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.settings, headerFragment)
                        .commit();
            } else {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.settings, preferenciasFragment)
                        .commit();
                setTitle(R.string.preferencias);
            }
        } else {
            setTitle(savedInstanceState.getCharSequence(TITLE_TAG));
        }
        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                        setTitle(R.string.title_activity_settings);
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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (headerFragment.isAdded()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        } else {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, headerFragment)
                    .commit();
            setTitle(R.string.title_activity_settings);
        }
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

            switch (SharedPref.INSTANCE.getTheme()){
                case Constants.PREFERENCE_AMARILLO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes));
                    break;
                case Constants.PREFERENCE_ROJO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_rojo));
                    break;
                case Constants.PREFERENCE_MARRON:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_marron));
                    break;
                case Constants.PREFERENCE_LILA:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_lila));
                    break;
            }
            return view;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.header_preferences, rootKey);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Preference datosPersonales = findPreference("datos_personales_header");
            Preference preferenceAbout = findPreference("about");

            String providerId = "";

            if (user != null) {
                for (UserInfo profile : user.getProviderData()) {
                    providerId = profile.getProviderId();
                }
            }
            if (datosPersonales != null) {
                datosPersonales.setVisible(!providerId.equals("google.com"));
            }

            preferenceAbout.setOnPreferenceClickListener(preference -> {
                requireActivity().startActivity(new Intent(requireContext(), AcercaActivity.class));
                return false;
            });
        }
    }

    public static class PreferenciasFragment extends PreferenceFragmentCompat {

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = super.onCreateView(inflater, container, savedInstanceState);
            switch (SharedPref.INSTANCE.getTheme()){
                case Constants.PREFERENCE_AMARILLO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes));
                    break;
                case Constants.PREFERENCE_ROJO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_rojo));
                    break;
                case Constants.PREFERENCE_MARRON:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_marron));
                    break;
                case Constants.PREFERENCE_LILA:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_lila));
                    break;
            }
            return view;
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferencias_preferences, rootKey);

            ListPreference listaBloqueo = findPreference(Constants.PREFERENCE_TIPO_BLOQUEO);
            ListPreference listaTemas = findPreference(Constants.PREFERENCE_TEMA);
            ListPreference listShowData = findPreference(Constants.PREFERENCE_ALMACENAMIENTO_NUBE);

            switch (SharedPref.INSTANCE.getTheme()){
                case Constants.PREFERENCE_AMARILLO:
                    listaTemas.setValue(Constants.PREFERENCE_AMARILLO);
                    break;
                case Constants.PREFERENCE_ROJO:
                    listaTemas.setValue(Constants.PREFERENCE_ROJO);
                    break;
                case Constants.PREFERENCE_MARRON:
                    listaTemas.setValue(Constants.PREFERENCE_MARRON);
                    break;
                case Constants.PREFERENCE_LILA:
                    listaTemas.setValue(Constants.PREFERENCE_LILA);
                    break;
            }

            switch (SharedPref.INSTANCE.getLock()){
                case Constants.PREFERENCE_SIN_BLOQUEO:
                    listaBloqueo.setValue(Constants.PREFERENCE_SIN_BLOQUEO);
                    break;
                case Constants.PREFERENCE_HUELLA:
                    listaBloqueo.setValue(Constants.PREFERENCE_HUELLA);
                    break;
                case Constants.PREFERENCE_PIN:
                    listaBloqueo.setValue(Constants.PREFERENCE_PIN);
                    break;
            }

            switch (SharedPref.INSTANCE.getShowData()) {
                case Constants.PREFERENCE_SHOW_ALL:
                    listShowData.setValue(Constants.PREFERENCE_SHOW_ALL);
                    break;
                case Constants.PREFERENCE_SHOW_CLOUD:
                    listShowData.setValue(Constants.PREFERENCE_SHOW_CLOUD);
                    break;
                case Constants.PREFERENCE_SHOW_DEVICE:
                    listShowData.setValue(Constants.PREFERENCE_SHOW_DEVICE);
                    break;
            }


            listaBloqueo.setOnPreferenceChangeListener((preference, newValue) -> {
                String bloqueoEscogido = (String) newValue;

                switch (bloqueoEscogido){
                    case Constants.PREFERENCE_SIN_BLOQUEO:
                        if (!bloqueoEscogido.equals(SharedPref.INSTANCE.getLock())) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_SIN_BLOQUEO);
                            Intent intent = new Intent(getContext(), BloqueoActivity.class);
                            intent.putExtras(bundle);
                            requireActivity().startActivity(intent);
                        }
                        break;
                    case Constants.PREFERENCE_HUELLA:
                        if (!bloqueoEscogido.equals(SharedPref.INSTANCE.getLock())) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_HUELLA);
                            Intent intent = new Intent(getContext(), BloqueoActivity.class);
                            intent.putExtras(bundle);
                            requireActivity().startActivity(intent);
                        }
                        break;
                    case Constants.PREFERENCE_PIN:
                        if (!bloqueoEscogido.equals(SharedPref.INSTANCE.getLock())) {
                            Bundle bundle = new Bundle();
                            bundle.putString(Constants.PREFERENCE_TIPO_BLOQUEO, Constants.PREFERENCE_PIN);
                            Intent intent = new Intent(getContext(), BloqueoActivity.class);
                            intent.putExtras(bundle);
                            requireActivity().startActivity(intent);
                        }
                        break;
                }
                return true;
            });


            listaTemas.setOnPreferenceChangeListener((preference, newValue) -> {
                String temaEscogido = (String) newValue;
                switch (temaEscogido){
                    case Constants.PREFERENCE_AMARILLO:
                        if (!temaEscogido.equals(SharedPref.INSTANCE.getTheme())) {
                            SharedPref.INSTANCE.changeTheme(Constants.PREFERENCE_AMARILLO);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.PREFERENCE_TEMA, true);
                            Intent intent = new Intent(getContext(), SettingsActivity.class);
                            intent.putExtras(bundle);
                            requireActivity().finish();
                            requireActivity().startActivity(intent);
                            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                        break;
                    case Constants.PREFERENCE_ROJO:
                        if (!temaEscogido.equals(SharedPref.INSTANCE.getTheme())) {
                            SharedPref.INSTANCE.changeTheme(Constants.PREFERENCE_ROJO);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.PREFERENCE_TEMA, true);
                            Intent intent = new Intent(getContext(), SettingsActivity.class);
                            intent.putExtras(bundle);
                            requireActivity().finish();
                            requireActivity().startActivity(intent);
                            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                        break;
                    case Constants.PREFERENCE_MARRON:
                        if (!temaEscogido.equals(SharedPref.INSTANCE.getTheme())) {
                            SharedPref.INSTANCE.changeTheme(Constants.PREFERENCE_MARRON);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.PREFERENCE_TEMA, true);
                            Intent intent = new Intent(getContext(), SettingsActivity.class);
                            intent.putExtras(bundle);
                            requireActivity().finish();
                            requireActivity().startActivity(intent);
                            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                        break;
                    case Constants.PREFERENCE_LILA:
                        if (!temaEscogido.equals(SharedPref.INSTANCE.getTheme())) {
                            SharedPref.INSTANCE.changeTheme(Constants.PREFERENCE_LILA);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean(Constants.PREFERENCE_TEMA, true);
                            Intent intent = new Intent(getContext(), SettingsActivity.class);
                            intent.putExtras(bundle);
                            requireActivity().finish();
                            requireActivity().startActivity(intent);
                            requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                        break;
                }
                return true;
            });


            listShowData.setOnPreferenceChangeListener((preference, newValue) -> {
                String showData = (String) newValue;
                switch (showData) {
                    case Constants.PREFERENCE_SHOW_ALL:
                        SharedPref.INSTANCE.changeShowData(Constants.PREFERENCE_SHOW_ALL);
                        break;
                    case Constants.PREFERENCE_SHOW_CLOUD:
                        SharedPref.INSTANCE.changeShowData(Constants.PREFERENCE_SHOW_CLOUD);
                        break;
                    case Constants.PREFERENCE_SHOW_DEVICE:
                        SharedPref.INSTANCE.changeShowData(Constants.PREFERENCE_SHOW_DEVICE);
                        break;
                }
                return true;
            });
        }


    }

    public static class DatosPersonalesFragment extends Fragment {

        private EditText etEmail, etRepetirEmail, etPass, etRepetirPass;
        private TextInputLayout inputLayoutEmail, inputLayoutPass, inputLayoutPassRepetir, inputLayoutEmailRepetir;
        private RadioButton rbEmail, rbPass;
        private LinearLayout layoutEmail, layoutPass;
        private ProgressBar progressBar;
        private Button button;
        private RadioGroup radioGroup;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_datos_personales, container, false);

            button = view.findViewById(R.id.button);

            switch (SharedPref.INSTANCE.getTheme()){
                case Constants.PREFERENCE_AMARILLO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes));
                    button.setBackgroundColor(getResources().getColor(R.color.color_blue_grey));
                    break;
                case Constants.PREFERENCE_ROJO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_rojo));
                    button.setBackgroundColor(getResources().getColor(R.color.color_black_ligth));
                    break;
                case Constants.PREFERENCE_MARRON:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_marron));
                    button.setBackgroundColor(getResources().getColor(R.color.color_brown));
                    break;
                case Constants.PREFERENCE_LILA:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_lila));
                    button.setBackgroundColor(getResources().getColor(R.color.color_purple));
                    break;
            }


            etEmail = view.findViewById(R.id.editTextUpdateEmail);
            etRepetirEmail = view.findViewById(R.id.editTextRepetirUpdateEmail);
            etPass = view.findViewById(R.id.editTextUpdatePass);
            etRepetirPass = view.findViewById(R.id.editTextRepetirUpdatePass);
            radioGroup = view.findViewById(R.id.radioGroupUpdate);
            rbEmail = view.findViewById(R.id.radioButtonUpdateEmail);
            rbPass = view.findViewById(R.id.radioButtonUpdatePass);
            inputLayoutPass = view.findViewById(R.id.input_layout_pass);
            inputLayoutEmail = view.findViewById(R.id.input_layout_email);
            inputLayoutEmailRepetir = view.findViewById(R.id.input_layout_repetir_email);
            inputLayoutPassRepetir = view.findViewById(R.id.input_layout_repetir_pass);
            progressBar = view.findViewById(R.id.progressBar);
            layoutEmail = view.findViewById(R.id.layoutEmail);
            layoutPass = view.findViewById(R.id.layoutPass);

            rbEmail.setChecked(true);
            layoutPass.setVisibility(View.GONE);
            layoutEmail.setVisibility(View.VISIBLE);

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                switch (checkedId) {
                    case R.id.radioButtonUpdateEmail:
                        layoutEmail.setVisibility(View.VISIBLE);
                        layoutPass.setVisibility(View.GONE);
                        inputLayoutPass.setError(null);
                        inputLayoutPassRepetir.setError(null);
                        etPass.setText("");
                        etRepetirPass.setText("");
                        break;

                    case R.id.radioButtonUpdatePass:
                        inputLayoutEmail.setError(null);
                        inputLayoutEmailRepetir.setError(null);
                        layoutEmail.setVisibility(View.GONE);
                        layoutPass.setVisibility(View.VISIBLE);
                        etEmail.setText("");
                        etRepetirEmail.setText("");
                        break;
                }
            });

            button.setOnClickListener(v -> {
                if (rbEmail.isChecked()) {
                    validarEmail();
                } else if (rbPass.isChecked()) {
                    validarPass();
                }
            });

            return view;
        }


        public void validarEmail() {
            inputLayoutEmail.setError(null);
            inputLayoutEmailRepetir.setError(null);
            String email = etEmail.getText().toString();
            String emailRepetir = etRepetirEmail.getText().toString();

            if (!email.isEmpty()) {
                if (email.contains("@")) {
                    if (email.equals(emailRepetir)) {
                        actualizarEmail(email);
                    } else {
                        inputLayoutEmail.setError("Las correos deben coincidir");
                        inputLayoutEmailRepetir.setError("Las correos deben coincidir");
                    }
                } else {
                    inputLayoutEmail.setError("Formato incorrecto para correo");
                }
            } else {
                inputLayoutEmail.setError("El campo no puede estar vacío");
                inputLayoutEmailRepetir.setError("El campo no puede estar vacío");
            }
        }

        public void validarPass() {
            inputLayoutPass.setError(null);
            inputLayoutPassRepetir.setError(null);
            String pass = etPass.getText().toString();
            String passRepetir = etRepetirPass.getText().toString();

            if (pass.isEmpty() || (pass.length() < 6)) {
                inputLayoutPass.setError("Mínimo 6 caracteres");
                inputLayoutPassRepetir.setError("Mínimo 6 caracteres");
            } else {
                if (pass.equals(passRepetir)) {
                    actualizarPass(pass);
                } else {
                    inputLayoutPass.setError("Las contraseñas deben coincidir");
                    inputLayoutPassRepetir.setError("Las contraseñas deben coincidir");
                }

            }

        }

        public void actualizarEmail(String email) {
            layoutPass.setEnabled(false);
            layoutEmail.setEnabled(false);
            button.setEnabled(false);
            radioGroup.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                user.updateEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("msg", "User email address updated.");
                                Toast.makeText(getContext(), "Correo actualizado", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(getContext(), SettingsActivity.class));
                            } else {
                                Toast.makeText(getContext(), "Error al actualizar. Intene nuevamente", Toast.LENGTH_SHORT).show();
                                layoutPass.setEnabled(true);
                                layoutEmail.setEnabled(true);
                                button.setEnabled(true);
                                radioGroup.setEnabled(true);
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        }

        public void actualizarPass(String pass) {
            layoutPass.setEnabled(false);
            layoutEmail.setEnabled(false);
            button.setEnabled(false);
            radioGroup.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if (user != null) {
                user.updatePassword(pass)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("msg", "User password updated.");
                                Toast.makeText(getContext(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                startActivity(new Intent(getContext(), SettingsActivity.class));
                            } else {
                                Toast.makeText(getContext(), "Error al actualizar. Intene nuevamente", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                layoutPass.setEnabled(true);
                                layoutEmail.setEnabled(true);
                                button.setEnabled(true);
                                radioGroup.setEnabled(true);
                            }
                        });
            }
        }

    }


    public static class BorrarDatosFragment extends Fragment {

        private FirebaseUser user;
        private ArrayList<String> listContrasena, listBancos, listTarjetas, listNota;
        private EliminarCuenta eliminarCuenta;
        private ProgressBar progressBar;
        private Button buttonDispositivo, buttonNube, buttonTodos;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_borrar_datos, container, false);

            buttonDispositivo = view.findViewById(R.id.button_borrar_dispositivo);
            buttonNube = view.findViewById(R.id.button_borrar_nube);
            buttonTodos = view.findViewById(R.id.button_borrar_todos);

            user = FirebaseAuth.getInstance().getCurrentUser();

            switch (SharedPref.INSTANCE.getTheme()){
                case Constants.PREFERENCE_AMARILLO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes));
                    buttonTodos.setBackgroundColor(getResources().getColor(R.color.color_blue_grey));
                    buttonDispositivo.setBackgroundColor(getResources().getColor(R.color.color_blue_grey));
                    buttonNube.setBackgroundColor(getResources().getColor(R.color.color_blue_grey));
                    break;
                case Constants.PREFERENCE_ROJO:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_rojo));
                    buttonTodos.setBackgroundColor(getResources().getColor(R.color.color_black_ligth));
                    buttonDispositivo.setBackgroundColor(getResources().getColor(R.color.color_black_ligth));
                    buttonNube.setBackgroundColor(getResources().getColor(R.color.color_black_ligth));
                    break;
                case Constants.PREFERENCE_MARRON:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_marron));
                    buttonTodos.setBackgroundColor(getResources().getColor(R.color.color_brown));
                    buttonDispositivo.setBackgroundColor(getResources().getColor(R.color.color_brown));
                    buttonNube.setBackgroundColor(getResources().getColor(R.color.color_brown));
                    break;
                case Constants.PREFERENCE_LILA:
                    view.setBackgroundColor(getResources().getColor(R.color.color_fondo_ajustes_lila));
                    buttonTodos.setBackgroundColor(getResources().getColor(R.color.color_purple));
                    buttonDispositivo.setBackgroundColor(getResources().getColor(R.color.color_purple));
                    buttonNube.setBackgroundColor(getResources().getColor(R.color.color_purple));
                    break;
            }

            eliminarCuenta = new EliminarCuenta(getContext());
            progressBar = view.findViewById(R.id.progressBar);

            buttonNube.setOnClickListener(v -> recuperarContrasenaNube());

            buttonDispositivo.setOnClickListener(v -> eliminarDataDispositivo(false));

            buttonTodos.setOnClickListener(v -> eliminarDataDispositivo(true));

            return view;
        }

        private void recuperarContrasenaNube() {
            buttonDispositivo.setEnabled(false);
            buttonNube.setEnabled(false);
            buttonTodos.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            String userID = user.getUid();
            listContrasena = new ArrayList<>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_CONTRASENAS);

            Query query = reference.orderBy(Constants.BD_SERVICIO, Query.Direction.ASCENDING);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        String id = doc.getId();

                        listContrasena.add(id);
                    }

                    recuperarCuentaNube();
                } else {
                    Toast.makeText(getContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void recuperarCuentaNube() {
            String userID = user.getUid();
            listBancos = new ArrayList<>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_CUENTAS);

            Query query = reference.orderBy(Constants.BD_BANCO, Query.Direction.ASCENDING);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        String id = doc.getId();

                        listBancos.add(id);
                    }

                    recuperarTarjetaNube();
                } else {
                    Toast.makeText(getContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void recuperarTarjetaNube() {
            String userID = user.getUid();
            listTarjetas = new ArrayList<>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_TARJETAS);

            Query query = reference.orderBy(Constants.BD_TITULAR_TARJETA, Query.Direction.ASCENDING);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        String id = doc.getId();

                        listTarjetas.add(id);
                    }

                    recuperarNotaNube();
                } else {
                    Toast.makeText(getContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void recuperarNotaNube() {
            String userID = user.getUid();
            listNota = new ArrayList<>();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference reference = db.collection(Constants.BD_PROPIETARIOS).document(userID).collection(Constants.BD_NOTAS);

            Query query = reference.orderBy(Constants.BD_TITULO_NOTAS, Query.Direction.ASCENDING);
            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        String id = doc.getId();

                        listNota.add(id);
                    }
                    validarNube();
                } else {
                    Toast.makeText(getContext(), "Error al cargar la lista. Intente nuevamente", Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void validarNube() {
            if (listContrasena.isEmpty() && listBancos.isEmpty() && listNota.isEmpty() && listTarjetas.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                buttonDispositivo.setEnabled(true);
                buttonNube.setEnabled(true);
                buttonTodos.setEnabled(true);
                Toast.makeText(getContext(), "No hay datos en la Nube para eliminar", Toast.LENGTH_LONG).show();
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
                buttonDispositivo.setEnabled(true);
                buttonNube.setEnabled(true);
                buttonTodos.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Datos eliminados de la Nube satisfactoriamente", Toast.LENGTH_LONG).show();
            }
        }

        private void eliminarDataDispositivo(boolean borrarTodo) {
            Room.INSTANCE.deleteAllPasswords();
            Room.INSTANCE.deleteAllAccounts();
            Room.INSTANCE.deleteAllCards();
            Room.INSTANCE.deleteAllNotes();

            Toast.makeText(getContext(), "Datos eliminados del Dispositivo satisfactoriamente", Toast.LENGTH_LONG).show();

            if (borrarTodo) {
                recuperarContrasenaNube();
            }
        }
    }
}