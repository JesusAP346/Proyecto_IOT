package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.databinding.FragmentHotelBinding;

public class HotelFragment extends Fragment {

    private FragmentHotelBinding binding;

    public HotelFragment() {
        // Constructor vacío requerido
    }

    public static HotelFragment newInstance(String seccion) {
        HotelFragment fragment = new HotelFragment();
        Bundle args = new Bundle();
        args.putString("seccion", seccion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHotelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtenemos la sección deseada, por defecto "info"
        String seccion = getArguments() != null ? getArguments().getString("seccion", "info") : "info";

        // Solo si es la primera vez (no volver a sobrecargar al hacer popBackStack)
        if (savedInstanceState == null) {
            switch (seccion) {
                case "habitaciones":
                    loadChildFragment(new HotelHabitacionesFragment());
                    break;
                case "servicios":
                    loadChildFragment(new HotelServicioFragment());
                    break;
                case "reportes":
                    loadChildFragment(new AdminReportesFragment());
                    break;
                default:
                    loadChildFragment(new HotelInfoFragment());
                    break;
            }
        }

        // Botones de navegación entre fragmentos hijos
        binding.btnInfo.setOnClickListener(v -> loadChildFragment(new HotelInfoFragment()));
        binding.btnHabitaciones.setOnClickListener(v -> loadChildFragment(new HotelHabitacionesFragment()));
        binding.btnServicios.setOnClickListener(v -> loadChildFragment(new HotelServicioFragment()));
        binding.btnInfoNada.setOnClickListener(v -> loadChildFragment(new HotelInfoNadaFragment()));
        binding.btnHabitacionesNada.setOnClickListener(v -> loadChildFragment(new HotelHabitacionesNadaFragment()));
        binding.btnServiciosNada.setOnClickListener(v -> loadChildFragment(new HotelServicioNadaFragment()));
        binding.btnReportes.setOnClickListener(v -> loadChildFragment(new AdminReportesFragment()));
    }

    private void loadChildFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(binding.hotelDynamicContainer.getId(), fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}