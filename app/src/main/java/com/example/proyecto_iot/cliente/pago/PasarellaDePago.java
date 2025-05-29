package com.example.proyecto_iot.cliente.pago;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.ArrayList;
import java.util.List;

public class PasarellaDePago extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pasarella_de_pago);


        recyclerView = findViewById(R.id.recyclerTarjetas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Tarjeta> listaTarjetas = TarjetaStorage.obtenerTarjetas(this);
        TarjetaAdapter adapter = new TarjetaAdapter(listaTarjetas);
        recyclerView.setAdapter(adapter);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Tarjeta> listaTarjetasActualizadas = TarjetaStorage.obtenerTarjetas(this);
        TarjetaAdapter adapter = new TarjetaAdapter(listaTarjetasActualizadas);
        recyclerView.setAdapter(adapter);
    }


}