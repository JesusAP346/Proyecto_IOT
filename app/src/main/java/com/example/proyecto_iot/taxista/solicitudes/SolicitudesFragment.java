package com.example.proyecto_iot.taxista.solicitudes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentSolicitudesBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SolicitudesFragment extends Fragment {

    private FragmentSolicitudesBinding binding;

    public SolicitudesFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate usando ViewBinding
        binding = FragmentSolicitudesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Configurar el RecyclerView como carrusel
        binding.carouselRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        List<CarouselItemModel> itemList = new ArrayList<>();
        itemList.add(new CarouselItemModel(R.drawable.hotel1, "Hotel Paraíso", "3 solicitudes", "SJL", "★★★★☆"));
        itemList.add(new CarouselItemModel(R.drawable.hotel2, "Hotel Amanecer", "15 solicitudes", "Miraflores", "★★★★★"));
        itemList.add(new CarouselItemModel(R.drawable.hotel3, "Hotel Playa", "1 solicitud", "Barranco", "★★★☆☆"));





        CarouselAdapter adapter = new CarouselAdapter(itemList, item -> {
            SolicitudesHotelFragment fragment = new SolicitudesHotelFragment();
            fragment.setNombreHotel(item.title);  // Usamos el setter

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedor_fragment_hijo, fragment)
                    .commit();
        });
        binding.carouselRecyclerView.setAdapter(adapter);

        binding.carouselRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );



        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.contenedor_fragment_hijo, new SolicitudesHotelFragment())
                .commit();



        return view;
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Limpieza para evitar memory leaks
    }
}
