package com.example.tresdos.sistemaq;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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

import java.util.Calendar;
import java.util.Date;

public class EmergencyService extends Service implements SensorEventListener, LocationListener{

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private float mAccel;
    private float mAccelCurrent;
    private float mAccelLast;
    private DatabaseReference rootRef,HijosRef;
    private FirebaseAuth auth;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Huid = "HuidKey";
    public static final String UrlNoti = "http://138.197.27.208/hijoNotificacion/";
    public String tokenP;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude,longitude;
    LocationManager locationManager;
    Location location;

    public EmergencyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager
                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer,
                SensorManager.SENSOR_DELAY_UI, new Handler());
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        HijosRef = rootRef.child(user.getUid());
        return START_STICKY;
    }

//region [Sensores]
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        mAccelLast = mAccelCurrent;
        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;

        if (mAccel > 15) {
            showNotification();
            guardarE();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private void showNotification() {
        final NotificationManager mgr = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder note = new NotificationCompat.Builder(this);
        note.setContentTitle("SistemaQ");
        note.setContentText("Tus padres seran notificados");
        note.setContentInfo("Enviando..");
        note.setAutoCancel(true);
        note.setDefaults(Notification.DEFAULT_ALL);
        note.setSmallIcon(R.mipmap.ic_launcher);
        mgr.notify(101, note.build());
    }
    private void guardarE(){
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        HijosRef = rootRef.child(user.getUid());
        String texto = sharedpreferences.getString(Huid,"");
//        maps nuevo = new maps(location.getLatitude(),location.getLongitude());
        HijosRef.child("hijos").child(texto).child("emergencia").child("latitud").setValue("");
        HijosRef.child("hijos").child(texto).child("emergencia").child("longitud").setValue("");
        HijosRef.child("hijos").child(texto).child("alerta").setValue("no");
        HijosRef.child("hijos").child(texto).child("fecha").setValue("");
        rootRef.child(user.getUid()).child("tokenP").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tokenP = dataSnapshot.getValue(String.class);
                EnviarNot(tokenP);
                fn_getlocation();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
//        String TokenPadre = "dbezkT_Y3uw:APA91bHS1LL8pHQrvdHJa76TAgH7vdYe7xa4m4PIx0kEHHIZXXbPaqAxJL5ES8iz_GwsllxFXxxjm8Ph2L9jXhTeM_iJcbZvqq0C6RTsgxGZfCr46vRqfRnh_6d7JOp7QaezOMJ1ygUA";

    }
    @Override
    public void onDestroy() {
        android.os.Process.killProcess(Process.myPid());
    }
    //endregion
//    region[Enviar Notificacion]
    public void EnviarNot(String TokenPadre){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, UrlNoti + TokenPadre,
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
//    endregion
//    region[ubicacion]
private void fn_getlocation(){
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
    }
    locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
    isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    if (!isGPSEnable && !isNetworkEnable){

    }else {
        if (isGPSEnable){
            location = null;
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
            if (locationManager!=null){
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location!=null){
                    Log.d("latitude2",location.getLatitude()+"");
                    Log.d("longitude2",location.getLongitude()+"");
                    auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    rootRef = FirebaseDatabase.getInstance().getReference();
                    HijosRef = rootRef.child(user.getUid());
                    String texto = sharedpreferences.getString(Huid,"");
                    HijosRef.child("hijos").child(texto).child("emergencia").child("latitud").setValue(location.getLatitude());
                    HijosRef.child("hijos").child(texto).child("emergencia").child("longitud").setValue(location.getLongitude());
                    HijosRef.child("hijos").child(texto).child("alerta").setValue("si");
                    HijosRef.child("hijos").child(texto).child("fecha").setValue(new Date().toString());
                    latitude = location.getLatitude();
                    Log.d("DatosL", "fn_getlocation: " + location.getLatitude());
                    longitude = location.getLongitude();
                    Log.d("DatosL", "fn_getlocation: " + location.getLongitude());
                }
            }
        }


    }

}
//    endregion

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
