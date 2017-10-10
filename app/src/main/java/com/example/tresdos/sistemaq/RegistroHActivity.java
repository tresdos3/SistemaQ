package com.example.tresdos.sistemaq;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.valdesekamdem.library.mdtoast.MDToast;

import butterknife.ButterKnife;

public class RegistroHActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnRegistrar;
    private DatabaseReference rootRef,HijosRef;
    private FirebaseAuth auth;
    private MDToast mdToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_h);
        editText = (EditText) findViewById(R.id.txtNombreH);
        btnRegistrar = (Button) findViewById(R.id.btnRegistro);
        ButterKnife.bind(this);
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
//        este es el id de registro
        HijosRef = rootRef.child(user.getUid());

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                si : usuario activo |||| no usuario no actico
                hijos nuevo = new hijos(editText.getText().toString(), "null","no","no");
                HijosRef.child("hijos").push().setValue(nuevo);
                mdToast = MDToast.makeText(RegistroHActivity.this, "Registrado correctamente", MDToast.LENGTH_LONG, mdToast.TYPE_SUCCESS);
                mdToast.show();
//                Toast.makeText(RegistroHActivity.this, "Registrado", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
