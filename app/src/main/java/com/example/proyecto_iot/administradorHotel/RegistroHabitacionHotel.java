package com.example.proyecto_iot.administradorHotel;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.fragmentos.RegistroPaso1Fragment;
import com.example.proyecto_iot.databinding.ActivityRegistroHabitacionHotelBinding;

public class RegistroHabitacionHotel extends AppCompatActivity {

    ActivityRegistroHabitacionHotelBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroHabitacionHotelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Mostrar paso 1 al iniciar
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(binding.fragmentContainerHabitacion.getId(), new RegistroPaso1Fragment())
                    .commit();
        }

        // Botón de retroceso personalizado
        binding.backRegistroHabitacion.setOnClickListener(v -> {
            onBackPressed(); // Retrocede paso a paso
        });
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack(); // Retroceder paso a paso
        } else {
            finish(); // Salir si no hay más pasos atrás
        }
    }
}