package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.adapter.LogAdapter;
import com.example.proyecto_iot.dtos.LogSA;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class LogsFragment extends Fragment {

    private RecyclerView recyclerLogs;
    private LogAdapter logAdapter;
    private List<LogSA> listaLogs = new ArrayList<>();
    private FirebaseFirestore db;
    private ListenerRegistration listenerLogs;

    public LogsFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_logs, container, false);

        recyclerLogs = view.findViewById(R.id.recyclerLogs);
        recyclerLogs.setLayoutManager(new LinearLayoutManager(getContext()));
        logAdapter = new LogAdapter(listaLogs);
        recyclerLogs.setAdapter(logAdapter);

        db = FirebaseFirestore.getInstance();

        escucharCambiosLogs();  // Aquí activamos la escucha en tiempo real

        return view;
    }

    private void escucharCambiosLogs() {
        listenerLogs = db.collection("logs")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener((querySnapshot, error) -> {
                    if (error != null || querySnapshot == null) {
                        return;
                    }

                    listaLogs.clear();  // Limpiamos y volvemos a llenar
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        LogSA log = doc.toObject(LogSA.class);
                        if (log != null) {
                            listaLogs.add(log);
                        }
                    }
                    logAdapter.notifyDataSetChanged();
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerLogs != null) {
            listenerLogs.remove();  // Para evitar fugas de memoria
        }
    }
}
