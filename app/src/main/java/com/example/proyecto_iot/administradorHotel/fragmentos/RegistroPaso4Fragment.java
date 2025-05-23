package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentRegistroPaso4Binding;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistroPaso4Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistroPaso4Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistroPaso4Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistroPaso4Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistroPaso4Fragment newInstance(String param1, String param2) {
        RegistroPaso4Fragment fragment = new RegistroPaso4Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    FragmentRegistroPaso4Binding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistroPaso4Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.carousel.registerLifecycle(getLifecycle());

        List<CarouselItem> list = new ArrayList<>();
        list.add(new CarouselItem(R.drawable.hotel1));
        list.add(new CarouselItem(R.drawable.hotel2));
        list.add(new CarouselItem(R.drawable.hotel3));

        binding.carousel.setData(list);


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}