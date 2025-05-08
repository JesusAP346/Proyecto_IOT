package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.administradorHotel.dto.HabitacionDto;
import com.example.proyecto_iot.administradorHotel.entity.Habitacion;
import com.example.proyecto_iot.databinding.FragmentDetalleHabitacionBinding;

public class DetalleHabitacionFragment extends Fragment {

    private FragmentDetalleHabitacionBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleHabitacionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Habitacion habitacion = (Habitacion) getArguments().getSerializable("habitacion");

        if (habitacion != null) {
            binding.txtTipo.setText(habitacion.getTipo());
            binding.txtCapacidad.setText(habitacion.getCapacidad());
            binding.txtTamanho.setText(String.valueOf(habitacion.getTamanho()));

            if (!habitacion.getFotosResIds().isEmpty()) {
                binding.img1.setImageResource(habitacion.getFotosResIds().get(0));
                if (habitacion.getFotosResIds().size() > 1) {
                    binding.img2.setImageResource(habitacion.getFotosResIds().get(1));
                }
            }

            // TODO: Mostrar lista de equipamientos y servicios
        }

        binding.backdetallehabitacion.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}