package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_iot.administradorHotel.adapter.NotificacionAdapter;
import com.example.proyecto_iot.administradorHotel.entity.NotificacionAdmin;
import com.example.proyecto_iot.databinding.FragmentAdminNotificacionesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class AdminNotificacionesFragment extends Fragment {

    private FragmentAdminNotificacionesBinding binding;
    private NotificacionAdapter adapter;
    private final List<NotificacionAdmin> notificaciones = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAdminNotificacionesBinding.inflate(inflater, container, false);

        binding.backdenotificaciones.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        binding.recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotificacionAdapter(notificaciones);
        binding.recyclerNotificaciones.setAdapter(adapter);

        cargarNotificaciones();

        return binding.getRoot();
    }

    private void cargarNotificaciones() {
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
                                        notificaciones.clear();

                                        for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                                            String mensaje = doc.getString("mensaje");
                                            Long timestamp = doc.getLong("timestamp");

                                            String horaFormateada = "Hora desconocida";
                                            if (timestamp != null) {
                                                Date fecha = new Date(timestamp);
                                                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                                formato.setTimeZone(TimeZone.getTimeZone("America/Lima"));  // Forzar zona horaria Perú
                                                horaFormateada = formato.format(fecha);
                                            }

                                            notificaciones.add(new NotificacionAdmin(mensaje, horaFormateada));
                                        }

                                        adapter.notifyDataSetChanged();
                                    });
                        }
                    }
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
