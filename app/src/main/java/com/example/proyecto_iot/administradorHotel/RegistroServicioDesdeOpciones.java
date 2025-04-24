package com.example.proyecto_iot.administradorHotel;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.ActivityRegistroServicioDesdeOpcionesBinding;

public class RegistroServicioDesdeOpciones extends AppCompatActivity {

    ActivityRegistroServicioDesdeOpcionesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroServicioDesdeOpcionesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retroceder al presionar el botón de la flecha
        binding.backRegistroServicio.setOnClickListener(v -> finish());
    }
}