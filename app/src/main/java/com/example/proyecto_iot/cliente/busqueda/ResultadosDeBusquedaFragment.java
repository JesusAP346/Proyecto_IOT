package com.example.proyecto_iot.cliente.busqueda;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ResultadosDeBusquedaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ResultadosDeBusquedaFragment extends Fragment implements HotelAdapter.OnHotelClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int adultos = 0;
    private int ninos = 0;
    private int habitaciones = 0;

    private String fechaInicioGlobal;
    private String fechaFinGlobal;

    public ResultadosDeBusquedaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ResultadosDeBusquedaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ResultadosDeBusquedaFragment newInstance(String param1, String param2) {
        ResultadosDeBusquedaFragment fragment = new ResultadosDeBusquedaFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        if (getArguments() != null) {
            String destinoBusqueda = getArguments().getString("destino", "");
            String fechasBusqueda = getArguments().getString("fechas", "");
            String huespedesBusqueda = getArguments().getString("huespedes", "");

            adultos = getArguments().getInt("adultos", 0);
            ninos = getArguments().getInt("ninos", 0);
            habitaciones = getArguments().getInt("habitaciones", 0);

            Log.d("DEBUG ZZZZZ", "Adultos: " + adultos + ", Niños: " + ninos + ", Habitaciones: " + habitaciones);

            // Recibir coordenadas si están disponibles
            double destinoLat = getArguments().getDouble("destinoLat", 0.0);
            double destinoLng = getArguments().getDouble("destinoLng", 0.0);
            String destinoPlaceId = getArguments().getString("destinoPlaceId", "");

            fechaInicioGlobal = getArguments().getString("fechaInicio", "");
            fechaFinGlobal = getArguments().getString("fechaFin", "");

            Log.d("RESULTADOS_FECHAS", "Fechas guardadas: " + fechaInicioGlobal + " - " + fechaFinGlobal);

            Log.d("RESULTADOS", "Datos recibidos:");
            Log.d("RESULTADOS", "Destino: " + destinoBusqueda);
            Log.d("RESULTADOS", "Fechas: " + fechasBusqueda);
            Log.d("RESULTADOS", "Huéspedes: " + huespedesBusqueda);
            Log.d("RESULTADOS", "Coordenadas: " + destinoLat + ", " + destinoLng);
            Log.d("RESULTADOS", "Destino ID: " + destinoPlaceId);
        }
    }

    private RecyclerView recyclerView;
    private HotelAdapter hotelAdapter;
    private List<Hotel> hotelList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_resultados_de_busqueda, container, false);

        recyclerView = view.findViewById(R.id.recyclerHoteles);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        hotelList = new ArrayList<>();

        // Configurar el adapter con la lista vacía inicial
        hotelAdapter = new HotelAdapter(getContext(), hotelList, this, (hotel, pos, favorito) -> {
            actualizarEstadoFavoritos(hotel, favorito);
        });

        recyclerView.setAdapter(hotelAdapter);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        if (getArguments() != null) {
            String destinoBusqueda = getArguments().getString("destino", "");
            String fechasBusqueda = getArguments().getString("fechas", "");
            String huespedesBusqueda = getArguments().getString("huespedes", "");

            fechaInicioGlobal = getArguments().getString("fechaInicio");
            fechaFinGlobal = getArguments().getString("fechaFin");

            Log.d("ResultadosFragment", ", fechaInicio OWO= " + fechaInicioGlobal
                    + ", fechaFin OWO = " + fechaFinGlobal);

            double destinoLat = getArguments().getDouble("destinoLat", 0.0);
            double destinoLng = getArguments().getDouble("destinoLng", 0.0);
            String destinoPlaceId = getArguments().getString("destinoPlaceId", "");

            TextView textUbicacion = view.findViewById(R.id.textUbicacion);
            TextView textFechas = view.findViewById(R.id.textFechas);
            TextView textHuespedes = view.findViewById(R.id.textHuespedes);

            textUbicacion.setText(destinoBusqueda);
            textFechas.setText(fechasBusqueda);
            textHuespedes.setText(huespedesBusqueda);

            // Filtrar hoteles si tenemos coordenadas válidas
            if (destinoLat != 0.0 && destinoLng != 0.0) {
                filtrarHotelesPorUbicacion(destinoLat, destinoLng, 20.0);
            } else {
                // Si no hay coordenadas, mostrar todos los hoteles o un mensaje
                cargarTodosLosHoteles();
            }
        }

        return view;
    }

    private void actualizarEstadoFavoritos(Hotel hotel, boolean favorito) {
        Set<String> favoritos = FavoritosStorage.obtenerFavoritos(getContext());
        if (favorito) {
            favoritos.add(hotel.getId());
        } else {
            favoritos.remove(hotel.getId());
        }
        FavoritosStorage.guardarFavoritos(getContext(), favoritos);
    }

    private double calcularDistancia(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // Radio de la Tierra en kilómetros

        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c; // Distancia en kilómetros
    }

    private void filtrarHotelesPorUbicacion(double destinoLat, double destinoLng, double radioKm) {
        Log.d("FILTRO_HOTELES", "Buscando hoteles en radio de " + radioKm + " km...");

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("hoteles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Hotel> hotelesEncontrados = new ArrayList<>();
                        int totalHoteles = task.getResult().size();
                        int[] hotelesProcessed = {0}; // Contador para trackear hoteles procesados

                        if (totalHoteles == 0) {
                            actualizarListaHoteles(hotelesEncontrados);
                            return;
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Double hotelLatObj = document.getDouble("ubicacionLat");
                                Double hotelLngObj = document.getDouble("ubicacionLng");

                                if (hotelLatObj != null && hotelLngObj != null) {
                                    double hotelLat = hotelLatObj;
                                    double hotelLng = hotelLngObj;

                                    double distancia = calcularDistancia(destinoLat, destinoLng, hotelLat, hotelLng);

                                    Log.d("FILTRO_HOTELES", "Hotel: " + document.getId() +
                                            ", Distancia: " + String.format("%.2f", distancia) + " km");

                                    if (distancia <= radioKm) {
                                        Hotel hotel = document.toObject(Hotel.class);
                                        hotel.setId(document.getId());
                                        String distrito = obtenerDistritoDesdeLatLng(hotelLat, hotelLng);
                                        hotel.setUbicacion(distrito);

                                        // Obtener el precio mínimo de las habitaciones
                                        obtenerPrecioMinimoHotel(hotel, precioMinimo -> {
                                            hotel.setPrecio(String.valueOf(precioMinimo)); // Asume que tienes este setter
                                            hotelesEncontrados.add(hotel);

                                            hotelesProcessed[0]++;
                                            // Cuando todos los hoteles han sido procesados, actualizar la UI
                                            if (hotelesProcessed[0] == totalHoteles) {
                                                actualizarListaHoteles(hotelesEncontrados);
                                            }
                                        });
                                    } else {
                                        hotelesProcessed[0]++;
                                        if (hotelesProcessed[0] == totalHoteles) {
                                            actualizarListaHoteles(hotelesEncontrados);
                                        }
                                    }
                                } else {
                                    Log.w("FILTRO_HOTELES", "Hotel sin coordenadas: " + document.getId());
                                    hotelesProcessed[0]++;
                                    if (hotelesProcessed[0] == totalHoteles) {
                                        actualizarListaHoteles(hotelesEncontrados);
                                    }
                                }

                            } catch (Exception e) {
                                Log.e("FILTRO_HOTELES", "Error procesando hotel: " + e.getMessage());
                                hotelesProcessed[0]++;
                                if (hotelesProcessed[0] == totalHoteles) {
                                    actualizarListaHoteles(hotelesEncontrados);
                                }
                            }
                        }

                        Log.d("FILTRO_HOTELES", "Hoteles encontrados: " + hotelesEncontrados.size());

                    } else {
                        Log.e("FILTRO_HOTELES", "Error obteniendo hoteles: ", task.getException());
                        mostrarMensajeError("Error al buscar hoteles");
                    }
                });
    }

    private String obtenerDistritoDesdeLatLng(double lat, double lng) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocation(lat, lng, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address direccion = direcciones.get(0);
                // Puedes probar con: getSubLocality(), getLocality(), getSubAdminArea()
                return direccion.getSubLocality() != null ? direccion.getSubLocality() :
                        direccion.getLocality() != null ? direccion.getLocality() :
                                direccion.getSubAdminArea() != null ? direccion.getSubAdminArea() : "Desconocido";
            }
        } catch (IOException e) {
            Log.e("GEOCODER", "Error al obtener dirección: " + e.getMessage());
        }
        return "Desconocido";
    }



    private void cargarTodosLosHoteles() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("hoteles")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Hotel> todosLosHoteles = new ArrayList<>();
                        int totalHoteles = task.getResult().size();
                        int[] hotelesProcessed = {0};

                        if (totalHoteles == 0) {
                            actualizarListaHoteles(todosLosHoteles);
                            return;
                        }

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Hotel hotel = document.toObject(Hotel.class);
                                hotel.setId(document.getId());

                                // Obtener el precio mínimo de las habitaciones
                                obtenerPrecioMinimoHotel(hotel, precioMinimo -> {
                                    hotel.setPrecio(String.valueOf(precioMinimo));
                                    todosLosHoteles.add(hotel);

                                    hotelesProcessed[0]++;
                                    if (hotelesProcessed[0] == totalHoteles) {
                                        actualizarListaHoteles(todosLosHoteles);
                                        Log.d("CARGAR_HOTELES", "Total hoteles cargados: " + todosLosHoteles.size());
                                    }
                                });

                            } catch (Exception e) {
                                Log.e("CARGAR_HOTELES", "Error procesando hotel: " + e.getMessage());
                                hotelesProcessed[0]++;
                                if (hotelesProcessed[0] == totalHoteles) {
                                    actualizarListaHoteles(todosLosHoteles);
                                }
                            }
                        }

                    } else {
                        Log.e("CARGAR_HOTELES", "Error obteniendo hoteles: ", task.getException());
                        mostrarMensajeError("Error al cargar hoteles");
                    }
                });
    }


    // Nuevo método para obtener el precio mínimo de las habitaciones de un hotel
    private void obtenerPrecioMinimoHotel(Hotel hotel, OnPrecioMinimoCallback callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("hoteles")
                .document(hotel.getId())
                .collection("habitaciones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        double precioMinimo = Double.MAX_VALUE;
                        boolean hayHabitaciones = false;

                        for (QueryDocumentSnapshot habitacionDoc : task.getResult()) {
                            try {
                                // Asumiendo que el campo se llama "precioPorNoche"
                                Double precio = habitacionDoc.getDouble("precioPorNoche");

                                if (precio != null && precio > 0) {
                                    hayHabitaciones = true;
                                    if (precio < precioMinimo) {
                                        precioMinimo = precio;
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("PRECIO_MINIMO", "Error obteniendo precio de habitación: " + e.getMessage());
                            }
                        }

                        // Si no hay habitaciones o no se encontraron precios válidos, usar 0 o un valor por defecto
                        if (!hayHabitaciones || precioMinimo == Double.MAX_VALUE) {
                            precioMinimo = 0.0;
                        }

                        Log.d("PRECIO_MINIMO", "Hotel: " + hotel.getId() +
                                ", Precio mínimo: " + precioMinimo);

                        callback.onPrecioObtenido(precioMinimo);

                    } else {
                        Log.e("PRECIO_MINIMO", "Error obteniendo habitaciones del hotel " +
                                hotel.getId() + ": ", task.getException());
                        // En caso de error, usar 0 como precio por defecto
                        callback.onPrecioObtenido(0.0);
                    }
                });
    }

    // Interface para el callback del precio mínimo
    public interface OnPrecioMinimoCallback {
        void onPrecioObtenido(double precioMinimo);
    }

    private void actualizarListaHoteles(List<Hotel> hotelesFiltrados) {
        hotelList.clear();
        hotelList.addAll(hotelesFiltrados);


        // Notificar al adapter que los datos han cambiado
        hotelAdapter.notifyDataSetChanged();

        // Mostrar mensaje si no hay resultados
        if (hotelesFiltrados.isEmpty()) {
            mostrarMensajeSinResultados();
        }
    }

    private void mostrarMensajeSinResultados() {
        // Puedes mostrar un Toast o actualizar un TextView en tu layout
        if (getContext() != null) {
            Toast.makeText(getContext(), "No se encontraron hoteles en esta área", Toast.LENGTH_LONG).show();
        }
        Log.d("RESULTADOS", "No se encontraron hoteles en el área especificada");
    }

    private void mostrarMensajeError(String mensaje) {
        if (getContext() != null) {
            Toast.makeText(getContext(), mensaje, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onHotelClick(Hotel hotel, int position) {
        adultos = getArguments().getInt("adultos", 0);
        ninos = getArguments().getInt("ninos", 0);
        habitaciones = getArguments().getInt("habitaciones", 0);

        hotel.setFechaInicio(fechaInicioGlobal);
        hotel.setFechaFin(fechaFinGlobal);
        hotel.setCapacidadNinos(ninos);
        hotel.setCapacidadAdultos(adultos);
        DetalleHotelFragment detalleFragment = DetalleHotelFragment.newInstance(hotel);

        Log.d("DEBUG BUSQUEDAHOTEL", "Adultos: " + adultos + ", Niños: " + ninos + ", Habitaciones: " + habitaciones);






        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_busqueda, detalleFragment)
                .addToBackStack(null)
                .commit();
    }
}