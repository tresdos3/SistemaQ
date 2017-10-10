package com.example.tresdos.sistemaq;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

public class geoService extends Service implements LocationListener{

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude,longitude;
    LocationManager locationManager;
    Location location;
    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 10000;
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    private DatabaseReference rootRef,HijosRef;
    private FirebaseAuth auth;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Huid = "HuidKey";

    public geoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                Log.d("Servicio", "Ejecutando");
////                Log.d("Ubicacion","Lat: "+ latitud+" Long: "+ longitud);
//            }
//        },2000,3000);
//        return  START_STICKY;
//    }
@Override
    public void onCreate() {
        super.onCreate();

        mTimer = new Timer();
        mTimer.schedule(new TimerTaskToGetLocation(),5,notify_interval);
        intent = new Intent(str_receiver);
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        HijosRef = rootRef.child(user.getUid());
        String texto = sharedpreferences.getString(Huid,"");
//        maps nuevo = new maps(location.getLatitude(),location.getLongitude());
        HijosRef.child("hijos").child(texto).child("ubicacion").child("latitud").setValue("");
        HijosRef.child("hijos").child(texto).child("ubicacion").child("longitud").setValue("");
    //        fn_getlocation();
    }
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
    @Override
    public void onDestroy() {
        android.os.Process.killProcess(Process.myPid());
        Toast.makeText(this, "MyService Completed or Stopped.", Toast.LENGTH_SHORT).show();
    }
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,7000,0,this);
                if (locationManager!=null){
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location!=null){
                        auth = FirebaseAuth.getInstance();
                        FirebaseUser user = auth.getCurrentUser();
                        rootRef = FirebaseDatabase.getInstance().getReference();
                        HijosRef = rootRef.child(user.getUid());
                        String texto = sharedpreferences.getString(Huid,"");
                        HijosRef.child("hijos").child(texto).child("ubicacion").child("latitud").setValue(location.getLatitude());
                        HijosRef.child("hijos").child(texto).child("ubicacion").child("longitud").setValue(location.getLongitude());
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
//                        fn_update(location);
                    }
                }
            }


        }

    }
    private class TimerTaskToGetLocation extends TimerTask{
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    fn_getlocation();
                }
            });

        }
    }
}
