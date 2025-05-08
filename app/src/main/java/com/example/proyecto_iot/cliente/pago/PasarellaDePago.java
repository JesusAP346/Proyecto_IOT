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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pasarella_de_pago);


        RecyclerView recyclerView = findViewById(R.id.recyclerTarjetas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Tarjeta> listaTarjetas = new ArrayList<>();
        listaTarjetas.add(new Tarjeta("INTERBANK", "**** 1826", "KELY TAFUR", "Crédito", "Visa"));
        listaTarjetas.add(new Tarjeta("BANCO DE CREDITO DEL PERU", "**** 5089", "ROBERTO TAFUR", "Débito", "Visa"));


        TarjetaAdapter adapter = new TarjetaAdapter(listaTarjetas);
        recyclerView.setAdapter(adapter);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish();
        });


    }


}