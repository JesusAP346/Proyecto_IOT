package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.type.LatLng;
import com.google.android.gms.maps.model.LatLng; // ✅ ESTA ES LA CORRECTA


public class MapaTaxiActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitud, longitud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapa_taxi);

        // Recibir coordenadas
        latitud = getIntent().getDoubleExtra("latitud", 0.0);
        longitud = getIntent().getDoubleExtra("longitud", 0.0);

        // Iniciar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // UI previa (nombre, placa, etc.)
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        String nombre = getIntent().getStringExtra("nombre");
        String placa = getIntent().getStringExtra("placa");
        String tiempo = getIntent().getStringExtra("tiempo");
        String distancia = getIntent().getStringExtra("distancia");

        TextView tvNombre = findViewById(R.id.tvNombre);
        TextView tvPlaca = findViewById(R.id.tvPlaca);
        TextView tvTiempo = findViewById(R.id.tvTiempo);
        TextView tvDistancia = findViewById(R.id.tvDistancia);

        tvNombre.setText(nombre);
        tvPlaca.setText("Placa: " + placa);
        tvTiempo.setText(tiempo);
        tvDistancia.setText(distancia);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng ubicacionTaxista = new LatLng(latitud, longitud);
        googleMap.addMarker(new MarkerOptions().position(ubicacionTaxista).title("Ubicación del taxista"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionTaxista, 16));
    }
}