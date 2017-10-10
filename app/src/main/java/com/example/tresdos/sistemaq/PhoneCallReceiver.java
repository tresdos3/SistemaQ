package com.example.tresdos.sistemaq;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
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

import java.util.Date;

/**
 * Created by tresdos on 11-07-17.
 */

public class PhoneCallReceiver extends BroadcastReceiver {

    private DatabaseReference rootRef,HijosRef;
    private FirebaseAuth auth;
    public String tokenP;
    public String Nombre;
    public String phoneNumber;
    public static final String UrlNoti = "http://138.197.27.208/NotificacionBrowser";
    SharedPreferences sharedpreferences;
    //    varibles sharedpreferences
    public static final String mypreference = "mypref";
    public static  final String NombreHIJO = "Nombrehijo";
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (intent.getAction().equals("android.intent.action.PHONE_STATE"))
            {
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
                if(state.equals(TelephonyManager.EXTRA_STATE_RINGING))
                {
                    TelephonyManager tmgr = (TelephonyManager) context
                            .getSystemService(Context.TELEPHONY_SERVICE);

                    auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    rootRef = FirebaseDatabase.getInstance().getReference();
                    HijosRef = rootRef.child(user.getUid());
                    Bundle bundle = intent.getExtras();
                    phoneNumber = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    rootRef.child(user.getUid()).child("tokenP").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (sharedpreferences.contains(NombreHIJO)) {
                                Nombre =sharedpreferences.getString(NombreHIJO, "");
                            }
                            tokenP = dataSnapshot.getValue(String.class);
                            Log.d("Llamada en proceso: ", "Numero: "+phoneNumber);
                            Log.d("Llamada en proceso: ", "Nombre: "+Nombre);
                            Log.d("Llamada en proceso: ", "Token: "+tokenP);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });



//                    String aaaa = bundle.getString(TelephonyManager.EXTRA_PHONE_ACCOUNT_HANDLE);



                }
            }
        } catch (Exception e) {
            Log.e("Phone Receive Error", " " + e);
        }
    }
}
