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

import android.widget.PopupMenu;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import com.google.android.material.button.MaterialButton;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.List;

public class LogsFragment extends Fragment {

    private RecyclerView recyclerLogs;
    private LogAdapter logAdapter;
    private List<LogSA> listaLogs = new ArrayList<>();
    private FirebaseFirestore db;
    private ListenerRegistration listenerLogs;
    private MaterialButton btnTipoLog, btnRolLog, btnFechaLog, btnLimpiarFiltros;
    public LogsFragment() {}
    private String filtroTipo = null;
    private String filtroRol = null;
    private String filtroFecha = null;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_logs, container, false);

        recyclerLogs = view.findViewById(R.id.recyclerLogs);
        recyclerLogs.setLayoutManager(new LinearLayoutManager(getContext()));
        logAdapter = new LogAdapter(listaLogs);
        recyclerLogs.setAdapter(logAdapter);

        db = FirebaseFirestore.getInstance();

        // Referencias a botones
        btnTipoLog = view.findViewById(R.id.btnTipoLog);
        btnRolLog = view.findViewById(R.id.btnRolLog);
        btnFechaLog = view.findViewById(R.id.btnFechaLog);
        btnLimpiarFiltros = view.findViewById(R.id.btnLimpiarFiltros);

        configurarFiltros();

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

    private void configurarFiltros() {
        btnTipoLog.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.getMenu().add("Registro");
            popup.getMenu().add("Eliminación");
            popup.getMenu().add("Modificación");
            popup.setOnMenuItemClickListener(item -> {
                filtroTipo = item.getTitle().toString();
                btnTipoLog.setText(filtroTipo);
                aplicarFiltros();
                return true;
            });
            popup.show();
        });

        btnRolLog.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);
            popup.getMenu().add("Cliente");
            popup.getMenu().add("Administrador");
            popup.getMenu().add("Taxista");
            popup.getMenu().add("Super Admin");
            popup.setOnMenuItemClickListener(item -> {
                filtroRol = item.getTitle().toString();
                btnRolLog.setText(filtroRol);
                aplicarFiltros();
                return true;
            });
            popup.show();
        });

        btnFechaLog.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePicker = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        filtroFecha = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year);
                        btnFechaLog.setText(filtroFecha);
                        aplicarFiltros();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePicker.show();
        });

        btnLimpiarFiltros.setOnClickListener(v -> {
            filtroTipo = null;
            filtroRol = null;
            filtroFecha = null;

            btnTipoLog.setText("Tipo Log");
            btnRolLog.setText("Rol");
            btnFechaLog.setText("Fecha");

            escucharCambiosLogs();  // Recarga sin filtros
        });
    }

    private void aplicarFiltros() {
        if (listenerLogs != null) {
            listenerLogs.remove();
        }

        Query query = db.collection("logs");

        if (filtroTipo != null) {
            query = query.whereEqualTo("tipo", filtroTipo);
        }

        if (filtroRol != null) {
            query = query.whereEqualTo("rolUsuario", filtroRol);
        }

        // Aquí puedes implementar filtrado por fecha si `timestamp` es tipo Timestamp en Firestore.

        query = query.orderBy("timestamp", Query.Direction.DESCENDING).limit(50);

        listenerLogs = query.addSnapshotListener((querySnapshot, error) -> {
            if (error != null || querySnapshot == null) {
                return;
            }

            listaLogs.clear();
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
