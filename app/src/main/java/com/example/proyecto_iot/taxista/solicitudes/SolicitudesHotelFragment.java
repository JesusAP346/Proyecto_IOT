package com.example.proyecto_iot.taxista.solicitudes;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentSolicitudesHotelBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;




public class SolicitudesHotelFragment extends Fragment {

    private FragmentSolicitudesHotelBinding binding;
    private String nombreHotel = "Hotel Paraíso";


    public SolicitudesHotelFragment() {
        // Constructor vacío requerido
    }

    public void setNombreHotel(String nombreHotel) {
        this.nombreHotel = nombreHotel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSolicitudesHotelBinding.inflate(inflater, container, false);

        Log.d("SolicitudesHotel", "Fragment iniciado para hotel: " + nombreHotel);

        binding.nombreHotel.setText("Hotel: " + nombreHotel);
        binding.recyclerSolicitudes.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("servicios_taxi")
                .whereEqualTo("nombreHotel", nombreHotel)
                .whereEqualTo("estado", "pendiente")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Solicitud> solicitudes = new ArrayList<>();

                    if (queryDocumentSnapshots.isEmpty()) {
                        Log.w("SolicitudesHotel", "No hay documentos para el hotel: " + nombreHotel);
                    }

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Log.d("SolicitudesHotel", "Documento encontrado: " + doc.getId());

                        String idCliente = doc.getString("idCliente");
                        String nombre = doc.getString("nombreCliente");
                        String telefono = doc.getString("celularCliente");
                        String destino = doc.getString("destino");
                        String hotel = doc.getString("nombreHotel");
                        String direccionHotel = doc.getString("direccionHotel");
                        int viajes = 0;
                        double lat = doc.getDouble("latitudHotel") != null ? doc.getDouble("latitudHotel") : 0.0;
                        double lng = doc.getDouble("longitudHotel") != null ? doc.getDouble("longitudHotel") : 0.0;

                        if (idCliente == null || idCliente.isEmpty()) {
                            Log.e("SolicitudesHotel", "idCliente es nulo o vacío en doc: " + doc.getId());
                            continue;
                        }

                        db.collection("usuarios")
                                .document(idCliente)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    String urlFoto = userDoc.getString("urlFotoPerfil");

                                    if (urlFoto == null || urlFoto.isEmpty()) {
                                        urlFoto = "drawable://" + R.drawable.usuario_10;
                                        Log.w("SolicitudesHotel", "Usuario sin foto, usando default: " + idCliente);
                                    } else {
                                        Log.d("SolicitudesHotel", "Foto de usuario encontrada: " + urlFoto);
                                    }

                                    Solicitud solicitud = new Solicitud(
                                            nombre, telefono, viajes, "3 min.\n1.2 km",
                                            hotel, direccionHotel, destino,
                                            urlFoto, lat, lng
                                    );

                                    solicitudes.add(solicitud);

                                    // ¡Actualizar el RecyclerView!
                                    binding.recyclerSolicitudes.setAdapter(new SolicitudAdapter(solicitudes));
                                    Log.d("SolicitudesHotel", "Solicitud agregada: " + nombre);
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firestore", "Error al obtener usuario: " + idCliente, e);
                                });
                    }

                    Log.d("SolicitudesHotel", "Total solicitudes iniciales encontradas: " + queryDocumentSnapshots.size());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener solicitudes", e);
                });

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


    //storage:
    private String getFileName() {
        return "solicitudes_" + nombreHotel.replace(" ", "_").toLowerCase() + ".json";
    }

    // Guardar lista en JSON en Internal Storage
    private void guardarListaSolicitudes(List<Solicitud> lista) {
        Gson gson = new Gson();
        String json = gson.toJson(lista);

        String fileName = getFileName();

        try (FileOutputStream fos = requireContext().openFileOutput(fileName, Context.MODE_PRIVATE);
             FileWriter writer = new FileWriter(fos.getFD())) {

            writer.write(json);
            Log.d("Storage", "Lista de solicitudes guardada en archivo: " + fileName);

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
            Log.e("Storage", "No existe archivo para " + fileName + " o error al leer.");
        }

        return lista;
    }
    /*
    private List<Solicitud> crearSolicitudesHardcodeadas() {
        List<Solicitud> lista = new ArrayList<>();

        switch (nombreHotel) {
            case "Hotel Paraíso":
                lista.add(new Solicitud("Roberto Tafur", "945 854 123", 5, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.roberto,-12.056101891213803, -77.10993562244279));
                lista.add(new Solicitud("Ricardo Calderón", "945 854 123", 3, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.usuario_10,-12.056101891213803, -77.10993562244279));
                lista.add(new Solicitud("Juan Perez", "945 854 123", 3, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.usuario_10,-12.056101891213803, -77.10993562244279));
                lista.add(new Solicitud("Alejandra Ríos", "945 854 123", 3, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.usuario_12));
                break;

            case "Hotel Amanecer":
                lista.add(new Solicitud("Raul Castillo", "912 345 678", 2, "6 min.\n2.5 km",
                        "Hotel Amanecer", "Miraflores", "Aeropuerto", R.drawable.usuario_10,-12.056101891213803, -77.10993562244279));
                lista.add(new Solicitud("Diego Galindo", "912 345 678", 2, "6 min.\n2.5 km",
                        "Hotel Amanecer", "Miraflores", "Aeropuerto", R.drawable.usuario_11,-12.056101891213803, -77.10993562244279));
                lista.add(new Solicitud("Carolina Sanchez", "912 345 678", 2, "6 min.\n2.5 km",
                        "Hotel Amanecer", "Miraflores", "Aeropuerto", R.drawable.usuario_12,-12.056101891213803, -77.10993562244279));
                break;

            case "Hotel Playa":
                lista.add(new Solicitud("Marco Gómez", "987 654 321", 4, "3 min.\n1.0 km",
                        "Hotel Playa", "Barranco", "Aeropuerto", R.drawable.roberto,-12.056101891213803, -77.10993562244279));
                lista.add(new Solicitud("Leonardo Torres", "987 654 321", 4, "3 min.\n1.0 km",
                        "Hotel Playa", "Barranco", "Aeropuerto", R.drawable.roberto,-12.056101891213803, -77.10993562244279));
                break;

            default:
                // Si no coincide con ningún hotel, la lista queda vacía
                break;
        }
        return lista;
    }

     */


}