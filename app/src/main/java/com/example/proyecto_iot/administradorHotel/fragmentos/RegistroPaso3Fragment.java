package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.RegistroServicioDesdeHabitacion;
import com.example.proyecto_iot.databinding.FragmentRegistroPaso3Binding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistroPaso3Fragment extends Fragment {

    private FragmentRegistroPaso3Binding binding;
    private final List<String> serviciosSeleccionados = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistroPaso3Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupSpinnerServicios();

        binding.btnRegistrarServicio.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegistroServicioDesdeHabitacion.class);
            startActivity(intent);
        });

        binding.btnSiguientePaso3.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerHabitacion, new RegistroPaso4Fragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupSpinnerServicios() {
        List<String> opciones = Arrays.asList("Seleccionar", "Gimnasio", "Spa", "Room Service", "Lavandería", "Desayuno incluido", "Pet Friendly");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opciones);
        binding.spinnerServicios.setAdapter(adapter);

        binding.spinnerServicios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = opciones.get(position);
                if (!seleccionado.equals("Seleccionar") && !serviciosSeleccionados.contains(seleccionado)) {
                    serviciosSeleccionados.add(seleccionado);
                    renderizarServicios();
                }
                binding.spinnerServicios.setSelection(0);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        verificarServiciosVisibles();
    }

    private void renderizarServicios() {
        binding.layoutServicioDinamico.removeAllViews();
        verificarServiciosVisibles();

        for (String servicio : serviciosSeleccionados) {
            LinearLayout chip = new LinearLayout(requireContext());
            chip.setOrientation(LinearLayout.HORIZONTAL);
            chip.setBackgroundResource(R.drawable.customedittext);
            chip.setPadding(16, 12, 16, 12);
            chip.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.55),  // 65% del ancho
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            chipParams.setMargins(0, 8, 0, 0);
            chip.setLayoutParams(chipParams);

            TextView textView = new TextView(requireContext());
            textView.setText(servicio);
            textView.setTextSize(16);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            textView.setLayoutParams(tvParams);

            ImageView icono = new ImageView(requireContext());
            icono.setImageResource(R.drawable.ic_delete);
            icono.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(40, 40);
            iconParams.setMargins(16, 0, 0, 0);
            icono.setLayoutParams(iconParams);

            icono.setOnClickListener(v -> {
                serviciosSeleccionados.remove(servicio);
                renderizarServicios();
            });

            chip.addView(textView);
            chip.addView(icono);
            binding.layoutServicioDinamico.addView(chip);
        }
    }

    private void verificarServiciosVisibles() {
        if (serviciosSeleccionados.isEmpty()) {
            binding.textServicioVacio.setVisibility(View.VISIBLE);
        } else {
            binding.textServicioVacio.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
