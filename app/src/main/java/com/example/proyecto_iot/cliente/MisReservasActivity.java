package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MisReservasActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);
        // ✅ Selecciona "Mis reservas" como ítem activo
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_reservas);

        // Cargar el fragmento en esta pantalla
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new MisReservasFragment())
                .commit();
    }
}
