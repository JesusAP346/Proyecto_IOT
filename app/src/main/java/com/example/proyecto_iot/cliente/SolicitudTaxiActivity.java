package com.example.proyecto_iot.cliente;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SolicitudTaxiActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "canal_reservas";

    RadioButton rbSi, rbNo;
    EditText etAeropuerto;
    Button btnEnviar;
    private String idReserva;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solicitud_taxi);

        String nombreHotel = getIntent().getStringExtra("nombreHotel");

        //AGREGUE ESTO
        TextView tvHotel = findViewById(R.id.tvHotelTaxi);
        tvHotel.setText(nombreHotel != null ? nombreHotel : "Hotel no identificado");

        idReserva = getIntent().getStringExtra("idReserva");

        crearCanalNotificacion();

        rbSi = findViewById(R.id.rbSi);
        rbNo = findViewById(R.id.rbNo);
        //etAeropuerto = findViewById(R.id.etAeropuerto);
        btnEnviar = findViewById(R.id.btnEnviarTaxi);

        TextView tvDestinoFijo;

        // Mostrar o esconder campo según opción
        //rbSi.setOnClickListener(v -> etAeropuerto.setVisibility(View.VISIBLE));
        //rbNo.setOnClickListener(v -> etAeropuerto.setVisibility(View.GONE));


        tvDestinoFijo = findViewById(R.id.tvDestinoFijo);

        rbSi.setOnClickListener(v -> tvDestinoFijo.setVisibility(View.VISIBLE));
        rbNo.setOnClickListener(v -> tvDestinoFijo.setVisibility(View.GONE));


        btnEnviar.setOnClickListener(v -> {

            if (!rbSi.isChecked() && !rbNo.isChecked()) {
                Toast.makeText(this, "Seleccione una opción", Toast.LENGTH_SHORT).show();
                return;
            }
/*
            if (rbSi.isChecked() && etAeropuerto.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Por favor, ingrese el aeropuerto", Toast.LENGTH_SHORT).show();
                return;
            }*/

            View customView = getLayoutInflater().inflate(R.layout.dialog_confirmacion, null);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setView(customView)
                    .setCancelable(false)
                    .create();

            Button btnCerrar = customView.findViewById(R.id.btnCerrarDialogo);

            btnCerrar.setOnClickListener(v1 -> {
                dialog.dismiss();

                boolean deseaTaxi = rbSi.isChecked();
                String aeropuerto = deseaTaxi ? "Aeropuerto Jorge Chávez" : "Sin taxi";

                SharedPreferences prefs = getSharedPreferences("solicitudes_taxi", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(nombreHotel + "_taxi", deseaTaxi);
                editor.putString(nombreHotel + "_aeropuerto", aeropuerto);
                editor.apply();

// ⬇⬇ Guardar el servicio en Firestore ⬇⬇

                /*--------------------moddddd
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                // GUARDAR SERVICIO DE TAXI EN FIRESTORE (para generar QR)
                Map<String, Object> servicio = new HashMap<>();
                servicio.put("nombreHotel", nombreHotel);
                servicio.put("destino", "Aeropuerto Internacional Jorge Chávez");
                servicio.put("estado", "pendiente");
                servicio.put("timestamp", System.currentTimeMillis());

                FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
                if (usuario != null) {
                    servicio.put("idCliente", usuario.getUid());
                }

                // TEMPORAL: puedes asignar un idTaxista fijo si aún no lo eligen dinámicamente
// Ejemplo:
                //servicio.put("idTaxista", "we2XgqecngOv8BLF5YUXIWtJKw42");

                db.collection("servicios_taxi")
                        .add(servicio)
                        .addOnSuccessListener(documentReference -> {
                            String idServicio = documentReference.getId();
                            SharedPreferences prefsQR = getSharedPreferences("servicios_qr", MODE_PRIVATE);
                            prefsQR.edit().putString("idUltimoServicio", idServicio).apply();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SolicitudTaxiActivity.this, "Error al registrar servicio", Toast.LENGTH_SHORT).show();
                        }); */

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
                if (usuario == null) return;

                String uidCliente = usuario.getUid();

// Buscar los datos del cliente
                db.collection("usuarios").document(uidCliente).get()
                        .addOnSuccessListener(clienteDoc -> {
                            String nombreCliente = clienteDoc.getString("nombres");
                            String celularCliente = clienteDoc.getString("numCelular");

                            // Ahora consulta el hotel
                            db.collection("hoteles")
                                    .whereEqualTo("nombre", nombreHotel)
                                    .get()
                                    .addOnSuccessListener(hotelQuery -> {
                                        if (!hotelQuery.isEmpty()) {
                                            String idHotel = hotelQuery.getDocuments().get(0).getId();
                                            String direccion = hotelQuery.getDocuments().get(0).getString("direccion");
                                            Double lat = hotelQuery.getDocuments().get(0).getDouble("ubicacionLat");
                                            Double lng = hotelQuery.getDocuments().get(0).getDouble("ubicacionLng");

                                            Map<String, Object> servicio = new HashMap<>();
                                            servicio.put("nombreHotel", nombreHotel);
                                            servicio.put("idHotel", idHotel);
                                            servicio.put("direccionHotel", direccion);
                                            servicio.put("latitudHotel", lat);
                                            servicio.put("longitudHotel", lng);
                                            servicio.put("destino", "Aeropuerto Internacional Jorge Chávez");
                                            servicio.put("estado", "pendiente");
                                            servicio.put("timestamp", System.currentTimeMillis());
                                            servicio.put("idCliente", uidCliente);
                                            servicio.put("nombreCliente", nombreCliente);
                                            servicio.put("celularCliente", celularCliente);
                                            servicio.put("idReserva", idReserva);
                                            db.collection("servicios_taxi")
                                                    .add(servicio)
                                                    .addOnSuccessListener(documentReference -> {
                                                        String idServicio = documentReference.getId();
                                                        SharedPreferences prefsQR = getSharedPreferences("servicios_qr", MODE_PRIVATE);
                                                        prefsQR.edit().putString("idUltimoServicio", idServicio).apply();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(SolicitudTaxiActivity.this, "Error al registrar servicio", Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {
                                            Toast.makeText(this, "No se encontró información del hotel", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(this, "Error al buscar hotel", Toast.LENGTH_SHORT).show();
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(this, "Error al buscar datos del cliente", Toast.LENGTH_SHORT).show();
                        });



/*------------------------------------------------------------------------------
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> solicitud = new HashMap<>();
                solicitud.put("hotel", nombreHotel);
                solicitud.put("deseaTaxi", deseaTaxi);
                solicitud.put("aeropuerto", aeropuerto);
                solicitud.put("timestamp", System.currentTimeMillis());

                db.collection("solicitudes_taxi")
                        .add(solicitud)
                        .addOnSuccessListener(documentReference -> {
                            // Opcional: mensaje de éxito
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SolicitudTaxiActivity.this, "Error al guardar en Firestore", Toast.LENGTH_SHORT).show();
                        });
//------------------------------------------------------------------------------*/

// GUARDAR NOTIFICACIÓN EN FIRESTORE
                //FirebaseFirestore db = FirebaseFirestore.getInstance();
                Map<String, Object> noti = new HashMap<>();
                noti.put("mensaje", deseaTaxi ?
                        " \uD83D\uDE95 Se generó QR para el servicio de taxi. Puede verlo en la sección Taxi" :
                        "Checkout confirmado sin servicio de taxi");
                noti.put("timestamp", System.currentTimeMillis());
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                String idCliente = currentUser.getUid();
                noti.put("idCliente", idCliente);

                db.collection("notificaciones")
                        .add(noti)
                        .addOnSuccessListener(documentReference -> {
                            // Éxito (opcional)
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(SolicitudTaxiActivity.this, "Error al guardar notificación", Toast.LENGTH_SHORT).show();
                        });

//---------------------------------------------------------------------------------------------------------

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
