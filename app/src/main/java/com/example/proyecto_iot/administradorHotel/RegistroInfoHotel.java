package com.example.proyecto_iot.administradorHotel;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.ActivityRegistroInfoHotelBinding;

import java.util.ArrayList;
import java.util.List;

public class RegistroInfoHotel extends AppCompatActivity {

    ActivityRegistroInfoHotelBinding binding;
    private final List<String> referencias = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroInfoHotelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Botón de retroceso
        binding.backRegistro.setOnClickListener(v -> finish());

        // Acción botón + Agregar referencia
        binding.btnAgregarReferencia.setOnClickListener(v -> {
            String texto = binding.etReferencia.getText().toString().trim();
            if (!texto.isEmpty() && !referencias.contains(texto)) {
                referencias.add(texto);
                agregarChipReferencia(texto);
                binding.etReferencia.setText("");
            }
        });
    }

    private void agregarChipReferencia(String texto) {
        // Contenedor externo
        LinearLayout chip = new LinearLayout(this);
        chip.setOrientation(LinearLayout.HORIZONTAL);
        chip.setBackgroundResource(R.drawable.customedittext);
        chip.setPadding(16, 12, 16, 12);
        chip.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        chipParams.setMargins(0, 8, 0, 0);
        chip.setLayoutParams(chipParams);

        // Texto
        TextView textView = new TextView(this);
        textView.setText(texto);
        textView.setTextSize(16);
        textView.setTextColor(Color.BLACK);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

        // Icono eliminar
        ImageView icono = new ImageView(this);
        icono.setImageResource(R.drawable.ic_delete);
        icono.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(40, 40);
        iconParams.setMargins(16, 0, 0, 0);
        icono.setLayoutParams(iconParams);

        icono.setOnClickListener(v -> {
            referencias.remove(texto);
            binding.layoutReferenciasDinamicas.removeView(chip);
        });

        chip.addView(textView);
        chip.addView(icono);
        binding.layoutReferenciasDinamicas.addView(chip);
    }
}
