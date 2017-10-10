package com.example.tresdos.sistemaq;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.michaldrabik.tapbarmenulib.TapBarMenu;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.tresdos.sistemaq.Main2Activity.RequestPermissionCode;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{

    @Bind(R.id.tapBarMenu) TapBarMenu tapBarMenu;
    private ImageView photoImageView;
    private TextView nameTextView;
    private TextView emailTextView;
    private TextView idTextView;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    SharedPreferences sharedpreferences;
    //    varibles sharedpreferences
    public static final String mypreference = "mypref";
    //    tipo p=padre h=hijo n=ninguno
    public static final String Tipo = "tipoKey";
    private DatabaseReference rootRef,HijosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        ButterKnife.bind(this);
        nameTextView = (TextView) findViewById(R.id.nameTextView);
        emailTextView = (TextView) findViewById(R.id.emailTextView);
       idTextView = (TextView) findViewById(R.id.idTextView);
        photoImageView = (ImageView) findViewById(R.id.photoImageView);
        rootRef = FirebaseDatabase.getInstance().getReference();;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent intent = new Intent(this, PermisosService.class);
        startService(intent);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    HijosRef = rootRef.child(user.getUid());
                    String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                    HijosRef.child("tokenP").setValue(refreshedToken);
                    setUserData(user);
                }
                else {
                    goLoginScreen();
                }
            }
        };

    }

    private void setUserData(FirebaseUser user) {
        nameTextView.setText(user.getDisplayName());
        emailTextView.setText(user.getEmail());
        idTextView.setText(user.getUid());
        Glide.with(this).load(user.getPhotoUrl()).into(photoImageView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void goLoginScreen() {
        Intent intent = new Intent(this, Pregunta.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
    public void logOut(View view) {
        firebaseAuth.signOut();

        Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString(Tipo, "n");
                    editor.commit();
                    goLoginScreen();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.not_close_session, Toast.LENGTH_SHORT).show();
                }
            }
        });
        Auth.GoogleSignInApi.revokeAccess(googleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    goLoginScreen();
                } else {
                }
            }
        });
    }

    protected void onStop() {
        super.onStop();
        if (authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
    @OnClick(R.id.tapBarMenu) public void onMenuButtonClick() {
        tapBarMenu.toggle();
    }

    @OnClick({ R.id.item1, R.id.item2, R.id.item3, R.id.item4 }) public void onMenuItemClick(View view) {
        tapBarMenu.close();
        switch (view.getId()) {
            case R.id.item1:
                Intent i = new Intent(MainActivity.this, RegistroHActivity.class);
                startActivity(i);
                break;
            case R.id.item2:
                Intent v = new Intent(MainActivity.this, MapasActivity.class);
                startActivity(v);
                break;
            case R.id.item3:
//                Toast.makeText(MainActivity.this, "Item 3 selected", Toast.LENGTH_SHORT).show();
//                Log.i("TAG", "Item 3 selected");
                Intent d = new Intent(MainActivity.this, BrowserActivity.class);
                startActivity(d);
                break;
            case R.id.item4:
                Intent c = new Intent(MainActivity.this, CardsHijosActivity.class);
                startActivity(c);
//                Toast.makeText(MainActivity.this, "Item 4 selected", Toast.LENGTH_SHORT).show();
//                Log.i("TAG", "Item 4 selected");
                break;
        }
    }
    //region[permisos]
    public void permisos(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(
                MainActivity.this,
                Manifest.permission.CALL_PHONE))
        {

            Toast.makeText(MainActivity.this,"Okey", Toast.LENGTH_LONG).show();

        } else {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                    Manifest.permission.CALL_PHONE}, RequestPermissionCode);

        }
    }
    @Override
    public void onRequestPermissionsResult(int RC, String per[], int[] PResult) {

        switch (RC) {

            case RequestPermissionCode:

                if (PResult.length > 0 && PResult[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(MainActivity.this,"Okey", Toast.LENGTH_LONG).show();

                } else {

                    Toast.makeText(MainActivity.this,"Okey Algo Salio Mal", Toast.LENGTH_LONG).show();

                }
                break;
        }
    }

    //endregion
}

