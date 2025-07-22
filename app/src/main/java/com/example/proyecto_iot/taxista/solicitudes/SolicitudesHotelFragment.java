package com.example.proyecto_iot.taxista.solicitudes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_iot.databinding.FragmentSolicitudesHotelBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SolicitudesHotelFragment extends Fragment {

    private ActivityResultLauncher<Intent> launcherMaps;
    private FragmentSolicitudesHotelBinding binding;
    private String nombreHotel = "Hotel Paraíso";

    public SolicitudesHotelFragment() {}

    public void setNombreHotel(String nombreHotel) {
        this.nombreHotel = nombreHotel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        launcherMaps = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == requireActivity().RESULT_OK) {
                        cargarSolicitudes();
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSolicitudesHotelBinding.inflate(inflater, container, false);
        binding.nombreHotel.setText("Hotel: " + nombreHotel);
        binding.recyclerSolicitudes.setLayoutManager(new LinearLayoutManager(getContext()));
        cargarSolicitudes();
        return binding.getRoot();
    }

    private void cargarSolicitudes() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("servicios_taxi")
                .whereEqualTo("nombreHotel", nombreHotel)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Solicitud> solicitudes = new ArrayList<>();
                    List<DocumentSnapshot> docs = new ArrayList<>(queryDocumentSnapshots.getDocuments());

                    if (docs.isEmpty()) {
                        binding.recyclerSolicitudes.setAdapter(new SolicitudAdapter(new ArrayList<>(), s -> {}));
                        return;
                    }

                    long totalValidas = docs.stream().filter(d -> {
                        String estado = d.getString("estado");
                        return "pendiente".equals(estado) || "aceptado".equals(estado);
                    }).count();

                    for (DocumentSnapshot doc : docs) {
                        String estado = doc.getString("estado");
                        if (!"pendiente".equals(estado) && !"aceptado".equals(estado)) continue;

                        String idCliente = doc.getString("idCliente");
                        String nombre = doc.getString("nombreCliente");
                        String telefono = doc.getString("celularCliente");
                        String destino = doc.getString("destino");
                        String hotel = doc.getString("nombreHotel");
                        String direccionHotel = doc.getString("direccionHotel");
                        double lat = doc.getDouble("latitudHotel") != null ? doc.getDouble("latitudHotel") : 0.0;
                        double lng = doc.getDouble("longitudHotel") != null ? doc.getDouble("longitudHotel") : 0.0;
                        double latTaxista = doc.getDouble("latTaxista") != null ? doc.getDouble("latTaxista") : 0.0;
                        double lngTaxista = doc.getDouble("longTaxista") != null ? doc.getDouble("longTaxista") : 0.0;

                        db.collection("usuarios")
                                .document(idCliente)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String urlFoto = userDoc.getString("urlFotoPerfil");
                                    if (urlFoto == null || urlFoto.isEmpty()) {
                                        urlFoto = "drawable://icono_perfil";
                                    }

                                    Solicitud item = new Solicitud(
                                            nombre, telefono, 0, "Cargando...",
                                            hotel, direccionHotel, destino, urlFoto,
                                            lat, lng, latTaxista, lngTaxista,
                                            doc.getId(), estado
                                    );

                                    solicitudes.add(item);

                                    if (solicitudes.size() == totalValidas) {
                                        binding.recyclerSolicitudes.setAdapter(new SolicitudAdapter(solicitudes, selected -> {
                                            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                                    != PackageManager.PERMISSION_GRANTED) {
                                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                                                Toast.makeText(getContext(), "Debes aceptar el permiso de ubicación para abrir el mapa", Toast.LENGTH_LONG).show();
                                                return;
                                            }

                                            if ("pendiente".equals(selected.estado)) {
                                                db.collection("servicios_taxi")
                                                        .document(selected.idDocumento)
                                                        .update(
                                                                "estado", "aceptado",
                                                                "idTaxista", FirebaseAuth.getInstance().getCurrentUser().getUid()
                                                        )
                                                        .addOnSuccessListener(aVoid -> abrirMapa(selected))
                                                        .addOnFailureListener(e -> {
                                                            e.printStackTrace();
                                                            Toast.makeText(getContext(), "Error al aceptar solicitud", Toast.LENGTH_SHORT).show();
                                                        });
                                            } else {
                                                abrirMapa(selected);
                                            }
                                        }));
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener solicitudes", e));
    }

    private void abrirMapa(Solicitud solicitud) {
        Intent intent = new Intent(requireContext(), MapsActivity.class);
        intent.putExtra("nombre", solicitud.nombre);
        intent.putExtra("telefono", solicitud.telefono);
        intent.putExtra("viajes", solicitud.viajes + " viajes");
        intent.putExtra("hotel", solicitud.origen);
        intent.putExtra("imagenPerfilUrl", solicitud.urlFotoPerfil);
        intent.putExtra("latDestino", solicitud.latDestino);
        intent.putExtra("lngDestino", solicitud.lngDestino);
        intent.putExtra("idServicio", solicitud.idDocumento);
        launcherMaps.launch(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }
}
