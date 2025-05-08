package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.pago.Tarjeta;
import com.example.proyecto_iot.cliente.pago.TarjetaAdapter;

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

// Si el adapter maneja el ítem de "Agregar tarjeta", lo añades como un item especial, o maneja internamente su view type

        TarjetaAdapter adapter = new TarjetaAdapter(listaTarjetas);
        recyclerView.setAdapter(adapter);

    }


}