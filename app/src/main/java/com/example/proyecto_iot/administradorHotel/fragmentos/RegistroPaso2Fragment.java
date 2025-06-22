package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.viewmodel.HabitacionViewModel;
import com.example.proyecto_iot.databinding.DialogAgregarEquipamientoBinding;
import com.example.proyecto_iot.databinding.FragmentRegistroPaso2Binding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegistroPaso2Fragment extends Fragment {

    private FragmentRegistroPaso2Binding binding;
    private final List<String> equipamientosSeleccionados = new ArrayList<>();

    private final List<String> opcionesEquipamiento = Arrays.asList(
            "Seleccionar", "TV", "Toallas", "Wifi", "Escritorio", "Caja fuerte", "Aire acondicionado",
            "Mini bar", "Secador de cabello", "Plancha", "Cafetera", "Teléfono", "Ropa de cama extra",
            "Lámpara de lectura", "Espejo ", "Alarma", "Cortinas blackout", "Detector de humo",
            "Utensilios de cocina", "Mesa de noche", "Sillas", "Estante para maletas"
    );

    private HabitacionViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistroPaso2Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HabitacionViewModel.class);

        // Evitar duplicar al retroceder
        equipamientosSeleccionados.clear();
        List<String> desdeViewModel = viewModel.getHabitacion().getValue().getEquipamiento();
        if (desdeViewModel != null) equipamientosSeleccionados.addAll(desdeViewModel);

        setupEquipamientoSpinner();
        setupAgregarEquipamientoButton();
        renderizarChips();

        binding.btnSiguientePaso2.setOnClickListener(v -> {
            if (equipamientosSeleccionados.isEmpty()) {
                Toast.makeText(requireContext(), "Debe agregar al menos un equipamiento", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.actualizarCampo("equipamiento", new ArrayList<>(equipamientosSeleccionados));
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerHabitacion, new RegistroPaso3Fragment())
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void setupEquipamientoSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opcionesEquipamiento);
        binding.spinnerEquipamiento.setAdapter(adapter);

        binding.spinnerEquipamiento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = opcionesEquipamiento.get(position);
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

    private void setupAgregarEquipamientoButton() {
        binding.btnAgregarEquipamiento.setOnClickListener(v -> mostrarDialogoAgregarEquipamiento());
    }

    private void mostrarDialogoAgregarEquipamiento() {
        DialogAgregarEquipamientoBinding dialogBinding = DialogAgregarEquipamientoBinding.inflate(getLayoutInflater());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Agregar Equipamiento")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("AGREGAR", (dialogInterface, i) -> {
                    String nuevoEquipamiento = dialogBinding.editEquipamiento.getText().toString().trim();
                    nuevoEquipamiento = nuevoEquipamiento.substring(0, 1).toUpperCase() + nuevoEquipamiento.substring(1).toLowerCase();

                    if (!nuevoEquipamiento.isEmpty()) {
                        if (!equipamientosSeleccionados.contains(nuevoEquipamiento)) {
                            equipamientosSeleccionados.add(nuevoEquipamiento);
                            renderizarChips();
                        } else {
                            Toast.makeText(requireContext(), "Este equipamiento ya está agregado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Por favor ingrese un nombre válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
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
        binding.textEquipamientoVacio.setVisibility(equipamientosSeleccionados.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
