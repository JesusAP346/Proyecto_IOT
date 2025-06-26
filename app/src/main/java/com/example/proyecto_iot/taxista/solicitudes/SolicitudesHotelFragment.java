package com.example.proyecto_iot.taxista.solicitudes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_iot.databinding.FragmentSolicitudesHotelBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import com.example.proyecto_iot.taxista.perfil.Notificacion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

                                    Solicitud solicitud = new Solicitud(
                                            nombre, telefono, 0, "3 min.\n1.2 km",
                                            hotel, direccionHotel, destino, urlFoto,
                                            lat, lng, doc.getId(), estado
                                    );

                                    solicitudes.add(solicitud);
                                    binding.recyclerSolicitudes.setAdapter(new SolicitudAdapter(solicitudes));
                                });
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener solicitudes", e));

        return binding.getRoot();
    }

    public void abrirMapa() {
        Intent intent = new Intent(requireContext(), MapsActivity.class);
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
