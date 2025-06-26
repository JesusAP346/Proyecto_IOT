package com.example.proyecto_iot.taxista.solicitudes;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.BuildConfig;
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
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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

        String nombre = getIntent().getStringExtra("nombre");
        String telefono = getIntent().getStringExtra("telefono");
        String viajes = getIntent().getStringExtra("viajes");
        String hotel = getIntent().getStringExtra("hotel");
        String imagenPerfilUrl = getIntent().getStringExtra("imagenPerfilUrl");
        latDestino = getIntent().getDoubleExtra("latDestino", 0.0);
        lngDestino = getIntent().getDoubleExtra("lngDestino", 0.0);

        Log.d("MapsActivity", "LatDestino: " + latDestino + ", LngDestino: " + lngDestino);

        binding.tvNombre.setText(nombre != null ? nombre : "Sin nombre");
        binding.tvTelefono.setText(telefono != null ? telefono : "Sin teléfono");
        binding.tvViajes.setText(viajes != null ? viajes : "0 viajes");
        binding.subtitulo.setText(hotel != null ? hotel : "Destino no especificado");

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

                    trazarRuta(miUbicacion, destino);
                } else {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 15));
                    Log.w("MapsActivity", "Destino no válido: lat/lng = 0.0");
                }
            }
        });
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
                    Log.d("Ruta", "Respuesta de Directions API: " + json);

                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        JSONArray routes = jsonObject.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject route = routes.getJSONObject(0);
                            JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                            String points = overviewPolyline.getString("points");

                            List<LatLng> decodedPath = decodePolyline(points);

                            runOnUiThread(() -> {
                                mMap.addPolyline(new PolylineOptions()
                                        .addAll(decodedPath)
                                        .width(10)
                                        .color(Color.parseColor("#FF5722")));

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

            LatLng p = new LatLng((lat / 1E5), (lng / 1E5));
            poly.add(p);
        }

        return poly;
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
