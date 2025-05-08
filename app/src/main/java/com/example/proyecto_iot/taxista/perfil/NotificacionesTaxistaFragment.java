package com.example.proyecto_iot.taxista.perfil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.adapters.NotificacionesAdapter;
import com.example.proyecto_iot.databinding.FragmentNotificacionesTaxistaBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacionesTaxistaFragment extends Fragment {

    private FragmentNotificacionesTaxistaBinding binding;
    private List<Notificacion> listaNotificaciones;
    private NotificacionesAdapter adapter;

    public NotificacionesTaxistaFragment() {}

    public static NotificacionesTaxistaFragment newInstance() {
        return new NotificacionesTaxistaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificacionesTaxistaBinding.inflate(inflater, container, false);

        // Acción del botón de retroceso
        binding.backdenotificaciones.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Inicializar datos
        listaNotificaciones = new ArrayList<>();

        for (int i = 1; i <= 15; i++) {
            listaNotificaciones.add(new Notificacion(
                    "Nuevo pedido asignado: Recoge al huésped en Hotel Libertador",
                    "Hoy, 08:" + (10 + i) + " AM",
                    R.drawable.ic_hotel_solid));
        }

        for (int i = 1; i <= 10; i++) {
            listaNotificaciones.add(new Notificacion(
                    "El huésped canceló el servicio asignado desde el Hotel Meliá",
                    "Hoy, 09:" + (10 + i) + " AM",
                    R.drawable.ic_delete));
        }

        for (int i = 1; i <= 15; i++) {
            listaNotificaciones.add(new Notificacion(
                    "QR escaneado correctamente. Servicio #" + i + " finalizado con éxito.",
                    "Hoy, 10:" + (5 + i) + " AM",
                    R.drawable.ic_qr));
        }

        for (int i = 1; i <= 10; i++) {
            listaNotificaciones.add(new Notificacion(
                    "Nueva ubicación de recogida actualizada por Hotel Casa Andina",
                    "Hoy, 11:" + (10 + i) + " AM",
                    R.drawable.ic_location));
        }

        // Mezclar las notificaciones aleatoriamente
        Collections.shuffle(listaNotificaciones);


        adapter = new NotificacionesAdapter(listaNotificaciones);
        binding.recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerNotificaciones.setAdapter(adapter);

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
