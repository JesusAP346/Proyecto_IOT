package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;

public class SolicitudTaxiActivity extends AppCompatActivity {
    RadioButton rbSi, rbNo;
    EditText etAeropuerto;
    Button btnEnviar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_taxi);

        rbSi = findViewById(R.id.rbSi);
        rbNo = findViewById(R.id.rbNo);
        etAeropuerto = findViewById(R.id.etAeropuerto);
        btnEnviar = findViewById(R.id.btnEnviarTaxi);


        // Mostrar o esconder campo según opción
        rbSi.setOnClickListener(v -> etAeropuerto.setVisibility(View.VISIBLE));
        rbNo.setOnClickListener(v -> etAeropuerto.setVisibility(View.GONE));

        btnEnviar.setOnClickListener(v -> {
            if (!rbSi.isChecked() && !rbNo.isChecked()) {
                Toast.makeText(this, "Seleccione una opción", Toast.LENGTH_SHORT).show();
                return;
            }

            if (rbSi.isChecked() && etAeropuerto.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese el aeropuerto", Toast.LENGTH_SHORT).show();
                return;
            }

            View customView = getLayoutInflater().inflate(R.layout.dialog_confirmacion, null);

            androidx.appcompat.app.AlertDialog dialog = new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setView(customView)
                    .setCancelable(false)
                    .create();

            Button btnCerrar = customView.findViewById(R.id.btnCerrarDialogo);
            //btnCerrar.setOnClickListener(v1 -> dialog.dismiss());

            //dialog.show();

            btnCerrar.setOnClickListener(v1 -> {
                dialog.dismiss();
                Intent intent = new Intent(SolicitudTaxiActivity.this, MisReservasActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish(); // opcional: cierra esta activity para no volver atrás
            });
            dialog.show();
        });
    }
}