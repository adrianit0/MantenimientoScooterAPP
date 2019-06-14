package com.kidev.adrian.scooterappmantenimiento.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.kidev.adrian.scooterappmantenimiento.R;
import com.kidev.adrian.scooterappmantenimiento.interfaces.IOnInputDialog;
import com.kidev.adrian.scooterappmantenimiento.util.AndroidUtil;
import com.kidev.adrian.scooterappmantenimiento.interfaces.CallbackRespuesta;
import com.kidev.adrian.scooterappmantenimiento.util.ConectorTCP;
import com.kidev.adrian.scooterappmantenimiento.util.Util;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configuración previa de la IP
        String serverHost = ConectorTCP.getHostServerName();
        AndroidUtil.crearInputDialog(this, "Introducir IP", serverHost, new IOnInputDialog() {
            @Override
            public void onAccept(String message) {
                ConectorTCP.setHostServerName(message);
                ConectorTCP.getInstance();
            }

            @Override
            public void onCancel(String message) {
                ConectorTCP.getInstance();
            }
        });
        // Fin configuración de la IP

        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }

    private void login () {
        TextView textoEmail = findViewById(R.id.emailLogin);
        TextView textoPass = findViewById(R.id.passLogin);

        if (textoEmail.getText().toString().isEmpty()) {
            AndroidUtil.crearErrorDialog(this, getApplicationContext().getString(R.string.error_nick));
            return;
        } else if (textoPass.getText().toString().isEmpty()) {
            AndroidUtil.crearErrorDialog(this, getApplicationContext().getString(R.string.error_pass));
            return;
        }

        Map<String,String> parametros = new HashMap<>();
        parametros.put("email", textoEmail.getText().toString());
        parametros.put("pass", Util.getMd5(textoPass.getText().toString()));

        loginButton.setEnabled(false);

        final Activity activity = this;

        ConectorTCP.getInstance().realizarConexion(this,"loginAsEmpleado", parametros, new CallbackRespuesta() {
            @Override
            public void success(Map<String, String> contenido) {
                Intent intent = new Intent(getApplication(), MenuActivity.class);
                for (Map.Entry<String, String> entry : contenido.entrySet()) {
                    intent.putExtra(entry.getKey(), entry.getValue());
                }

                startActivity(intent);
            }

            @Override
            public void error(Map<String, String> contenido, Util.CODIGO codigoError) {
                AndroidUtil.crearErrorDialog(activity, contenido.get("error"));
                loginButton.setEnabled(true);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Nos salimos del programa
        finishAffinity();
    }
}
