package com.skysam.datossegurosFirebaseFinal.FragmentsBloqueo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.textfield.TextInputLayout;
import com.skysam.datossegurosFirebaseFinal.InicSesionActivity;
import com.skysam.datossegurosFirebaseFinal.R;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private Context context;
    private int valorNull;

    public FingerprintHandler (Context context, int valorNull) {
        this.context = context;
        this.valorNull = valorNull;
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
        final TextInputLayout tlPinRepetir = ((Activity) context).findViewById(R.id.inputLayoutRepetirPINRespaldo);
        final EditText etPin = ((Activity) context).findViewById(R.id.etRegistrarPINRespaldo);
        final EditText etPinRepetir = ((Activity) context).findViewById(R.id.etRegistrarPINRepetirRespaldo);
        LottieAnimationView lottieAnimationView = ((Activity) context).findViewById(R.id.lottieAnimationView);

            if (!b) {
                lottieAnimationView.setAnimation("huellaerror.json");
                lottieAnimationView.playAnimation();
                mensaje.setText(s);
                mensaje.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
            } else {
                if (valorNull == 1) {
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
                            tlPinRepetir.setVisibility(View.VISIBLE);
                            etPin.setText("");
                            etPinRepetir.setText("");
                            linearHuella.setVisibility(View.GONE);
                        }
                    }, 1500);

                }

            }


    }

}
