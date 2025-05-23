package com.example.proyecto_iot.cliente;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.proyecto_iot.cliente.FormularioCheckoutActivity;

public class DetalleReservaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reserva);
        /*
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detalle_reserva);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvTituloDetalle), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });*/

        // Obtener referencias de vistas
        TextView tvNombre = findViewById(R.id.tvNombreHotel);
        TextView tvEstado = findViewById(R.id.tvEstado);
        TextView tvEntrada = findViewById(R.id.tvFechaEntrada);
        TextView tvSalida = findViewById(R.id.tvFechaSalida);
        TextView tvMonto = findViewById(R.id.tvMonto);
        ImageView imgHotel = findViewById(R.id.imgHotel);
        Button btnCheckout = findViewById(R.id.btnCheckout);

        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String estado = intent.getStringExtra("estado");
        String entrada = intent.getStringExtra("entrada");
        String salida = intent.getStringExtra("salida");
        String monto = intent.getStringExtra("monto");
        int imagen = intent.getIntExtra("imagen", 0);

        // Setear datos en la vista
        tvNombre.setText(nombre);
        tvEstado.setText("Estado: " + estado);
        tvEntrada.setText("Fecha de entrada: " + entrada);
        tvSalida.setText("Fecha de salida: " + salida);
        tvMonto.setText("Monto total: " + monto);
        imgHotel.setImageResource(imagen);

        // Estilo dinámico del botón
        if ("Finalizado".equalsIgnoreCase(estado)) {
            btnCheckout.setEnabled(false);
            btnCheckout.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        } else {
            btnCheckout.setEnabled(true);
            btnCheckout.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_orange_dark));
        }


        //BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //bottomNav.setSelectedItemId(R.id.nav_reservas);
        btnCheckout.setOnClickListener(v -> {
            new AlertDialog.Builder(DetalleReservaActivity.this)
                    .setTitle("¿Desea realizar checkout?")
                    .setCancelable(false)
                    .setPositiveButton("SÍ", (dialog, which) -> {
                        // Ir al formulario de preguntas
                        Intent intent2 = new Intent(DetalleReservaActivity.this, FormularioCheckoutActivity.class);
                        intent2.putExtra("nombreHotel", nombre); // Reemplaza por el valor real
                        //startActivity(intent);
                        startActivity(intent2);
                    })
                    .setNegativeButton("NO", (dialog, which) -> {
                        dialog.dismiss(); // simplemente cierra el diálogo
                    })
                    .show();
        });

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });

    }
}