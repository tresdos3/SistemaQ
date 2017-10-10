package com.example.tresdos.sistemaq;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.roger.catloadinglibrary.CatLoadingView;

public class Login2Activity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText txtEmail, txtpassword;
    private Button btnIniciar;
    private CatLoadingView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        txtEmail = (EditText) findViewById(R.id.txtemail);
        txtpassword = (EditText) findViewById(R.id.txtpassword);
        btnIniciar = (Button) findViewById(R.id.btnIniciar);
        mView = new CatLoadingView();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null){
            Intent i = new Intent(Login2Activity.this, SeleccionActivity.class);
            startActivity(i);
        }
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(Login2Activity.this, "Click",Toast.LENGTH_SHORT).show();
                mView.show(getSupportFragmentManager(), "");
                LoginUsuario(txtEmail.getText().toString(), txtpassword.getText().toString());
                mView.isHidden();
            }
        });
    }

    private void LoginUsuario(final String email, final String password) {
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(Login2Activity.this, "Haz Iniciado Session "+task.isSuccessful(),Toast.LENGTH_SHORT).show();
                        if (!task.isSuccessful()){
                            if (email.equals("")){
                                Toast.makeText(Login2Activity.this, "Debes llenar el campo email",Toast.LENGTH_SHORT).show();
                            }
                            if (password.length() < 6){
                                Toast.makeText(Login2Activity.this, "Contracena demasiada corta",Toast.LENGTH_SHORT).show();
                            }
                            if (password.equals("")){
                                Toast.makeText(Login2Activity.this, "Debes llenar el campo password",Toast.LENGTH_SHORT).show();
                            }
                            else{
//                                Log.d("Error", "onComplete: "+task.getException().toString());
//                                error de firebase comunmente conexion de internet
                                Toast.makeText(Login2Activity.this, task.getException().toString(),
                                        Toast.LENGTH_SHORT).show();
//
                            }
                        }
                        else{
                            Intent i = new Intent(Login2Activity.this, SeleccionActivity.class);
                            finish();
                            startActivity(i);
                        }
                    }
                });
    }

}
