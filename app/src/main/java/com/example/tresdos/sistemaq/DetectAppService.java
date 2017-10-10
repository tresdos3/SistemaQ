package com.example.tresdos.sistemaq;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.android.processes.AndroidProcesses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DetectAppService extends Service {
    private DatabaseReference rootRef,HijosRef;
    private FirebaseAuth auth;
    public String tokenP;
    public String ProcesoR;
    public String Nombre;
    public static final String UrlNoti = "http://138.197.27.208/NotificacionAplicacion";
    private static final String TAG = DetectAppService.class.getSimpleName();
    SharedPreferences sharedpreferences;
    //    varibles sharedpreferences
    public static final String mypreference = "mypref";
    public static  final String NombreHIJO = "Nombrehijo";
    //    tipo p=padre h=hijo n=ninguno
    public static final String UltimoNotificado2 = "ultimoKey";
    public static final String Huid = "HuidKey";
    TimerTask timerTask;
    List<String> listanegra = new ArrayList<String>(
            Arrays.asList("com.wo.voice", "com.supercell.clashroyale")
    );
    public DetectAppService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public void onCreate() {

        super.onCreate();
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(UltimoNotificado2, "prueba");
        editor.commit();

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        HijosRef = rootRef.child(user.getUid());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                final List<ActivityManager.RunningAppProcessInfo> processes = AndroidProcesses.getRunningAppProcessInfo(getApplicationContext());
                for (int i = 0; i< processes.size();i++){
                    final String proceso = processes.get(i).processName;

//                    Log.d(TAG, "run: "+ processes.get(i).processName + " PID: " + processes.get(i).pid);
                    for (int v = 0; v <listanegra.size(); v++){
                        String Lista = listanegra.get(v);
                        String texto = sharedpreferences.getString(UltimoNotificado2,"");
                        if (proceso.contains(Lista)){
                            if (!proceso.contains(texto)){
                                auth = FirebaseAuth.getInstance();
                                FirebaseUser user = auth.getCurrentUser();
                                rootRef = FirebaseDatabase.getInstance().getReference();
                                HijosRef = rootRef.child(user.getUid());
                                ProcesoR= processes.get(i).processName;
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(UltimoNotificado2, processes.get(i).processName);
                                editor.commit();
                                rootRef.child(user.getUid()).child("tokenP").addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (sharedpreferences.contains(Huid)) {
                                            String t =sharedpreferences.getString(Huid, "");
                                        }
                                        else{
                                            Log.d("Token Hijo", "No Existe Token");
                                        }
                                        if (sharedpreferences.contains(NombreHIJO)) {
                                            Nombre =sharedpreferences.getString(NombreHIJO, "");
                                        }
                                        tokenP = dataSnapshot.getValue(String.class);
                                        EnviarNot(tokenP,ProcesoR, Nombre);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
//                            android.os.Process.killProcess(processes.get(i).pid);
                            Log.d("App", "La siguiente app se esta ejecutando "+processes.get(i).processName);
//                            Log.d("Mensaje", "run: Desea Cerrar esta aplicacion");
                        }
                    }
                }
                processes.clear();
            }
        },2000,3000);
        return START_STICKY;
    }
    public void EnviarNot(String TokenPadre , String App, String Nombre){
        String Datos ="?App="+App+"&Token="+TokenPadre+"&Hijo="+Nombre;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlNoti +Datos,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}
