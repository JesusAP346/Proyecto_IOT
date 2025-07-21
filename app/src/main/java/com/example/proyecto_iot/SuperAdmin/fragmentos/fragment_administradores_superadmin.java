package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.RegistroActivitySuperAdmin;
import com.example.proyecto_iot.SuperAdmin.adapter.AdministradoresAdapter;
import com.example.proyecto_iot.dtos.Usuario;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class fragment_administradores_superadmin extends Fragment {

    private RecyclerView recyclerView;
    private AdministradoresAdapter administradoresAdapter;
    private List<Usuario> allAdminsLoaded = new ArrayList<>();
    private List<Usuario> adminsFiltrados = new ArrayList<>();
    private String filtroActual = "todos";
    private SearchView searchView;

    private final CollectionReference usuariosRef = FirebaseFirestore.getInstance().collection("usuarios");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_administradores_superadmin, container, false);

        recyclerView = view.findViewById(R.id.recyclerLogs);
        searchView = view.findViewById(R.id.buscarAdmin);
        Button btnActivos = view.findViewById(R.id.FiltroActivos);
        Button btnDesactivos = view.findViewById(R.id.FiltroDesactivos);
        Button btnTodos = view.findViewById(R.id.FiltroTodos);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        administradoresAdapter = new AdministradoresAdapter(adminsFiltrados);
        recyclerView.setAdapter(administradoresAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                aplicarFiltro(filtroActual, newText);
                return true;
            }
        });

        btnActivos.setOnClickListener(v -> {
            filtroActual = "activos";
            aplicarFiltro(filtroActual, searchView.getQuery().toString());
        });

        btnDesactivos.setOnClickListener(v -> {
            filtroActual = "desactivos";
            aplicarFiltro(filtroActual, searchView.getQuery().toString());
        });

        btnTodos.setOnClickListener(v -> {
            filtroActual = "todos";
            aplicarFiltro(filtroActual, searchView.getQuery().toString());
        });

        Button btnAgregar = view.findViewById(R.id.button2);
        btnAgregar.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegistroActivitySuperAdmin.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarAdminsDesdeFirestore();
    }

    private void cargarAdminsDesdeFirestore() {
        usuariosRef.whereEqualTo("idRol", "Administrador")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error al cargar administradores: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    allAdminsLoaded.clear();

                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Usuario admin = doc.toObject(Usuario.class);
                            if (admin != null) {
                                admin.setId(doc.getId());
                                allAdminsLoaded.add(admin);
                            }
                        }
                    }

                    aplicarFiltro(filtroActual, searchView.getQuery().toString());
                });
    }

    private void aplicarFiltro(String tipo, String textoBusqueda) {
        adminsFiltrados.clear();

        for (Usuario admin : allAdminsLoaded) {
            boolean cumpleFiltroEstado = false;

            switch (tipo) {
                case "todos":
                    cumpleFiltroEstado = true;
                    break;
                case "activos":
                    cumpleFiltroEstado = admin.isEstadoCuenta();
                    break;
                case "desactivos":
                    cumpleFiltroEstado = !admin.isEstadoCuenta();
                    break;
            }

            if (cumpleFiltroEstado) {
                adminsFiltrados.add(admin);
            }
        }

        if (!textoBusqueda.isEmpty()) {
            String queryLower = textoBusqueda.toLowerCase(Locale.getDefault());
            List<Usuario> filtradosPorTexto = new ArrayList<>();

            for (Usuario admin : adminsFiltrados) {
                String nombreCompleto = (admin.getNombres() + " " + admin.getApellidos()).toLowerCase(Locale.getDefault());
                if (nombreCompleto.contains(queryLower)) {
                    filtradosPorTexto.add(admin);
                }
            }

            administradoresAdapter.updateList(filtradosPorTexto);
        } else {
            administradoresAdapter.updateList(new ArrayList<>(adminsFiltrados));
        }
    }
}
