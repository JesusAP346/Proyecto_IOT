package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.proyecto_iot.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.administradorHotel.RegistroHabitacionHotel;
import com.example.proyecto_iot.administradorHotel.adapter.HabitacionAdapter;

import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.Hotel;
import com.example.proyecto_iot.databinding.FragmentHotelHabitacionesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class HotelHabitacionesFragment extends Fragment {

    private FragmentHotelHabitacionesBinding binding;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private HabitacionAdapter adapter;
    private List<HabitacionHotel> listaHabitaciones = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHotelHabitacionesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        binding.recyclerHabitaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new HabitacionAdapter(listaHabitaciones, requireContext(), habitacion -> {
            DetalleHabitacionFragment fragment = new DetalleHabitacionFragment();

            Bundle bundle = new Bundle();
            bundle.putSerializable("habitacion", habitacion);
            fragment.setArguments(bundle);

            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });



        binding.recyclerHabitaciones.setAdapter(adapter);

        cargarHabitacionesDelhotel();

        binding.btnRegistrarInformacion.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), RegistroHabitacionHotel.class);
            startActivityForResult(intent, 1234);
        });
    }

    private void cargarHabitacionesDelhotel() {
        if (currentUser == null) {
            mostrarMensajeSinHabitaciones();
            return;
        }

        String idAdmin = currentUser.getUid();

        // Buscar el hotel que le pertenece al admin
        db.collection("hoteles")
                .whereEqualTo("idAdministrador", idAdmin)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (querySnapshot.isEmpty()) {
                        mostrarMensajeSinHabitaciones();
                        return;
                    }

                    DocumentSnapshot hotelDoc = querySnapshot.getDocuments().get(0);
                    String idHotel = hotelDoc.getId();

                    db.collection("hoteles")
                            .document(idHotel)
                            .collection("habitaciones")
                            .get()
                            .addOnSuccessListener(snapshot -> {
                                listaHabitaciones.clear();
                                for (DocumentSnapshot doc : snapshot) {
                                    HabitacionHotel habitacion = doc.toObject(HabitacionHotel.class);
                                    listaHabitaciones.add(habitacion);
                                }

                                if (listaHabitaciones.isEmpty()) {
                                    if (isAdded() && binding != null) {
                                        mostrarMensajeSinHabitaciones();
                                    }
                                } else {
                                    if (isAdded() && binding != null) {
                                        binding.layoutMensajeVacio.setVisibility(View.GONE); // Ocultamos el mensaje
                                        binding.recyclerHabitaciones.setVisibility(View.VISIBLE);
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(requireContext(), "Error al cargar habitaciones", Toast.LENGTH_SHORT).show();
                                mostrarMensajeSinHabitaciones();
                            });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al verificar hotel del administrador", Toast.LENGTH_SHORT).show();
                    mostrarMensajeSinHabitaciones();
                });
    }

    private void mostrarMensajeSinHabitaciones() {
        if (binding != null) {
            binding.recyclerHabitaciones.setVisibility(View.GONE);
            binding.layoutMensajeVacio.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
