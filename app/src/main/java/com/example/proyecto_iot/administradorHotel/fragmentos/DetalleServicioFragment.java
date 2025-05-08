package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.administradorHotel.entity.Servicio;
import com.example.proyecto_iot.databinding.FragmentDetalleServicioBinding;

public class DetalleServicioFragment extends Fragment {

    private FragmentDetalleServicioBinding binding;

    public DetalleServicioFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleServicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Recuperar el objeto Servicio del Bundle
        Servicio servicio = (Servicio) getArguments().getSerializable("servicio");

        if (servicio != null) {
            binding.editTextNombre.setText(servicio.getNombre());
            binding.editTextDescripcion.setText(servicio.getDescripcion());
            binding.editTextPrecio.setText(String.valueOf(servicio.getPrecio()));

            // Cargar imágenes si hay
            if (!servicio.getFotosServicioIds().isEmpty()) {
                binding.img1.setImageResource(servicio.getFotosServicioIds().get(0));
                if (servicio.getFotosServicioIds().size() > 1) {
                    binding.img2.setImageResource(servicio.getFotosServicioIds().get(1));
                } else {
                    binding.img2.setVisibility(View.GONE);
                }
            }
        }

        // Acción del botón de retroceso
        binding.backdetalleservicio.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
