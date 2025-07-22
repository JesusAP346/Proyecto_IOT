package com.example.proyecto_iot.administradorHotel;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerTaxistaAdminActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Marker markerTaxi;
    private Marker markerHotel;
    private double latHotel, longHotel;
    private String idTaxista;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final Handler handler = new Handler();
    private final int UPDATE_INTERVAL = 3000; // cada 3 segundos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_taxista_admin);

        latHotel = getIntent().getDoubleExtra("latHotel", 0);
        longHotel = getIntent().getDoubleExtra("longHotel", 0);
        idTaxista = getIntent().getStringExtra("idTaxista");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.mapTaxi);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Ubicación del hotel (fijo)
        LatLng hotelLatLng = new LatLng(latHotel, longHotel);
        markerHotel = mMap.addMarker(new MarkerOptions()
                .position(hotelLatLng)
                .title("Hotel")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hotelLatLng, 14));

        iniciarActualizacionPosicionTaxi();
    }

    private void iniciarActualizacionPosicionTaxi() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                db.collection("usuarios").document(idTaxista).get()
                        .addOnSuccessListener(doc -> {
                            if (doc.exists()) {
                                Double lat = doc.getDouble("latitudActual");
                                Double lng = doc.getDouble("longitudActual");
                                if (lat != null && lng != null) {
                                    LatLng taxiLatLng = new LatLng(lat, lng);
                                    if (markerTaxi == null) {
                                        markerTaxi = mMap.addMarker(new MarkerOptions()
                                                .position(taxiLatLng)
                                                .title("Taxista")
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE)));
                                    } else {
                                        markerTaxi.setPosition(taxiLatLng);
                                    }
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.e("MAPA_TAXI", "Error obteniendo ubicación", e));

                handler.postDelayed(this, UPDATE_INTERVAL);
            }
        }, UPDATE_INTERVAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
