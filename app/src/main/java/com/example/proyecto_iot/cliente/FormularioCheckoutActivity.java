package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;

public class FormularioCheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_checkout);
        TextView tvNombreHotel = findViewById(R.id.tvNombreHotel);
        String nombreHotel = getIntent().getStringExtra("nombreHotel");

        if (nombreHotel != null && tvNombreHotel != null) {
            tvNombreHotel.setText(nombreHotel);
        }


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}