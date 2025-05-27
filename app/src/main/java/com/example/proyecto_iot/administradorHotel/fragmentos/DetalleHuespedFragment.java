package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.Reserva;
import com.example.proyecto_iot.databinding.FragmentDetalleHuespedBinding;

public class DetalleHuespedFragment extends Fragment {

    private FragmentDetalleHuespedBinding binding;
    private Reserva reserva;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDetalleHuespedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null && getArguments().containsKey("reserva")) {
            reserva = (Reserva) getArguments().getSerializable("reserva");
            if (reserva != null) {
                mostrarDatosReserva();
            }
        }

        binding.backdetallehuesped.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());

        binding.btnCheckout.setOnClickListener(v -> {
            CheckoutFragment checkoutFragment = new CheckoutFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("reserva", reserva);
            checkoutFragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, checkoutFragment)
                    .addToBackStack(null)
                    .commit();
        });
    }

    private void mostrarDatosReserva() {
        // Datos huésped
        binding.textNombre.setText(reserva.getNombreCompleto());
        binding.textDni.setText(reserva.getDni());
        binding.textCorreo.setText(reserva.getCorreo());
        binding.textTelefono.setText(reserva.getTelefono());

        // Detalles habitación
        binding.textTipoHabitacion.setText(reserva.getTipoHabitacion());
        binding.textCapacidad.setText(reserva.getCapacidad());
        binding.textTamano.setText(reserva.getTamanioM2() + "");

        // Check-in / Check-out
        binding.checkinText.setText("Check-in\n" + reserva.getCheckIn());
        binding.checkoutText.setText("Check-out\n" + reserva.getCheckOut());

        // Equipamientos (dinámico, en GridLayout de 2 columnas)
        binding.gridEquipamiento.removeAllViews();
        GridLayout grid = binding.gridEquipamiento;
        grid.setColumnCount(2);

        for (int i = 0; i < reserva.getEquipamientos().size(); i++) {
            String item = reserva.getEquipamientos().get(i);

            TextView textView = new TextView(requireContext());
            textView.setText(item);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setTextSize(15);

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.rowSpec = GridLayout.spec(i / 2);
            params.columnSpec = GridLayout.spec(i % 2, 1f); // ocupa mitad exacta
            params.width = 0; // necesario para que columnSpec 1f funcione
            params.setMargins(0, 8, 0, 8);

            textView.setLayoutParams(params);
            grid.addView(textView);
        }



        // Servicios adicionales (uno debajo del otro en LinearLayout)
        binding.layoutServiciosDinamicos.removeAllViews();
        for (String servicio : reserva.getServiciosAdicionales()) {
            TextView textView = new TextView(requireContext());
            textView.setText(servicio);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            textView.setTextSize(15);
            textView.setPadding(0, 4, 0, 4);
            binding.layoutServiciosDinamicos.addView(textView);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
