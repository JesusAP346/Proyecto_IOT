package com.example.proyecto_iot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SolicitudesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SolicitudesFragment extends Fragment {

    List<CarouselItem> list = new ArrayList<>();



    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SolicitudesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SolicitudesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SolicitudesFragment newInstance(String param1, String param2) {
        SolicitudesFragment fragment = new SolicitudesFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar la vista primero
        View view = inflater.inflate(R.layout.fragment_solicitudes, container, false);

        // Referenciar el ImageCarousel usando la vista inflada
        ImageCarousel carousel = view.findViewById(R.id.carousel);
        carousel.registerLifecycle(getLifecycle());
        List<CarouselItem> list = new ArrayList<>();



        // Agregar imágenes (te recomiendo que uses URLs directas a imágenes .jpg, .png, etc.)
        list.add(new CarouselItem("https://images.pexels.com/photos/261102/pexels-photo-261102.jpeg"));
        list.add(new CarouselItem("https://traveler.marriott.com/es/wp-content/uploads/sites/2/2024/06/6345971-Almare_2C_a_Luxury_Collection_Adult_All-Inclusive_Resort_2C_Isla_Mujeres-1.jpg"));
        list.add(new CarouselItem("https://www.construcia.com/wp-content/uploads/2023/10/Construcia-Hyde-158-700x467.jpg"));





        // Establecer los datos en el carrusel
        carousel.setData(list);


        View card = view.findViewById(R.id.card1); // o el ID que uses realmente
        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamada a metodo definido en MainActivity
                ((MainActivity) requireActivity()).navegarA(new SolicitudesHotelFragment());
            }
        });






        // Devolver la vista inflada
        return view;
    }





}