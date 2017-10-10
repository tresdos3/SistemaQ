package com.example.tresdos.sistemaq;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UbicacionActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference rootRef,HijosRef;
    private FirebaseAuth auth;
    private String Key;
    public double Lat, Long;
    private TextView txtlat, txtlong;
    maps mapas;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        txtlat= (TextView) findViewById(R.id.txtLat);
        txtlong= (TextView) findViewById(R.id.txtLong);
//        mapas = new maps();
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        HijosRef = rootRef.child(user.getUid());
//        name.setText(getIntent().getExtras().getString("name"));
        Key = getIntent().getExtras().getString("key");
        HijosRef.child("hijos").child(Key).child("alerta").setValue("no");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        HijosRef.child("hijos").child(Key).child("ubicacion").child("latitud").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Lat = dataSnapshot.getValue(Double.class);
                txtlat.setText(dataSnapshot.getValue(Double.class).toString());
                HijosRef.child("hijos").child(Key).child("ubicacion").child("longitud").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Long = dataSnapshot.getValue(Double.class);
                        txtlong.setText(dataSnapshot.getValue(Double.class).toString());
                        LatLng sydney = new LatLng(Lat,Long);
                        mMap.clear();
                        mMap.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.trekking))
                                .anchor(0.0f, 1.0f)
                                .title("Estoy aki")
                                .snippet("Tu hijo se encuentra aki...")
                                .position(sydney));
                        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 20));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
