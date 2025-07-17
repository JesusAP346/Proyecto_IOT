package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.adapter.UsuariosAdapter;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class fragment_usuarios_superadmin extends Fragment {

    private RecyclerView recyclerView;
    private UsuariosAdapter usuariosAdapter;
    private List<Usuario> usuariosList = new ArrayList<>();
    private List<Usuario> usuariosFiltrados = new ArrayList<>();
    private String filtroActual = "todos";
    private SearchView searchView;

    private final CollectionReference usuariosRef = FirebaseFirestore.getInstance().collection("usuarios");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_usuarios_superadmin, container, false);

        recyclerView = view.findViewById(R.id.recyclerLogs);
        searchView = view.findViewById(R.id.searchViewUsuarios);
        Button btnActivos = view.findViewById(R.id.FiltroActivos);
        Button btnDesactivos = view.findViewById(R.id.FiltroDesactivos);
        Button btnTodos = view.findViewById(R.id.FiltroTodos);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        usuariosAdapter = new UsuariosAdapter(usuariosFiltrados);
        recyclerView.setAdapter(usuariosAdapter);

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
            Toast.makeText(requireContext(), "Mostrando usuarios activos", Toast.LENGTH_SHORT).show();
        });

        btnDesactivos.setOnClickListener(v -> {
            filtroActual = "desactivos";
            aplicarFiltro(filtroActual, searchView.getQuery().toString());
            Toast.makeText(requireContext(), "Mostrando usuarios desactivos", Toast.LENGTH_SHORT).show();
        });

        btnTodos.setOnClickListener(v -> {
            filtroActual = "todos";
            aplicarFiltro(filtroActual, searchView.getQuery().toString());
            Toast.makeText(requireContext(), "Mostrando todos los usuarios", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarUsuariosDesdeFirestore();
    }

    private void cargarUsuariosDesdeFirestore() {
        usuariosRef.whereEqualTo("idRol", "Cliente")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getContext(), "Error al cargar usuarios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                usuariosList.clear();

                if (snapshots != null) {
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        Usuario usuario = doc.toObject(Usuario.class);
                        if (usuario != null) {
                            usuario.setId(doc.getId());
                            usuariosList.add(usuario);
                        }
                    }
                }

                aplicarFiltro(filtroActual, searchView.getQuery().toString());
            }
        });
    }

    private void aplicarFiltro(String tipo, String textoBusqueda) {
        usuariosFiltrados.clear();

        for (Usuario u : usuariosList) {
            boolean cumpleFiltroEstado = false;

            switch (tipo) {
                case "todos":
                    cumpleFiltroEstado = true;
                    break;
                case "activos":
                    cumpleFiltroEstado = u.isEstadoCuenta();
                    break;
                case "desactivos":
                    cumpleFiltroEstado = !u.isEstadoCuenta();
                    break;
            }

            if (cumpleFiltroEstado) {
                usuariosFiltrados.add(u);
            }
        }

        if (!textoBusqueda.isEmpty()) {
            String queryLower = textoBusqueda.toLowerCase(Locale.getDefault());
            List<Usuario> filtradosPorTexto = new ArrayList<>();

            for (Usuario usuario : usuariosFiltrados) {
                String nombreCompleto = (usuario.getNombres() + " " + usuario.getApellidos()).toLowerCase(Locale.getDefault());
                if (nombreCompleto.contains(queryLower)) {
                    filtradosPorTexto.add(usuario);
                }
            }

            usuariosAdapter.updateList(filtradosPorTexto);
        } else {
            usuariosAdapter.updateList(new ArrayList<>(usuariosFiltrados));
        }
    }
}
