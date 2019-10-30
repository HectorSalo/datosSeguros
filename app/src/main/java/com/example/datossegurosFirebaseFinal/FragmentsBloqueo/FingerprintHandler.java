package com.example.datossegurosFirebaseFinal.FragmentsBloqueo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.example.datossegurosFirebaseFinal.BloqueoActivity;
import com.example.datossegurosFirebaseFinal.InicSesionActivity;
import com.example.datossegurosFirebaseFinal.MainActivity;
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
        LinearLayout linearHuella = (LinearLayout) ((Activity) context).findViewById(R.id.linearHuella);
        LinearLayout linearPin = (LinearLayout) ((Activity) context).findViewById(R.id.linearPIN);

        if (Utilidades.conf_bloqueo == 1000) {
            context.startActivity(new Intent(context, MainActivity.class));
        } else {
            if (!b) {
                mensaje.setText(s);
                mensaje.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            } else {
                linearPin.setVisibility(View.VISIBLE);
                linearHuella.setVisibility(View.GONE);

            }
        }

    }



}
