package com.example.proyecto_iot.taxista;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentSolicitudesBinding;

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

        List<CarouselItemModel> itemList = Arrays.asList(
                new CarouselItemModel(R.drawable.hotel1, "Hotel Paraíso", "3 solicitudes"),
                new CarouselItemModel(R.drawable.hotel2, "Hotel Amanecer", "15 solicitudes"),
                new CarouselItemModel(R.drawable.hotel3, "Hotel Playa", "1 solicitud")
        );


        CarouselAdapter adapter = new CarouselAdapter(itemList);
        binding.carouselRecyclerView.setAdapter(adapter);

        binding.carouselRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );



        // Listener para la tarjeta
        binding.card2.setOnClickListener(v -> {
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, new SolicitudesHotelFragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Limpieza para evitar memory leaks
    }
}
