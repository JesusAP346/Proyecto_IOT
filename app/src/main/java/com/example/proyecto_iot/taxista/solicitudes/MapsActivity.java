package com.example.proyecto_iot.taxista.solicitudes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private FusedLocationProviderClient fusedLocationClient;
    private ActivityMapsBinding binding;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;

    private double latDestino = 0.0;
    private double lngDestino = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        binding.btnBack.setOnClickListener(v -> finish());

        // RECIBIR DATOS DEL INTENT
        String nombre = getIntent().getStringExtra("nombre");
        String telefono = getIntent().getStringExtra("telefono");
        String viajes = getIntent().getStringExtra("viajes");
        String hotel = getIntent().getStringExtra("hotel");
        String imagenPerfilUrl = getIntent().getStringExtra("imagenPerfilUrl");
        latDestino = getIntent().getDoubleExtra("latDestino", 0.0);
        lngDestino = getIntent().getDoubleExtra("lngDestino", 0.0);

        Log.d("MapsActivity", "LatDestino: " + latDestino + ", LngDestino: " + lngDestino);

        // MOSTRAR INFO
        binding.tvNombre.setText(nombre != null ? nombre : "Sin nombre");
        binding.tvTelefono.setText(telefono != null ? telefono : "Sin teléfono");
        binding.tvViajes.setText(viajes != null ? viajes : "0 viajes");
        binding.subtitulo.setText(hotel != null ? hotel : "Destino no especificado");

        // Mostrar imagen con Glide
        Glide.with(this)
                .load(imagenPerfilUrl)
                .placeholder(R.drawable.usuario_10)
                .error(R.drawable.usuario_10)
                .into(binding.imgConductor);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        obtenerUbicacion();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        mMap.setMyLocationEnabled(true);

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(miUbicacion).title("Tú estás aquí"));

                if (!(latDestino == 0.0 && lngDestino == 0.0)) {
                    LatLng destino = new LatLng(latDestino, lngDestino);
                    mMap.addMarker(new MarkerOptions().position(destino).title("Destino del pasajero"));

                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(miUbicacion);
                    builder.include(destino);
                    LatLngBounds bounds = builder.build();

                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15));
                    Log.w("MapsActivity", "Destino no válido: lat/lng = 0.0");
                }
            }
        });
    }

    private void obtenerUbicacion() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                double lat = location.getLatitude();
                double lon = location.getLongitude();
                Log.d("Ubicacion", "Lat: " + lat + ", Lng: " + lon);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion();
        }
    }
}
