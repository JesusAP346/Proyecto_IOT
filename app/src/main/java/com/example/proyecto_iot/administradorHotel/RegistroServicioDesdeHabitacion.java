package com.example.proyecto_iot.administradorHotel;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.ActivityRegistroServicioDesdeHabitacionBinding;

public class RegistroServicioDesdeHabitacion extends AppCompatActivity {

    ActivityRegistroServicioDesdeHabitacionBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroServicioDesdeHabitacionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Manejo de insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 🔙 Acción del botón de retroceso
        binding.backRegistroHabitacion.setOnClickListener(v -> {
            finish(); // Cierra esta activity y regresa
        });
    }
}