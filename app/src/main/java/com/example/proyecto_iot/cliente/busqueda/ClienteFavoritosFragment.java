package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.proyecto_iot.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ClienteFavoritosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ClienteFavoritosFragment extends Fragment implements HotelAdapter.OnHotelClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ClienteFavoritosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ClienteFavoritosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ClienteFavoritosFragment newInstance(String param1, String param2) {
        ClienteFavoritosFragment fragment = new ClienteFavoritosFragment();
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
        View view = inflater.inflate(R.layout.fragment_cliente_favoritos, container, false);
        recyclerView = view.findViewById(R.id.recyclerHoteles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<String> servicios1 = new ArrayList<>();
        servicios1.add("Wi-Fi");
        servicios1.add("Piscina");
        servicios1.add("Gimnasio");
        servicios1.add("Desayuno incluido");
        servicios1.add("Recepción 24h");
        servicios1.add("Aire acondicionado");
        servicios1.add("Estacionamiento gratuito");

        List<String> servicios2 = new ArrayList<>();
        servicios2.add("Wi-Fi");
        servicios2.add("Spa");
        servicios2.add("Restaurante");
        servicios2.add("Bar");
        servicios2.add("Servicio a la habitación");
        servicios2.add("Transporte al aeropuerto");
        servicios2.add("Centro de negocios");


        hotelList = new ArrayList<>();

        hotelList.add(new Hotel("Hotel Caribe", "San Miguel", 2550, R.drawable.hotel1, 5, servicios1, 1, true));
        hotelList.add(new Hotel("Hotel Las Rosas", "San Pincho", 355, R.drawable.hotel2, 4, servicios2, 1, true));
        hotelList.add(new Hotel("Hotel ASD", "San Isidro", 2550, R.drawable.hotel1, 5, servicios1, 1, true));
        hotelList.add(new Hotel("Hotel JKL", "Pueblo libre", 355, R.drawable.hotel2, 4, servicios2, 1, true));
        hotelList.add(new Hotel("Hotel BYU", "San Miguel", 2550, R.drawable.hotel1, 5, servicios1, 1, true));
        hotelList.add(new Hotel("Hotel 98I", "Miraflores", 355, R.drawable.hotel2, 4, servicios2, 1, true));
        hotelList.add(new Hotel("Hotel MNB", "San Borja", 2550, R.drawable.hotel1, 5, servicios1, 1, true));
        hotelList.add(new Hotel("Hotel FOX", "Surquillo", 355, R.drawable.hotel2, 4, servicios2, 1, true));

        hotelAdapter = new HotelAdapter(getContext(), hotelList, (this));
        recyclerView.setAdapter(hotelAdapter);

        TextView txtBuscar = view.findViewById(R.id.txtBuscar);
        txtBuscar.setOnClickListener(v -> {
            BusquedaFragment busquedaFragment = new BusquedaFragment();
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container_busqueda, busquedaFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return  view;
    }
    @Override
    public void onHotelClick(Hotel hotel, int position) {
        // Crear una instancia del DetalleHotelFragment con los datos del hotel
        DetalleHotelFragment detalleFragment = DetalleHotelFragment.newInstance(hotel);

        // Realizar la transacción del fragmento
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_busqueda, detalleFragment) // Reemplaza "fragmentContainer" con el ID de tu contenedor
                .addToBackStack(null) // Permite volver atrás con el botón Back
                .commit();
    }
}