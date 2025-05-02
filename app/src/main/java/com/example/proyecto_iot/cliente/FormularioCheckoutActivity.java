package com.example.proyecto_iot.cliente;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario_checkout);

        TextView tvNombreHotel = findViewById(R.id.tvNombreHotel);
        String nombreHotel = getIntent().getStringExtra("nombreHotel");

        if (nombreHotel != null) {
            tvNombreHotel.setText(nombreHotel);
        }

        // validación aquí
        EditText pregunta1 = findViewById(R.id.pregunta1);
        EditText pregunta2 = findViewById(R.id.pregunta2);
        EditText observaciones = findViewById(R.id.observaciones);
        Button btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(v -> {
            String p1 = pregunta1.getText().toString().trim();
            String p2 = pregunta2.getText().toString().trim();
            String obs = observaciones.getText().toString().trim();

            if (p1.isEmpty() || p2.isEmpty() || obs.isEmpty()) {
                new AlertDialog.Builder(FormularioCheckoutActivity.this)
                        .setTitle("Por favor, llene todos los campos")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                // Si todos los campos están llenos, continúa al siguiente paso
                Intent intent = new Intent(FormularioCheckoutActivity.this, SolicitudTaxiActivity.class);
                intent.putExtra("nombreHotel", nombreHotel);
                startActivity(intent);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}
