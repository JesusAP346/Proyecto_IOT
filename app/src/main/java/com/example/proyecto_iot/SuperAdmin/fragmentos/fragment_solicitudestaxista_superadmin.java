package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.SearchView; // Importar SearchView

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.adapter.SolicitudTaxistaAdapter; // Asegúrate de que este adapter maneje objetos Usuario
import com.example.proyecto_iot.dtos.Usuario; // Importar tu DTO de Usuario

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale; // Para manejar mayúsculas/minúsculas en la búsqueda

public class fragment_solicitudestaxista_superadmin extends Fragment {

    private List<Usuario> solicitudesList; // La lista que se muestra en el RecyclerView (puede estar filtrada)
    private List<Usuario> allSolicitudesLoaded; // La lista completa de todas las solicitudes cargadas de Firestore

    private RecyclerView recyclerView;
    private SolicitudTaxistaAdapter solicitudTaxistaAdapter; // Reemplazamos 'adapter' por un nombre más específico
    private CollectionReference usersCollectionRef; // Referencia a la colección de usuarios en Firestore

    private SearchView etBuscarSolicitud; // SearchView para buscar solicitudes

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    public fragment_solicitudestaxista_superadmin() {
        // Required empty public constructor
    }

    public static fragment_solicitudestaxista_superadmin newInstance(String param1, String param2) {
        fragment_solicitudestaxista_superadmin fragment = new fragment_solicitudestaxista_superadmin();
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

        solicitudesList = new ArrayList<>();
        allSolicitudesLoaded = new ArrayList<>();
        // Inicializa la referencia a la colección "usuarios"
        usersCollectionRef = FirebaseFirestore.getInstance().collection("usuarios");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solicitudestaxista_superadmin, container, false);

        recyclerView = view.findViewById(R.id.rv_music); // Asegúrate de que el ID sea correcto en tu layout
        // Pasa 'solicitudesList' al constructor del adapter
        solicitudTaxistaAdapter = new SolicitudTaxistaAdapter(solicitudesList, getContext()); // Pasa el contexto si tu adapter lo necesita
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(solicitudTaxistaAdapter);

        etBuscarSolicitud = view.findViewById(R.id.buscarSolicitud); // Asegúrate de tener un SearchView en tu layout con este ID

        etBuscarSolicitud.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterSolicitudes(query);
                etBuscarSolicitud.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterSolicitudes(newText);
                return true;
            }
        });

        // Carga inicial de las solicitudes desde Firestore
        loadSolicitudesFromFirestore();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Recargar las solicitudes para asegurar que la lista esté actualizada
        loadSolicitudesFromFirestore();
    }


    private void loadSolicitudesFromFirestore() {
        // Consulta Firestore para obtener usuarios con estadoSolicitudTaxista en "pendiente"
        // Ojo: Si tienes otros estados que consideras "solicitud" (ej. "en_revision"), añádelos con .whereIn()
        usersCollectionRef.whereEqualTo("estadoSolicitudTaxista", "pendiente")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Toast.makeText(getContext(), "Error al cargar solicitudes: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            allSolicitudesLoaded.clear(); // Limpiar la lista completa
                            for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                                Usuario usuario = document.toObject(Usuario.class);
                                if (usuario != null) {
                                    usuario.setId(document.getId()); // Asegúrate de establecer el ID del documento
                                    allSolicitudesLoaded.add(usuario);
                                }
                            }
                            // Después de cargar todos, aplicar el filtro actual de la SearchView
                            filterSolicitudes(etBuscarSolicitud.getQuery().toString());
                        }
                    }
                });
    }


    private void filterSolicitudes(String searchText) {
        solicitudesList.clear(); // Limpiar la lista que se muestra actualmente

        if (searchText == null || searchText.isEmpty()) {
            // Si el texto de búsqueda está vacío, mostrar todas las solicitudes cargadas
            solicitudesList.addAll(allSolicitudesLoaded);
        } else {
            String lowerCaseSearchText = searchText.toLowerCase(Locale.getDefault());

            // Recorrer la lista completa de solicitudes y aplicar el filtro
            for (Usuario usuario : allSolicitudesLoaded) {
                // Puedes buscar por nombres, apellidos, número de documento, etc.
                boolean matchesNombres = usuario.getNombres() != null &&
                        usuario.getNombres().toLowerCase(Locale.getDefault()).contains(lowerCaseSearchText);

                boolean matchesApellidos = usuario.getApellidos() != null &&
                        usuario.getApellidos().toLowerCase(Locale.getDefault()).contains(lowerCaseSearchText);

                boolean matchesNumDocumento = usuario.getNumDocumento() != null &&
                        usuario.getNumDocumento().toLowerCase(Locale.getDefault()).contains(lowerCaseSearchText);

                // Agrega más campos si deseas que la búsqueda sea más amplia
                if (matchesNombres || matchesApellidos || matchesNumDocumento) {
                    solicitudesList.add(usuario);
                }
            }
        }
        solicitudTaxistaAdapter.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }
}