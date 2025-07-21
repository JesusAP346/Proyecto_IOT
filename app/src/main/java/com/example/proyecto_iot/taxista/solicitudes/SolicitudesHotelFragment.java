package com.example.proyecto_iot.taxista.solicitudes;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_iot.databinding.FragmentSolicitudesHotelBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SolicitudesHotelFragment extends Fragment {

    private FragmentSolicitudesHotelBinding binding;
    private String nombreHotel = "Hotel Paraíso";

    public SolicitudesHotelFragment() {}

    public void setNombreHotel(String nombreHotel) {
        this.nombreHotel = nombreHotel;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSolicitudesHotelBinding.inflate(inflater, container, false);
        binding.nombreHotel.setText("Hotel: " + nombreHotel);
        binding.recyclerSolicitudes.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("servicios_taxi")
                .whereEqualTo("nombreHotel", nombreHotel)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Solicitud> solicitudes = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
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

                        db.collection("usuarios")
                                .document(idCliente)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String urlFoto = userDoc.getString("urlFotoPerfil");
                                    if (urlFoto == null || urlFoto.isEmpty()) {
                                        urlFoto = "drawable://usuario_10";
                                    }

                                    Solicitud item = new Solicitud(
                                            nombre, telefono, 0, "3 min.\n1.2 km",
                                            hotel, direccionHotel, destino, urlFoto,
                                            lat, lng, doc.getId(), estado
                                    );

                                    solicitudes.add(item);
                                    binding.recyclerSolicitudes.setAdapter(new SolicitudAdapter(solicitudes, selected -> {
                                        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                                                != PackageManager.PERMISSION_GRANTED) {
                                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
                                            Toast.makeText(getContext(), "Debes aceptar el permiso de ubicación para abrir el mapa", Toast.LENGTH_LONG).show();
                                            return;
                                        }

                                        // Actualiza el estado solo si está pendiente
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
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener solicitudes", e));

        return binding.getRoot();
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
        startActivity(intent);
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

    private String getFileName() {
        return "solicitudes_" + nombreHotel.replace(" ", "_").toLowerCase() + ".json";
    }

    private void guardarListaSolicitudes(List<Solicitud> lista) {
        Gson gson = new Gson();
        String json = gson.toJson(lista);
        String fileName = getFileName();

        try (FileOutputStream fos = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE);
             FileWriter writer = new FileWriter(fos.getFD())) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Solicitud> leerListaSolicitudes() {
        List<Solicitud> lista = new ArrayList<>();
        String fileName = getFileName();

        try (FileInputStream fis = requireContext().openFileInput(fileName);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader reader = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = reader.readLine()) != null) {
                sb.append(linea);
            }

            String json = sb.toString();
            Type tipoLista = new TypeToken<List<Solicitud>>() {}.getType();
            lista = new Gson().fromJson(json, tipoLista);

        } catch (Exception e) {
            Log.e("Storage", "Error al leer archivo: " + fileName);
        }

        return lista;
    }
}
