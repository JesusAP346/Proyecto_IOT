package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ValoracionesActivity extends AppCompatActivity {

    private RecyclerView recyclerValoraciones;
    private ValoracionAdapter valoracionAdapter;
    private List<Valoracion> listaValoraciones;
    private FirebaseFirestore db;
    private TextView textTituloValoraciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoraciones);

        db = FirebaseFirestore.getInstance();

        initViews();
        setupRecyclerView();

        String hotelId = getIntent().getStringExtra("hotelId");
        String nombreHotel = getIntent().getStringExtra("nombreHotel");

        textTituloValoraciones.setText("Opiniones de " + nombreHotel);

        if (hotelId != null) {
            cargarValoraciones(hotelId);
        }
    }

    private void initViews() {
        textTituloValoraciones = findViewById(R.id.textTituloValoraciones);
        recyclerValoraciones = findViewById(R.id.recyclerValoraciones);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        listaValoraciones = new ArrayList<>();
        valoracionAdapter = new ValoracionAdapter(this, listaValoraciones);
        recyclerValoraciones.setLayoutManager(new LinearLayoutManager(this));
        recyclerValoraciones.setAdapter(valoracionAdapter);
    }

    private void cargarValoraciones(String hotelId) {
        db.collection("hoteles")
                .document(hotelId)
                .collection("valoraciones")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        listaValoraciones.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String idCliente = document.getString("idCliente");
                            Long estrellas = document.getLong("estrellas");
                            String observaciones = document.getString("observaciones");
                            Map<String, String> respuestas = (Map<String, String>) document.get("respuestas");

                            if (idCliente != null) {
                                // Cargar datos del usuario
                                cargarDatosUsuario(idCliente, estrellas, observaciones, respuestas);
                            }
                        }
                    } else {
                        Log.e("VALORACIONES", "Error al cargar valoraciones", task.getException());
                    }
                });
    }

    private void cargarDatosUsuario(String idCliente, Long estrellas, String observaciones, Map<String, String> respuestas) {
        db.collection("usuarios")
                .document(idCliente)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombres = documentSnapshot.getString("nombres");
                        String apellidos = documentSnapshot.getString("apellidos");

                        String nombreCompleto = (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
                        nombreCompleto = nombreCompleto.trim();

                        if (nombreCompleto.isEmpty()) {
                            nombreCompleto = "Usuario anónimo";
                        }

                        // Crear objeto Valoracion
                        Valoracion valoracion = new Valoracion();
                        valoracion.setNombreUsuario(nombreCompleto);
                        valoracion.setEstrellas(estrellas != null ? estrellas.intValue() : 0);
                        valoracion.setObservaciones(observaciones);

                        if (respuestas != null) {
                            valoracion.setServicio(respuestas.get("¿Qué le pareció el servicio?"));
                            valoracion.setVolveria(respuestas.get("¿Volvería a hospedarse aquí? ¿Por qué?"));
                        }

                        listaValoraciones.add(valoracion);
                        valoracionAdapter.notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("VALORACIONES", "Error al cargar usuario", e);
                });
    }
}