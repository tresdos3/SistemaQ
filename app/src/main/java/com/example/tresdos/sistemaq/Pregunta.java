package com.example.tresdos.sistemaq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Pregunta extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    Button btnPadre, btnHijo;
    TextView txttipo;
//    varibles sharedpreferences
    public static final String mypreference = "mypref";
//    tipo p=padre h=hijo n=ninguno
    public static final String Tipo = "tipoKey";
    public static final String Session = "SessionKey";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pregunta);
        btnPadre = (Button) findViewById(R.id.btnpadre);
        btnHijo = (Button) findViewById(R.id.btnhijo);
        txttipo = (TextView) findViewById(R.id.txttipo);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        tengoInternet();
        if (sharedpreferences.contains(Tipo)) {
            String texto = sharedpreferences.getString(Tipo,"");
            if (texto.equals("p")){
                Intent i = new Intent(Pregunta.this, LoginActivity.class);
                finish();
                startActivity(i);
            }
            if (texto.equals("h")){
                if (sharedpreferences.contains(Session)){
                    String texto1 = sharedpreferences.getString(Session,"");
                    if (texto1.equals("si")){
                        Intent i = new Intent(Pregunta.this, Main2Activity.class);
                        finish();
                        startActivity(i);
                    }
                }
            }
        }

        btnPadre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Tipo, "p");
                editor.commit();
                if (sharedpreferences.contains(Tipo)) {
                    txttipo.setText(sharedpreferences.getString(Tipo, ""));
                }
                Intent i = new Intent(Pregunta.this, LoginActivity.class);
                finish();
                startActivity(i);
            }
        });
        btnHijo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Tipo, "h");
                editor.commit();
                if (sharedpreferences.contains(Tipo)) {
                    txttipo.setText(sharedpreferences.getString(Tipo, ""));
                }
                Intent i = new Intent(Pregunta.this, Login2Activity.class);
                finish();
                startActivity(i);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        tengoInternet();
    }

    private void tengoInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            Log.d("Internet", "Existe Conexion");
        }
        else{
            Alerta();
        }
    }
    private void Alerta(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Sin Conexion...")
                .setContentText("Activa tu conexion a internet...")
                .setConfirmText("Activar")
                .setCancelText("Cancelar")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(dialogIntent);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        finish();
                        System.exit(0);
                    }
                })
                .show();
    }
}
