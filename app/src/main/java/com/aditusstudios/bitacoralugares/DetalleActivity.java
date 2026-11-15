package com.aditusstudios.bitacoralugares;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

public class DetalleActivity extends AppCompatActivity {
    private MaterialToolbar tlbDetalle;
    private ImageView imgFotoDetalle, imgSinFoto;
    private TextView txtCategoriaDetalle, txtTituloDetalle, txtFechaDetalle;
    private TextView txtCoordenadasDetalle, txtNotaDetalle;
    private Button btnVerMapa, btnEditar, btnBorrar;

    private DatabaseHelper db = null;
    private Parada parada;
    private long idParada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_detalle);

        tlbDetalle = findViewById(R.id.tlbDetalle);
        imgFotoDetalle = findViewById(R.id.imgFotoDetalle);
        imgSinFoto = findViewById(R.id.imgSinFoto);
        txtCategoriaDetalle = findViewById(R.id.txtCategoriaDetalle);
        txtTituloDetalle = findViewById(R.id.txtTituloDetalle);
        txtFechaDetalle = findViewById(R.id.txtFechaDetalle);
        txtCoordenadasDetalle = findViewById(R.id.txtCoordenadasDetalle);
        txtNotaDetalle = findViewById(R.id.txtNotaDetalle);
        btnVerMapa = findViewById(R.id.btnVerMapa);
        btnEditar = findViewById(R.id.btnEditar);
        btnBorrar = findViewById(R.id.btnBorrar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
                tlbDetalle.setPadding(0, systemBars.top, 0, 0);
                return insets;
            }
        });

        tlbDetalle.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnVerMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abreMapa();
            }
        });

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editaParada();
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borra();
            }
        });

        db = new DatabaseHelper(this);
        idParada = getIntent().getLongExtra("id", -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargaParada();
    }

    private void cargaParada() {
        parada = db.obtenParada(idParada);
        if (parada == null) {
            finish();
            return;
        }

        txtCategoriaDetalle.setText(parada.getCategoriaTexto());
        txtCategoriaDetalle.setBackgroundColor(getColor(parada.getColorFondo()));
        txtCategoriaDetalle.setTextColor(getColor(parada.getColorTexto()));
        txtTituloDetalle.setText(parada.getTitulo());
        txtFechaDetalle.setText(parada.getFechaCompleta());
        txtCoordenadasDetalle.setText(parada.getCoordenadas());

        if (parada.getNota().isEmpty()) {
            txtNotaDetalle.setText(R.string.sin_nota);
            txtNotaDetalle.setTextColor(getColor(R.color.texto_secundario));
        } else {
            txtNotaDetalle.setText(parada.getNota());
            txtNotaDetalle.setTextColor(getColor(R.color.texto));
        }

        Bitmap foto = UtilFotos.cargaFoto(parada.getFoto());
        if (foto != null) {
            imgFotoDetalle.setImageBitmap(foto);
            imgFotoDetalle.setVisibility(View.VISIBLE);
            imgSinFoto.setVisibility(View.GONE);
        } else {
            imgFotoDetalle.setVisibility(View.GONE);
            imgSinFoto.setBackgroundColor(getColor(parada.getColorFondo()));
            imgSinFoto.setVisibility(View.VISIBLE);
        }
    }

    private void abreMapa() {
        String coordenadas = parada.getLatitud() + "," + parada.getLongitud();
        Uri uri = Uri.parse("geo:" + coordenadas + "?q=" + coordenadas
                + "(" + Uri.encode(parada.getTitulo()) + ")");

        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.sin_mapas, Toast.LENGTH_LONG).show();
        }
    }

    private void editaParada() {
        Intent intent = new Intent();
        intent.setClassName("com.aditusstudios.bitacoralugares",
                "com.aditusstudios.bitacoralugares.FormularioActivity");
        intent.putExtra("id", parada.getId());
        startActivity(intent);
    }

    private void borra() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.borrar_titulo)
                .setMessage(R.string.borrar_mensaje)
                .setPositiveButton(R.string.borrar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        procesaBorrado();
                    }
                })
                .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    private void procesaBorrado() {
        UtilFotos.borraFoto(parada.getFoto());
        db.borraParada(parada.getId());
        Toast.makeText(this, R.string.parada_borrada, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
    }
}
