package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_iot.SuperAdmin.AdminDataStore;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.adapter.AdministradoresAdapter;
import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;

import java.util.ArrayList;
import java.util.List;

public class fragment_administradores_superadmin extends Fragment {

    private List<AdministradoresDomain> adminsList = AdminDataStore.adminsList;

    private RecyclerView recyclerView;
    private AdministradoresAdapter administradoresAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public fragment_administradores_superadmin() {
        // Constructor vacío requerido
    }

    public static fragment_administradores_superadmin newInstance(String param1, String param2) {
        fragment_administradores_superadmin fragment = new fragment_administradores_superadmin();
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

        initData();  // puedes dejarlo aquí
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout del fragmento
        View view = inflater.inflate(R.layout.fragment_administradores_superadmin, container, false);

        // Inicializa el RecyclerView aquí con `view.findViewById(...)`
        recyclerView = view.findViewById(R.id.rv_music);
        administradoresAdapter = new AdministradoresAdapter(adminsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(administradoresAdapter);


        Button btnAgregar = view.findViewById(R.id.button2); // ID del botón "Agregar Administrador"
        btnAgregar.setOnClickListener(v -> {
            FragmentGestionAdministradorSuperadmin fragment = FragmentGestionAdministradorSuperadmin.newInstance(
                    new AdministradoresDomain("", "", "", "", "", "", "Administrador"), // datos vacíos
                    true,  // modo edición activado desde el inicio
                    -1     // índice -1 indica "nuevo"
            );

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (administradoresAdapter != null) {
            administradoresAdapter.notifyDataSetChanged();
        }
    }



    private void initData() {

    }
}
