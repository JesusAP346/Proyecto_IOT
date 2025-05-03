package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentDetalleHuespedBinding;
import com.google.android.material.button.MaterialButton;

import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetalleHuespedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetalleHuespedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DetalleHuespedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetalleHuespedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetalleHuespedFragment newInstance(String param1, String param2) {
        DetalleHuespedFragment fragment = new DetalleHuespedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    FragmentDetalleHuespedBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetalleHuespedBinding.inflate(inflater, container, false);

        // Retroceso
        binding.backdetallehuesped.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        // 👉 Acción del botón "Realizar Check-out"
        binding.btnCheckout.setOnClickListener(v -> {
            // Cargar el fragmento CheckoutFragment
            Fragment checkoutFragment = new CheckoutFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, checkoutFragment) // Usa el mismo contenedor del BottomNav
                    .addToBackStack(null) // Permite volver con el botón atrás
                    .commit();
        });

        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}