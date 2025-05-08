package com.example.proyecto_iot.taxista.perfil;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.fragmentos.AdminNotificacionesFragment;
import com.example.proyecto_iot.cliente.MainActivityCliente;
import com.example.proyecto_iot.cliente.busqueda.ClienteBusquedaActivity;
import com.example.proyecto_iot.databinding.FragmentPerfilBinding;

public class PerfilFragment extends Fragment {

    private FragmentPerfilBinding binding;

    public PerfilFragment() {
        // Required empty public constructor
    }

    public static PerfilFragment newInstance(String param1, String param2) {
        PerfilFragment fragment = new PerfilFragment();
        Bundle args = new Bundle();
        args.putString("param1", param1);
        args.putString("param2", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar usando binding
        binding = FragmentPerfilBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // Acción: Información Personal
        binding.informacionPersonal.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), InformacionPersonalActivity.class));
        });

        // Acción: Seguridad
        binding.seguridadPersonal.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), SeguridadActivity.class));
        });

        // Acción: Perfil del Taxista
        binding.cardPerfil.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), PerfilTaxistaActivity.class));
        });

        // Acción: Cambiar a modo cliente
        binding.btnModoCliente.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), ClienteBusquedaActivity.class));
        });

        binding.iconoCampana.setOnClickListener(v -> {
            Fragment notificacionesTaxistaFragment = new NotificacionesTaxistaFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, notificacionesTaxistaFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    public void abrirEditarPerfil() {
        startActivity(new Intent(requireContext(), InformacionPersonalActivity.class));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // evitar memory leaks
    }
}
