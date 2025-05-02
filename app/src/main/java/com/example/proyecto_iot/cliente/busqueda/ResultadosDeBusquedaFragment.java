package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultadosDeBusquedaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultadosDeBusquedaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ResultadosDeBusquedaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultadosDeBusquedaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultadosDeBusquedaFragment newInstance(String param1, String param2) {
        ResultadosDeBusquedaFragment fragment = new ResultadosDeBusquedaFragment();
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

    private RecyclerView recyclerView;
    private HotelAdapter hotelAdapter;
    private List<Hotel> hotelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resultados_de_busqueda, container, false);

        recyclerView = view.findViewById(R.id.recyclerHoteles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        hotelList = new ArrayList<>();
        hotelList.add(new Hotel("Hotel Caribe", "San Miguel", 2550, R.drawable.hotel1, 5));
        hotelList.add(new Hotel("Hotel Las Rosas", "San Miguel", 355, R.drawable.hotel2, 4));

        hotelAdapter = new HotelAdapter(getContext(), hotelList);
        recyclerView.setAdapter(hotelAdapter);

        return view;
    }
}