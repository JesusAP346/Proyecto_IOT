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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
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

public class SolicitudesFragment extends Fragment {

    private FragmentSolicitudesBinding binding;

    public SolicitudesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSolicitudesBinding.inflate(inflater, container, false);
        binding.carouselRecyclerView.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false)
        );
        cargarHotelesDesdeFirestore();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        cargarHotelesDesdeFirestore(); // 🔄 Se actualiza al volver desde MapsActivity
    }

    private void cargarHotelesDesdeFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("hoteles")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CarouselItemModel> itemList = new ArrayList<>();
                    int totalHoteles = queryDocumentSnapshots.size();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String idHotel = doc.getString("id");
                        String nombre = doc.getString("nombre");
                        List<String> referencias = (List<String>) doc.get("referencias");
                        String location = (referencias != null && !referencias.isEmpty()) ? referencias.get(0) : "";
                        List<String> fotos = (List<String>) doc.get("fotosHotelUrls");
                        String urlPrimeraFoto = (fotos != null && !fotos.isEmpty()) ? fotos.get(0) : null;
                        int resId = getDrawableIdPorNombre("hotel1");

                        db.collection("servicios_taxi")
                                .whereEqualTo("idHotel", idHotel)
                                .whereEqualTo("estado", "pendiente")
                                .get()
                                .addOnSuccessListener(solicitudesSnapshot -> {
                                    int cantidad = solicitudesSnapshot.size();

                                    // 🔍 Obtener las valoraciones para calcular promedio
                                    db.collection("hoteles")
                                            .document(idHotel)
                                            .collection("valoraciones")
                                            .get()
                                            .addOnSuccessListener(valoracionesSnapshot -> {
                                                int sumaEstrellas = 0;
                                                int totalValoraciones = 0;
                                                for (QueryDocumentSnapshot valoracion : valoracionesSnapshot) {
                                                    Long estrellas = valoracion.getLong("estrellas");
                                                    if (estrellas != null) {
                                                        sumaEstrellas += estrellas;
                                                        totalValoraciones++;
                                                    }
                                                }

                                                float promedio = totalValoraciones > 0
                                                        ? (float) sumaEstrellas / totalValoraciones
                                                        : 0f;

                                                String estrellasTexto = convertirPromedioAEstrellas(promedio);

                                                itemList.add(new CarouselItemModel(
                                                        resId,
                                                        nombre,
                                                        cantidad + " solicitudes",
                                                        location,
                                                        estrellasTexto,
                                                        urlPrimeraFoto
                                                ));

                                                if (isAdded() && binding != null && itemList.size() == totalHoteles) {
                                                    cargarAdapter(itemList);
                                                }
                                            })
                                            .addOnFailureListener(Throwable::printStackTrace);
                                })
                                .addOnFailureListener(Throwable::printStackTrace);
                    }
                })
                .addOnFailureListener(Throwable::printStackTrace);
    }
    private String convertirPromedioAEstrellas(float promedio) {
        int estrellasLlenas = Math.round(promedio);
        StringBuilder estrellas = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            estrellas.append(i < estrellasLlenas ? "★" : "☆");
        }
        return estrellas.toString();
    }



    private void cargarAdapter(List<CarouselItemModel> itemList) {
        if (!isAdded() || binding == null) return;

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
        binding = null;
    }

    private static final String FILE_NAME = "hoteles.json";

    private List<CarouselItemDTO> leerHotelesDesdeJson() {
        List<CarouselItemDTO> lista = new ArrayList<>();
        try (FileInputStream fis = requireContext().openFileInput(FILE_NAME);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) sb.append(line);
            Type listType = new TypeToken<List<CarouselItemDTO>>() {}.getType();
            lista = new Gson().fromJson(sb.toString(), listType);
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
        if (context == null) return R.drawable.ic_launcher_foreground;
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
