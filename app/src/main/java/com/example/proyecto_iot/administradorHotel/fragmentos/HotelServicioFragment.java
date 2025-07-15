package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.RegistroHabitacionHotel;
import com.example.proyecto_iot.administradorHotel.RegistroServicioDesdeOpciones;
import com.example.proyecto_iot.administradorHotel.adapter.HabitacionAdapter;
import com.example.proyecto_iot.administradorHotel.adapter.ServicioAdapter;
import com.example.proyecto_iot.administradorHotel.dto.ServicioDto;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.Servicio;
import com.example.proyecto_iot.administradorHotel.entity.ServicioHotel;
import com.example.proyecto_iot.databinding.FragmentHotelServicioBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HotelServicioFragment extends Fragment {

    FragmentHotelServicioBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private ServicioAdapter adapter;
    private List<ServicioHotel> listaServicios = new ArrayList<>();



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHotelServicioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.recyclerServicios.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ServicioAdapter(listaServicios, requireContext(), servicio -> {
            DetalleServicioFragment fragment = new DetalleServicioFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("servicio", servicio);
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.recyclerServicios.setAdapter(adapter);

        cargarServiciosDelHotel();

        binding.btnRegistrarServicio.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegistroServicioDesdeOpciones.class);
            startActivityForResult(intent, 1234);
            //startActivity(intent);
        });

    }

    private void cargarServiciosDelHotel() {
        if (currentUser == null) {
            mostrarMensajeSinServicios();
            return;
        }

        String idAdmin = currentUser.getUid();

        // Buscar el hotel que le pertenece al admin
        db.collection("hoteles")
                .whereEqualTo("idAdministrador", idAdmin)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        mostrarMensajeSinServicios();
                        return;
                    }

                    DocumentSnapshot hotelDoc = querySnapshot.getDocuments().get(0);
                    String idHotel = hotelDoc.getId();

                    db.collection("hoteles")
                            .document(idHotel)
                            .collection("servicios")
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                listaServicios.clear();
                                for (DocumentSnapshot doc : snapshot) {
                                    ServicioHotel servicio = doc.toObject(ServicioHotel.class);
                                    listaServicios.add(servicio);
                                }

                                if (listaServicios.isEmpty()) {
                                    if (isAdded() && binding != null) {
                                        mostrarMensajeSinServicios();
                                    }
                                } else {
                                    if (isAdded() && binding != null) {
                                        binding.layoutMensajeVacio.setVisibility(View.GONE); // Ocultamos el mensaje
                                        binding.recyclerServicios.setVisibility(View.VISIBLE);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Error al cargar habitaciones", Toast.LENGTH_SHORT).show();
                                mostrarMensajeSinServicios();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al verificar hotel del administrador", Toast.LENGTH_SHORT).show();
                    mostrarMensajeSinServicios();
                });
    }

    private void mostrarMensajeSinServicios() {
        if (binding != null) {
            binding.recyclerServicios.setVisibility(View.GONE);
            binding.layoutMensajeVacio.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
