package com.example.proyecto_iot.cliente;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FormularioCheckoutActivity extends AppCompatActivity {

    private String idHotel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_formulario_checkout);

        TextView tvNombreHotel = findViewById(R.id.tvNombreHotel);
        String nombreHotel = getIntent().getStringExtra("nombreHotel");

        idHotel = getIntent().getStringExtra("idHotel");
        Log.d("FormularioCheckout", "ID del hotel recibido: " + idHotel);

        if (nombreHotel != null) {
            tvNombreHotel.setText(nombreHotel);
        }

        // validación aquí
        EditText pregunta1 = findViewById(R.id.pregunta1);
        EditText pregunta2 = findViewById(R.id.pregunta2);
        EditText observaciones = findViewById(R.id.observaciones);
        RatingBar ratingBar = findViewById(R.id.ratingBar);

        Button btnEnviar = findViewById(R.id.btnEnviar);

        btnEnviar.setOnClickListener(v -> {
            String p1 = pregunta1.getText().toString().trim();
            String p2 = pregunta2.getText().toString().trim();
            String obs = observaciones.getText().toString().trim();
            float estrellas = ratingBar.getRating();

            if (p1.isEmpty() || p2.isEmpty() || obs.isEmpty() || estrellas == 0) {
                new AlertDialog.Builder(FormularioCheckoutActivity.this)
                        .setTitle("Por favor, complete todos los campos y la valoración")
                        .setPositiveButton("OK", null)
                        .show();
            } else {
                // Guardar en SharedPreferences como respaldo local
                SharedPreferences prefs = getSharedPreferences("valoraciones", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(nombreHotel + "_p1", p1);
                editor.putString(nombreHotel + "_p2", p2);
                editor.putString(nombreHotel + "_obs", obs);
                editor.putFloat(nombreHotel + "_estrellas", estrellas);
                editor.apply();

                // Obtener usuario autenticado
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String idCliente = currentUser.getUid();

                    // Guardar valoración en Firestore
                    guardarValoracionFirestore(idCliente, estrellas, p1, p2, obs, nombreHotel);
                } else {
                    Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void guardarValoracionFirestore(String idCliente, float estrellas, String p1, String p2, String obs, String nombreHotel) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Crear el mapa con los datos de la valoración
        Map<String, Object> valoracion = new HashMap<>();
        valoracion.put("idCliente", idCliente);
        valoracion.put("estrellas", estrellas);
        valoracion.put("timestamp", System.currentTimeMillis());
        valoracion.put("observaciones", obs);

        // Crear un mapa para las respuestas de las preguntas
        Map<String, String> respuestas = new HashMap<>();
        respuestas.put("¿Qué le pareció el servicio?", p1);
        respuestas.put("¿Volvería a hospedarse aquí? ¿Por qué?", p2);
        valoracion.put("respuestas", respuestas);

        // Guardar en la subcolección "valoraciones" del hotel específico
        db.collection("hoteles")
                .document(idHotel)
                .collection("valoraciones")
                .add(valoracion)
                .addOnSuccessListener(documentReference -> {
                    Log.d("FormularioCheckout", "Valoración guardada con ID: " + documentReference.getId());

                    // 🔔 Crear notificación
                    Map<String, Object> noti = new HashMap<>();
                    noti.put("mensaje", "✅ Checkout confirmado en " + nombreHotel);
                    noti.put("timestamp", System.currentTimeMillis());
                    noti.put("idHotel", idHotel);
                    noti.put("idCliente", idCliente);

                    db.collection("notificaciones").add(noti)
                            .addOnSuccessListener(notificationDoc -> {
                                Log.d("FormularioCheckout", "Notificación creada");
                            })
                            .addOnFailureListener(e -> {
                                Log.w("FormularioCheckout", "Error al crear notificación", e);
                            });

                    Toast.makeText(FormularioCheckoutActivity.this, "Valoración enviada exitosamente", Toast.LENGTH_SHORT).show();

                    // Continuar al siguiente paso
                    Intent intent = new Intent(FormularioCheckoutActivity.this, SolicitudTaxiActivity.class);
                    intent.putExtra("nombreHotel", nombreHotel);
                    intent.putExtra("idHotel", idHotel);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    Log.w("FormularioCheckout", "Error al guardar valoración", e);
                    Toast.makeText(FormularioCheckoutActivity.this, "Error al enviar valoración. Intente nuevamente.", Toast.LENGTH_SHORT).show();
                });
    }
}