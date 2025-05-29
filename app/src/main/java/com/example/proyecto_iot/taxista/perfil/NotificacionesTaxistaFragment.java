package com.example.proyecto_iot.taxista.perfil;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.adapters.NotificacionesAdapter;
import com.example.proyecto_iot.databinding.FragmentNotificacionesTaxistaBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacionesTaxistaFragment extends Fragment {

    private FragmentNotificacionesTaxistaBinding binding;
    private List<Notificacion> listaNotificaciones;
    private NotificacionesAdapter adapter;

    private static final String FILE_NOTIFICACIONES = "notificaciones.json";

    public NotificacionesTaxistaFragment() {}

    public static NotificacionesTaxistaFragment newInstance() {
        return new NotificacionesTaxistaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNotificacionesTaxistaBinding.inflate(inflater, container, false);

        binding.backdenotificaciones.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        listaNotificaciones = leerNotificacionesDesdeStorage();

        if (listaNotificaciones.isEmpty()) {
            listaNotificaciones = crearListaHardcodeada();
            guardarNotificacionesEnStorage(listaNotificaciones);
        }

// Ahora sí invertimos la lista para mostrar las más recientes primero
        Collections.reverse(listaNotificaciones);



        if (listaNotificaciones.isEmpty()) {
            listaNotificaciones = crearListaHardcodeada(); // metodo con las notificaciones de ejemplo
            guardarNotificacionesEnStorage(listaNotificaciones); // guardarlas para futuras cargas
        }


        if (listaNotificaciones.isEmpty()) {
            // Opcional: iniciar con lista vacía o con datos hardcodeados
            // listaNotificaciones = datosHardcodeados();
        }

        adapter = new NotificacionesAdapter(listaNotificaciones);
        binding.recyclerNotificaciones.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerNotificaciones.setAdapter(adapter);

        return binding.getRoot();
    }

    private List<Notificacion> leerNotificacionesDesdeStorage() {
        List<Notificacion> lista = new ArrayList<>();
        try {
            Context context = requireContext();
            FileInputStream fis = context.openFileInput(FILE_NOTIFICACIONES);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
            br.close();

            String json = sb.toString();

            Type listType = new TypeToken<List<NotificacionDTO>>() {}.getType();
            List<NotificacionDTO> dtoList = new Gson().fromJson(json, listType);

            lista = NotificacionUtils.fromDTOList(dtoList);

        } catch (Exception e) {
            Log.e("Storage", "Error leyendo notificaciones: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private List<Notificacion> crearListaHardcodeada() {
        List<Notificacion> lista = new ArrayList<>();

        for (int i = 1; i <= 15; i++) {
            lista.add(new Notificacion(
                    "Nuevo pedido asignado: Recoge al huésped en Hotel Libertador",
                    "Hoy, 08:" + (10 + i) + " AM",
                    R.drawable.ic_hotel_solid));
        }

        for (int i = 1; i <= 10; i++) {
            lista.add(new Notificacion(
                    "El huésped canceló el servicio asignado desde el Hotel Meliá",
                    "Hoy, 09:" + (10 + i) + " AM",
                    R.drawable.ic_delete));
        }

        for (int i = 1; i <= 15; i++) {
            lista.add(new Notificacion(
                    "QR escaneado correctamente. Servicio #" + i + " finalizado con éxito.",
                    "Hoy, 10:" + (5 + i) + " AM",
                    R.drawable.ic_qr));
        }



        return lista;
    }


    private void guardarNotificacionesEnStorage(List<Notificacion> lista) {
        Gson gson = new Gson();
        String json = gson.toJson(lista);

        try (FileOutputStream fos = requireContext().openFileOutput("notificaciones.json", Context.MODE_PRIVATE);
             FileWriter writer = new FileWriter(fos.getFD())) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
