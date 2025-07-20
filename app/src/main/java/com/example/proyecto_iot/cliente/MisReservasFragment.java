package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class MisReservasFragment extends Fragment {

    public MisReservasFragment() {
        // Constructor vacío requerido
    }
/*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_reservas, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*Lista de ejemplo (fotos deben estar en drawable)
        List<Reserva> listaReservas = new ArrayList<>();
        listaReservas.add(new Reserva("Hotel RascaCielos", "San Miguel", "Activo", R.drawable.hotel1));
        listaReservas.add(new Reserva("Hotel Telodije", "Miraflores", "Finalizado", R.drawable.hotel2));
        listaReservas.add(new Reserva("Hotel Ventura", "La Molina", "Activo", R.drawable.hotel1)); */
/*
        List<Reserva> listaReservas = new ArrayList<>();
        listaReservas.add(new Reserva(
                "Hotel RascaCielos", "San Miguel", "Activo", R.drawable.hotel1,
                "12-05-2025", "16-05-2025", "S/.500"));

        listaReservas.add(new Reserva(
                "Hotel Telodije", "Miraflores", "Finalizado", R.drawable.hotel2,
                "12-05-2025", "16-05-2025", "S/.500"));

        listaReservas.add(new Reserva(
                "Hotel Ventura", "La Molina", "Activo", R.drawable.hotel1,
                "15-05-2025", "18-05-2025", "S/.600"));


        ReservaAdapter adapter = new ReservaAdapter(getContext(), listaReservas);
        recyclerView.setAdapter(adapter);

        return view;
    }*/


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mis_reservas, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerReservas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Reserva> listaReservas = new ArrayList<>();
        ReservaAdapter adapter = new ReservaAdapter(getContext(), listaReservas);
        recyclerView.setAdapter(adapter);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return view;

        String idCliente = user.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("reservas")
                .whereEqualTo("idCliente", idCliente)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaReservas.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String idHotel = doc.getString("idHotel");
                        String estado = doc.getString("estado");
                        String entrada = doc.getString("fechaEntrada");
                        String salida = doc.getString("fechaSalida");
                        String monto = doc.getString("monto");

                        // Obtener info del hotel con su ID
                        db.collection("hoteles").document(idHotel).get()
                                .addOnSuccessListener(hotelDoc -> {
                                    String nombreHotel = hotelDoc.getString("nombre");
                                    String ubicacion = hotelDoc.getString("direccion");
                                    int imagen = R.drawable.hotel1; // temporal (puedes cargar desde URL si lo necesitas)

                                    listaReservas.add(new Reserva(

                                            nombreHotel,
                                            ubicacion,
                                            estado,
                                            imagen,
                                            entrada,
                                            salida,
                                            "S/." + monto,
                                            doc.getId()
                                    ));
                                    adapter.notifyDataSetChanged();
                                });
                    }
                });

        return view;
    }

}
