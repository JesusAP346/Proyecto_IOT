package com.example.proyecto_iot;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyecto_iot.databinding.ActivityMainBinding;
import com.example.proyecto_iot.taxista.PerfilFragment;
import com.example.proyecto_iot.taxista.QrFragment;
import com.example.proyecto_iot.taxista.SolicitudesFragment;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

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

        /*  NO SE QUE HACE ESTO PERO NO PARECE NECESARIO UWU
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */
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