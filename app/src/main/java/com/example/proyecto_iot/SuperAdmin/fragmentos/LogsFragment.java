package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.HistorialActivity;
import com.example.proyecto_iot.SuperAdmin.adapter.LogAdapter;
import com.example.proyecto_iot.dtos.LogSA;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.ListenerRegistration;

import android.widget.PopupMenu;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.button.MaterialButton;
import java.util.Calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class LogsFragment extends Fragment {

    private RecyclerView recyclerLogs;
    private LogAdapter logAdapter;
    private List<LogSA> listaLogs = new ArrayList<>();
    private FirebaseFirestore db;
    private ListenerRegistration listenerLogs;
    private MaterialButton btnTipoLog, btnRolLog, btnFechaLog, btnLimpiarFiltros, btnImprimir;
    public LogsFragment() {}
    private String filtroTipo = null;
    private String filtroRol = null;
    private String filtroFecha = null;
    private Calendar fechaInicio = null;
    private Calendar fechaFin = null;
    private SearchView searchView;

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
        btnImprimir= view.findViewById(R.id.btnImprimirHistorial);
        searchView = view.findViewById(R.id.buscarSolicitud);

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
                    aplicarFiltros();
                });
    }

    private void configurarFiltros() {

        btnImprimir.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), HistorialActivity.class);
            startActivity(intent);
        });

        btnTipoLog.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);

            // Opciones fijas:
            popup.getMenu().add("Todos");
            popup.getMenu().add("Solicitudes");
            popup.getMenu().add("Registro de usuario");
            popup.getMenu().add("Eliminación de usuario");
            popup.getMenu().add("Reserva de hotel");
            popup.getMenu().add("Registro de checkout");
            popup.getMenu().add("Pago de reserva");

            popup.setOnMenuItemClickListener(item -> {
                String tipoSeleccionado = item.getTitle().toString();
                if (tipoSeleccionado.equals("Todos")) {
                    filtroTipo = null;
                    btnTipoLog.setText("Tipo Log");
                } else {
                    filtroTipo = tipoSeleccionado;
                    btnTipoLog.setText(filtroTipo);
                }
                aplicarFiltros();
                return true;
            });

            popup.show();
        });


        btnRolLog.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(getContext(), v);

            popup.getMenu().add("Todos");  // Para limpiar el filtro de rol
            popup.getMenu().add("Cliente");
            popup.getMenu().add("Administrador");
            popup.getMenu().add("Taxista");
            popup.getMenu().add("Super Admin");

            popup.setOnMenuItemClickListener(item -> {
                String rolSeleccionado = item.getTitle().toString();
                if (rolSeleccionado.equals("Todos")) {
                    filtroRol = null;
                    btnRolLog.setText("Rol");  // Texto por defecto si limpias filtro
                } else {
                    filtroRol = rolSeleccionado;
                    btnRolLog.setText(filtroRol);  // Mostrar rol seleccionado
                }

                aplicarFiltros();
                return true;
            });

            popup.show();
        });



        btnFechaLog.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerInicio = new DatePickerDialog(requireContext(),
                    (view1, year1, month1, dayOfMonth1) -> {
                        fechaInicio = Calendar.getInstance();
                        fechaInicio.set(year1, month1, dayOfMonth1, 0, 0, 0);

                        // Segundo picker para fecha fin
                        DatePickerDialog datePickerFin = new DatePickerDialog(requireContext(),
                                (view2, year2, month2, dayOfMonth2) -> {
                                    fechaFin = Calendar.getInstance();
                                    fechaFin.set(year2, month2, dayOfMonth2, 23, 59, 59);

                                    // Mostrar texto en botón
                                    String texto = String.format("%02d/%02d/%04d - %02d/%02d/%04d",
                                            dayOfMonth1, month1 + 1, year1,
                                            dayOfMonth2, month2 + 1, year2);
                                    btnFechaLog.setText(texto);

                                    aplicarFiltros();

                                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                        datePickerFin.setTitle("Seleccione fecha de fin");
                        datePickerFin.show();

                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            datePickerInicio.setTitle("Seleccione fecha de inicio");
            datePickerInicio.show();
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                aplicarFiltros();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                aplicarFiltros();
                return true;
            }
        });



        btnLimpiarFiltros.setOnClickListener(v -> {
            filtroTipo = null;
            filtroRol = null;
            filtroFecha = null;
            fechaInicio = null;
            fechaFin = null;

            btnTipoLog.setText("Tipo Log");
            btnRolLog.setText("Rol");
            btnFechaLog.setText("Fecha");

            aplicarFiltros();   // Recarga sin filtros
        });
    }

    private void aplicarFiltros() {
        String textoBusqueda = searchView.getQuery().toString().toLowerCase(Locale.getDefault());
        List<LogSA> listaFiltrada = new ArrayList<>();

        for (LogSA log : listaLogs) {


            boolean cumpleTipo = (filtroTipo == null ||
                    (log.getTipoLog() != null && log.getTipoLog().equalsIgnoreCase(filtroTipo)));

            boolean cumpleRol = (filtroRol == null ||
                    (log.getRolEditado() != null && log.getRolEditado().equalsIgnoreCase(filtroRol)));

            boolean cumpleFecha = true;
            if (fechaInicio != null && fechaFin != null) {
                Date fechaLog = log.getTimestamp();
                cumpleFecha = (fechaLog != null &&
                        !fechaLog.before(fechaInicio.getTime()) &&
                        !fechaLog.after(fechaFin.getTime()));
            }

            if (fechaInicio != null && fechaFin != null) {
                Date fechaLog = log.getTimestamp();
                cumpleFecha = (fechaLog != null &&
                        !fechaLog.before(fechaInicio.getTime()) &&
                        !fechaLog.after(fechaFin.getTime()));
            }

            boolean cumpleTexto = true;
            if (!textoBusqueda.isEmpty()) {
                String titulo = log.getTitulo() != null ? log.getTitulo().toLowerCase(Locale.getDefault()) : "";
                String mensaje = log.getMensaje() != null ? log.getMensaje().toLowerCase(Locale.getDefault()) : "";
                String editor = log.getNombreEditor() != null ? log.getNombreEditor().toLowerCase(Locale.getDefault()) : "";

                cumpleTexto = titulo.contains(textoBusqueda) || mensaje.contains(textoBusqueda) || editor.contains(textoBusqueda);
            }

            if (cumpleTipo && cumpleRol && cumpleFecha && cumpleTexto) {
                listaFiltrada.add(log);
            }
        }

        logAdapter.updateList(listaFiltrada);
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerLogs != null) {
            listenerLogs.remove();  // Para evitar fugas de memoria
        }
    }
}
