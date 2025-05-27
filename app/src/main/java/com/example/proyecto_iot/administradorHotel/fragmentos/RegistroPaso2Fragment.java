package com.example.proyecto_iot.administradorHotel.fragmentos;

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
import com.example.proyecto_iot.databinding.FragmentRegistroPaso2Binding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistroPaso2Fragment extends Fragment {

    private FragmentRegistroPaso2Binding binding;
    private final List<String> equipamientosSeleccionados = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistroPaso2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupEquipamientoSpinner();

        binding.btnSiguientePaso2.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerHabitacion, new RegistroPaso3Fragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupEquipamientoSpinner() {
        List<String> opciones = Arrays.asList("Seleccionar", "TV", "Toallas", "Wifi", "Escritorio", "Caja fuerte", "Aire acondicionado");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opciones);
        binding.spinnerEquipamiento.setAdapter(adapter);

        binding.spinnerEquipamiento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = opciones.get(position);
                if (!seleccionado.equals("Seleccionar") && !equipamientosSeleccionados.contains(seleccionado)) {
                    equipamientosSeleccionados.add(seleccionado);
                    renderizarChips();
                }
                binding.spinnerEquipamiento.setSelection(0);
            }

            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        verificarEquipamientosVisibles();
    }

    private void renderizarChips() {
        binding.layoutEquipamientoDinamico.removeAllViews();
        verificarEquipamientosVisibles();

        for (int i = 0; i < equipamientosSeleccionados.size(); i += 2) {
            LinearLayout fila = new LinearLayout(requireContext());
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setWeightSum(2);
            fila.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            fila.setPadding(0, 4, 0, 4);

            fila.addView(crearChip(equipamientosSeleccionados.get(i)));

            if (i + 1 < equipamientosSeleccionados.size()) {
                fila.addView(crearChip(equipamientosSeleccionados.get(i + 1)));
            } else {
                View espacio = new View(requireContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0, 1);
                espacio.setLayoutParams(params);
                fila.addView(espacio);
            }

            binding.layoutEquipamientoDinamico.addView(fila);
        }
    }

    private View crearChip(String texto) {
        LinearLayout chip = new LinearLayout(requireContext());
        chip.setOrientation(LinearLayout.HORIZONTAL);
        chip.setBackgroundResource(R.drawable.customedittext);
        chip.setPadding(16, 12, 16, 12);
        chip.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(0, 0, 8, 0);
        chip.setLayoutParams(params);

        TextView textView = new TextView(requireContext());
        textView.setText(texto);
        textView.setTextSize(14);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        textView.setTextColor(getResources().getColor(android.R.color.black));

        ImageView icono = new ImageView(requireContext());
        icono.setImageResource(R.drawable.ic_delete);
        icono.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(36, 36);
        iconParams.setMargins(16, 0, 0, 0);
        icono.setLayoutParams(iconParams);

        icono.setOnClickListener(v -> {
            equipamientosSeleccionados.remove(texto);
            renderizarChips();
        });

        chip.addView(textView);
        chip.addView(icono);
        return chip;
    }

    private void verificarEquipamientosVisibles() {
        if (equipamientosSeleccionados.isEmpty()) {
            binding.textEquipamientoVacio.setVisibility(View.VISIBLE);
        } else {
            binding.textEquipamientoVacio.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
