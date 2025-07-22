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
//ajajajajaajajajaj
    @Nullable
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
                .addSnapshotListener((queryDocumentSnapshots, error) -> {
                    if (error != null) {
                        Toast.makeText(getContext(), "Error en tiempo real", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    lista.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String nombreHotel = doc.getString("nombreHotel");
                        String destino = doc.getString("destino");
                        String estado = doc.getString("estado");
                        String idTaxista = doc.getString("idTaxista");

                        if (idTaxista == null || idTaxista.isEmpty()) {
                            lista.add(new TaxiItem(
                                    "Sin asignar",
                                    "Pendiente",
                                    destino,
                                    R.drawable.roberto,
                                    false,
                                    doc.getId(),
                                    0.0, 0.0,
                                    0.0, 0.0,
                                    "",
                                    ""
                            ));
                            adapter.notifyDataSetChanged();
                            continue;
                        }

                        db.collection("usuarios").document(idTaxista).get()
                                .addOnSuccessListener(taxistaDoc -> {
                                    String nombre = taxistaDoc.getString("nombres");
                                    String placa = taxistaDoc.getString("placaAuto");
                                    String urlFoto = taxistaDoc.getString("urlFotoPerfil");
                                    Double lat = doc.getDouble("latTaxista");
                                    Double lng = doc.getDouble("longTaxista");
                                    String idServicio = doc.getId();

                                    db.collection("hoteles")
                                            .whereEqualTo("nombre", nombreHotel)
                                            .get()
                                            .addOnSuccessListener(hotelDocs -> {
                                                for (QueryDocumentSnapshot hotelDoc : hotelDocs) {
                                                    Double latHotel = hotelDoc.getDouble("ubicacionLat");
                                                    Double lngHotel = hotelDoc.getDouble("ubicacionLng");

                                                    boolean activa = "aceptado".equalsIgnoreCase(estado);
                                                    lista.add(new TaxiItem(
                                                            placa != null ? placa : "Sin placa",
                                                            nombre != null ? nombre : "Sin nombre",
                                                            destino,
                                                            R.drawable.roberto,
                                                            activa,
                                                            idServicio,
                                                            lat != null ? lat : 0,
                                                            lng != null ? lng : 0,
                                                            latHotel != null ? latHotel : 0,
                                                            lngHotel != null ? lngHotel : 0,
                                                            idTaxista,
                                                            urlFoto != null ? urlFoto : ""
                                                    ));
                                                    adapter.notifyDataSetChanged();
                                                }
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(getContext(), "Error al obtener ubicación del hotel", Toast.LENGTH_SHORT).show();
                                            });
                                });
                    }
                });

        return view;
    }
}
