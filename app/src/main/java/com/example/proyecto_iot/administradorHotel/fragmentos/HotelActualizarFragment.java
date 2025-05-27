package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentHotelActualizarBinding;

import java.util.ArrayList;
import java.util.List;

public class HotelActualizarFragment extends Fragment {

    private FragmentHotelActualizarBinding binding;
    private final List<String> referencias = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHotelActualizarBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Back button
        binding.backActualizar.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        // Ejemplo inicial
        referencias.add("Parque Kennedy");
        referencias.add("Plasa Sésamo");
        renderizarReferencias();

        // Acción agregar
        binding.btnAgregarReferencia.setOnClickListener(v -> {
            String texto = binding.etReferencia.getText().toString().trim();
            if (!texto.isEmpty() && !referencias.contains(texto)) {
                referencias.add(texto);
                binding.etReferencia.setText("");
                renderizarReferencias();
            }
        });
    }

    private void renderizarReferencias() {
        binding.layoutReferenciasActualizadas.removeAllViews();

        if (referencias.isEmpty()) {
            binding.textReferenciaVacia.setVisibility(View.VISIBLE);
        } else {
            binding.textReferenciaVacia.setVisibility(View.GONE);
        }

        for (String ref : referencias) {
            LinearLayout chip = new LinearLayout(requireContext());
            chip.setOrientation(LinearLayout.HORIZONTAL);
            chip.setBackgroundResource(R.drawable.bg_edittext_simulado);
            chip.setPadding(20, 18, 20, 18);
            chip.setMinimumHeight(60);
            chip.setGravity(Gravity.CENTER_VERTICAL);

            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            chipParams.setMargins(0, 10, 0, 10);
            chip.setLayoutParams(chipParams);

            TextView text = new TextView(requireContext());
            text.setText(ref);
            text.setTextSize(15);
            text.setTextColor(getResources().getColor(android.R.color.black));
            text.setLayoutParams(new LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
            ));

            ImageView icono = new ImageView(requireContext());
            icono.setImageResource(R.drawable.ic_delete);
            icono.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(40, 40);
            iconParams.setMargins(16, 0, 0, 0);
            icono.setLayoutParams(iconParams);

            icono.setOnClickListener(v -> {
                referencias.remove(ref);
                renderizarReferencias();
            });

            chip.addView(text);
            chip.addView(icono);
            binding.layoutReferenciasActualizadas.addView(chip);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
