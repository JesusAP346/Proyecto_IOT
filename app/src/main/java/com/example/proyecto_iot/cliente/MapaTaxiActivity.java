package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MapaTaxiActivity extends AppCompatActivity {

    private double latitud, longitud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mapa_taxi);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        //SSS
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

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_buscar) {
                // Abrir otra activity o fragment
                return true;
            } else if (id == R.id.nav_taxi) {
                return true;
            } else if (id == R.id.nav_notificaciones) {
                return true;
            }
            return false;
        });


    }
}