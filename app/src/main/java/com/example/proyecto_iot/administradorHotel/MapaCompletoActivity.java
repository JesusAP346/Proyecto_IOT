package com.example.proyecto_iot.administradorHotel;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.ActivityMapaCompletoBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapaCompletoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityMapaCompletoBinding binding;
    private GoogleMap mMap;
    private double latitud;
    private double longitud;
    private String nombreHotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapaCompletoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener datos del intent
        latitud = getIntent().getDoubleExtra("latitud", 0.0);
        longitud = getIntent().getDoubleExtra("longitud", 0.0);
        nombreHotel = getIntent().getStringExtra("nombre");

        // Inicializar mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_completo);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this); // <- aquí usamos this porque implementamos OnMapReadyCallback
        }

        // Botón para cerrar
        binding.fabCerrar.setOnClickListener(v -> finish());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ubicacion = new LatLng(latitud, longitud);

        mMap.addMarker(new MarkerOptions()
                .position(ubicacion)
                .title(nombreHotel)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 17f));
    }
}