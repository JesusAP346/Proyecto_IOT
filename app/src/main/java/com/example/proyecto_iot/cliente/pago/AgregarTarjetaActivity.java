package com.example.proyecto_iot.cliente.pago;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;

import java.util.List;

public class AgregarTarjetaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_agregar_tarjeta);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });

        Button btnAgregar = findViewById(R.id.btnAgregarTarjeta);
        btnAgregar.setOnClickListener(v -> {
            EditText etNumero = findViewById(R.id.etNumero);
            EditText etTitular = findViewById(R.id.etTitular);

            String numero = etNumero.getText().toString();
            String titular = etTitular.getText().toString();

            if (numero.isEmpty() || titular.isEmpty()) {
                Toast.makeText(this, "Completa los campos requeridos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Hardcode temporal para ejemplo
            Tarjeta nueva = new Tarjeta("INTERBANK", "**** " + numero.substring(numero.length() - 4), titular, "Débito", "Visa");

            List<Tarjeta> tarjetas = TarjetaStorage.obtenerTarjetas(this);
            tarjetas.add(nueva);
            TarjetaStorage.guardarTarjetas(this, tarjetas);

            mostrarNotificacionTarjetaAgregada();

            finish();
        });


    }
    private void mostrarNotificacionTarjetaAgregada() {
        String channelId = "tarjeta_channel";
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notificaciones de Tarjetas",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.ic_credit_card) // Usa un ícono válido
                .setContentTitle("Tarjeta agregada")
                .setContentText("La tarjeta fue agregada exitosamente.")
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }

}