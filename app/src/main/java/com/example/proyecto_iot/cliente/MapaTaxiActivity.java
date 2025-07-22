package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.BuildConfig;
import com.example.proyecto_iot.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class MapaTaxiActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double latitud, longitud;
    private double latCliente, lonCliente;
    private String idServicio;

    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistro;
    private GoogleMap mapa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_taxi);

        // Recibir coordenadas
        latitud = getIntent().getDoubleExtra("latitud", 0.0);
        longitud = getIntent().getDoubleExtra("longitud", 0.0);
        latCliente = getIntent().getDoubleExtra("latCliente", 0.0);
        lonCliente = getIntent().getDoubleExtra("lonCliente", 0.0);
        idServicio = getIntent().getStringExtra("idServicio");

        db = FirebaseFirestore.getInstance();

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

        String fotoUrl = getIntent().getStringExtra("fotoUrl");
        String telefono = getIntent().getStringExtra("telefono");
        TextView tvTelefono = findViewById(R.id.tvTelefono);
        tvTelefono.setText(telefono != null && !telefono.isEmpty() ? "Teléfono: " + telefono : "Teléfono: -");

        ImageView imageView = findViewById(R.id.imgConductor);
        if (fotoUrl != null && !fotoUrl.isEmpty()) {
            Glide.with(this)
                    .load(fotoUrl)
                    .placeholder(R.drawable.baseline_account_circle_24)
                    .into(imageView);
        }

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
        this.mapa = googleMap;
        Log.d("MAPA_TAXI", "🗺️ Mapa listo");


        if (idServicio != null) {
            escucharUbicacionTaxistaTiempoReal();
        } else {
            // Carga inicial sin seguimiento en tiempo real
            LatLng ubicacionTaxista = new LatLng(latitud, longitud);
            LatLng ubicacionCliente = new LatLng(latCliente, lonCliente);

            mapa.addMarker(new MarkerOptions().position(ubicacionTaxista).title("Ubicación del taxista"));
            mapa.addMarker(new MarkerOptions().position(ubicacionCliente).title("Ubicación del hotel"));
            mapa.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionCliente, 14));

            trazarRutaEntre(mapa, ubicacionTaxista, ubicacionCliente);
        }
    }

    private void escucharUbicacionTaxistaTiempoReal() {
        Log.d("MAPA_TAXI", "📡 Se activó escucharUbicacionTaxistaTiempoReal()");

        DocumentReference docRef = db.collection("servicios_taxi").document(idServicio);
        listenerRegistro = docRef.addSnapshotListener((snapshot, e) -> {
            if (e != null || snapshot == null || !snapshot.exists()) return;

            Double lat = snapshot.getDouble("latTaxista");
            Double lon = snapshot.getDouble("longTaxista");
            Log.d("MAPA_TAXI", "Ubicación recibida: " + lat + ", " + lon); // 👈 Agrega esto


            if (lat != null && lon != null && mapa != null) {
                LatLng nuevaUbicacion = new LatLng(lat, lon);
                LatLng ubicacionCliente = new LatLng(latCliente, lonCliente);

                mapa.clear();
                mapa.addMarker(new MarkerOptions().position(nuevaUbicacion).title("Taxista"));
                mapa.addMarker(new MarkerOptions().position(ubicacionCliente).title("Cliente"));
                mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(nuevaUbicacion, 15));

                trazarRutaEntre(mapa, nuevaUbicacion, ubicacionCliente);
            }
        });
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

                            TextView tvTiempo = findViewById(R.id.tvTiempo);
                            TextView tvDistancia = findViewById(R.id.tvDistancia);
                            tvTiempo.setText(duracionTexto);
                            tvDistancia.setText(distanciaTexto);
                        }

                        String points = route.getJSONObject("overview_polyline").getString("points");
                        List<LatLng> decodedPath = com.google.maps.android.PolyUtil.decode(points);
                        map.addPolyline(new PolylineOptions()
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

    @Override
    protected void onDestroy() {
        if (listenerRegistro != null) {
            listenerRegistro.remove();
        }
        super.onDestroy();
    }
}
