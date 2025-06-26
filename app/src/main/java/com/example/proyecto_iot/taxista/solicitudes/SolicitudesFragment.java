package com.example.proyecto_iot.taxista.solicitudes;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.FragmentSolicitudesBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;



public class SolicitudesFragment extends Fragment {

    private FragmentSolicitudesBinding binding;

    public SolicitudesFragment() {
        // Constructor vacío requerido
    }
    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSolicitudesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.carouselRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        List<CarouselItemDTO> dtoList = leerHotelesDesdeJson();

        if (dtoList.isEmpty()) {
            dtoList = crearListaHardcodeadaDTO();
            guardarHotelesEnJson(dtoList);
        }

        List<CarouselItemModel> itemList = convertirDtoAModelo(dtoList);

        CarouselAdapter adapter = new CarouselAdapter(itemList, item -> {
            SolicitudesHotelFragment fragment = new SolicitudesHotelFragment();
            fragment.setNombreHotel(item.title);

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedor_fragment_hijo, fragment)
                    .commit();
        });
        binding.carouselRecyclerView.setAdapter(adapter);

        // Cargar por defecto el hotel "Hotel Paraíso"
        for (CarouselItemModel item : itemList) {
            if ("Hotel Paraíso".equals(item.title)) {
                SolicitudesHotelFragment defaultFragment = new SolicitudesHotelFragment();
                defaultFragment.setNombreHotel(item.title);
                getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.contenedor_fragment_hijo, defaultFragment)
                        .commit();
                break;
            }
        }

        return view;
    }


    */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSolicitudesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.carouselRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("hoteles")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CarouselItemModel> itemList = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String idHotel = doc.getString("id");
                        String nombre = doc.getString("nombre");
                        List<String> referencias = (List<String>) doc.get("referencias");
                        String location = (referencias != null && !referencias.isEmpty()) ? referencias.get(0) : "";
                        List<String> fotos = (List<String>) doc.get("fotosHotelUrls");
                        String urlPrimeraFoto = (fotos != null && !fotos.isEmpty()) ? fotos.get(0) : null;
                        int resId = getDrawableIdPorNombre("hotel1");

                        // Subconsulta: contar solicitudes por hotel con estado pendiente
                        db.collection("servicios_taxi")
                                .whereEqualTo("idHotel", idHotel)
                                .whereEqualTo("estado", "pendiente")
                                .get()
                                .addOnSuccessListener(solicitudesSnapshot -> {
                                    int cantidad = solicitudesSnapshot.size();

                                    itemList.add(new CarouselItemModel(
                                            resId,
                                            nombre,
                                            cantidad + " solicitudes ",
                                            location,
                                            "★★★★☆",
                                            urlPrimeraFoto
                                    ));

                                    // Verifica si ya se cargaron todos los hoteles
                                    if (itemList.size() == queryDocumentSnapshots.size()) {
                                        cargarAdapter(itemList);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> e.printStackTrace());

        return view;
    }

    private void cargarAdapter(List<CarouselItemModel> itemList) {
        CarouselAdapter adapter = new CarouselAdapter(itemList, item -> {
            SolicitudesHotelFragment fragment = new SolicitudesHotelFragment();
            fragment.setNombreHotel(item.title);

            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedor_fragment_hijo, fragment)
                    .commit();
        });
        binding.carouselRecyclerView.setAdapter(adapter);

        if (!itemList.isEmpty()) {
            CarouselItemModel primerHotel = itemList.get(0);
            SolicitudesHotelFragment defaultFragment = new SolicitudesHotelFragment();
            defaultFragment.setNombreHotel(primerHotel.title);
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.contenedor_fragment_hijo, defaultFragment)
                    .commit();
        }
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Limpieza para evitar memory leaks
    }

    //storage:
    private static final String FILE_NAME = "hoteles.json";

    private List<CarouselItemDTO> leerHotelesDesdeJson() {
        List<CarouselItemDTO> lista = new ArrayList<>();

        try (FileInputStream fis = requireContext().openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            String json = sb.toString();
            Type listType = new TypeToken<List<CarouselItemDTO>>() {}.getType();
            lista = new Gson().fromJson(json, listType);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private void guardarHotelesEnJson(List<CarouselItemDTO> lista) {
        Gson gson = new Gson();
        String json = gson.toJson(lista);

        try (FileOutputStream fos = requireContext().openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             FileWriter writer = new FileWriter(fos.getFD())) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int getDrawableIdPorNombre(String nombre) {
        Context context = getContext();
        if (context == null) return R.drawable.ic_launcher_foreground; // drawable por defecto
        return context.getResources().getIdentifier(nombre, "drawable", context.getPackageName());
    }

    private List<CarouselItemModel> convertirDtoAModelo(List<CarouselItemDTO> dtoList) {
        List<CarouselItemModel> modeloList = new ArrayList<>();
        for (CarouselItemDTO dto : dtoList) {
            int resId = getDrawableIdPorNombre(dto.imageResName);
            modeloList.add(new CarouselItemModel(resId, dto.title, dto.subtitle, dto.location, dto.stars));
        }
        return modeloList;
    }

    private List<CarouselItemDTO> crearListaHardcodeadaDTO() {
        List<CarouselItemDTO> lista = new ArrayList<>();
        lista.add(new CarouselItemDTO("hotel1", "Hotel Paraíso", "4 solicitudes", "SJL", "★★★★☆"));
        lista.add(new CarouselItemDTO("hotel2", "Hotel Amanecer", "3 solicitudes", "Miraflores", "★★★★★"));
        lista.add(new CarouselItemDTO("hotel3", "Hotel Playa", "2 solicitud", "Barranco", "★★★☆☆"));
        return lista;
    }





}
