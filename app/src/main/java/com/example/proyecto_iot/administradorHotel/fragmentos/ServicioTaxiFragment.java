package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.administradorHotel.entity.ReservaCompletaHotel;
import com.example.proyecto_iot.databinding.FragmentServicioTaxiBinding;

public class ServicioTaxiFragment extends Fragment {

    private FragmentServicioTaxiBinding binding;
    private ReservaCompletaHotel reservaCompleta;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            reservaCompleta = (ReservaCompletaHotel) getArguments().getSerializable("reservaCompleta");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentServicioTaxiBinding.inflate(inflater, container, false);

        binding.backserviciotaxi.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // Puedes acceder a los datos de la reserva así:
        if (reservaCompleta != null) {

            String idCliente = reservaCompleta.getReserva().getIdCliente();
            String idHotel = reservaCompleta.getReserva().getIdHotel();

            Log.d("ServicioTaxiFragment", "idCliente: " + idCliente + ", idHotel: " + idHotel);

        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
