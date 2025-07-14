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
import android.widget.Toast;
import android.widget.SearchView; // Importar SearchView

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
import java.util.Locale; // Para manejar mayúsculas/minúsculas en la búsqueda

public class fragment_administradores_superadmin extends Fragment {

    private List<Usuario> adminsList; // La lista que se muestra en el RecyclerView (puede estar filtrada)
    private List<Usuario> allAdminsLoaded; // La lista completa de todos los administradores cargados de Firestore

    private RecyclerView recyclerView;
    private AdministradoresAdapter administradoresAdapter;
    private CollectionReference usersCollectionRef;

    private SearchView etBuscarAdmin;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public fragment_administradores_superadmin() {
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

        adminsList = new ArrayList<>();
        allAdminsLoaded = new ArrayList<>(); // Inicializar la lista para todos los administradores
        usersCollectionRef = FirebaseFirestore.getInstance().collection("usuarios");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_administradores_superadmin, container, false);

        recyclerView = view.findViewById(R.id.recyclerLogs);
        administradoresAdapter = new AdministradoresAdapter(adminsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(administradoresAdapter);

        etBuscarAdmin = view.findViewById(R.id.buscarAdmin);

        etBuscarAdmin.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Cuando el usuario envía la búsqueda (presiona Enter)
                filterAdmins(query); // Llama al método de filtrado en cliente
                etBuscarAdmin.clearFocus(); // Oculta el teclado
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Se llama cada vez que el texto cambia
                filterAdmins(newText); // Llama al método de filtrado en cliente
                return true;
            }
        });

        // Carga inicial: Ahora, siempre escuchamos a TODOS los administradores
        // y el filtrado se hará en el cliente.
        // La consulta de Firestore no incluye el searchText aquí.
        loadAllAdminsFromFirestore();

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
        // Recargar todos los administradores de Firestore para asegurar la lista más reciente.
        // Después de cargar, el filtro actual del SearchView se aplicará automáticamente.
        loadAllAdminsFromFirestore();
    }

    // --- NUEVO MÉTODO: Carga todos los administradores desde Firestore ---
    private void loadAllAdminsFromFirestore() {
        // Consulta Firestore para obtener todos los usuarios con rol "Administrador"
        usersCollectionRef.whereEqualTo("idRol", "Administrador")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getContext(), "Error al cargar administradores: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            allAdminsLoaded.clear(); // Limpiar la lista de todos los administradores
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Usuario usuario = document.toObject(Usuario.class);
                                if (usuario != null) {
                                    usuario.setId(document.getId());
                                    allAdminsLoaded.add(usuario); // Añadir a la lista de todos los administradores
                                }
                            }
                            // Después de cargar todos, aplicar el filtro actual del SearchView
                            filterAdmins(etBuscarAdmin.getQuery().toString());
                        }
                    }
                });
    }

    // --- NUEVO MÉTODO: Filtrar la lista en el cliente ---
    private void filterAdmins(String searchText) {
        adminsList.clear(); // Limpiar la lista que se muestra actualmente

        if (searchText == null || searchText.isEmpty()) {
            // Si el texto de búsqueda está vacío, mostrar todos los administradores cargados
            adminsList.addAll(allAdminsLoaded);
        } else {
            String lowerCaseSearchText = searchText.toLowerCase(Locale.getDefault()); // Convertir a minúsculas una vez

            // Recorrer la lista completa de administradores y aplicar el filtro
            for (Usuario admin : allAdminsLoaded) {
                // Comprobación de nulos para evitar NullPointerException si un campo está vacío
                boolean matchesNombres = admin.getNombres() != null &&
                        admin.getNombres().toLowerCase(Locale.getDefault()).contains(lowerCaseSearchText);

                boolean matchesApellidos = admin.getApellidos() != null &&
                        admin.getApellidos().toLowerCase(Locale.getDefault()).contains(lowerCaseSearchText);



                // Puedes añadir más campos aquí, por ejemplo:
                // boolean matchesNumCelular = admin.getNumCelular() != null &&
                //                             admin.getNumCelular().contains(searchText); // Para números, "contains" puede ser directo

                if (matchesNombres || matchesApellidos /* || matchesNumCelular */) {
                    adminsList.add(admin);
                }
            }
        }
        administradoresAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }
}