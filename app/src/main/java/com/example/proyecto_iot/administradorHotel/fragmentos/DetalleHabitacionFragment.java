package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.Equipamiento;
import com.example.proyecto_iot.administradorHotel.entity.Habitacion;
import com.example.proyecto_iot.administradorHotel.entity.Servicio;
import com.example.proyecto_iot.databinding.FragmentDetalleHabitacionBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetalleHabitacionFragment extends Fragment {

    private FragmentDetalleHabitacionBinding binding;
    private final List<String> equipamientosActuales = new ArrayList<>();
    private final List<String> serviciosActuales = new ArrayList<>();

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

            binding.textCantidadAdultos.setText(String.valueOf(habitacion.getCapacidadAdultos()));
            binding.textCantidadNinos.setText(String.valueOf(habitacion.getCapacidadNinos()));

            // Adultos
            binding.btnMasAdultos.setOnClickListener(v -> {
                int count = Integer.parseInt(binding.textCantidadAdultos.getText().toString());
                binding.textCantidadAdultos.setText(String.valueOf(count + 1));
            });
            binding.btnMenosAdultos.setOnClickListener(v -> {
                int count = Integer.parseInt(binding.textCantidadAdultos.getText().toString());
                if (count > 0) binding.textCantidadAdultos.setText(String.valueOf(count - 1));
            });

            // Niños
            binding.btnMasNinos.setOnClickListener(v -> {
                int count = Integer.parseInt(binding.textCantidadNinos.getText().toString());
                binding.textCantidadNinos.setText(String.valueOf(count + 1));
            });
            binding.btnMenosNinos.setOnClickListener(v -> {
                int count = Integer.parseInt(binding.textCantidadNinos.getText().toString());
                if (count > 0) binding.textCantidadNinos.setText(String.valueOf(count - 1));
            });


            binding.txtTipo.setText(habitacion.getTipo());
            binding.txtTamanho.setText(String.valueOf(habitacion.getTamanho()));
            binding.txtHabitacionesRegistradas.setText(String.valueOf(habitacion.getCantidadHabitaciones()));
            binding.txtPrecioPorNoche.setText(String.format("%.2f", habitacion.getPrecioPorNoche()));

            if (!habitacion.getFotosResIds().isEmpty()) {
                binding.img1.setImageResource(habitacion.getFotosResIds().get(0));
                if (habitacion.getFotosResIds().size() > 1) {
                    binding.img2.setImageResource(habitacion.getFotosResIds().get(1));
                }
            }

            for (Equipamiento e : habitacion.getEquipamiento()) {
                equipamientosActuales.add(e.getNombre());
            }
            for (Servicio s : habitacion.getServicio()) {
                serviciosActuales.add(s.getNombre());
            }

            renderizarChips(binding.layoutEquipamientoDinamico, equipamientosActuales, true);
            renderizarChips(binding.layoutServicioDinamico, serviciosActuales, false);
        }

        setupSpinner(binding.spinnerEquipamiento, getEquipamientosDisponibles(), true);
        setupSpinner(binding.spinnerServicios, getServiciosDisponibles(), false);

        binding.backdetallehabitacion.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }

    private List<String> getEquipamientosDisponibles() {
        return Arrays.asList("Seleccionar", "TV", "Ducha", "Caja fuerte", "Aire acondicionado", "Frigobar");
    }

    private List<String> getServiciosDisponibles() {
        return Arrays.asList("Seleccionar", "Gimnasio", "Spa", "Desayuno incluido", "Parqueo", "Room Service");
    }

    private void setupSpinner(Spinner spinner, List<String> opciones, boolean esEquipamiento) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opciones);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = opciones.get(position);
                if (!seleccionado.equals("Seleccionar")) {
                    if (esEquipamiento && !equipamientosActuales.contains(seleccionado)) {
                        equipamientosActuales.add(seleccionado);
                        renderizarChips(binding.layoutEquipamientoDinamico, equipamientosActuales, true);
                    } else if (!esEquipamiento && !serviciosActuales.contains(seleccionado)) {
                        serviciosActuales.add(seleccionado);
                        renderizarChips(binding.layoutServicioDinamico, serviciosActuales, false);
                    }
                    spinner.setSelection(0); // reset
                }
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void renderizarChips(LinearLayout contenedor, List<String> lista, boolean esEquipamiento) {
        contenedor.removeAllViews();
        if (esEquipamiento) {
            verificarEquipamientosVisibles();
        } else {
            verificarServiciosVisibles();
        }
        for (int i = 0; i < lista.size(); i += 2) {
            LinearLayout fila = new LinearLayout(requireContext());
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setWeightSum(2);
            fila.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            fila.setPadding(0, 4, 0, 4);

            fila.addView(crearChip(lista.get(i), lista, esEquipamiento));

            if (i + 1 < lista.size()) {
                fila.addView(crearChip(lista.get(i + 1), lista, esEquipamiento));
            } else {
                // Rellenar con espacio si es impar
                View espacio = new View(requireContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0, 1);
                espacio.setLayoutParams(params);
                fila.addView(espacio);
            }

            contenedor.addView(fila);
        }
    }

    private View crearChip(String texto, List<String> lista, boolean esEquipamiento) {
        LinearLayout chip = new LinearLayout(requireContext());
        chip.setOrientation(LinearLayout.HORIZONTAL);
        chip.setBackgroundResource(R.drawable.bg_edittext_simulado);
        chip.setPadding(16, 12, 16, 12); // Más padding para más altura visual
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
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(36, 36); // Tamaño ícono grande
        iconParams.setMargins(16, 0, 0, 0);
        icono.setLayoutParams(iconParams);

        icono.setOnClickListener(v -> {
            lista.remove(texto);
            renderizarChips(
                    esEquipamiento ? binding.layoutEquipamientoDinamico : binding.layoutServicioDinamico,
                    lista,
                    esEquipamiento
            );
        });

        chip.addView(textView);
        chip.addView(icono);
        return chip;
    }

    private void verificarEquipamientosVisibles() {
        if (equipamientosActuales.isEmpty()) {
            binding.textEquipamientoVacio.setVisibility(View.VISIBLE);
        } else {
            binding.textEquipamientoVacio.setVisibility(View.GONE);
        }
    }

    private void verificarServiciosVisibles() {
        if (serviciosActuales.isEmpty()) {
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
