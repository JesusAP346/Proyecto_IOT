package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.PagPrincipalAdmin;
import com.example.proyecto_iot.administradorHotel.chat.ListaChatAdminActivity;
import com.example.proyecto_iot.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeFragment extends Fragment {

    private int cantidadNotificacionesNuevas = 0;
    FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        verificarNotificacionesNuevas();

        binding.iconoCampana.setOnClickListener(v -> {
            // Marcar notificaciones como vistas
            requireActivity().getSharedPreferences("prefs_admin", 0)
                    .edit()
                    .putBoolean("notificaciones_vistas", true)
                    .apply();

            // Ocultar badge inmediatamente
            binding.notificacionBadge.setVisibility(View.GONE);

            // Abrir fragmento de notificaciones
            Fragment adminNotificacionesFragment = new AdminNotificacionesFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, adminNotificacionesFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.iconoChat.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), ListaChatAdminActivity.class);
            startActivity(intent);
        });

        binding.btnHabitaciones.setOnClickListener(v -> {
            PagPrincipalAdmin activity = (PagPrincipalAdmin) requireActivity();
            activity.seleccionarTab(R.id.hotel);
            Fragment hotelFragment = HotelFragment.newInstance("habitaciones");
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frame_layout, hotelFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnServiciosExtras.setOnClickListener(v -> {
            PagPrincipalAdmin activity = (PagPrincipalAdmin) requireActivity();
            activity.seleccionarTab(R.id.hotel);
            Fragment hotelFragment = HotelFragment.newInstance("servicios");
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, hotelFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnReportes.setOnClickListener(v -> {
            PagPrincipalAdmin activity = (PagPrincipalAdmin) requireActivity();
            activity.seleccionarTab(R.id.hotel);
            Fragment hotelFragment = HotelFragment.newInstance("reportes");
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, hotelFragment)
                    .addToBackStack(null)
                    .commit();
        });

        binding.btnReservas.setOnClickListener(v -> {
            PagPrincipalAdmin activity = (PagPrincipalAdmin) requireActivity();
            activity.seleccionarTab(R.id.reservas);
        });
    }

    private void verificarNotificacionesNuevas() {
        String uidAdmin = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("usuarios")
                .document(uidAdmin)
                .get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        String idHotel = snapshot.getString("idHotel");

                        if (idHotel != null) {
                            FirebaseFirestore.getInstance()
                                    .collection("notificaciones")
                                    .whereEqualTo("idHotel", idHotel)
                                    .whereEqualTo("rol", "administrador")
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        cantidadNotificacionesNuevas = queryDocumentSnapshots.size();

                                        boolean yaSeVieron = requireActivity()
                                                .getSharedPreferences("prefs_admin", 0)
                                                .getBoolean("notificaciones_vistas", false);

                                        if (!yaSeVieron && cantidadNotificacionesNuevas > 0) {
                                            binding.notificacionBadge.setVisibility(View.VISIBLE);
                                            binding.notificacionBadge.setText(String.valueOf(cantidadNotificacionesNuevas));
                                        } else {
                                            binding.notificacionBadge.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
        verificarNotificacionesNuevas(); // se vuelve a chequear al regresar
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
