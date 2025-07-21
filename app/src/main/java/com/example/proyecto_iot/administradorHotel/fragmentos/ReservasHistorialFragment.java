package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.adapter.ReservaAdapter;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.ReservaCompletaHotel;
import com.example.proyecto_iot.administradorHotel.entity.ReservaHotel;
import com.example.proyecto_iot.administradorHotel.entity.ServicioAdicionalNombrePrecio;
import com.example.proyecto_iot.cliente.busqueda.ServicioAdicionalReserva;
import com.example.proyecto_iot.databinding.FragmentReservasHistorialBinding;
import com.example.proyecto_iot.databinding.FragmentReservasTodasBinding;
import com.example.proyecto_iot.dtos.Usuario;
import com.example.proyecto_iot.administradorHotel.entity.EstadoReservaUI;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class ReservasHistorialFragment extends Fragment {

    private FragmentReservasHistorialBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ReservaAdapter adapter;
    private final List<ReservaCompletaHotel> listaReservasCompletas = new ArrayList<>();
    private static final String TAG = "CARGA_RESERVAS";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservasHistorialBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Listener para mostrar el calendario cuando se toca el EditText
        View.OnClickListener datePickerListener = v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                        selectedMonth += 1;
                        String selectedDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;
                        binding.etSelectDate.setText(selectedDate);
                    },
                    year, month, day);
            datePickerDialog.show();
        };

        binding.etSelectDate.setOnClickListener(datePickerListener);


        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.recyclerReservasTodas.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ReservaAdapter(listaReservasCompletas, requireContext(), reservaCompleta -> {

            DetalleReservaHistorialFragment fragment = new DetalleReservaHistorialFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("reservaCompleta", reservaCompleta);
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        binding.recyclerReservasTodas.setAdapter(adapter);

        cargarReservasFinalizadasDelHotel();

    }


    private void cargarReservasFinalizadasDelHotel() {
        if (currentUser == null) {
            mostrarMensajeSinReservas();
            return;
        }

        String idAdmin = currentUser.getUid();

        db.collection("hoteles")
                .whereEqualTo("idAdministrador", idAdmin)
                .limit(1)
                .get()
                .addOnSuccessListener(hotelSnapshot -> {
                    if (hotelSnapshot.isEmpty()) {
                        mostrarMensajeSinReservas();
                        return;
                    }

                    String idHotel = hotelSnapshot.getDocuments().get(0).getId();
                    final boolean[] detalleAbierto = {false};

                    db.collection("reservas")
                            .whereEqualTo("idHotel", idHotel)
                            .whereEqualTo("estado", "FINALIZADO")
                            .get()
                            .addOnSuccessListener(reservaSnapshot -> {
                                if (!isAdded() || binding == null) return;

                                listaReservasCompletas.clear();

                                for (DocumentSnapshot doc : reservaSnapshot) {
                                    ReservaHotel reserva = doc.toObject(ReservaHotel.class);
                                    if (reserva == null) continue;

                                    reserva.setIdReserva(doc.getId());

                                    String idCliente = reserva.getIdCliente();
                                    String idHabitacion = reserva.getIdHabitacion();

                                    if (idCliente == null || idHabitacion == null ||
                                            idCliente.trim().isEmpty() || idHabitacion.trim().isEmpty()) {
                                        continue;
                                    }

                                    db.collection("usuarios").document(idCliente).get()
                                            .addOnSuccessListener(userDoc -> {
                                                Usuario usuario = userDoc.toObject(Usuario.class);
                                                if (usuario == null) return;

                                                db.collection("hoteles").document(idHotel)
                                                        .collection("habitaciones").document(idHabitacion)
                                                        .get()
                                                        .addOnSuccessListener(habDoc -> {
                                                            HabitacionHotel habitacion = habDoc.toObject(HabitacionHotel.class);
                                                            if (habitacion == null) return;

                                                            ReservaCompletaHotel reservaCompleta = new ReservaCompletaHotel(reserva, usuario, habitacion);

                                                            List<ServicioAdicionalNombrePrecio> serviciosAdicionalesInfo = new ArrayList<>();
                                                            List<ServicioAdicionalReserva> serviciosAdicionales = reserva.getServiciosAdicionales();

                                                            if (serviciosAdicionales == null || serviciosAdicionales.isEmpty()) {
                                                                reservaCompleta.setServiciosAdicionalesInfo(serviciosAdicionalesInfo);
                                                                listaReservasCompletas.add(reservaCompleta);
                                                                adapter.notifyDataSetChanged();
                                                                mostrarReservas();

                                                                // Verifica si es la reserva finalizada
                                                                if (!detalleAbierto[0] &&
                                                                        EstadoReservaUI.reservaFinalizadaId != null &&
                                                                        reserva.getIdReserva().equals(EstadoReservaUI.reservaFinalizadaId)) {

                                                                    detalleAbierto[0] = true; // Marcar que ya abrimos
                                                                    EstadoReservaUI.reservaFinalizadaId = null;

                                                                    DetalleReservaHistorialFragment fragment = new DetalleReservaHistorialFragment();
                                                                    Bundle bundle = new Bundle();
                                                                    bundle.putSerializable("reservaCompleta", reservaCompleta);
                                                                    fragment.setArguments(bundle);

                                                                    requireActivity().getSupportFragmentManager()
                                                                            .beginTransaction()
                                                                            .replace(R.id.frame_layout, fragment)
                                                                            .addToBackStack(null)
                                                                            .commit();
                                                                }


                                                            } else {
                                                                for (ServicioAdicionalReserva s : serviciosAdicionales) {
                                                                    db.collection("hoteles").document(idHotel)
                                                                            .collection("servicios").document(s.getIdServicioAdicional())
                                                                            .get()
                                                                            .addOnSuccessListener(servicioDoc -> {
                                                                                if (servicioDoc.exists()) {
                                                                                    String nombre = servicioDoc.getString("nombre");
                                                                                    Double precioUnitario = servicioDoc.getDouble("precio");
                                                                                    if (nombre != null && precioUnitario != null) {
                                                                                        double total = precioUnitario * s.getCantDias();
                                                                                        serviciosAdicionalesInfo.add(new ServicioAdicionalNombrePrecio(nombre, total));
                                                                                    }
                                                                                }

                                                                                if (serviciosAdicionalesInfo.size() == serviciosAdicionales.size()) {
                                                                                    reservaCompleta.setServiciosAdicionalesInfo(serviciosAdicionalesInfo);
                                                                                    listaReservasCompletas.add(reservaCompleta);
                                                                                    adapter.notifyDataSetChanged();
                                                                                    mostrarReservas();

                                                                                    // Verifica si es la reserva finalizada
                                                                                    if (!detalleAbierto[0] &&
                                                                                            EstadoReservaUI.reservaFinalizadaId != null &&
                                                                                            reserva.getIdReserva().equals(EstadoReservaUI.reservaFinalizadaId)) {

                                                                                        detalleAbierto[0] = true; // Marcar que ya abrimos
                                                                                        EstadoReservaUI.reservaFinalizadaId = null;

                                                                                        DetalleReservaHistorialFragment fragment = new DetalleReservaHistorialFragment();
                                                                                        Bundle bundle = new Bundle();
                                                                                        bundle.putSerializable("reservaCompleta", reservaCompleta);
                                                                                        fragment.setArguments(bundle);

                                                                                        requireActivity().getSupportFragmentManager()
                                                                                                .beginTransaction()
                                                                                                .replace(R.id.frame_layout, fragment)
                                                                                                .addToBackStack(null)
                                                                                                .commit();
                                                                                    }

                                                                                }
                                                                            });
                                                                }
                                                            }

                                                        });
                                            });
                                }

                                if (reservaSnapshot.isEmpty()) {
                                    mostrarMensajeSinReservas();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Error al cargar reservas activas", Toast.LENGTH_SHORT).show();
                                mostrarMensajeSinReservas();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al obtener hotel", Toast.LENGTH_SHORT).show();
                    mostrarMensajeSinReservas();
                });
    }

    private void mostrarReservas() {
        if (binding != null) {
            binding.layoutMensajeVacio.setVisibility(View.GONE);
            binding.recyclerReservasTodas.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarMensajeSinReservas() {
        if (binding != null) {
            binding.recyclerReservasTodas.setVisibility(View.GONE);
            binding.layoutMensajeVacio.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
