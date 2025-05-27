package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.administradorHotel.adapter.ReservaAdapter;
import com.example.proyecto_iot.administradorHotel.dto.ReservaDto;
import com.example.proyecto_iot.administradorHotel.entity.Reserva;
import com.example.proyecto_iot.databinding.FragmentReservasTodasBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReservasTodasFragment extends Fragment {

    private FragmentReservasTodasBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservasTodasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Reserva> reservas = mockReservas();
        List<ReservaDto> dtoList = new ArrayList<>();
        for (Reserva r : reservas) dtoList.add(new ReservaDto(r));

        ReservaAdapter adapter = new ReservaAdapter(dtoList, reservas, requireContext());
        binding.recyclerReservas.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerReservas.setAdapter(adapter);
    }

    private List<Reserva> mockReservas() {
        return Arrays.asList(
                new Reserva(
                        "Jesús Romero", "7654433", "jesus.gonzales@gmail.com", "94787842",
                        "Deluxe", "2 adultos", 30,
                        Arrays.asList("TV", "Toallas", "Wifi", "2 camas", "Escritorio"),
                        "24/04/2025", "26/04/2025",
                        Arrays.asList("Gimnasio", "Desayuno")
                ),
                new Reserva(
                        "María López", "8745521", "maria.lopez@mail.com", "936582741",
                        "Suite Ejecutiva", "1 adulto", 45,
                        Arrays.asList("Mini bar", "Caja fuerte", "Aire acondicionado", "Frigobar"),
                        "01/05/2025", "05/05/2025",
                        Arrays.asList("Spa", "Room Service")
                ),
                new Reserva(
                        "Carlos Fernández", "6523412", "carlosf@gmail.com", "921547836",
                        "Familiar", "2 adultos, 2 niños", 60,
                        Arrays.asList("Cocina", "TV", "Balcón", "Wi-Fi"),
                        "10/06/2025", "15/06/2025",
                        Arrays.asList("Piscina", "Parqueo")
                ),
                new Reserva(
                        "Lucía Gómez", "7921345", "lucia.gomez@hotmail.com", "978452130",
                        "Suite Presidencial", "2 adultos", 80,
                        Arrays.asList("Jacuzzi", "Escritorio", "Sofá cama", "Frigobar"),
                        "20/07/2025", "25/07/2025",
                        Arrays.asList("Desayuno", "Servicio de taxi")
                )
        );
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
