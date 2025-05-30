package com.example.proyecto_iot.cliente;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

//import androidx.activity.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.busqueda.ClienteBusquedaActivity;

public class SolicitudTaxiActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "canal_reservas";

    RadioButton rbSi, rbNo;
    EditText etAeropuerto;
    Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_taxi);

        crearCanalNotificacion();

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

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(customView)
                    .setCancelable(false)
                    .create();

            Button btnCerrar = customView.findViewById(R.id.btnCerrarDialogo);

            btnCerrar.setOnClickListener(v1 -> {
                dialog.dismiss();
                mostrarNotificacion();

                Intent intent = new Intent(SolicitudTaxiActivity.this, ClienteBusquedaActivity.class);
                intent.putExtra("mostrar_reservas", true);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            });
            dialog.show();
        });
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Canal Reservas";
            String description = "Canal para notificaciones de reservas y taxi";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void mostrarNotificacion() {
        // Pedir permiso para Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.app.ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                androidx.core.app.ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
                return; // Espera permiso
            }
        }

        Intent intent = new Intent(this, ClienteBusquedaActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_chat_notification) // Cambia por un ícono válido en tu drawable
                .setContentTitle("Solicitud de Taxi Confirmada")
                .setContentText("Tu solicitud de taxi fue registrada correctamente.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1001, builder.build());
    }
}
