package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotificacionesFragment extends Fragment {

    private List<Notificacion> notis = new ArrayList<>();
    private NotificacionesAdapter adapter;

    public NotificacionesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerNotificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new NotificacionesAdapter(notis);
        recyclerView.setAdapter(adapter);

        cargarNotificaciones();

        return view;
    }

    private void cargarNotificaciones() {
        // Obtener el usuario autenticado
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        String idCliente = currentUser.getUid();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Cargar todos los documentos y filtrar en el cliente
        db.collection("notificaciones")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notis.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        // Verificar que tenga idCliente y que coincida con el usuario actual
                        String docIdCliente = doc.getString("idCliente");
                        if (docIdCliente != null && docIdCliente.equals(idCliente)) {
                            // Verificar que no tenga el campo "rol"
                            if (!doc.contains("rol")) {
                                String mensaje = doc.getString("mensaje");
                                if (mensaje != null) {
                                    notis.add(new Notificacion(mensaje));
                                }
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar notificaciones: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}