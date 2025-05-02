package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetalleHotelFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetalleHotelFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String hotel;

    public DetalleHotelFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetalleHotelFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetalleHotelFragment newInstance(String param1, String param2) {
        DetalleHotelFragment fragment = new DetalleHotelFragment();
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
            hotel = (String) getArguments().getSerializable("hotel");
        }
    }

    private RecyclerView recyclerHabitaciones;
    private HabitacionAdapter habitacionAdapter;
    private List<Habitacion> listaHabitaciones;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detalle_hotel, container, false);
        recyclerHabitaciones = view.findViewById(R.id.recyclerHabitaciones);
        recyclerHabitaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        listaHabitaciones = new ArrayList<>();
        listaHabitaciones.add(new Habitacion("S/. 355 por noche", "El precio más bajo que tenemos",
                "• 1 habitación\n• 2 camas individuales\n• Jacuzzi\n• Aparcamiento cerrado\n• Wi-Fi"));
        listaHabitaciones.add(new Habitacion("S/. 485 por noche", "",
                "• 1 habitación\n• 2 camas matrimoniales\n• Jacuzzi\n• Aparcamiento cerrado\n• Wi-Fi"));

        habitacionAdapter = new HabitacionAdapter(getContext(), listaHabitaciones);
        recyclerHabitaciones.setAdapter(habitacionAdapter);


        return view;
    }

    public static DetalleHotelFragment newInstance(Hotel hotel) {
        DetalleHotelFragment fragment = new DetalleHotelFragment();
        Bundle args = new Bundle();
        args.putSerializable("hotel", (Serializable) hotel);
        fragment.setArguments(args);
        return fragment;
    }

}