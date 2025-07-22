package com.example.proyecto_iot.taxista.solicitudes;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.BuildConfig;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.ActivityMapsBinding;
import com.example.proyecto_iot.taxista.qr.QrFragment;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.*;

import okhttp3.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityMapsBinding binding;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private double latDestino = 0.0;
    private double lngDestino = 0.0;
    private final double LAT_AEROPUERTO = -12.02189;
    private final double LNG_AEROPUERTO = -77.11432;
    private String idServicioActual;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        binding.btnBack.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        String nombre = getIntent().getStringExtra("nombre");
        String telefono = getIntent().getStringExtra("telefono");
        String viajes = getIntent().getStringExtra("viajes");
        String hotel = getIntent().getStringExtra("hotel");
        String imagenPerfilUrl = getIntent().getStringExtra("imagenPerfilUrl");
        latDestino = getIntent().getDoubleExtra("latDestino", 0.0);
        lngDestino = getIntent().getDoubleExtra("lngDestino", 0.0);
        idServicioActual = getIntent().getStringExtra("idServicio");

        binding.tvNombre.setText(nombre != null ? nombre : "Sin nombre");
        binding.tvTelefono.setText(telefono != null ? telefono : "Sin tel");
        if (binding.tvViajes != null && viajes != null) {
            binding.tvViajes.setText(viajes);
        }
        String destinoNombre = getIntent().getStringExtra("destinoNombre");
        binding.subtitulo.setText(destinoNombre != null ? destinoNombre : "Destino no especificado");


        Glide.with(this).load(imagenPerfilUrl)
                .placeholder(R.drawable.usuario_10)
                .error(R.drawable.usuario_10)
                .into(binding.imgConductor);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) mapFragment.getMapAsync(this);

        configurarActualizacionUbicacion();

        binding.btnFinalizarViaje.setOnClickListener(v -> {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new QrFragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        if (tienePermisoUbicacion()) {
            mMap.setMyLocationEnabled(true);
            fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null) {
                    LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng destino = new LatLng(latDestino, lngDestino);
                    trazarRuta(miUbicacion, destino);
                }
            });
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }
    }

    private void configurarActualizacionUbicacion() {
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null || idServicioActual == null) return;

                android.location.Location location = locationResult.getLastLocation();
                if (location != null) {
                    double lat = location.getLatitude();
                    double lng = location.getLongitude();

                    db.collection("servicios_taxi")
                            .document(idServicioActual)
                            .update("latTaxista", lat, "longTaxista", lng)
                            .addOnSuccessListener(aVoid -> Log.d("Ubicacion", "Ubicación actualizada"))
                            .addOnFailureListener(e -> Log.e("Ubicacion", "Error actualizando", e));

                    double distancia = calcularDistancia(lat, lng, latDestino, lngDestino);
                    LatLng origenActual = new LatLng(lat, lng);
                    LatLng destinoActual = new LatLng(latDestino, lngDestino);
                    trazarRuta(origenActual, destinoActual);

                    runOnUiThread(() -> {
                        if (distancia < 1.0) {
                            if (esDestinoAeropuerto()) {
                                binding.btnIrAeropuerto.setVisibility(View.GONE);
                                binding.btnFinalizarViaje.setVisibility(View.VISIBLE);
                            } else {
                                binding.btnIrAeropuerto.setVisibility(View.VISIBLE);
                                binding.btnFinalizarViaje.setVisibility(View.GONE);
                            }
                        } else {
                            binding.btnIrAeropuerto.setVisibility(View.GONE);
                            binding.btnFinalizarViaje.setVisibility(View.GONE);
                        }
                    });
                }
            }
        };

        binding.btnIrAeropuerto.setOnClickListener(v -> mostrarDialogoConfirmacion());
    }

    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(this)
                .setTitle("¿Cambiar destino?")
                .setMessage("¿Deseas dirigirte ahora al Aeropuerto Jorge Chávez?")
                .setPositiveButton("Sí", (dialog, which) -> redirigirAlAeropuerto())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void redirigirAlAeropuerto() {
        latDestino = LAT_AEROPUERTO;
        lngDestino = LNG_AEROPUERTO;

        db.collection("servicios_taxi")
                .document(idServicioActual)
                .update("latitudHotel", LAT_AEROPUERTO, "longitudHotel", LNG_AEROPUERTO)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Destino actualizado a aeropuerto"))
                .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar destino", e));

        binding.subtitulo.setText("Aeropuerto Jorge Chávez");
        binding.btnIrAeropuerto.setVisibility(View.GONE);
    }

    private void trazarRuta(LatLng origen, LatLng destino) {
        String apiKey = BuildConfig.MAPS_API_KEY;
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" + origen.latitude + "," + origen.longitude +
                "&destination=" + destino.latitude + "," + destino.longitude + "&key=" + apiKey;

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e("Ruta", "Error al obtener ruta", e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String json = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String points = overviewPolyline.getString("points");

                            JSONObject leg = route.getJSONArray("legs").getJSONObject(0);
                            String duracionTexto = leg.getJSONObject("duration").getString("text");
                            String distanciaTexto = leg.getJSONObject("distance").getString("text");

                            runOnUiThread(() -> {
                                mMap.clear();
                                mMap.addPolyline(new PolylineOptions().addAll(decodePolyline(points)).width(10).color(Color.parseColor("#FF5722")));
                                mMap.addMarker(new MarkerOptions().position(destino).title("Destino"));
                                mMap.addMarker(new MarkerOptions().position(origen).title("Tú estás aquí"));

                                binding.tvDistancia.setText(distanciaTexto);
                                binding.tvTiempo.setText(duracionTexto);

                                LatLngBounds bounds = new LatLngBounds.Builder()
                                        .include(origen)
                                        .include(destino)
                                        .build();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
                            });
                        }
                    } catch (JSONException e) {
                        Log.e("Ruta", "Error al parsear JSON", e);
                    }
                }
            }
        });
    }

    private List<LatLng> decodePolyline(String encoded) {
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            poly.add(new LatLng((lat / 1E5), (lng / 1E5)));
        }

        return poly;
    }

    private boolean tienePermisoUbicacion() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean esDestinoAeropuerto() {
        return calcularDistancia(latDestino, lngDestino, LAT_AEROPUERTO, LNG_AEROPUERTO) < 0.1;
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (tienePermisoUbicacion()) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            configurarActualizacionUbicacion();
        }
    }
}
