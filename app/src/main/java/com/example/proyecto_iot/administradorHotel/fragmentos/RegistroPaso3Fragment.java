package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.RegistroServicioDesdeHabitacion;
import com.example.proyecto_iot.databinding.FragmentRegistroPaso3Binding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegistroPaso3Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegistroPaso3Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegistroPaso3Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegistroPaso3Fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegistroPaso3Fragment newInstance(String param1, String param2) {
        RegistroPaso3Fragment fragment = new RegistroPaso3Fragment();
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

    FragmentRegistroPaso3Binding binding;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistroPaso3Binding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Ir a RegistroPaso4Fragment al hacer clic en "Siguiente"
        binding.btnSiguientePaso3.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainerHabitacion, new RegistroPaso4Fragment())
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onViewCreated( View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Acción al hacer clic en "Registrar Servicio"
        binding.btnRegistrarServicio.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegistroServicioDesdeHabitacion.class);
            startActivity(intent);
        });

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}