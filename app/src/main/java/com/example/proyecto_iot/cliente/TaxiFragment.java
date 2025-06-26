package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class TaxiFragment extends Fragment {

    public TaxiFragment() {
        // Constructor vacío requerido
    }

    @Nullable

    /*
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taxi, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerSolicitudesTaxi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Simular datos de prueba
        List<TaxiItem> lista = new ArrayList<>();
        lista.add(new TaxiItem("ABC-123", "Juancito Pérez", "Aeropuerto Internacional Jorge Chavez", R.drawable.roberto, true));
        lista.add(new TaxiItem("DEF-132", "Ppeito Pepin", "Aeropuerto Internacional De Chile", R.drawable.roberto, false));

        TaxiAdapter adapter = new TaxiAdapter(lista, getContext());
        recyclerView.setAdapter(adapter);

        return view;
    }*/
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taxi, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerSolicitudesTaxi);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<TaxiItem> lista = new ArrayList<>();
        TaxiAdapter adapter = new TaxiAdapter(lista, getContext());
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return view;

        String idClienteActual = user.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("servicios_taxi")
                .whereEqualTo("idCliente", idClienteActual)
                //.orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    lista.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombreHotel = doc.getString("nombreHotel");
                        String destino = doc.getString("destino");
                        String estado = doc.getString("estado");
                        String idTaxista = doc.getString("idTaxista");

                        if (idTaxista == null || idTaxista.isEmpty()) {
                            // Aún no se asigna taxista, puedes mostrar datos genéricos o ignorar
                            lista.add(new TaxiItem(
                                    "Sin asignar",
                                    "Pendiente",
                                    destino,
                                    R.drawable.roberto,
                                    false,
                                    doc.getId()
                            ));
                            adapter.notifyDataSetChanged();
                            return;
                        }

                        // Obtener datos del taxista
                        db.collection("usuarios").document(idTaxista).get()
                                .addOnSuccessListener(taxistaDoc -> {
                                    String nombre = taxistaDoc.getString("nombres");
                                    String placa = taxistaDoc.getString("placaAuto");
/*
                                    lista.add(new TaxiItem(
                                            placa != null ? placa : "Sin placa",
                                            nombre != null ? nombre : "Sin nombre",
                                            destino,
                                            R.drawable.roberto,
                                            "pendiente".equalsIgnoreCase(estado)
                                    ));*/
                                    String idServicio = doc.getId();  // ID de Firestore

                                    lista.add(new TaxiItem(
                                            placa != null ? placa : "Sin placa",
                                            nombre != null ? nombre : "Sin nombre",
                                            destino,
                                            R.drawable.roberto,
                                            "pendiente".equalsIgnoreCase(estado),
                                            idServicio
                                    ));




                                    adapter.notifyDataSetChanged();
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar servicios", Toast.LENGTH_SHORT).show();
                });

        return view;
    }





}