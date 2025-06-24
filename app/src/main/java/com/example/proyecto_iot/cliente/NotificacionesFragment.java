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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

/*
public class NotificacionesFragment extends Fragment {

    public NotificacionesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notificaciones, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerNotificaciones);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Notificacion> notis = new ArrayList<>();
        notis.add(new Notificacion("El cobro se ha realizado con éxito"));
        notis.add(new Notificacion("Se generó QR para el servicio de taxi. Puede verlo en la sección Taxi"));
        notis.add(new Notificacion("El cobro se ha realizado con éxito"));

        NotificacionesAdapter adapter = new NotificacionesAdapter(notis);
        recyclerView.setAdapter(adapter);

        return view;
    }

}*/

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
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("notificaciones")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    notis.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String mensaje = doc.getString("mensaje");
                        if (mensaje != null) {
                            notis.add(new Notificacion(mensaje));
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar notificaciones", Toast.LENGTH_SHORT).show();
                });
    }
}

