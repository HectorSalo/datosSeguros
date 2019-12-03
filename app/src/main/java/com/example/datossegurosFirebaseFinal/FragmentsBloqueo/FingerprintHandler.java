package com.example.datossegurosFirebaseFinal.FragmentsBloqueo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.example.datossegurosFirebaseFinal.InicSesionActivity;
import com.example.datossegurosFirebaseFinal.MainActivity;
import com.example.datossegurosFirebaseFinal.R;
import com.example.datossegurosFirebaseFinal.Variables.VariablesGenerales;

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
        TextView mensaje = ((Activity)context).findViewById(R.id.textViewHuella);
        final LinearLayout linearHuella = ((Activity) context).findViewById(R.id.linearHuella);
        final LinearLayout linearPin = ((Activity) context).findViewById(R.id.linearPIN);
        LottieAnimationView lottieAnimationView = ((Activity) context).findViewById(R.id.lottieAnimationView);

            if (!b) {
                lottieAnimationView.setAnimation("huellaerror.json");
                lottieAnimationView.playAnimation();
                mensaje.setText(s);
                mensaje.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            } else {
                if (VariablesGenerales.conf_bloqueo == 1000) {
                    lottieAnimationView.setAnimation("huellaactivo.json");
                    lottieAnimationView.playAnimation();
                    mensaje.setText(s);
                    mensaje.setTextColor(Color.parseColor("#FFFFFF"));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            context.startActivity(new Intent(context, InicSesionActivity.class));
                        }
                    }, 1500);

                } else {
                    lottieAnimationView.setAnimation("huellaactivo.json");
                    lottieAnimationView.playAnimation();
                    mensaje.setText(s);
                    mensaje.setTextColor(Color.parseColor("#FFFFFF"));

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            linearPin.setVisibility(View.VISIBLE);
                            linearHuella.setVisibility(View.GONE);
                        }
                    }, 1500);

                }

            }


    }

}
