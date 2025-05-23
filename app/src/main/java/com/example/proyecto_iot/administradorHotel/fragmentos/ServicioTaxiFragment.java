package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentDetalleHuespedBinding;
import com.example.proyecto_iot.databinding.FragmentServicioTaxiBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServicioTaxiFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServicioTaxiFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServicioTaxiFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServicioTaxiFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServicioTaxiFragment newInstance(String param1, String param2) {
        ServicioTaxiFragment fragment = new ServicioTaxiFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    FragmentServicioTaxiBinding binding;


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
        binding = FragmentServicioTaxiBinding.inflate(inflater, container, false);


        binding.backserviciotaxi.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}