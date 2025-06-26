package com.example.proyecto_iot.administradorHotel;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.ActivitySeleccionUbicacionBinding;
import com.example.proyecto_iot.databinding.ActivitySeleccionUbicacionEditarBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

public class SeleccionUbicacionEditarActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng ubicacionSeleccionada = null;
    private TextView tvNombreLugar;
    private View layoutInfoMarcador;
    private ImageView btnCerrarInfo;
    private ActivitySeleccionUbicacionEditarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySeleccionUbicacionEditarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("Ubicación del Hotel");

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map_completo);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        binding.btnCancelar.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        binding.btnActualizar.setOnClickListener(v -> {
            if (ubicacionSeleccionada != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("latitud", ubicacionSeleccionada.latitude);
                resultIntent.putExtra("longitud", ubicacionSeleccionada.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Selecciona una ubicación en el mapa", Toast.LENGTH_SHORT).show();
            }
        });

        tvNombreLugar = findViewById(R.id.tv_nombre_lugar);
        layoutInfoMarcador = findViewById(R.id.layout_info_marcador);
        btnCerrarInfo = findViewById(R.id.btn_cerrar_info);

        btnCerrarInfo.setOnClickListener(v -> {
            mMap.clear();
            layoutInfoMarcador.setVisibility(View.GONE);
            ubicacionSeleccionada = null;
        });


    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Recuperar datos enviados
        double lat = getIntent().getDoubleExtra("latitud", -1);
        double lng = getIntent().getDoubleExtra("longitud", -1);
        String nombre = getIntent().getStringExtra("nombre");

        if (lat != -1 && lng != -1) {
            ubicacionSeleccionada = new LatLng(lat, lng);

            mMap.addMarker(new MarkerOptions()
                    .position(ubicacionSeleccionada)
                    .title(nombre != null ? nombre : "Ubicación actual del hotel"));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionSeleccionada, 17f));

            layoutInfoMarcador.setVisibility(View.VISIBLE);
            tvNombreLugar.setText(nombre != null ? nombre : "Ubicación actual del hotel");
        } else {
            // Si no hay ubicación previa, centrar en Lima
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-12.0464, -77.0428), 12));
        }

        // Click para seleccionar nueva ubicación
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            ubicacionSeleccionada = latLng;

            mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .title("Ubicación del hotel"));

            layoutInfoMarcador.setVisibility(View.VISIBLE);
            tvNombreLugar.setText("Ubicación del hotel");
        });
    }

}
