package com.example.datossegurosFirebaseFinal.FragmentsBloqueo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.datossegurosFirebaseFinal.InicSesionActivity;
import com.example.datossegurosFirebaseFinal.R;
import com.example.datossegurosFirebaseFinal.Utilidades.Utilidades;
import com.example.datossegurosFirebaseFinal.Utilidades.UtilidadesStatic;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private Context context;

    public FingerprintHandler (Context context) {
        this.context = context;
    }

    public void starAuth (FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        CancellationSignal cancellationSignal = new CancellationSignal();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        this.update("Error al leer huella. " + errString, false);
    }

    @Override
    public void onAuthenticationFailed() {
        this.update("Error al autenticar", false);
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        this.update("Error: " + helpString, false);
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        this.update("Huella verificada", true);
    }

    private void update (String s, boolean b) {
        TextView mensaje = (TextView) ((Activity)context).findViewById(R.id.textViewHuella);
        ImageView image = (ImageView) ((Activity)context).findViewById(R.id.imageViewHuella);

        mensaje.setText(s);

        if (Utilidades.uso_huella == 1) {
            if (!b) {
                mensaje.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            } else {
                context.startActivity(new Intent(context, InicSesionActivity.class));
                mensaje.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
        } else if (Utilidades.uso_huella == 2) {
            if (!b) {
                mensaje.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            } else {
                SharedPreferences preferences = context.getSharedPreferences(UtilidadesStatic.BLOQUEO, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(UtilidadesStatic.HUELLA, true);
                editor.putBoolean(UtilidadesStatic.PATRON, false);
                editor.putBoolean(UtilidadesStatic.PIN, false);
                editor.putBoolean(UtilidadesStatic.SIN_BLOQUEO, false);
                editor.commit();
                mensaje.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
            }
        }


    }
}
