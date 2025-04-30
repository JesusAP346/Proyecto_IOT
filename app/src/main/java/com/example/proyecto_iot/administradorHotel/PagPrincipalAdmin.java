package com.example.proyecto_iot.administradorHotel;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.fragmentos.HomeFragment;
import com.example.proyecto_iot.administradorHotel.fragmentos.HotelFragment;
import com.example.proyecto_iot.administradorHotel.fragmentos.PerfilAdminFragment;
import com.example.proyecto_iot.administradorHotel.fragmentos.ReservasFragment;
import com.example.proyecto_iot.databinding.ActivityPagPrincipalAdminBinding;

public class PagPrincipalAdmin extends AppCompatActivity {


    ActivityPagPrincipalAdminBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPagPrincipalAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        replaceFragment(new HomeFragment());

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.inicio) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.hotel) {
                replaceFragment(new HotelFragment());
            } else if (itemId == R.id.reservas) {
                replaceFragment(new ReservasFragment());
            } else if (itemId == R.id.perfil) {
                replaceFragment(new PerfilAdminFragment());
            }

            return true;
        });

    }


    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }
}