package com.example.tresdos.sistemaq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
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

import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapasActivity extends AppCompatActivity {

    private DatabaseReference rootRef,HijosRef;
    private FirebaseAuth auth;
    private ArrayList<String> nombre = new ArrayList<>();
    private ArrayList<String> estado = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();
    private ArrayList<String> alertas = new ArrayList<>();
    private ArrayList<String> fechas = new ArrayList<>();
    SharedPreferences sharedpreferences;
    public static final String mypreference = "mypref";
    public static final String Huid = "HuidKey";
    private ListView Lista;
    TextView txtnombre,txtestado, txtkey;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapas);

        Lista = (ListView) findViewById(R.id.apps);
        txtnombre = (TextView) findViewById(R.id.list_app_name);
        txtestado = (TextView) findViewById(R.id.list_app_name2);
        txtkey = (TextView) findViewById(R.id.list_key);
        auth = FirebaseAuth.getInstance();
        sharedpreferences = getSharedPreferences(mypreference,
                Context.MODE_PRIVATE);
        FirebaseUser user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        HijosRef = rootRef.child(user.getUid());

        cargar();
        Lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(MapasActivity.this, keys.get(i), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(),UbicacionActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("key", keys.get(i));
                startActivity(intent);
            }
        });


    }
    private  void cargar(){

        HijosRef.child("hijos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nombre.clear();
                estado.clear();
                keys.clear();
                alertas.clear();
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    hijos hijo = postSnapshot.getValue(hijos.class);
                    keys.add(postSnapshot.getKey());
                    nombre.add(hijo.getNombre());
                    estado.add(hijo.getEstado());
                    alertas.add(hijo.getAlerta());
                    fechas.add(hijo.getFecha());
                }
                Adapter thisadapter = new Adapter(MapasActivity.this);
                Lista.setAdapter(thisadapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapasActivity.this, "Error al conectar base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }
    class Adapter extends BaseAdapter{
        private LayoutInflater layoutInflater;
        private Adapter(Context context) {
            layoutInflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return nombre.size();
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
            if(view == null){
                listViewHolder = new Valor();
                view = layoutInflater.inflate(R.layout.viewlist, viewGroup, false);

                listViewHolder.txtnombre = (TextView)view.findViewById(R.id.list_app_name);
                listViewHolder.txtestado = (TextView) view.findViewById(R.id.list_app_name2);
                listViewHolder.txtkey = (TextView)view.findViewById(R.id.list_key);
                listViewHolder.img = (ImageView) view.findViewById(R.id.app_alert);
                listViewHolder.img.setVisibility(view.INVISIBLE);
                view.setTag(listViewHolder);
            }
            else{
                listViewHolder = (Valor) view.getTag();
            }
            listViewHolder.txtnombre.setText(nombre.get(i));
            String estados = estado.get(i);
            if (!estados.equals("no")){
                listViewHolder.txtestado.setText("Activo");
            }
            else{
                listViewHolder.txtestado.setText("No Activo");
            }
            listViewHolder.txtkey.setText(keys.get(i));
            String vall = alertas.get(i);
            if (vall.equals("si")){
                listViewHolder.img.setVisibility(View.VISIBLE);
            }
//            view = getLayoutInflater().inflate(R.layout.viewlist,null);
//            txtnombre = (TextView) findViewById(R.id.list_app_name);
//            txtestado = (TextView) findViewById(R.id.list_app_name2);
//            txtkey = (TextView) findViewById(R.id.list_key);
//            txtnombre.setText(nombre.get(i));
//            txtestado.setText(estado.get(i));
//            txtkey.setText(keys.get(i));
            return view;
        }
    }
    static  class Valor{
        TextView txtnombre, txtestado, txtkey;
        ImageView img;
    }
}
