/*
 * Proyecto final - Programación de Dispositivos Móviles (ISW211)
 * Bitácora de Lugares
 *
 * Integrantes:
 *   Emilio Perea Montes
 *   Benito Abi Ramírez Vázquez
 */
package com.aditusstudios.bitacoralugares;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar tlbPrincipal;
    private ListView lvwParadas;
    private TextView txtContador;
    private LinearLayout llyVacio;
    private ExtendedFloatingActionButton fabNuevaParada;
    private DatabaseHelper dbHelper;
    private ArrayList<Parada> paradas;
    private ParadaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_main);

        tlbPrincipal = findViewById(R.id.tlbPrincipal);
        lvwParadas = findViewById(R.id.lvwParadas);
        txtContador = findViewById(R.id.txtContador);
        llyVacio = findViewById(R.id.llyVacio);
        fabNuevaParada = findViewById(R.id.fabNuevaParada);

        fabNuevaParada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.aditusstudios.bitacoralugares",
                        "com.aditusstudios.bitacoralugares.FormularioActivity");
                startActivity(intent);
            }
        });

        setSupportActionBar(tlbPrincipal);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
                tlbPrincipal.setPadding(0, systemBars.top, 0, 0);
                return insets;
            }
        });

        dbHelper = new DatabaseHelper(this);

        lvwParadas.setEmptyView(llyVacio);

        lvwParadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Parada parada = paradas.get(position);
                Intent intent = new Intent();
                intent.setClassName("com.aditusstudios.bitacoralugares",
                        "com.aditusstudios.bitacoralugares.DetalleActivity");
                intent.putExtra("id", parada.getId());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargaParadas();
    }

    private void cargaParadas() {
        paradas = dbHelper.obtenParadas("", null, false);
        adapter = new ParadaAdapter(this, paradas);
        lvwParadas.setAdapter(adapter);
        actualizaContador();
    }

    private void actualizaContador() {
        if (paradas.size() == 1)
            txtContador.setText(R.string.contador_una);
        else
            txtContador.setText(getString(R.string.contador_varias, paradas.size()));
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}