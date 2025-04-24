package com.example.proyecto_iot.administradorHotel;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.ActivityRegistroInfoHotelBinding;

public class RegistroInfoHotel extends AppCompatActivity {

    ActivityRegistroInfoHotelBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroInfoHotelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Botón de retroceso
        binding.backRegistro.setOnClickListener(v -> {
            finish(); // Vuelve al fragment anterior (HotelInfoNadaFragment)
        });
    }
}