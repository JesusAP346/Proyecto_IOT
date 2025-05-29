package com.example.proyecto_iot.cliente.pago;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.busqueda.ClienteBusquedaActivity;


import java.util.List;

public class PasarellaDePago extends AppCompatActivity implements TarjetaAdapter.OnTarjetaSelectedListener {

    private static final String CHANNEL_ID = "PAYMENT_NOTIFICATIONS";
    private static final int NOTIFICATION_ID = 1;

    private RecyclerView recyclerView;
    private Button btnPagar;
    private TarjetaAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pasarella_de_pago);

        // Crear canal de notificaciones
        createNotificationChannel();

        // Inicializar vistas
        recyclerView = findViewById(R.id.recyclerTarjetas);
        btnPagar = findViewById(R.id.btnPagar);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Cargar tarjetas y configurar adapter
        List<Tarjeta> listaTarjetas = TarjetaStorage.obtenerTarjetas(this);
        adapter = new TarjetaAdapter(listaTarjetas);
        adapter.setOnTarjetaSelectedListener(this);
        recyclerView.setAdapter(adapter);

        // Inicialmente el botón está deshabilitado
        btnPagar.setEnabled(false);
        btnPagar.setAlpha(0.5f);

        // Configurar botón de retroceso
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Configurar botón de pago
        btnPagar.setOnClickListener(v -> procesarPago());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Actualizar lista de tarjetas cuando se regrese a la actividad
        List<Tarjeta> listaTarjetasActualizadas = TarjetaStorage.obtenerTarjetas(this);
        adapter = new TarjetaAdapter(listaTarjetasActualizadas);
        adapter.setOnTarjetaSelectedListener(this);
        recyclerView.setAdapter(adapter);

        // Resetear estado del botón
        btnPagar.setEnabled(false);
        btnPagar.setAlpha(0.5f);
    }

    @Override
    public void onTarjetaSelected(boolean haySeleccion) {
        // Habilitar/deshabilitar botón según si hay tarjeta seleccionada
        btnPagar.setEnabled(haySeleccion);
        btnPagar.setAlpha(haySeleccion ? 1.0f : 0.5f);
    }

    private void procesarPago() {
        Tarjeta tarjetaSeleccionada = adapter.getTarjetaSeleccionada();

        if (tarjetaSeleccionada != null) {
            // Mostrar notificación de pago exitoso
            mostrarNotificacionPagoExitoso(tarjetaSeleccionada);

            // Mostrar mensaje temporal
            Toast.makeText(this, "Pago procesado exitosamente", Toast.LENGTH_SHORT).show();

            // Redirigir a ClienteBusquedaActivity
            Intent intent = new Intent(this, ClienteBusquedaActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Por favor selecciona una tarjeta", Toast.LENGTH_SHORT).show();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Notificaciones de Pago";
            String description = "Canal para notificaciones de pagos exitosos";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void mostrarNotificacionPagoExitoso(Tarjeta tarjeta) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_check_circle)
                .setContentTitle("Pago Exitoso")
                .setContentText("Pago procesado con tarjeta " + tarjeta.getBanco() + " terminada en " +
                        tarjeta.getNumero().substring(tarjeta.getNumero().length() - 4))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}