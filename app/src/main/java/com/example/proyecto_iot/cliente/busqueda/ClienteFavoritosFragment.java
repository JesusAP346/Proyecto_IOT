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
import java.util.Set;

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

        hotelList = HotelData.getTodosLosHoteles();

        for (Hotel hotel : hotelList) {
            hotel.setFavorito(HotelPreferences.esFavorito(getContext(), hotel.getId()));
        }

        hotelAdapter = new HotelAdapter(getContext(), hotelList, this, (hotel, pos, favorito) -> {
            actualizarEstadoFavoritos(hotel, favorito);
            cargarFavoritos();
        });

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

    private void actualizarEstadoFavoritos(Hotel hotel, boolean favorito) {
        Set<Integer> favoritos = FavoritosStorage.obtenerFavoritos(getContext());
        if (favorito) {
            favoritos.add(hotel.getId());
        } else {
            favoritos.remove(hotel.getId());
        }
        FavoritosStorage.guardarFavoritos(getContext(), favoritos);
    }


    @Override
    public void onResume() {
        super.onResume();
        cargarFavoritos();
    }

    private void cargarFavoritos() {
        Set<Integer> favoritosIds = FavoritosStorage.obtenerFavoritos(getContext());
        List<Hotel> todos = HotelData.getTodosLosHoteles();
        List<Hotel> favoritos = new ArrayList<>();

        for (Hotel hotel : todos) {
            hotel.setFavorito(favoritosIds.contains(hotel.getId()));
            if (hotel.isFavorito()) {
                favoritos.add(hotel);
            }
        }

        hotelList = favoritos;
        hotelAdapter.actualizarLista(hotelList);
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