package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//Para el buscador
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;


import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.UsuariosDataStore;
import com.example.proyecto_iot.SuperAdmin.adapter.UsuariosAdapter;
import com.example.proyecto_iot.SuperAdmin.database.UsuariosSeeder;
import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;
import com.example.proyecto_iot.SuperAdmin.domain.UsuariosDomain;

import java.util.List;

import androidx.room.Room;
import com.example.proyecto_iot.SuperAdmin.adapter.UsuariosAdapter;
import com.example.proyecto_iot.SuperAdmin.database.AppDatabase;
import com.example.proyecto_iot.SuperAdmin.database.UsuariosEntity;


public class fragment_usuarios_superadmin extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private RecyclerView recyclerView;
    private UsuariosAdapter usuariosAdapter;
    private List<UsuariosEntity> usuariosList = new ArrayList<>();
    private List<UsuariosEntity> usuariosFiltrados = new ArrayList<>();
    private String filtroActual = "todos";  // Puede ser: "todos", "activos", "desactivos"



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
        Button btnActivos = view.findViewById(R.id.FiltroActivos);
        Button btnDesactivos = view.findViewById(R.id.FiltroDesactivos);
        Button btnTodos = view.findViewById(R.id.FiltroTodos);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        UsuariosSeeder.insertarUsuariosPorDefecto(requireContext());

        // Cargar desde Room
        AppDatabase db = Room.databaseBuilder(requireContext(), AppDatabase.class, "usuarios-db")
                .allowMainThreadQueries()
                .build();

        usuariosList = db.usuariosDao().getAll();
        usuariosFiltrados = new ArrayList<>(usuariosList); // copia inicial que se usará en buscador
        usuariosAdapter = new UsuariosAdapter(new ArrayList<>(usuariosFiltrados), requireContext());
        usuariosAdapter.setOnUsuarioActualizadoListener(() -> aplicarFiltro(filtroActual));


        recyclerView.setAdapter(usuariosAdapter);

        // Buscador
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                AppDatabase db = Room.databaseBuilder(requireContext(), AppDatabase.class, "usuarios-db")
                        .allowMainThreadQueries()
                        .build();
                usuariosList = db.usuariosDao().getAll();

                usuariosFiltrados.clear();
                for (UsuariosEntity u : usuariosList) {
                    switch (filtroActual) {
                        case "todos":
                            usuariosFiltrados.add(u);
                            break;
                        case "activos":
                            if (u.estadoCuenta.equalsIgnoreCase("activo")) usuariosFiltrados.add(u);
                            break;
                        case "desactivos":
                            if (u.estadoCuenta.equalsIgnoreCase("suspendido") || u.estadoCuenta.equalsIgnoreCase("desactivo")) usuariosFiltrados.add(u);
                            break;
                    }
                }

                List<UsuariosEntity> filtradosPorTexto = new ArrayList<>();
                for (UsuariosEntity usuario : usuariosFiltrados) {
                    if (usuario.nombre.toLowerCase().contains(newText.toLowerCase())) {
                        filtradosPorTexto.add(usuario);
                    }
                }

                usuariosAdapter.updateList(filtradosPorTexto);
                return true;
            }
        });



        btnActivos.setOnClickListener(v -> {
            filtroActual = "activos";
            aplicarFiltro(filtroActual);
            Toast.makeText(requireContext(), "Mostrando usuarios activos", Toast.LENGTH_SHORT).show();
        });

        btnDesactivos.setOnClickListener(v -> {
            filtroActual = "desactivos";
            aplicarFiltro(filtroActual);
            Toast.makeText(requireContext(), "Mostrando usuarios desactivos", Toast.LENGTH_SHORT).show();
        });

        btnTodos.setOnClickListener(v -> {
            filtroActual = "todos";
            aplicarFiltro(filtroActual);
            Toast.makeText(requireContext(), "Mostrando todos los usuarios", Toast.LENGTH_SHORT).show();
        });




        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        aplicarFiltro(filtroActual);

        // Reaplicar el texto del SearchView si existía
        SearchView searchView = requireView().findViewById(R.id.searchViewUsuarios);
        String texto = searchView.getQuery().toString();
        if (!texto.isEmpty()) {
            List<UsuariosEntity> filtrados = new ArrayList<>();
            for (UsuariosEntity usuario : usuariosFiltrados) {
                if (usuario.nombre.toLowerCase().contains(texto.toLowerCase())) {
                    filtrados.add(usuario);
                }
            }
            usuariosAdapter.updateList(filtrados);
        }
    }

    private void aplicarFiltro(String tipo) {
        AppDatabase db = Room.databaseBuilder(requireContext(), AppDatabase.class, "usuarios-db")
                .allowMainThreadQueries()
                .build();
        usuariosList = db.usuariosDao().getAll(); // actualiza la lista base

        usuariosFiltrados.clear();

        // 1. Aplica filtro de botón (estado)
        for (UsuariosEntity u : usuariosList) {
            switch (tipo) {
                case "todos":
                    usuariosFiltrados.add(u);
                    break;
                case "activos":
                    if (u.estadoCuenta.equalsIgnoreCase("activo")) usuariosFiltrados.add(u);
                    break;
                case "desactivos":
                    if (u.estadoCuenta.equalsIgnoreCase("suspendido") || u.estadoCuenta.equalsIgnoreCase("desactivo")) usuariosFiltrados.add(u);
                    break;
            }
        }

        // 2. Aplica filtro de texto si existe
        SearchView searchView = requireView().findViewById(R.id.searchViewUsuarios);
        String texto = searchView.getQuery().toString();
        if (!texto.isEmpty()) {
            List<UsuariosEntity> filtradosPorTexto = new ArrayList<>();
            for (UsuariosEntity usuario : usuariosFiltrados) {
                if (usuario.nombre.toLowerCase().contains(texto.toLowerCase())) {
                    filtradosPorTexto.add(usuario);
                }
            }
            usuariosAdapter.updateList(filtradosPorTexto);
        } else {
            usuariosAdapter.updateList(new ArrayList<>(usuariosFiltrados));
        }
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
