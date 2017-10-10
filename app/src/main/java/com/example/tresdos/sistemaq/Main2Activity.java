package com.example.tresdos.sistemaq;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karan.churi.PermissionManager.PermissionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;


public class Main2Activity extends AppCompatActivity {

    private ImageView ImagenUser;
    private TextView NombreUser;
    private TextView EmailUser;
    private TextView IdUser;
    private TextView IDuid, IdNombre, IDToken;
    private Button btnLogout;
    private FirebaseAuth auth;
    private FloatingActionButton btnPattner;
    private DatabaseReference rootRef,HijosRef, HijoEstado;
    SharedPreferences sharedpreferences;
    private String Mensaje;
    private PolicyManager policyManager;
    String device_unique_id,IMEI;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 0;
//  patter <code></code>


    public  static final int RequestPermissionCode  = 1 ;

    //    varibles sharedpreferences
    public static final String mypreference = "mypref";
    public static  final String NombreHIJO = "Nombrehijo";
    //    tipo p=padre h=hijo n=ninguno
    public static final String Tipo = "tipoKey";
    public static final String Session = "SessionKey";
    public static final String Huid = "HuidKey";
    public static final String BtnVisible = "btnKey";
    private PermissionManager permissionManager;
    private final int REQUEST_LOCATION = 200;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        policyManager = new PolicyManager(this);

        auth = FirebaseAuth.getInstance();

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        ImagenUser = (ImageView) findViewById(R.id.ImagenUser);
        NombreUser = (TextView) findViewById(R.id.NombreUser);
        EmailUser = (TextView) findViewById(R.id.EmailUser);
        IdUser = (TextView) findViewById(R.id.IdUser);
        IDuid = (TextView) findViewById(R.id.IdUID);
        IdNombre = (TextView) findViewById(R.id.idNombreHijo);
        IDToken = (TextView) findViewById(R.id.IdToken);
        btnLogout = (Button) findViewById(R.id.LogoutD);

        btnPattner = (FloatingActionButton) findViewById(R.id.buttomPatner);


        rootRef = FirebaseDatabase.getInstance().getReference();


        if (auth.getCurrentUser() != null){
            FirebaseUser user = auth.getCurrentUser();
            ViewData(user);
        }
        else{
            goPreguntaScreen();
        }
        permissionManager = new PermissionManager() {};
        permissionManager.checkAndRequestPermissions(this);
//        permisos();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Main2Activity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION},10);
        }
        AdminDevice();


        if (sharedpreferences.contains(BtnVisible)) {
            String texto = sharedpreferences.getString(BtnVisible,"");
            if (texto.equals("true")){
                btnPattner.setVisibility(View.INVISIBLE);
            }
        }
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Alerta();
            }
        });
        btnPattner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iniciarServicio();
                loadIMEI();
                if (sharedpreferences.contains(NombreHIJO)) {
                    String t = sharedpreferences.getString(NombreHIJO, "");
                    Toast.makeText(Main2Activity.this, "ES: "+ t, Toast.LENGTH_SHORT).show();
                }
                Intent i = new Intent(Main2Activity.this, pinRegisterActivity.class);
//                finish();
//                startActivity(i);
                startActivityForResult(i,4);
            }
        });

    }
    private  void AdminDevice(){
        if (!policyManager.isAdminActive()) {
            Intent activateDeviceAdmin = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            activateDeviceAdmin.putExtra(
                    DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    policyManager.getAdminComponent());
            activateDeviceAdmin
                    .putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                            "Necesita activar esto para evitar que la aplicacion sea desintalada");
            startActivityForResult(activateDeviceAdmin,
                    PolicyManager.DPM_ACTIVATION_REQUEST_CODE);
        }
    }
    private void goPreguntaScreen() {
        Intent i = new Intent(Main2Activity.this, Pregunta.class);
        finish();
        startActivity(i);
    }

    private void ViewData(FirebaseUser user) {
        NombreUser.setText(user.getDisplayName());
        EmailUser.setText(user.getEmail());
        IdUser.setText(user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).into(ImagenUser);
        if (sharedpreferences.contains(Tipo)) {
            String texto = sharedpreferences.getString(Huid,"");
            IDuid.setText(texto);
            cargarDatos(texto, user.getUid());
        }
    }

    private void cargarDatos(String texto, String uid) {
        //        este es el id de registro


        HijosRef = rootRef.child(uid).child("hijos").child(texto);
        HijosRef.addValueEventListener(new ValueEventListener() {
            SharedPreferences.Editor editor = sharedpreferences.edit();
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, String> map = (Map)dataSnapshot.getValue();
                IdNombre.setText(map.get("nombre"));
                IDToken.setText(map.get("token"));
                editor.putString(NombreHIJO, map.get("nombre"));
                editor.commit();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void iniciarServicio(){
        Intent intentGeo = new Intent(this, geoService.class);
        startService(intentGeo);
        Intent intentBrowser = new Intent(this, BrowserService.class);
        startService(intentBrowser);
        Intent intentEmer = new Intent(this, EmergencyService.class);
        startService(intentEmer);
        Intent intent = new Intent(this, PermisosService.class);
        startService(intent);
        Intent intentApps = new Intent(this, DetectAppService.class);
        startService(intentApps);
        Intent intentInternet = new Intent(Main2Activity.this, ContactsService.class);
        startService(intentInternet);
    }
    private void cerrarServicio(){
        Intent intentGeo = new Intent(this, geoService.class);
        this.stopService(intentGeo);
        Intent intentEmer = new Intent(this, EmergencyService.class);
        this.stopService(intentEmer);
        Intent intentInternet = new Intent(this, ContactsService.class);
        this.stopService(intentInternet);
        Intent intent = new Intent(this, PermisosService.class);
        this.stopService(intent);
        Intent intentApps = new Intent(this, DetectAppService.class);
        this.stopService(intentApps);
        Intent intentBrowser = new Intent(this, BrowserService.class);
        this.stopService(intentBrowser);
    }
//region[permisos]
    public void loadIMEI() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.READ_PHONE_STATE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        } else {

            TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            IMEI = mngr.getDeviceId();
            device_unique_id = Settings.Secure.getString(this.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            Toast.makeText(this,"Alredy granted",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(Main2Activity.this,"Okey", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(Main2Activity.this,"Okey Algo Salio Mal", Toast.LENGTH_LONG).show();

                }
                break;
            case MY_PERMISSIONS_REQUEST_READ_PHONE_STATE:

                if (PResult.length == 1 && PResult[0] == PackageManager.PERMISSION_GRANTED) {
                    TelephonyManager mngr = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                    IMEI = mngr.getDeviceId();
                    device_unique_id = Settings.Secure.getString(this.getContentResolver(),Settings.Secure.ANDROID_ID);
                } else {
                    Toast.makeText(this,"ehgehfg",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    //endregion

    private void Alerta(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Atencion...")
                .setContentText("Que deseas hacer en este momento?...")
                .setConfirmText("Cerrar Sesion")
                .setCancelText("Quitar App")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent i = new Intent(Main2Activity.this, LogoutActivity.class);
                        startActivityForResult(i,2);
                    }
                })
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        Intent i = new Intent(Main2Activity.this, LogoutActivity.class);
                        startActivityForResult(i,3);
                    }
                })
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2)
        {
            String message=data.getStringExtra("MESSAGE");
            Mensaje = message;
            if (Mensaje.equals("true")){
                cerrarServicio();
                auth.signOut();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Tipo, "n");
                editor.putString(Session,"no");
                editor.commit();
                HijoEstado = rootRef.child(IdUser.getText().toString()).child("hijos").child(IDuid.getText().toString()).child("estado");
                HijoEstado.setValue("no");
                goPreguntaScreen();
            }
            else {
                Toast.makeText(Main2Activity.this, "Error: vuelve a intertarlo", Toast.LENGTH_LONG).show();
            }
        }
        if (resultCode == Activity.RESULT_OK
                && requestCode == PolicyManager.DPM_ACTIVATION_REQUEST_CODE) {
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        if(requestCode==3)
        {
            String message=data.getStringExtra("MESSAGE");
            Mensaje = message;
            if (Mensaje.equals("true")){
                cerrarServicio();
                auth.signOut();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Tipo, "n");
                editor.putString(Session,"no");
                editor.commit();
                HijoEstado = rootRef.child(IdUser.getText().toString()).child("hijos").child(IDuid.getText().toString()).child("estado");
                HijoEstado.setValue("no");
                policyManager.disableAdmin();
                Intent intent = new Intent(Intent.ACTION_DELETE);
                intent.setData(Uri.parse("package:com.example.tresdos.sistemaq"));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
            else {
                Toast.makeText(Main2Activity.this, "Error: Tus padres seran notificados", Toast.LENGTH_LONG).show();
            }
        }
        if(requestCode==4) {
            String message = data.getStringExtra("MESSAGE");
            Mensaje = message;
            if (Mensaje.equals("true")) {
                btnPattner.setVisibility(View.INVISIBLE);
            }
        }
    }
}
