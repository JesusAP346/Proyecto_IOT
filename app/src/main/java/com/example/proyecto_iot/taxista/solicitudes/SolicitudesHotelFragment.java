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

        binding.nombreHotel.setText("Hotel: " + nombreHotel);

        binding.recyclerSolicitudes.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Solicitud> solicitudes = leerListaSolicitudes();

        if (solicitudes.isEmpty()) {
            solicitudes = crearSolicitudesHardcodeadas();
            guardarListaSolicitudes(solicitudes);
        }

        SolicitudAdapter adapter = new SolicitudAdapter(solicitudes);
        binding.recyclerSolicitudes.setAdapter(adapter);

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

    private List<Solicitud> crearSolicitudesHardcodeadas() {
        List<Solicitud> lista = new ArrayList<>();

        switch (nombreHotel) {
            case "Hotel Paraíso":
                lista.add(new Solicitud("Roberto Tafur", "945 854 123", 5, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.roberto));
                lista.add(new Solicitud("Ricardo Calderón", "945 854 123", 3, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.usuario_10));
                lista.add(new Solicitud("Juan Perez", "945 854 123", 3, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.usuario_11));
                lista.add(new Solicitud("Alejandra Ríos", "945 854 123", 3, "4 min.\n1.7 km",
                        "Hotel Paraíso", "SJL", "Aeropuerto", R.drawable.usuario_12));
                break;

            case "Hotel Amanecer":
                lista.add(new Solicitud("Raul Castillo", "912 345 678", 2, "6 min.\n2.5 km",
                        "Hotel Amanecer", "Miraflores", "Aeropuerto", R.drawable.usuario_10));
                lista.add(new Solicitud("Diego Galindo", "912 345 678", 2, "6 min.\n2.5 km",
                        "Hotel Amanecer", "Miraflores", "Aeropuerto", R.drawable.usuario_11));
                lista.add(new Solicitud("Carolina Sanchez", "912 345 678", 2, "6 min.\n2.5 km",
                        "Hotel Amanecer", "Miraflores", "Aeropuerto", R.drawable.usuario_12));
                break;

            case "Hotel Playa":
                lista.add(new Solicitud("Marco Gómez", "987 654 321", 4, "3 min.\n1.0 km",
                        "Hotel Playa", "Barranco", "Aeropuerto", R.drawable.roberto));
                lista.add(new Solicitud("Leonardo Torres", "987 654 321", 4, "3 min.\n1.0 km",
                        "Hotel Playa", "Barranco", "Aeropuerto", R.drawable.roberto));
                break;

            default:
                // Si no coincide con ningún hotel, la lista queda vacía
                break;
        }
        return lista;
    }


}