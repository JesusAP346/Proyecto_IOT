package com.example.proyecto_iot.cliente;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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


import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class DetalleReservaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reserva);

        // Obtener vistas
        TextView tvNombre = findViewById(R.id.tvNombreHotel);
        TextView tvEstado = findViewById(R.id.tvEstado);
        TextView tvEntrada = findViewById(R.id.tvFechaEntrada);
        TextView tvSalida = findViewById(R.id.tvFechaSalida);
        TextView tvMonto = findViewById(R.id.tvMonto);
        ImageView imgHotel = findViewById(R.id.imgHotel);
        Button btnCheckout = findViewById(R.id.btnCheckout);

        // Obtener extras
        Intent intent = getIntent();
        String nombre = intent.getStringExtra("nombre");
        String estado = intent.getStringExtra("estado");
        String entrada = intent.getStringExtra("entrada");
        String salida = intent.getStringExtra("salida");
        String monto = intent.getStringExtra("monto");
        int imagen = intent.getIntExtra("imagen", 0);
        String idReserva = intent.getStringExtra("idReserva");

        // Reemplazar "-" por "/" si es necesario
        entrada = entrada.replace("-", "/");

        // Formateador de fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date hoy = new Date();

        try {
            Date fechaEntradaDate = sdf.parse(entrada);

            Log.d("DEBUG_ENTRADA", "Fecha entrada (corregida): " + entrada);
            Log.d("DEBUG_HOY", "Fecha hoy: " + sdf.format(hoy));
            Log.d("DEBUG_ESTADO", "Estado original: " + estado);


            // Si es hoy y aún está pendiente, actualizar a activo
            if (sdf.format(hoy).equals(sdf.format(fechaEntradaDate)) && estado.equalsIgnoreCase("pendiente")) {
                FirebaseFirestore.getInstance()
                        .collection("reservas")
                        .document(idReserva)
                        .update("estado", "ACTIVO")
                        .addOnSuccessListener(unused -> {
                            tvEstado.setText("Estado: activo");
                            estadoBoton(btnCheckout, "ACTIVO"); // habilitar botón
                        })
                        .addOnFailureListener(e -> e.printStackTrace());
            } else {
                estadoBoton(btnCheckout, estado); // usar el estado recibido
            }
        } catch (Exception e) {
            e.printStackTrace();
            estadoBoton(btnCheckout, estado); // fallback en caso de error
        }

        // Setear vistas
        tvNombre.setText(nombre);
        tvEstado.setText("Estado: " + estado);
        tvEntrada.setText("Fecha de entrada: " + entrada);
        tvSalida.setText("Fecha de salida: " + salida);
        tvMonto.setText("Monto total: " + monto);
        imgHotel.setImageResource(imagen);

        // Botón Checkout
        btnCheckout.setOnClickListener(v -> {
            new AlertDialog.Builder(DetalleReservaActivity.this)
                    .setTitle("¿Desea realizar checkout?")
                    .setCancelable(false)
                    .setPositiveButton("SÍ", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("reservas")
                                .document(idReserva)
                                .update("estado", "CHECKOUT")
                                .addOnSuccessListener(unused -> {
                                    tvEstado.setText("Estado: CHECKOUT");
                                    Intent intent2 = new Intent(DetalleReservaActivity.this, FormularioCheckoutActivity.class);
                                    intent2.putExtra("nombreHotel", nombre);
                                    startActivity(intent2);
                                })
                                .addOnFailureListener(e -> e.printStackTrace());
                    })
                    .setNegativeButton("NO", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void estadoBoton(Button btn, String estado) {
        if (!"activo".equalsIgnoreCase(estado)) {
            btn.setEnabled(false);
            btn.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));
        } else {
            btn.setEnabled(true);
            btn.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_orange_dark));
        }
    }


}