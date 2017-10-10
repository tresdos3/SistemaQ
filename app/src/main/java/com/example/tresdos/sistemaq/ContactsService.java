package com.example.tresdos.sistemaq;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Process;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.support.annotation.IntDef;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class ContactsService extends Service {
    TimerTask timerTask;
    ArrayList<String> Nombres = new ArrayList<String>();
    ArrayList<String> Numeros = new ArrayList<String>();
    HashMap<String, String> names = new HashMap<>();
    private DatabaseReference rootRef,HijosRef;
    public static String str_receiver = "servicetutorial.service.receiver";
    String name, phonenumber ;
    private FirebaseAuth auth;
    Intent intent;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Huid = "HuidKey";
    Cursor cursor ;
    public ContactsService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        intent = new Intent(str_receiver);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        HijosRef = rootRef.child(user.getUid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer  =  new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String texto = sharedpreferences.getString(Huid,"");
                boolean connected = false;
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    Log.d("Internet", "Existe Conexion");
                    HijosRef.child("hijos").child(texto).child("contactos").removeValue();
                    cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null, null, null);

                    while (cursor.moveToNext()) {

                        name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                        phonenumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contact nuevo = new contact(name, phonenumber);
                        HijosRef.child("hijos").child(texto).child("contactos").push().setValue(nuevo);
//                        names.put(name, phonenumber);
                    }

                    cursor.close();
                }
                else{
                    Log.d("Internet", "No Existe Conexion");
                }
            }
        },0, 150000);
        return START_STICKY;
    }
    @Override
    public void onDestroy() {
        android.os.Process.killProcess(Process.myPid());
    }
}
