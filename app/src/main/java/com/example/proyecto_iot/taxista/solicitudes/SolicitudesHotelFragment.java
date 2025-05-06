package com.example.proyecto_iot.taxista.solicitudes;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentSolicitudesHotelBinding;
import com.example.proyecto_iot.taxista.Solicitud;


public class SolicitudesHotelFragment extends Fragment {

    private FragmentSolicitudesHotelBinding binding;
    private String nombreHotel = "Hotel Paraíso";


    public SolicitudesHotelFragment() {
        // Constructor vacío requerido
    }

    public void setNombreHotel(String nombreHotel) {
        this.nombreHotel = nombreHotel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSolicitudesHotelBinding.inflate(inflater, container, false);

        // Mostrar el nombre del hotel en el TextView
        binding.nombreHotel.setText("Hotel: " + nombreHotel);

        // Configurar RecyclerView
        binding.recyclerSolicitudes.setLayoutManager(new LinearLayoutManager(getContext()));

        // Lista que se llenará según el hotel seleccionado
        List<Solicitud> solicitudes = new ArrayList<>();

        // Lógica condicional para llenar solo los datos del hotel seleccionado
        switch (nombreHotel) {
            case "Hotel Paraíso":
                solicitudes.add(new Solicitud("Roberto Tafur", "945 854 123", 5, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.roberto));
                solicitudes.add(new Solicitud("Ricardo Calderón", "945 854 123", 3, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.usuario_10));
                solicitudes.add(new Solicitud("Juan Perez", "945 854 123", 3, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.usuario_11));
                solicitudes.add(new Solicitud("Alejandra Ríos", "945 854 123", 3, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.usuario_12));
                break;

            case "Hotel Amanecer":
                solicitudes.add(new Solicitud("Raul Castillo", "912 345 678", 2, "6 min.\n2.5 km",
                        "Hotel Amanecer", "Miraflores", "Aeropuerto", R.drawable.usuario_10));
                solicitudes.add(new Solicitud("Diego Galindo", "912 345 678", 2, "6 min.\n2.5 km",
                        "Hotel Amanecer", "Miraflores", "Aeropuerto", R.drawable.usuario_11));
                solicitudes.add(new Solicitud("Carolina Sanchez", "912 345 678", 2, "6 min.\n2.5 km",
                        "Hotel Amanecer", "Miraflores", "Aeropuerto", R.drawable.usuario_12));
                break;

            case "Hotel Playa":
                solicitudes.add(new Solicitud("Marco Gómez", "987 654 321", 4, "3 min.\n1.0 km",
                        "Hotel Playa", "Barranco", "Aeropuerto", R.drawable.roberto));
                solicitudes.add(new Solicitud("Leonardo Torres", "987 654 321", 4, "3 min.\n1.0 km",
                        "Hotel Playa", "Barranco", "Aeropuerto", R.drawable.roberto));
                break;

            default:
                // Si no coincide con ninguno, puedes dejarlo vacío o mostrar un mensaje
                break;
        }

        // Asignar al RecyclerView
        SolicitudAdapter adapter = new SolicitudAdapter(solicitudes);
        binding.recyclerSolicitudes.setAdapter(adapter);

        return binding.getRoot();
    }


    public void abrirMapa() {
        Intent intent = new Intent(requireContext(), MapsActivity.class);
        startActivity(intent);
    }




    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

}