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

import com.example.proyecto_iot.BuildConfig;
import com.example.proyecto_iot.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.type.LatLng;
import com.google.android.gms.maps.model.LatLng; // ✅ ESTA ES LA CORRECTA

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;


public class MapaTaxiActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitud, longitud;
    private double latCliente, lonCliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapa_taxi);

        // Recibir coordenadas
        latitud = getIntent().getDoubleExtra("latitud", 0.0);
        longitud = getIntent().getDoubleExtra("longitud", 0.0);
        latCliente = getIntent().getDoubleExtra("latCliente", 0.0);
        lonCliente = getIntent().getDoubleExtra("lonCliente", 0.0);


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
        LatLng ubicacionCliente = new LatLng(latCliente, lonCliente);

        // Marcadores
        googleMap.addMarker(new MarkerOptions().position(ubicacionTaxista).title("Ubicación del taxista"));
        googleMap.addMarker(new MarkerOptions().position(ubicacionCliente).title("Ubicación del hotel"));

        // Centrar vista
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCliente, 14));

        // Trazar ruta
        trazarRutaEntre(googleMap, ubicacionTaxista, ubicacionCliente);
    }
    private void trazarRutaEntre(GoogleMap map, LatLng origen, LatLng destino) {
        String apiKey = BuildConfig.MAPS_API_KEY;

        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origen.latitude + "," + origen.longitude +
                "&destination=" + destino.latitude + "," + destino.longitude +
                "&key=" + apiKey;

        new Thread(() -> {
            try {
                java.net.URL direccionUrl = new java.net.URL(url);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) direccionUrl.openConnection();
                conn.setRequestMethod("GET");

                java.io.InputStream in = new java.io.BufferedInputStream(conn.getInputStream());
                java.util.Scanner scanner = new java.util.Scanner(in).useDelimiter("\\A");
                final String response = scanner.hasNext() ? scanner.next() : "";

                // Procesamos todo el JSON en el hilo principal (UI)
                runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray routes = json.getJSONArray("routes");
                        if (routes.length() == 0) return;

                        JSONObject route = routes.getJSONObject(0);
                        JSONArray legs = route.getJSONArray("legs");
                        if (legs.length() > 0) {
                            JSONObject leg = legs.getJSONObject(0);
                            String duracionTexto = leg.getJSONObject("duration").getString("text");
                            String distanciaTexto = leg.getJSONObject("distance").getString("text");

                            // Actualiza UI
                            TextView tvTiempo = findViewById(R.id.tvTiempo);
                            TextView tvDistancia = findViewById(R.id.tvDistancia);
                            tvTiempo.setText(duracionTexto);
                            tvDistancia.setText(distanciaTexto);
                        }

                        // Dibujar la ruta en el mapa
                        String points = route.getJSONObject("overview_polyline").getString("points");
                        List<LatLng> decodedPath = com.google.maps.android.PolyUtil.decode(points);
                        map.addPolyline(new com.google.android.gms.maps.model.PolylineOptions()
                                .addAll(decodedPath)
                                .width(10f)
                                .color(android.graphics.Color.BLUE));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



}