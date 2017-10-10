package com.example.tresdos.sistemaq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.roger.catloadinglibrary.CatLoadingView;

import java.util.ArrayList;
import java.util.List;

public class SeleccionActivity extends AppCompatActivity {

    private EditText editText;
    private Button Registrar, btnSeleccionar;
    private DatabaseReference rootRef,HijosRef, ListaRef;
    private List<String> listaHijos = new ArrayList<>();
    private List<String> listaUID = new ArrayList<>();
    private List<String> listaTodo = new ArrayList<>();
    private CatLoadingView mView;
    private FirebaseAuth auth;
    private MaterialSpinner ListaSpiner;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    //    tipo p=padre h=hijo n=ninguno
    public static final String Session = "SessionKey";
    public static final String HNombre = "HNombreKey";
    public static final String Huid = "HuidKey";
    public static final String HToken = "HTokenKey";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();

        mView = new CatLoadingView();
//        editText = (EditText) findViewById(R.id.txtNombreH);
//        Registrar = (Button) findViewById(R.id.btnRegistro);
        btnSeleccionar = (Button) findViewById(R.id.btnSeleccionar);
        ListaSpiner = (MaterialSpinner) findViewById(R.id.ListaHijos);

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);

        rootRef = FirebaseDatabase.getInstance().getReference();
//        este es el id de registro
        HijosRef = rootRef.child(user.getUid());



//        Log.d("Token", "Refreshed token: " + refreshedToken);

        GetHijos();

        Log.d("aaa", "onCreate: esto llego aqui");

//        Registrar.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hijos nuevo = new hijos(editText.getText().toString(), refreshedToken);
//                HijosRef.child("hijos").push().setValue(nuevo);
//            }
//        });
        btnSeleccionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
                Toast.makeText(SeleccionActivity.this, "Seleccionado: "+ ListaSpiner.getText(), Toast.LENGTH_SHORT).show();
                int v = ListaSpiner.getSelectedIndex();
                HijosRef.child("hijos").child(listaUID.get(v)).child("token").setValue(refreshedToken);
                HijosRef.child("hijos").child(listaUID.get(v)).child("estado").setValue("si");
                HijosRef.child("hijos").child(listaUID.get(v)).child("alerta").setValue("no");
                HijosRef.child("hijos").child(listaUID.get(v)).child("SDK").setValue(Build.VERSION.SDK_INT);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putString(Session, "si");
                editor.putString(Huid, listaUID.get(v));
                editor.commit();
                Intent i = new Intent(SeleccionActivity.this, Main2Activity.class);
                finish();
                startActivity(i);
            }
        });

    }
    private void GetHijos(){
//        mView.show(getSupportFragmentManager(), "");
        HijosRef.child("hijos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                listaHijos.clear();
                listaUID.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    hijos hijo = postSnapshot.getValue(hijos.class);
                    if (hijo.getEstado().equals("no")){
                        listaUID.add(postSnapshot.getKey());
                        listaHijos.add(hijo.getNombre());
                    }
                }
                ListaSpiner.setItems(listaHijos);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(SeleccionActivity.this, "Error al conectar base de datos", Toast.LENGTH_SHORT).show();
            }
        });
//        mView.isHidden();
    }

}
