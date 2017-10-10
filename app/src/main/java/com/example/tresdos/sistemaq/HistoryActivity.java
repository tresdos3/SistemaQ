package com.example.tresdos.sistemaq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class HistoryActivity extends AppCompatActivity {

    private ArrayList<String> titulo = new ArrayList<>();
    private ArrayList<String> url = new ArrayList<>();
    private ArrayList<String> fecha_hora = new ArrayList<>();
    private DatabaseReference rootRef, HijosRef;
    private FirebaseAuth auth;
    private ListView ListaView;
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Huid = "HuidKey";
    public static String str_receiver = "servicetutorial.service.receiver";
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        intent = new Intent(str_receiver);
        ListaView = (ListView) findViewById(R.id.HistorialList);
        auth = FirebaseAuth.getInstance();

        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        String texto = sharedpreferences.getString(Huid, "");
        FirebaseUser user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        HijosRef = rootRef.child(user.getUid());
        cargar();
    }

    private void cargar() {
        String texto = getIntent().getExtras().getString("key");
        HijosRef.child("hijos").child(texto).child("historial").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                titulo.clear();
                url.clear();
                fecha_hora.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    browser hijo = postSnapshot.getValue(browser.class);
                    String T = hijo.getTitulo().substring(0,15) + "...";
                    titulo.add(T);
                    String U = hijo.getUrl().substring(0,21) + "...";
                    url.add(U);
                    fecha_hora.add(hijo.getFecha_Hora());
                }
                Adapter adapter = new Adapter(HistoryActivity.this);
                ListaView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HistoryActivity.this, "Error al conectar base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
    class Adapter extends BaseAdapter {
        private LayoutInflater layoutInflater;

        private Adapter(Context context) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return titulo.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            Valor listViewHolder;
            if (view == null) {
                listViewHolder = new HistoryActivity.Valor();
                view = layoutInflater.inflate(R.layout.viewlist2, viewGroup, false);
                listViewHolder.titulo = (TextView) view.findViewById(R.id.txtTitulo);
                listViewHolder.url = (TextView) view.findViewById(R.id.txtUrl);
                listViewHolder.fecha = (TextView) view.findViewById(R.id.txtFecha);
                view.setTag(listViewHolder);
            } else {
                listViewHolder = (Valor) view.getTag();
            }
            listViewHolder.titulo.setText(titulo.get(i));
            listViewHolder.url.setText(url.get(i));
            listViewHolder.fecha.setText(fecha_hora.get(i));
            return view;
        }
    }
    static  class Valor{
        TextView titulo, url, fecha;
    }
    private boolean estaInstalado(String uri) {
        PackageManager pm = getPackageManager();
        boolean app_installed = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
        }
        return app_installed;
    }
}
