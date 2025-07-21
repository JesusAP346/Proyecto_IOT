package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.adapter.TaxistasAdapter;
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

public class fragment_taxistas_superadmin extends Fragment {

    private RecyclerView recyclerView;
    private TaxistasAdapter taxistasAdapter;
    private List<Usuario> allTaxistasLoaded = new ArrayList<>();
    private List<Usuario> taxistasFiltrados = new ArrayList<>();
    private String filtroActual = "todos";
    private SearchView searchView;
    private TextView tvCantidadTaxistas;

    private final CollectionReference usuariosRef = FirebaseFirestore.getInstance().collection("usuarios");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taxistas_superadmin, container, false);

        tvCantidadTaxistas = view.findViewById(R.id.textView19);
        recyclerView = view.findViewById(R.id.recyclerLogs);
        searchView = view.findViewById(R.id.buscarTaxistas);

        Button btnActivos = view.findViewById(R.id.FiltroActivos);
        Button btnDesactivos = view.findViewById(R.id.FiltroDesactivos);
        Button btnTodos = view.findViewById(R.id.FiltroTodos);
        Button btnSolicitudes = view.findViewById(R.id.solicitudes);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        taxistasAdapter = new TaxistasAdapter(taxistasFiltrados);
        recyclerView.setAdapter(taxistasAdapter);

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

        btnSolicitudes.setOnClickListener(v -> {
            fragment_solicitudestaxista_superadmin nuevoFragment = new fragment_solicitudestaxista_superadmin();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, nuevoFragment)
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarTaxistasDesdeFirestore();
    }

    private void cargarTaxistasDesdeFirestore() {
        usuariosRef.whereEqualTo("idRol", "Taxista")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(getContext(), "Error al cargar taxistas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    allTaxistasLoaded.clear();

                    if (snapshots != null) {
                        for (DocumentSnapshot doc : snapshots.getDocuments()) {
                            Usuario taxista = doc.toObject(Usuario.class);
                            if (taxista != null) {
                                taxista.setId(doc.getId());
                                allTaxistasLoaded.add(taxista);
                            }
                        }
                    }

                    aplicarFiltro(filtroActual, searchView.getQuery().toString());
                    loadCantidadSolicitudesTaxista();
                });
    }

    private void aplicarFiltro(String tipo, String textoBusqueda) {
        taxistasFiltrados.clear();

        for (Usuario taxista : allTaxistasLoaded) {
            boolean cumpleFiltroEstado = false;

            switch (tipo) {
                case "todos":
                    cumpleFiltroEstado = true;
                    break;
                case "activos":
                    cumpleFiltroEstado = taxista.isEstadoCuenta();
                    break;
                case "desactivos":
                    cumpleFiltroEstado = !taxista.isEstadoCuenta();
                    break;
            }

            if (cumpleFiltroEstado) {
                taxistasFiltrados.add(taxista);
            }
        }

        if (!textoBusqueda.isEmpty()) {
            String queryLower = textoBusqueda.toLowerCase(Locale.getDefault());
            List<Usuario> filtradosPorTexto = new ArrayList<>();

            for (Usuario taxista : taxistasFiltrados) {
                boolean matchesNombres = taxista.getNombres() != null &&
                        taxista.getNombres().toLowerCase(Locale.getDefault()).contains(queryLower);

                boolean matchesApellidos = taxista.getApellidos() != null &&
                        taxista.getApellidos().toLowerCase(Locale.getDefault()).contains(queryLower);

                boolean matchesPlaca = taxista.getPlacaAuto() != null &&
                        taxista.getPlacaAuto().toLowerCase(Locale.getDefault()).contains(queryLower);

                boolean matchesNumDocumento = taxista.getNumDocumento() != null &&
                        taxista.getNumDocumento().toLowerCase(Locale.getDefault()).contains(queryLower);

                if (matchesNombres || matchesApellidos || matchesPlaca || matchesNumDocumento) {
                    filtradosPorTexto.add(taxista);
                }
            }

            taxistasAdapter.updateList(filtradosPorTexto);
        } else {
            taxistasAdapter.updateList(new ArrayList<>(taxistasFiltrados));
        }
    }

    private void loadCantidadSolicitudesTaxista() {
        usuariosRef.whereEqualTo("estadoSolicitudTaxista", "pendiente")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    int cantidadSolicitudes = queryDocumentSnapshots.size();
                    tvCantidadTaxistas.setText("Tienes " + cantidadSolicitudes + " solicitudes pendientes");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al contar solicitudes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
