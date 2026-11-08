/*
 * Proyecto final - Programación de Dispositivos Móviles (ISW211)
 * Bitácora de Lugares
 *
 * Integrantes:
 *   Emilio Perea Montes
 *   Benito Abi Ramírez Vázquez
 */
package com.aditusstudios.bitacoralugares;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView lvwParadas;
    private TextView txtContador;
    private LinearLayout llyVacio;
    private DatabaseHelper dbHelper;
    private ArrayList<Parada> paradas;
    private ParadaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvwParadas = findViewById(R.id.lvwParadas);
        txtContador = findViewById(R.id.txtContador);
        llyVacio = findViewById(R.id.llyVacio);

        dbHelper = new DatabaseHelper(this);

        lvwParadas.setEmptyView(llyVacio);

        lvwParadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Parada parada = paradas.get(position);
                Toast.makeText(MainActivity.this, parada.getTitulo(), Toast.LENGTH_LONG).show();
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