package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;

import java.util.ArrayList;
import java.util.List;

public class MisReservasFragment extends Fragment {

    public MisReservasFragment() {
        // Constructor vacío requerido
    }

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
    }
}
