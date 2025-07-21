package com.example.proyecto_iot.cliente;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.proyecto_iot.cliente.FormularioCheckoutActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetalleReservaActivity extends AppCompatActivity {

    private ViewPager2 viewPagerImagenes;
    private TextView indicadorImagen;
    private ImageCarouselAdapter imageAdapter;

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
        viewPagerImagenes = findViewById(R.id.viewPagerImagenes);
        indicadorImagen = findViewById(R.id.indicadorImagen);
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
        String idHotel = intent.getStringExtra("idHotel");
        String idHabitacion = intent.getStringExtra("idHabitacion");

        // Log para verificar cada valor recibido
        Log.d("ReservaDebug", "nombre: " + nombre);
        Log.d("ReservaDebug", "estado: " + estado);
        Log.d("ReservaDebug", "entrada: " + entrada);
        Log.d("ReservaDebug", "salida: " + salida);
        Log.d("ReservaDebug", "monto: " + monto);
        Log.d("ReservaDebug", "imagen: " + imagen);
        Log.d("ReservaDebug", "idReserva: " + idReserva);
        Log.d("ReservaDebug", "idHotel: " + idHotel);
        Log.d("ReservaDebug", "idHabitacion: " + idHabitacion);

        // Cargar imágenes del hotel desde Firebase
        cargarImagenesHotel(idHotel);

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
                                    intent2.putExtra("idHotel", idHotel);

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

    private void cargarImagenesHotel(String idHotel) {
        if (idHotel == null || idHotel.isEmpty()) {
            configurarImagenPorDefecto();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("hoteles")
                .document(idHotel)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Obtener la lista de URLs de imágenes
                        List<String> fotosUrls = (List<String>) documentSnapshot.get("fotosHotelUrls");

                        if (fotosUrls != null && !fotosUrls.isEmpty()) {
                            configurarCarrusel(fotosUrls);
                        } else {
                            configurarImagenPorDefecto();
                        }
                    } else {
                        Log.w("DetalleReserva", "Hotel no encontrado: " + idHotel);
                        configurarImagenPorDefecto();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DetalleReserva", "Error al cargar hotel", e);
                    configurarImagenPorDefecto();
                });
    }

    private void configurarCarrusel(List<String> fotosUrls) {
        // Configurar adaptador
        imageAdapter = new ImageCarouselAdapter(fotosUrls);
        viewPagerImagenes.setAdapter(imageAdapter);

        // Actualizar indicador inicial
        actualizarIndicador(1, fotosUrls.size());

        // Configurar listener para cambios de página
        viewPagerImagenes.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                actualizarIndicador(position + 1, fotosUrls.size());
            }
        });
    }

    private void configurarImagenPorDefecto() {
        List<String> imagenDefault = new ArrayList<>();
        imagenDefault.add("");

        imageAdapter = new ImageCarouselAdapter(imagenDefault);
        viewPagerImagenes.setAdapter(imageAdapter);
        actualizarIndicador(1, 1);
    }

    private void actualizarIndicador(int posicionActual, int total) {
        indicadorImagen.setText(posicionActual + "/" + total);
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