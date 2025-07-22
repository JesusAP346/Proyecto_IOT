package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_iot.administradorHotel.adapter.ReporteServicioAdapter;
import com.example.proyecto_iot.administradorHotel.adapter.ReporteUsuarioAdapter;
import com.example.proyecto_iot.administradorHotel.entity.ReporteServicio;
import com.example.proyecto_iot.administradorHotel.entity.ReporteUsuario;
import com.example.proyecto_iot.databinding.FragmentAdminReportesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AdminReportesFragment extends Fragment {

    private FragmentAdminReportesBinding binding;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth auth = FirebaseAuth.getInstance();

    private final Map<String, ReporteServicio> serviciosMap = new HashMap<>();
    private final Map<String, Double> usuarioMontos = new HashMap<>();

    private ReporteServicioAdapter servicioAdapter;
    private ReporteUsuarioAdapter usuarioAdapter;

    private String fechaDesdeStr = "";
    private String fechaHastaStr = "";

    private final SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminReportesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupCalendarios();
        setupAdapters();
        cargarDatosReportes();
    }

    private void setupCalendarios() {
        Calendar calendar = Calendar.getInstance();

        View.OnClickListener datePickerListener = v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                        selectedMonth += 1;
                        String selectedDate = String.format(Locale.getDefault(), "%02d-%02d-%04d", selectedDay, selectedMonth, selectedYear);

                        if (v.getId() == binding.etSelectDate.getId()) {
                            fechaDesdeStr = selectedDate;
                            binding.etSelectDate.setText(selectedDate);
                        } else {
                            fechaHastaStr = selectedDate;
                            binding.etSelectDate2.setText(selectedDate);
                        }

                        // Validar rango
                        if (!fechaDesdeStr.isEmpty() && !fechaHastaStr.isEmpty()) {
                            try {
                                Date desde = formatoFecha.parse(fechaDesdeStr);
                                Date hasta = formatoFecha.parse(fechaHastaStr);

                                if (hasta.before(desde)) {
                                    binding.errorTipoFecha.setText("Error en el filtro: la fecha 'Hasta' no puede ser menor que la fecha 'Desde'");
                                    binding.errorTipoFecha.setVisibility(View.VISIBLE);
                                    servicioAdapter.actualizarLista(new ArrayList<>());
                                    usuarioAdapter.actualizarLista(new ArrayList<>());
                                    return;
                                } else {
                                    binding.errorTipoFecha.setVisibility(View.GONE);
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        // Siempre cargar datos, aunque solo se seleccione una fecha
                        cargarDatosReportes();
                    }, year, month, day);
            datePickerDialog.show();
        };

        binding.etSelectDate.setOnClickListener(datePickerListener);
        binding.etSelectDate2.setOnClickListener(datePickerListener);
    }

    private void setupAdapters() {
        servicioAdapter = new ReporteServicioAdapter(new ArrayList<>());
        binding.recyclerServicios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerServicios.setAdapter(servicioAdapter);

        usuarioAdapter = new ReporteUsuarioAdapter(new ArrayList<>());
        binding.recyclerUsuarios.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerUsuarios.setAdapter(usuarioAdapter);
    }

    private void cargarDatosReportes() {
        serviciosMap.clear();
        usuarioMontos.clear();

        String uid = auth.getCurrentUser().getUid();
        db.collection("usuarios").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String idHotel = documentSnapshot.getString("idHotel");
                if (idHotel != null) {
                    obtenerReservasFinalizadas(idHotel);
                }
            }
        });
    }

    private void obtenerReservasFinalizadas(String idHotel) {
        db.collection("reservas")
                .whereEqualTo("idHotel", idHotel)
                .whereEqualTo("estado", "FINALIZADO")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        String idCliente = doc.getString("idCliente");
                        String fechaEntradaStr = doc.getString("fechaEntrada");
                        String fechaSalidaStr = doc.getString("fechaSalida");

                        try {
                            Date entrada = formatoFecha.parse(fechaEntradaStr);
                            Date salida = formatoFecha.parse(fechaSalidaStr);

                            // Solo filtra si ambas fechas están definidas
                            if (!fechaDesdeStr.isEmpty() && !fechaHastaStr.isEmpty()) {
                                Date desde = formatoFecha.parse(fechaDesdeStr);
                                Date hasta = formatoFecha.parse(fechaHastaStr);

                                // Si la reserva no se cruza con el rango, la ignoramos
                                if (salida.before(desde) || entrada.after(hasta)) {
                                    continue;
                                }
                            }

                            Map<String, Object> datosCheckout = (Map<String, Object>) doc.get("datosCheckout");
                            if (datosCheckout == null) continue;

                            // Total por usuario
                            Double total = datosCheckout.get("total") instanceof Number
                                    ? ((Number) datosCheckout.get("total")).doubleValue()
                                    : 0.0;

                            if (idCliente != null) {
                                usuarioMontos.put(idCliente, usuarioMontos.getOrDefault(idCliente, 0.0) + total);
                            }

                            // Servicios adicionales
                            List<Map<String, Object>> serviciosAdicionales = (List<Map<String, Object>>) datosCheckout.get("serviciosAdicionales");
                            if (serviciosAdicionales != null) {
                                for (Map<String, Object> item : serviciosAdicionales) {
                                    String nombre = (String) item.get("nombre");
                                    Double precio = item.get("precio") instanceof Number ? ((Number) item.get("precio")).doubleValue() : 0.0;

                                    if (nombre == null) continue;

                                    ReporteServicio rep = serviciosMap.getOrDefault(nombre, new ReporteServicio(nombre, 0L, 0.0));
                                    rep.setCantidad(rep.getCantidad() + 1);
                                    rep.setTotal(rep.getTotal() + precio);
                                    serviciosMap.put(nombre, rep);
                                }
                            }

                        } catch (ParseException e) {
                            e.printStackTrace(); // Si la fecha no se puede parsear, se salta esa reserva
                        }
                    }

                    actualizarUI();
                });
    }


    private void actualizarUI() {
        List<ReporteServicio> listaServicios = new ArrayList<>(serviciosMap.values());
        listaServicios.sort(Comparator.comparingDouble(ReporteServicio::getTotal));
        servicioAdapter.actualizarLista(listaServicios);

        List<ReporteUsuario> listaUsuarios = new ArrayList<>();
        List<String> pendientes = new ArrayList<>(usuarioMontos.keySet());

        for (String idUsuario : pendientes) {
            double monto = usuarioMontos.getOrDefault(idUsuario, 0.0);

            db.collection("usuarios").document(idUsuario).get().addOnSuccessListener(documentSnapshot -> {
                String nombreCompleto;
                if (documentSnapshot.exists()) {
                    String nombres = documentSnapshot.getString("nombres");
                    String apellidos = documentSnapshot.getString("apellidos");
                    nombreCompleto = (nombres != null ? nombres : "") + " " + (apellidos != null ? apellidos : "");
                } else {
                    nombreCompleto = "Usuario desconocido";
                }

                listaUsuarios.add(new ReporteUsuario(nombreCompleto.trim(), monto));

                if (listaUsuarios.size() == pendientes.size()) {
                    listaUsuarios.sort((a, b) -> Double.compare(b.getMontoTotal(), a.getMontoTotal()));
                    usuarioAdapter.actualizarLista(listaUsuarios);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
