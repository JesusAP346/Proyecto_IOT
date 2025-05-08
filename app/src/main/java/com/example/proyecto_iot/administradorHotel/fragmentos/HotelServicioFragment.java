package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.RegistroServicioDesdeOpciones;
import com.example.proyecto_iot.administradorHotel.adapter.ServicioAdapter;
import com.example.proyecto_iot.administradorHotel.dto.ServicioDto;
import com.example.proyecto_iot.administradorHotel.entity.Servicio;
import com.example.proyecto_iot.databinding.FragmentHotelServicioBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HotelServicioFragment extends Fragment {

    FragmentHotelServicioBinding binding;

    public HotelServicioFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHotelServicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRegistrarServicio.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegistroServicioDesdeOpciones.class);
            startActivity(intent);
        });

        List<Servicio> listaServicios = obtenerServiciosMock();
        List<ServicioDto> dtoList = new ArrayList<>();
        for (Servicio s : listaServicios) dtoList.add(new ServicioDto(s));

        ServicioAdapter adapter = new ServicioAdapter(dtoList, listaServicios, requireContext(), servicio -> {
            DetalleServicioFragment fragment = new DetalleServicioFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("servicio", servicio);
            fragment.setArguments(bundle);

            // 🧠 Reemplazamos el fragmento global para mostrar pantalla completa
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerServicios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerServicios.setAdapter(adapter);
    }

    private List<Servicio> obtenerServiciosMock() {
        return Arrays.asList(
                new Servicio("Gimnasio", "Acceso libre", 0, Arrays.asList(R.drawable.gimnasio, R.drawable.taxi)),
                new Servicio("Desayuno", "Buffet internacional", 20, Arrays.asList(R.drawable.desayuno)),
                new Servicio("Transporte", "Shuttle al aeropuerto", 15, Arrays.asList(R.drawable.taxi))
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
