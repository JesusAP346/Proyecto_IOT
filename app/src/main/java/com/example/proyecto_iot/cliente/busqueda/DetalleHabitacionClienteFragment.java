package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.proyecto_iot.R;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetalleHabitacionClienteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetalleHabitacionClienteFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetalleHabitacionClienteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetalleHabitacionClienteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetalleHabitacionClienteFragment newInstance(String param1, String param2) {
        DetalleHabitacionClienteFragment fragment = new DetalleHabitacionClienteFragment();
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
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_detalle_habitacion_cliente, container, false);
        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });
        ViewPager2 viewPager = view.findViewById(R.id.viewPagerHabitacion);

        List<Integer> imageResIds = Arrays.asList(
                R.drawable.foto_habitacion1,
                R.drawable.foto_habitacion2,
                R.drawable.foto_habitacion3,
                R.drawable.foto_habitacion4,
                R.drawable.foto_habitacion5
        );

        ImageSliderAdapter adapter = new ImageSliderAdapter(requireContext(), imageResIds);
        viewPager.setAdapter(adapter);


        return view;
    }
}