package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.content.Context;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentHotelBinding;
import com.google.android.material.button.MaterialButton;
import com.example.proyecto_iot.administradorHotel.EstadoHotelUI;

import java.util.Arrays;
import java.util.List;

public class HotelFragment extends Fragment {

    private FragmentHotelBinding binding;
    private List<MaterialButton> allButtons;

    public HotelFragment() {}

    public static HotelFragment newInstance(String seccion) {
        HotelFragment fragment = new HotelFragment();
        Bundle args = new Bundle();
        args.putString("seccion", seccion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHotelBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allButtons = Arrays.asList(
                binding.btnInfo,
                binding.btnHabitaciones,
                binding.btnServicios,
                binding.btnReportes
        );

        String seccion = EstadoHotelUI.seccionSeleccionada;
        if (seccion == null) {
            seccion = getArguments() != null ? getArguments().getString("seccion", "info") : "info";
        }

        switch (seccion) {
            case "habitaciones":
                loadChildFragment(new HotelHabitacionesFragment());
                updateSelectedButton(binding.btnHabitaciones);
                break;
            case "servicios":
                loadChildFragment(new HotelServicioFragment());
                updateSelectedButton(binding.btnServicios);
                break;
            case "reportes":
                loadChildFragment(new AdminReportesFragment());
                updateSelectedButton(binding.btnReportes);
                break;

            default:
                loadChildFragment(new HotelInfoFragment());
                updateSelectedButton(binding.btnInfo);
                break;
        }

        binding.btnInfo.setOnClickListener(v -> {
            loadChildFragment(new HotelInfoFragment());
            updateSelectedButton(binding.btnInfo);
            EstadoHotelUI.seccionSeleccionada = "info";
        });

        binding.btnHabitaciones.setOnClickListener(v -> {
            loadChildFragment(new HotelHabitacionesFragment());
            updateSelectedButton(binding.btnHabitaciones);
            EstadoHotelUI.seccionSeleccionada = "habitaciones";
        });

        binding.btnServicios.setOnClickListener(v -> {
            loadChildFragment(new HotelServicioFragment());
            updateSelectedButton(binding.btnServicios);
            EstadoHotelUI.seccionSeleccionada = "servicios";
        });

        binding.btnReportes.setOnClickListener(v -> {
            loadChildFragment(new AdminReportesFragment());
            updateSelectedButton(binding.btnReportes);
            EstadoHotelUI.seccionSeleccionada = "reportes";
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (EstadoHotelUI.ultimoBotonSeleccionado != null) {
            updateSelectedButton(EstadoHotelUI.ultimoBotonSeleccionado);
        }
    }

    private void updateSelectedButton(MaterialButton selectedButton) {
        Context context = requireContext();

        for (MaterialButton btn : allButtons) {
            btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.boton_normal));
        }

        selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.boton_seleccionado));
        EstadoHotelUI.ultimoBotonSeleccionado = selectedButton;

        // 🟢 Desplaza automáticamente el botón seleccionado al centro del ScrollView
        selectedButton.post(() -> {
            View parentScroll = (View) selectedButton.getParent().getParent(); // HorizontalScrollView
            if (parentScroll instanceof HorizontalScrollView) {
                int scrollX = selectedButton.getLeft() - (parentScroll.getWidth() - selectedButton.getWidth()) / 2;
                ((HorizontalScrollView) parentScroll).smoothScrollTo(scrollX, 0);
            }
        });
    }


    private void loadChildFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(binding.hotelDynamicContainer.getId(), fragment)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
