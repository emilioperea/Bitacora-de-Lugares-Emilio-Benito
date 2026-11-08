package com.aditusstudios.bitacoralugares;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.chip.ChipGroup;

import java.util.Locale;
import java.util.Map;

public class FormularioActivity extends AppCompatActivity {
    private static final float PRECISION_BUENA = 15;

    private MaterialToolbar tlbFormulario;
    private ProgressBar pgbGps;
    private ImageView imgGps;
    private TextView txtGpsTitulo, txtGpsDetalle, txtError;
    private ImageButton btnActualizarGps;
    private EditText edtTitulo, edtNota;
    private ChipGroup cgpCategoria;
    private Button btnGuardar;

    private DatabaseHelper dbHelper;
    private Parada parada;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private double latitud, longitud;
    private boolean ubicacionLista = false;
    private boolean gpsBuscando = false;

    private ActivityResultLauncher<String[]> pidePermisoUbicacion = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            new ActivityResultCallback<Map<String, Boolean>>() {
                @Override
                public void onActivityResult(Map<String, Boolean> resultado) {
                    if (Boolean.TRUE.equals(resultado.get(Manifest.permission.ACCESS_FINE_LOCATION))
                            || Boolean.TRUE.equals(resultado.get(Manifest.permission.ACCESS_COARSE_LOCATION))) {
                        iniciaGps();
                    } else {
                        muestraEstado(R.string.sin_permiso, getString(R.string.permiso_ubicacion));
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
        setContentView(R.layout.activity_formulario);

        tlbFormulario = findViewById(R.id.tlbFormulario);
        pgbGps = findViewById(R.id.pgbGps);
        imgGps = findViewById(R.id.imgGps);
        txtGpsTitulo = findViewById(R.id.txtGpsTitulo);
        txtGpsDetalle = findViewById(R.id.txtGpsDetalle);
        txtError = findViewById(R.id.txtError);
        btnActualizarGps = findViewById(R.id.btnActualizarGps);
        edtTitulo = findViewById(R.id.edtTitulo);
        edtNota = findViewById(R.id.edtNota);
        cgpCategoria = findViewById(R.id.cgpCategoria);
        btnGuardar = findViewById(R.id.btnGuardar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                Insets teclado = insets.getInsets(WindowInsetsCompat.Type.ime());
                v.setPadding(systemBars.left, 0, systemBars.right, Math.max(systemBars.bottom, teclado.bottom));
                tlbFormulario.setPadding(0, systemBars.top, 0, 0);
                return insets;
            }
        });

        tlbFormulario.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnActualizarGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iniciaGps();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardaParada();
            }
        });

        dbHelper = new DatabaseHelper(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                latitud = location.getLatitude();
                longitud = location.getLongitude();
                ubicacionLista = true;
                muestraUbicacion(Math.round(location.getAccuracy()));
                if (location.getAccuracy() <= PRECISION_BUENA) {
                    detenGps();
                }
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        long idParada = getIntent().getLongExtra("id", -1);
        if (idParada != -1) {
            cargaParada(idParada);
        } else {
            iniciaGps();
        }
    }

    private void cargaParada(long idParada) {
        parada = dbHelper.obtenParada(idParada);
        if (parada == null) {
            finish();
            return;
        }
        tlbFormulario.setTitle(R.string.editar_parada);
        btnGuardar.setText(R.string.guardar_cambios);
        edtTitulo.setText(parada.getTitulo());
        edtNota.setText(parada.getNota());
        seleccionaCategoria(parada.getCategoria());

        latitud = parada.getLatitud();
        longitud = parada.getLongitud();
        ubicacionLista = true;
        muestraUbicacion(-1);
    }

    @SuppressLint("MissingPermission")
    private void iniciaGps() {
        boolean permisoPreciso = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean permisoAproximado = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!permisoPreciso && !permisoAproximado) {
            pidePermisoUbicacion.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION});
            return;
        }

        boolean gpsActivo = permisoPreciso && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean redActiva = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!gpsActivo && !redActiva) {
            gpsBuscando = false;
            muestraEstado(R.string.gps_apagado_titulo, getString(R.string.gps_apagado));
            return;
        }

        gpsBuscando = true;
        muestraBuscando();
        if (gpsActivo)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        if (redActiva)
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
    }

    private void detenGps() {
        locationManager.removeUpdates(locationListener);
        gpsBuscando = false;
    }

    private void muestraBuscando() {
        pgbGps.setVisibility(View.VISIBLE);
        imgGps.setVisibility(View.GONE);
        txtGpsTitulo.setText(R.string.buscando_gps);
        if (ubicacionLista)
            txtGpsDetalle.setText(formatoCoordenadas());
        else
            txtGpsDetalle.setText(R.string.gps_consejo);
    }

    private void muestraUbicacion(int metros) {
        pgbGps.setVisibility(View.GONE);
        imgGps.setVisibility(View.VISIBLE);
        if (metros >= 0)
            txtGpsTitulo.setText(getString(R.string.ubicacion_precision, metros));
        else
            txtGpsTitulo.setText(R.string.ubicacion_detectada);
        txtGpsDetalle.setText(formatoCoordenadas());
    }

    private void muestraEstado(int titulo, String detalle) {
        pgbGps.setVisibility(View.GONE);
        imgGps.setVisibility(View.VISIBLE);
        txtGpsTitulo.setText(titulo);
        if (ubicacionLista)
            txtGpsDetalle.setText(formatoCoordenadas());
        else
            txtGpsDetalle.setText(detalle);
        Toast.makeText(this, detalle, Toast.LENGTH_LONG).show();
    }

    private String formatoCoordenadas() {
        return String.format(Locale.US, "%.5f, %.5f", latitud, longitud);
    }

    private String obtenCategoria() {
        int idChip = cgpCategoria.getCheckedChipId();
        if (idChip == R.id.chpFormComida)
            return getString(R.string.comida);
        else if (idChip == R.id.chpFormEscuela)
            return getString(R.string.escuela);
        else if (idChip == R.id.chpFormOtro)
            return getString(R.string.otro);
        else
            return getString(R.string.paisaje);
    }

    private void seleccionaCategoria(String categoria) {
        if (categoria.equals(getString(R.string.comida)))
            cgpCategoria.check(R.id.chpFormComida);
        else if (categoria.equals(getString(R.string.escuela)))
            cgpCategoria.check(R.id.chpFormEscuela);
        else if (categoria.equals(getString(R.string.otro)))
            cgpCategoria.check(R.id.chpFormOtro);
        else
            cgpCategoria.check(R.id.chpFormPaisaje);
    }

    private void muestraError(int mensaje) {
        txtError.setText(mensaje);
        txtError.setVisibility(View.VISIBLE);
    }

    private void guardaParada() {
        String titulo = edtTitulo.getText().toString().trim();
        String nota = edtNota.getText().toString().trim();
        String categoria = obtenCategoria();

        if (titulo.isEmpty()) {
            muestraError(R.string.error_titulo);
            return;
        }
        if (!ubicacionLista) {
            muestraError(R.string.error_gps);
            return;
        }

        detenGps();
        if (parada == null) {
            Parada nueva = new Parada(titulo, categoria, nota, "", latitud, longitud,
                    System.currentTimeMillis());
            dbHelper.insertaParada(nueva);
            Toast.makeText(this, R.string.parada_guardada, Toast.LENGTH_LONG).show();
        } else {
            parada.editar(titulo, categoria, nota);
            parada.setUbicacion(latitud, longitud);
            dbHelper.actualizaParada(parada);
            Toast.makeText(this, R.string.cambios_guardados, Toast.LENGTH_LONG).show();
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gpsBuscando)
            iniciaGps();
    }

    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }
}