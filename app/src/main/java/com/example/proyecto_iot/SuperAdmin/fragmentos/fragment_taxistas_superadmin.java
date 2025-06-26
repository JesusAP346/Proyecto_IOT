package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.SearchView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.adapter.TaxistasAdapter;
import com.example.proyecto_iot.dtos.Usuario; // Asegúrate de que tu DTO Usuario esté importado

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale; // Para manejar mayúsculas/minúsculas en la búsqueda

public class fragment_taxistas_superadmin extends Fragment {

    private List<Usuario> taxistasList; // La lista que se muestra en el RecyclerView (puede estar filtrada)
    private List<Usuario> allTaxistasLoaded; // La lista completa de todos los taxistas cargados de Firestore

    private RecyclerView recyclerView;
    private TaxistasAdapter taxistasAdapter;
    private CollectionReference usersCollectionRef;

    private SearchView etBuscarTaxista; // Asegúrate de que el ID de tu SearchView en el layout sea 'buscarTaxista'

    // Parámetros de fragmento (manteniéndolos, aunque no se usan actualmente)
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public fragment_taxistas_superadmin() {
        // Constructor público vacío requerido
    }

    public static fragment_taxistas_superadmin newInstance(String param1, String param2) {
        fragment_taxistas_superadmin fragment = new fragment_taxistas_superadmin();
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

        taxistasList = new ArrayList<>();
        allTaxistasLoaded = new ArrayList<>(); // Inicializar la lista para todos los taxistas
        usersCollectionRef = FirebaseFirestore.getInstance().collection("usuarios");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_taxistas_superadmin, container, false);

        recyclerView = view.findViewById(R.id.recyclerLogs); // Asegúrate de que 'rv_music' sea el ID de tu RecyclerView
        taxistasAdapter = new TaxistasAdapter(taxistasList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(taxistasAdapter);

        etBuscarTaxista = view.findViewById(R.id.buscarTaxistas); // ID del SearchView

        etBuscarTaxista.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterTaxistas(query); // Llama al método de filtrado en cliente
                etBuscarTaxista.clearFocus(); // Oculta el teclado
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterTaxistas(newText); // Llama al método de filtrado en cliente
                return true;
            }
        });

        // Carga inicial: Ahora, siempre escuchamos a TODOS los taxistas
        loadAllTaxistasFromFirestore();

        Button btnSolicitudes = view.findViewById(R.id.solicitudes); // Asegúrate de que 'solicitudes' sea el ID de tu botón

        btnSolicitudes.setOnClickListener(v -> {
            // Este fragmento podría ser para ver las solicitudes pendientes de taxistas
            fragment_solicitudestaxista_superadmin nuevoFragment = new fragment_solicitudestaxista_superadmin();
            getParentFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, nuevoFragment) // Asegúrate de que 'frame_layout' es el ID de tu contenedor de fragmentos
                    .addToBackStack(null)
                    .commit();
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar todos los taxistas de Firestore para asegurar la lista más reciente.
        // Después de cargar, el filtro actual del SearchView se aplicará automáticamente.
        loadAllTaxistasFromFirestore();
    }

    /**
     * Carga todos los taxistas con 'idRol' = "Taxista" desde Firebase Firestore
     * y actualiza la lista allTaxistasLoaded.
     * Este método usa un listener en tiempo real.
     */
    private void loadAllTaxistasFromFirestore() {
        // Consulta Firestore para obtener todos los usuarios con rol "Taxista"
        usersCollectionRef.whereEqualTo("idRol", "Taxista")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getContext(), "Error al cargar taxistas: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            allTaxistasLoaded.clear(); // Limpiar la lista de todos los taxistas
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Usuario usuario = document.toObject(Usuario.class);
                                if (usuario != null) {
                                    usuario.setId(document.getId()); // Establecer el ID del documento de Firestore
                                    allTaxistasLoaded.add(usuario); // Añadir a la lista de todos los taxistas
                                }
                            }
                            // Después de cargar todos, aplicar el filtro actual del SearchView
                            filterTaxistas(etBuscarTaxista.getQuery().toString());
                        }
                    }
                });
    }

    /**
     * Filtra la lista de taxistas basándose en el texto de búsqueda proporcionado.
     * Este método realiza el filtrado en el cliente sobre la lista allTaxistasLoaded.
     *
     * @param searchText El texto a filtrar (insensible a mayúsculas/minúsculas).
     */
    private void filterTaxistas(String searchText) {
        taxistasList.clear(); // Limpiar la lista que se muestra actualmente

        if (searchText == null || searchText.isEmpty()) {
            // Si el texto de búsqueda está vacío, mostrar todos los taxistas cargados
            taxistasList.addAll(allTaxistasLoaded);
        } else {
            String lowerCaseSearchText = searchText.toLowerCase(Locale.getDefault());

            // Recorrer la lista completa de taxistas y aplicar el filtro
            for (Usuario taxista : allTaxistasLoaded) {
                // Comprobación de nulos para evitar NullPointerException si un campo está vacío
                boolean matchesNombres = taxista.getNombres() != null &&
                        taxista.getNombres().toLowerCase(Locale.getDefault()).contains(lowerCaseSearchText);

                boolean matchesApellidos = taxista.getApellidos() != null &&
                        taxista.getApellidos().toLowerCase(Locale.getDefault()).contains(lowerCaseSearchText);

                boolean matchesPlaca = taxista.getPlacaAuto() != null &&
                        taxista.getPlacaAuto().toLowerCase(Locale.getDefault()).contains(lowerCaseSearchText);

                boolean matchesNumDocumento = taxista.getNumDocumento() != null &&
                        taxista.getNumDocumento().toLowerCase(Locale.getDefault()).contains(lowerCaseSearchText);


                if (matchesNombres || matchesApellidos || matchesPlaca || matchesNumDocumento) {
                    taxistasList.add(taxista);
                }
            }
        }
        taxistasAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }
}