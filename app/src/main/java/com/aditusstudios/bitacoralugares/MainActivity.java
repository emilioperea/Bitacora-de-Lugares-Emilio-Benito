/*
 * Proyecto final - Programación de Dispositivos Móviles (ISW211)
 * Bitácora de Lugares
 *
 * Integrantes:
 *   Emilio Perea Montes
 *   Benito Abi Ramírez Vázquez
 */
package com.aditusstudios.bitacoralugares;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private MaterialToolbar tlbPrincipal;
    private EditText edtBuscar;
    private ChipGroup cgpCategorias;
    private ListView lvwParadas;
    private TextView txtContador, txtVacio;
    private LinearLayout llyVacio;
    private ExtendedFloatingActionButton fabNuevaParada;

    private DatabaseHelper dbHelper;
    private ArrayList<Parada> paradas;
    private ParadaAdapter adapter;
    private SharedPreferences misPreferencias;
    private boolean porNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_main);

        tlbPrincipal = findViewById(R.id.tlbPrincipal);
        edtBuscar = findViewById(R.id.edtBuscar);
        cgpCategorias = findViewById(R.id.cgpCategorias);
        lvwParadas = findViewById(R.id.lvwParadas);
        txtContador = findViewById(R.id.txtContador);
        txtVacio = findViewById(R.id.txtVacio);
        llyVacio = findViewById(R.id.llyVacio);
        fabNuevaParada = findViewById(R.id.fabNuevaParada);

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

        misPreferencias = getSharedPreferences("Preferencias", MODE_PRIVATE);
        porNombre = misPreferencias.getBoolean("ordenPorNombre", false);

        registerForContextMenu(lvwParadas);

        lvwParadas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                abreDetalle(paradas.get(position));
            }
        });

        fabNuevaParada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClassName("com.aditusstudios.bitacoralugares",
                        "com.aditusstudios.bitacoralugares.FormularioActivity");
                startActivity(intent);
            }
        });

        edtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                cargaParadas();
            }
        });

        cgpCategorias.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {
                cargaParadas();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargaParadas();
    }

    private void cargaParadas() {
        String busqueda = edtBuscar.getText().toString().trim();
        String categoria = obtenCategoriaFiltro();

        paradas = dbHelper.obtenParadas(busqueda, categoria, porNombre);
        adapter = new ParadaAdapter(this, paradas);
        lvwParadas.setAdapter(adapter);
        actualizaContador();

        if (paradas.size() == 0) {
            lvwParadas.setVisibility(View.GONE);
            llyVacio.setVisibility(View.VISIBLE);
        } else {
            lvwParadas.setVisibility(View.VISIBLE);
            llyVacio.setVisibility(View.GONE);
        }

        if (busqueda.isEmpty() && categoria == null)
            txtVacio.setText(R.string.lista_vacia);
        else
            txtVacio.setText(R.string.sin_resultados);
    }

    private String obtenCategoriaFiltro() {
        int idChip = cgpCategorias.getCheckedChipId();
        if (idChip == R.id.chpComida)
            return getString(R.string.comida);
        else if (idChip == R.id.chpPaisaje)
            return getString(R.string.paisaje);
        else if (idChip == R.id.chpEscuela)
            return getString(R.string.escuela);
        else if (idChip == R.id.chpOtro)
            return getString(R.string.otro);
        else
            return null;
    }

    private void actualizaContador() {
        if (paradas.size() == 1)
            txtContador.setText(R.string.contador_una);
        else
            txtContador.setText(paradas.size() + " " + getString(R.string.paradas));
    }


    private void abreDetalle(Parada parada) {
        Intent intent = new Intent();
        intent.setClassName("com.aditusstudios.bitacoralugares",
                "com.aditusstudios.bitacoralugares.DetalleActivity");
        intent.putExtra("id", parada.getId());
        startActivity(intent);
    }

    private void abreFormulario(Parada parada) {
        Intent intent = new Intent();
        intent.setClassName("com.aditusstudios.bitacoralugares",
                "com.aditusstudios.bitacoralugares.FormularioActivity");
        intent.putExtra("id", parada.getId());
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        // Se marca el orden que estaba guardado
        if (porNombre)
            menu.findItem(R.id.mnuNombre).setChecked(true);
        else
            menu.findItem(R.id.mnuRecientes).setChecked(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.mnuRecientes) {
            item.setChecked(true);
            cambiaOrden(false);
        }
        if (item.getItemId() == R.id.mnuNombre) {
            item.setChecked(true);
            cambiaOrden(true);
        }
        if (item.getItemId() == R.id.mnuAcerca) {
            muestraAcerca();
        }
        return true;
    }

    private void cambiaOrden(boolean nuevoOrden) {
        porNombre = nuevoOrden;
        SharedPreferences.Editor editor = misPreferencias.edit();
        editor.putBoolean("ordenPorNombre", porNombre);
        editor.apply();
        cargaParadas();
    }

    private void muestraAcerca() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.app_name);
        builder.setMessage(R.string.acerca_mensaje);
        builder.setPositiveButton(R.string.cerrar, null);
        builder.show();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Parada parada = paradas.get(info.position);
        menu.setHeaderTitle(parada.getTitulo());
        getMenuInflater().inflate(R.menu.menu_contextual, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        super.onContextItemSelected(item);
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        Parada parada = paradas.get(info.position);

        if (item.getItemId() == R.id.mnuVerDetalle) {
            abreDetalle(parada);
        }
        if (item.getItemId() == R.id.mnuEditar) {
            abreFormulario(parada);
        }
        if (item.getItemId() == R.id.mnuBorrar) {
            confirmaBorrado(parada);
        }
        return true;
    }

    private void confirmaBorrado(final Parada parada) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.borrar_titulo);
        builder.setMessage(R.string.borrar_mensaje);
        builder.setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UtilFotos.borraFoto(parada.getFoto());
                dbHelper.borraParada(parada.getId());
                Toast.makeText(MainActivity.this, R.string.parada_borrada, Toast.LENGTH_LONG).show();
                cargaParadas();
            }
        });
        builder.setNegativeButton(R.string.cancelar, null);

        AlertDialog dialogo = builder.create();
        dialogo.show();
        dialogo.getButton(AlertDialog.BUTTON_POSITIVE)
                .setTextColor(ContextCompat.getColor(this, R.color.peligro));
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}