package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.PagPrincipalAdmin;
import com.example.proyecto_iot.cliente.NotificacionesFragment;
import com.example.proyecto_iot.databinding.FragmentHomeBinding;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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


    FragmentHomeBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate con ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.iconoCampana.setOnClickListener(v -> {
            // ✅ Reemplazar el fragmento padre en MainActivity
            Fragment adminNotificacionesFragment = new AdminNotificacionesFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, adminNotificacionesFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnHabitaciones.setOnClickListener(v -> {
            // Cambia visualmente la pestaña seleccionada en el BottomNavigationView
            PagPrincipalAdmin activity = (PagPrincipalAdmin) requireActivity();
            activity.seleccionarTab(R.id.hotel);

            // Reemplaza manualmente el fragmento con la sección de habitaciones
            Fragment hotelFragment = HotelFragment.newInstance("habitaciones");
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, hotelFragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.btnServiciosExtras.setOnClickListener(v -> {
            // Cambia visualmente la pestaña a "Hotel"
            PagPrincipalAdmin activity = (PagPrincipalAdmin) requireActivity();
            activity.seleccionarTab(R.id.hotel);

            // Reemplaza con la sección de servicios cargada
            Fragment hotelFragment = HotelFragment.newInstance("servicios");
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, hotelFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnReportes.setOnClickListener(v -> {
            // Cambia visualmente la pestaña a "Hotel"
            PagPrincipalAdmin activity = (PagPrincipalAdmin) requireActivity();
            activity.seleccionarTab(R.id.hotel);

            // Reemplaza con la sección de servicios cargada
            Fragment hotelFragment = HotelFragment.newInstance("reportes");
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, hotelFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnReservas.setOnClickListener(v -> {
            // Cambia visualmente la pestaña a "Hotel"
            PagPrincipalAdmin activity = (PagPrincipalAdmin) requireActivity();
            activity.seleccionarTab(R.id.reservas);

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}