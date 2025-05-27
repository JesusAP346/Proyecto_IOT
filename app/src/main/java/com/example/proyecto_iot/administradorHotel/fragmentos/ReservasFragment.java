package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.EstadoReservaUI;
import com.example.proyecto_iot.databinding.FragmentReservasBinding;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

public class ReservasFragment extends Fragment {

    private FragmentReservasBinding binding;
    private List<MaterialButton> allButtons;

    public ReservasFragment() {}

    public static ReservasFragment newInstance(String param1, String param2) {
        ReservasFragment fragment = new ReservasFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allButtons = Arrays.asList(binding.btnHistorial, binding.btnTodas, binding.btnReservas);

        String seccion = EstadoReservaUI.seccionSeleccionada != null
                ? EstadoReservaUI.seccionSeleccionada
                : "todas";

        if (getChildFragmentManager().getFragments().isEmpty()) {
            switch (seccion) {
                case "historial":
                    loadChildFragment(new ReservasHistorialFragment());
                    updateSelectedButton(binding.btnHistorial);
                    break;
                case "pendientes":
                    loadChildFragment(new ReservasPendientesFragment());
                    updateSelectedButton(binding.btnReservas);
                    break;
                default:
                    loadChildFragment(new ReservasTodasFragment());
                    updateSelectedButton(binding.btnTodas);
                    break;
            }
        }

        binding.btnHistorial.setOnClickListener(v -> {
            loadChildFragment(new ReservasHistorialFragment());
            updateSelectedButton(binding.btnHistorial);
            EstadoReservaUI.seccionSeleccionada = "historial";
        });

        binding.btnTodas.setOnClickListener(v -> {
            loadChildFragment(new ReservasTodasFragment());
            updateSelectedButton(binding.btnTodas);
            EstadoReservaUI.seccionSeleccionada = "todas";
        });

        binding.btnReservas.setOnClickListener(v -> {
            loadChildFragment(new ReservasPendientesFragment());
            updateSelectedButton(binding.btnReservas);
            EstadoReservaUI.seccionSeleccionada = "pendientes";
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (binding == null) return;

        String seccion = EstadoReservaUI.seccionSeleccionada != null
                ? EstadoReservaUI.seccionSeleccionada
                : "todas";

        switch (seccion) {
            case "historial":
                updateSelectedButton(binding.btnHistorial);
                loadChildFragment(new ReservasHistorialFragment());
                break;
            case "pendientes":
                updateSelectedButton(binding.btnReservas);
                loadChildFragment(new ReservasPendientesFragment());
                break;
            case "todas":
            default:
                updateSelectedButton(binding.btnTodas);
                loadChildFragment(new ReservasTodasFragment());
                break;
        }
    }



    private void updateSelectedButton(MaterialButton selectedButton) {
        Context context = requireContext();

        for (MaterialButton btn : allButtons) {
            btn.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.boton_normal));
        }

        selectedButton.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.boton_seleccionado));
        EstadoReservaUI.ultimoBotonSeleccionado = selectedButton;
    }

    private void loadChildFragment(Fragment fragment) {
        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.hotel_dynamic_container, fragment)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
