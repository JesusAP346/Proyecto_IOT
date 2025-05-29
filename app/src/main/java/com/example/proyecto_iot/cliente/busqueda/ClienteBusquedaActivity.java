package com.example.proyecto_iot.cliente.busqueda;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
//import com.example.proyecto_iot.administradorHotel.fragmentos.ReservasFragment;
import com.example.proyecto_iot.cliente.MisReservasFragment;

import com.example.proyecto_iot.cliente.NotificacionesFragment;
import com.example.proyecto_iot.cliente.PerfilFragmentC;
import com.example.proyecto_iot.cliente.TaxiFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Locale;

public class ClienteBusquedaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_busqueda);

        boolean mostrarReservas = getIntent().getBooleanExtra("mostrar_reservas", false);


        if (mostrarReservas) {
            BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
            bottomNav.setSelectedItemId(R.id.nav_reservas);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_busqueda, new MisReservasFragment())
                    .commit();
        } else if (savedInstanceState == null) {
            // Carga fragmento por defecto (Buscar)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_busqueda, new BusquedaFragment())
                    .commit();
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
/*
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_busqueda, new BusquedaFragment())
                    .commit();
        }*/
        Locale locale = new Locale("es");
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        // Navegación del menú inferior
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Fragment fragment = null;
            if (id == R.id.nav_buscar) {
                fragment = new BusquedaFragment();
            } else if (id == R.id.nav_notificaciones) {
                fragment = new NotificacionesFragment();
            } else if (id == R.id.nav_reservas) {
                fragment = new MisReservasFragment();
            } else if (id == R.id.nav_taxi) {
                fragment = new TaxiFragment();  // ✅ Aquí agregamos el fragmento de taxi
            } else if (id == R.id.nav_perfil) {
                fragment = new PerfilFragmentC();
            }

            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_busqueda, fragment)
                        .commit();
                return true;
            }
            return false;
        });

    }
}