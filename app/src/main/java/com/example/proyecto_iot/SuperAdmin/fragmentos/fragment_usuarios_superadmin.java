package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//Para el buscador
import android.widget.SearchView;
import java.util.ArrayList;


import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.UsuariosDataStore;
import com.example.proyecto_iot.SuperAdmin.adapter.UsuariosAdapter;
import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;
import com.example.proyecto_iot.SuperAdmin.domain.UsuariosDomain;

import java.util.List;

public class fragment_usuarios_superadmin extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private UsuariosAdapter usuariosAdapter;
    private List<UsuariosDomain> usuariosList = UsuariosDataStore.usuariosList;

    public fragment_usuarios_superadmin() {
        // Required empty public constructor
    }

    public static fragment_usuarios_superadmin newInstance(String param1, String param2) {
        fragment_usuarios_superadmin fragment = new fragment_usuarios_superadmin();
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
        View view = inflater.inflate(R.layout.fragment_usuarios_superadmin, container, false);

        // Referencias
        recyclerView = view.findViewById(R.id.rv_music);
        SearchView searchView = view.findViewById(R.id.searchViewUsuarios);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inicializar adapter con copia mutable
        usuariosAdapter = new UsuariosAdapter(new ArrayList<>(usuariosList));
        recyclerView.setAdapter(usuariosAdapter);

        // Buscar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                List<UsuariosDomain> filtrados = new ArrayList<>();
                for (UsuariosDomain usuario : usuariosList) {
                    if (usuario.getNombre().toLowerCase().contains(newText.toLowerCase())) {
                        filtrados.add(usuario);
                    }
                }
                //Este es un metodo para UsuariosAdapter
                usuariosAdapter.updateList(filtrados);
                return true;
            }
        });

        return view;
    }

//    Oncreate sin buscador
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//            View view = inflater.inflate(R.layout.fragment_usuarios_superadmin, container, false);
//
//            recyclerView = view.findViewById(R.id.rv_music);
//            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//            usuariosAdapter = new UsuariosAdapter(usuariosList);
//            recyclerView.setAdapter(usuariosAdapter);
//
//            return view;
//    }
}
