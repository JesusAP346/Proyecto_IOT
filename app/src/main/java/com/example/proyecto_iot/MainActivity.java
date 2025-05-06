package com.example.proyecto_iot;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyecto_iot.databinding.ActivityMainBinding;
import com.example.proyecto_iot.taxista.perfil.PerfilFragment;
import com.example.proyecto_iot.taxista.qr.QrFragment;
import com.example.proyecto_iot.taxista.solicitudes.SolicitudesFragment;


public class MainActivity extends AppCompatActivity {

    public ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        //EdgeToEdge.enable(this);
        setContentView(binding.getRoot());
        replaceFragment(new SolicitudesFragment());


        binding.bottomNavigationView.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            if (itemId == R.id.perfil) {
                replaceFragment(new PerfilFragment());
            } else if (itemId == R.id.qr) {
                replaceFragment(new QrFragment());
            } else if (itemId == R.id.solicitudes) {
                replaceFragment(new SolicitudesFragment());
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


    public void navegarA(Fragment fragment) {
        replaceFragment(fragment);
    }





}