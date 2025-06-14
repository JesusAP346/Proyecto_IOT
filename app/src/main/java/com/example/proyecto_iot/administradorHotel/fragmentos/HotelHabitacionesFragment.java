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
import com.example.proyecto_iot.administradorHotel.RegistroHabitacionHotel;
import com.example.proyecto_iot.administradorHotel.adapter.HabitacionAdapter;
import com.example.proyecto_iot.administradorHotel.dto.HabitacionDto;
import com.example.proyecto_iot.administradorHotel.entity.Equipamiento;
import com.example.proyecto_iot.administradorHotel.entity.Habitacion;
import com.example.proyecto_iot.administradorHotel.entity.Servicio;
import com.example.proyecto_iot.administradorHotel.fragmentos.DetalleHabitacionFragment;
import com.example.proyecto_iot.databinding.FragmentHotelHabitacionesBinding;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HotelHabitacionesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HotelHabitacionesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HotelHabitacionesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HotelHabitacionesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HotelHabitacionesFragment newInstance(String param1, String param2) {
        HotelHabitacionesFragment fragment = new HotelHabitacionesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    FragmentHotelHabitacionesBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHotelHabitacionesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.btnRegistrarInformacion.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), RegistroHabitacionHotel.class));
        });

        List<Habitacion> mockHabitaciones = getHabitacionesSimuladas();

        List<HabitacionDto> dtoList = new ArrayList<>();
        for (Habitacion h : mockHabitaciones) dtoList.add(new HabitacionDto(h));

        HabitacionAdapter adapter = new HabitacionAdapter(dtoList, mockHabitaciones, requireContext(), habitacion -> {
            DetalleHabitacionFragment fragment = new DetalleHabitacionFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("habitacion", habitacion);
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });


        binding.recyclerHabitaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerHabitaciones.setAdapter(adapter);
    }


    private List<Habitacion> getHabitacionesSimuladas() {
        return Arrays.asList(
                new Habitacion(
                        1,
                        "Deluxe King",
                        2,
                        1,
                        30,
                        2,
                        100,
                        Arrays.asList(
                                new Equipamiento("TV"),
                                new Equipamiento("Ducha"),
                                new Equipamiento("Escritorio")
                        ),
                        Arrays.asList(
                                new Servicio("Gimnasio", "Acceso libre", 0, Arrays.asList(R.drawable.gimnasio, R.drawable.desayuno)),
                                new Servicio("Desayuno", "Buffet diario", 0, Arrays.asList(R.drawable.gimnasio, R.drawable.desayuno))
                        ),
                        Arrays.asList(R.drawable.hotel1, R.drawable.hotel2)
                ),
                new Habitacion(
                        2,
                        "Suite Ejecutiva",
                        3,
                        1,
                        50,
                        1,
                        200,
                        Arrays.asList(
                                new Equipamiento("Caja fuerte"),
                                new Equipamiento("Wifi"),
                                new Equipamiento("Mini bar")
                        ),
                        Arrays.asList(
                                new Servicio("Spa", "Masajes y jacuzzi", 30, Arrays.asList(R.drawable.gimnasio, R.drawable.desayuno)),
                                new Servicio("Restaurante", "Comida internacional", 20, Arrays.asList(R.drawable.gimnasio, R.drawable.desayuno))
                        ),
                        Arrays.asList(R.drawable.hotel3, R.drawable.hotel4)
                ),
                new Habitacion(
                        3,
                        "Habitación Familiar",
                        4,
                        1,
                        60,
                        3,
                        300,
                        Arrays.asList(
                                new Equipamiento("Cocina"),
                                new Equipamiento("Secador"),
                                new Equipamiento("TV")
                        ),
                        Arrays.asList(
                                new Servicio("Piscina", "Piscina climatizada", 0, Arrays.asList(R.drawable.gimnasio, R.drawable.desayuno)),
                                new Servicio("Transporte", "Shuttle al aeropuerto", 10, Arrays.asList(R.drawable.gimnasio, R.drawable.desayuno))
                        ),
                        Arrays.asList(R.drawable.hotel5, R.drawable.hotel6)
                ),
                new Habitacion(
                        4,
                        "Premium Vista Mar",
                        2,
                        1,
                        45,
                        1,
                        400,
                        Arrays.asList(
                                new Equipamiento("Aire acondicionado"),
                                new Equipamiento("Sofá cama"),
                                new Equipamiento("Cafetera")
                        ),
                        Arrays.asList(
                                new Servicio("Room Service", "Disponible 24h", 15, Arrays.asList(R.drawable.gimnasio, R.drawable.desayuno)),
                                new Servicio("Bar", "Cócteles exclusivos", 12, Arrays.asList(R.drawable.gimnasio, R.drawable.desayuno))
                        ),
                        Arrays.asList(R.drawable.hotel1, R.drawable.hotel2)
                ),
                new Habitacion(
                        5,
                        "Suite Presidencial",
                        5,
                        1,
                        80,
                        2,
                        500,
                        Arrays.asList(
                                new Equipamiento("Balcón"),
                                new Equipamiento("Microondas"),
                                new Equipamiento("Frigobar")
                        ),
                        Arrays.asList(
                                new Servicio("Lavandería", "Servicio exprés", 5, Arrays.asList(R.drawable.gimnasio, R.drawable.desayuno)),
                                new Servicio("Parqueo", "Estacionamiento privado", 8, Arrays.asList(R.drawable.gimnasio, R.drawable.desayuno))
                        ),
                        Arrays.asList(R.drawable.hotel3, R.drawable.hotel4)
                )
        );
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
