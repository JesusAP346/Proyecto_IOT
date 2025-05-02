package com.example.proyecto_iot.cliente;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivityCliente extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cliente);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        MyViewPagerAdapter adapter = new MyViewPagerAdapter(this);
        viewPager.setAdapter(adapter);
/*
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(position == 0 ? "Buscar" : "Favoritos");
        }).attach();*/

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Buscar");
                    break;
                case 1:
                    tab.setText("Favoritos");
                    break;
                case 2:
                    tab.setText("Notificaciones");
                    break;
            }
        }).attach();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_buscar) {
                viewPager.setCurrentItem(0);
                return true;
            } else if (id == R.id.nav_taxi) {
                // abrir otra activity si es necesario
                return true;
            } else if (id == R.id.nav_notificaciones) {
                viewPager.setCurrentItem(2);
                return true;
            } else if (id == R.id.nav_reservas) {
                viewPager.setCurrentItem(1); // Ajusta según el orden
                return true;
            } else if (id == R.id.nav_perfil) {
                // abrir otra activity
                return true;
            }

            return false;
        });



    }
}
