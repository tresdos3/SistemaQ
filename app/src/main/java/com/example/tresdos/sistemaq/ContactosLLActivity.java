package com.example.tresdos.sistemaq;

import android.*;
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

import static com.example.tresdos.sistemaq.Main2Activity.RequestPermissionCode;

public class ContactosLLActivity extends AppCompatActivity {
    private ArrayList<String> nombres = new ArrayList<>();
    private ArrayList<String> numeros = new ArrayList<>();
    private ArrayList<String> keys = new ArrayList<>();
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
        setContentView(R.layout.activity_contactos_ll);
        intent = new Intent(str_receiver);
        ListaView = (ListView) findViewById(R.id.contactosP);
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
        HijosRef.child("hijos").child(texto).child("contactos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nombres.clear();
                numeros.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    contact hijo = postSnapshot.getValue(contact.class);
                    keys.add(postSnapshot.getKey());
                    nombres.add(hijo.getNombre());
                    numeros.add(hijo.getNumero());
                }
                Adapter adapter = new Adapter(ContactosLLActivity.this);
                ListaView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ContactosLLActivity.this, "Error al conectar base de datos", Toast.LENGTH_SHORT).show();
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
            return nombres.size();
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
                listViewHolder = new ContactosLLActivity.Valor();
                view = layoutInflater.inflate(R.layout.itemhijo, viewGroup, false);

                listViewHolder.txtnombre = (TextView) view.findViewById(R.id.txtTelN);
                listViewHolder.txtnumero = (TextView) view.findViewById(R.id.txtTelNum);
                listViewHolder.txtkey = (TextView) view.findViewById(R.id.txtTelkey);
                listViewHolder.llamada = (ImageView) view.findViewById(R.id.txtllamada);
                listViewHolder.whatsapp = (ImageView) view.findViewById(R.id.txtwhatsapp);
                view.setTag(listViewHolder);
            } else {
                listViewHolder = (Valor) view.getTag();
            }
            listViewHolder.txtnombre.setText(nombres.get(i));
            listViewHolder.txtnumero.setText(numeros.get(i));
            listViewHolder.txtkey.setText(keys.get(i));
            listViewHolder.llamada.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ContactosLLActivity.this, "SMS: " + numeros.get(i), Toast.LENGTH_SHORT).show();
                    String toNumber = numeros.get(i); // contains spaces.
                    boolean isWhatsappInstalled = estaInstalado("com.whatsapp");
                    if (isWhatsappInstalled) {
                        toNumber = toNumber.replace("+", "").replace(" ", "");
                        Intent sendIntent = new Intent("android.intent.action.MAIN");
                        sendIntent.putExtra("jid", toNumber + "@s.whatsapp.net");
                        sendIntent.putExtra(Intent.EXTRA_TEXT, "SistemaQ Mensaje: "+ nombres.get(i));
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.setPackage("com.whatsapp");
                        sendIntent.setType("text/plain");
                        startActivity(sendIntent);
                    }
                    else {
                        Uri uri = Uri.parse("market://details?id=com.whatsapp");
                        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                        Toast.makeText(ContactosLLActivity.this, "Usted no tiene Whatsapp installado", Toast.LENGTH_SHORT).show();
                        startActivity(goToMarket);
                    }

//
                }
            });
            listViewHolder.whatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(ContactosLLActivity.this, "Llamando: "+ numeros.get(i), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + numeros.get(i)));
                    if (ActivityCompat.checkSelfPermission(ContactosLLActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(intent);
                }
            });
            return view;
        }
    }
    static  class Valor{
        TextView txtnombre, txtnumero, txtkey;
        ImageView llamada, whatsapp;
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
