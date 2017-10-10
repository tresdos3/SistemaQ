package com.example.tresdos.sistemaq;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import me.everything.providers.android.browser.Bookmark;
import me.everything.providers.android.browser.BrowserProvider;
import me.everything.providers.android.browser.Search;

public class BrowserActivity extends AppCompatActivity {

    private DatabaseReference rootRef,HijosRef;
    private FirebaseAuth auth;
    private ArrayList<String> nombre = new ArrayList<>();
    private ArrayList<Integer> estado = new ArrayList<>();
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
        setContentView(R.layout.activity_browser);
        Lista = (ListView) findViewById(R.id.apps_b);
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
                if (estado.get(i) >= 23){
                    versionSkd();
                }
                else{
                    Toast.makeText(BrowserActivity.this, "Funciona", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),HistoryActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("key", keys.get(i));
                    startActivity(intent);
                }
            }
        });
    }

    private void cargar() {
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
                    estado.add(hijo.getSdk());
                    alertas.add(hijo.getAlerta());
                    fechas.add(hijo.getFecha());
                }
                Adapter thisadapter = new Adapter(BrowserActivity.this);
                Lista.setAdapter(thisadapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(BrowserActivity.this, "Error al conectar base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void versionSkd(){
        new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Lo Sentimos...")
                .setContentText("Esta caracteristica solo esta disponible para Android Lollipop o versiones inferiores")
                .setConfirmText("Lo Entiendo")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
    class Adapter extends BaseAdapter {
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
            MapasActivity.Valor listViewHolder;
            if(view == null){
                listViewHolder = new MapasActivity.Valor();
                view = layoutInflater.inflate(R.layout.viewlist, viewGroup, false);

                listViewHolder.txtnombre = (TextView)view.findViewById(R.id.list_app_name);
                listViewHolder.txtestado = (TextView) view.findViewById(R.id.list_app_name2);
                listViewHolder.txtkey = (TextView)view.findViewById(R.id.list_key);
                listViewHolder.img = (ImageView) view.findViewById(R.id.app_alert);
                listViewHolder.img.setVisibility(view.INVISIBLE);
                view.setTag(listViewHolder);
            }
            else{
                listViewHolder = (MapasActivity.Valor) view.getTag();
            }
            listViewHolder.txtnombre.setText(nombre.get(i));
            String estados = estado.get(i).toString();
            if (estado.get(i) < 23){
                listViewHolder.txtestado.setText("Caracteristica Disponible");
            }
            else{
                listViewHolder.txtestado.setText("Caracteristica No Disponible");
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
