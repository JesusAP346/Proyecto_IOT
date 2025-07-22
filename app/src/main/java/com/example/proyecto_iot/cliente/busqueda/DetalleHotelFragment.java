package com.example.proyecto_iot.cliente.busqueda;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.chat.ChatBottomSheet;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetalleHotelFragment extends Fragment implements HabitacionAdapter.OnHabitacionClickListener{

    private static final String ARG_HOTEL_ID = "hotel_id";
    private String fechaInicioGlobal;
    private String fechaFinGlobal;

    private RecyclerView recyclerHabitaciones;
    private FirebaseFirestore db;

    private ViewPager2 viewPagerImagenesHotel;

    // Nuevas variables para los indicadores
    private LinearLayout layoutIndicadores;
    private TextView textContadorImagenes;

    // Variables para las estrellas y valoración
    private ImageView[] estrellas = new ImageView[5];
    private TextView textValoracionHotel;

    private Hotel hotel;
    private TextView textTituloHotel;

    private RecyclerView recyclerServicios;
    private List<Servicio> listaServicios;
    private ServicioAdapter servicioAdapter;

    private Button btnVerOpiniones;

    private int adultos = 0;
    private int ninos = 0;
    private double ubicacionLng;
    private double ubicacionLat;

    private int habitaciones = 0;

    public DetalleHotelFragment() {
        // Constructor vacío requerido
    }

    public static DetalleHotelFragment newInstance(Hotel hotel) {
        DetalleHotelFragment fragment = new DetalleHotelFragment();
        Bundle args = new Bundle();
        Log.d("DETALLE_HOTEL", "Fechas Recibidas 0W0 22222: " + hotel.getFechaInicio() + hotel.getFechaFin() );
        Log.d("CANTADULTOSYKIDS", "Cantidad de niños: " + hotel.getCapacidadNinos() + " Cantidad adult: " +hotel.getCapacidadAdultos() );
        args.putString(ARG_HOTEL_ID, hotel.getId());
        args.putString("fechaInicio", hotel.getFechaInicio());
        args.putString("fechaFin", hotel.getFechaFin());
        args.putInt("capacidadAdultos", hotel.getCapacidadAdultos());
        args.putInt("capacidadNinos", hotel.getCapacidadNinos());

        args.putDouble("ubicacionLng", hotel.getUbicacionLng());
        args.putDouble("ubicacionLat", hotel.getUbicacionLat());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String hotelId = getArguments().getString(ARG_HOTEL_ID);
            Log.d("DETALLE_HOTEL", "Hotel ID XD recibido: " + hotelId);

            if (getArguments() != null) {
                adultos = getArguments().getInt("capacidadAdultos", 0);
                ninos = getArguments().getInt("capacidadNinos", 0);

                ubicacionLat = getArguments().getDouble("ubicacionLat", 0.0);
                ubicacionLng = getArguments().getDouble("ubicacionLng", 0.0);
            }

            Log.d("DEBUG ZZZZZ2", "Adultos: " + adultos + ", Niños: " + ninos + ", Habitaciones: " + habitaciones);
            Log.d("DEBUG LATLONG", "Lat: " + ubicacionLat + ", LONG: " + ubicacionLng);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_hotel, container, false);
        db = FirebaseFirestore.getInstance();

        // Inicializar las nuevas vistas
        initViews(view);

        recyclerHabitaciones = view.findViewById(R.id.recyclerHabitaciones);
        recyclerHabitaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        if (getArguments() != null) {
            String hotelId = getArguments().getString(ARG_HOTEL_ID);
            fechaInicioGlobal = getArguments().getString("fechaInicio");
            fechaFinGlobal = getArguments().getString("fechaFin");
            Log.d("DETALLE_HOTEL", "Hotel ID recibido: " + hotelId);
            Log.d("DETALLE_HOTEL", "Fechas recibidas en el fragmento: " + fechaInicioGlobal + " - " + fechaFinGlobal);
            cargarHabitaciones(hotelId);
            cargarDatosHotel(hotelId);

            // Cargar valoraciones del hotel
            cargarValoracionesHotel(hotelId);
        }

        List<Habitacion> lista = new ArrayList<>();
        HabitacionAdapter adapter = new HabitacionAdapter(getContext(), lista, (this), ARG_HOTEL_ID, fechaInicioGlobal, fechaFinGlobal);
        recyclerHabitaciones.setAdapter(adapter);

        // Tu código existente para los tabs
        setupTabs(view);

        textTituloHotel = view.findViewById(R.id.textTituloHotel);

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        FloatingActionButton fabChat = view.findViewById(R.id.fabChat);
        fabChat.setOnClickListener(v -> {
            if (getArguments() != null && hotel != null) {
                String hotelId = getArguments().getString(ARG_HOTEL_ID);

                // Crear el Bundle con el ID del administrador
                Bundle args = new Bundle();
                args.putString("idAdministrador", hotel.getIdAdministrador());

                Log.d("HOLAAAAAAA", "ID Administrador: " + hotel.getIdAdministrador());

                // Crear el ChatBottomSheet y pasarle los argumentos
                ChatBottomSheet chatBottomSheet = new ChatBottomSheet();
                chatBottomSheet.setArguments(args);  // Esta línea faltaba
                chatBottomSheet.show(getParentFragmentManager(), "ChatBottomSheet");
            }
        });

        return view;
    }

    // Método para inicializar las vistas de indicadores y estrellas
    private void initViews(View view) {
        viewPagerImagenesHotel = view.findViewById(R.id.viewPagerImagenesHotel);
        layoutIndicadores = view.findViewById(R.id.layoutIndicadores);
        textContadorImagenes = view.findViewById(R.id.textContadorImagenes);

        // Inicializar las estrellas (necesitas agregar IDs en el XML)
        estrellas[0] = view.findViewById(R.id.star1Hotel);
        estrellas[1] = view.findViewById(R.id.star2Hotel);
        estrellas[2] = view.findViewById(R.id.star3Hotel);
        estrellas[3] = view.findViewById(R.id.star4Hotel);
        estrellas[4] = view.findViewById(R.id.star5Hotel);

        textValoracionHotel = view.findViewById(R.id.textValoracionHotel);

        recyclerServicios = view.findViewById(R.id.recyclerServicios);
        recyclerServicios.setLayoutManager(new LinearLayoutManager(getContext()));
        listaServicios = new ArrayList<>();
        servicioAdapter = new ServicioAdapter(getContext(), listaServicios, new ServicioAdapter.OnServicioClickListener() {
            @Override
            public void onVerFotosClick(Servicio servicio) {
                mostrarFotosServicio(servicio);
            }
        });
        recyclerServicios.setAdapter(servicioAdapter);
        btnVerOpiniones = view.findViewById(R.id.btnVerOpiniones);
        btnVerOpiniones.setOnClickListener(v -> {
            if (getArguments() != null) {
                String hotelId = getArguments().getString(ARG_HOTEL_ID);
                abrirValoraciones(hotelId);
            }
        });
    }

    private void mostrarFotosServicio(Servicio servicio) {
        FotosServicioDialog dialog = FotosServicioDialog.newInstance(servicio);
        dialog.show(getParentFragmentManager(), "FotosServicioDialog");
    }
    private void cargarValoracionesHotel(String hotelId) {
        db.collection("hoteles")
                .document(hotelId)
                .collection("valoraciones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // No hay valoraciones
                            mostrarSinValoraciones();
                        } else {
                            // Calcular promedio
                            double sumaEstrellas = 0;
                            int cantidadValoraciones = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Double estrellas = document.getDouble("estrellas");
                                if (estrellas != null) {
                                    sumaEstrellas += estrellas;
                                    cantidadValoraciones++;
                                }
                            }

                            if (cantidadValoraciones > 0) {
                                double promedio = sumaEstrellas / cantidadValoraciones;
                                int estrellasRedondeadas = (int) Math.ceil(promedio);
                                mostrarEstrellas(estrellasRedondeadas, promedio);
                            } else {
                                mostrarSinValoraciones();
                            }
                        }
                    } else {
                        // Error al cargar, mostrar estado por defecto
                        mostrarSinValoraciones();
                    }
                });
    }

    private void mostrarEstrellas(int cantidadEstrellas, double promedioExacto) {
        // Actualizar las estrellas visuales
        for (int i = 0; i < 5; i++) {
            estrellas[i].setImageResource(
                    i < cantidadEstrellas ? R.drawable.ic_star : R.drawable.ic_star_border
            );
        }

        // Mostrar el texto con el promedio
        if (textValoracionHotel != null) {
            textValoracionHotel.setText(String.format("%.1f estrellas", promedioExacto));
            textValoracionHotel.setVisibility(View.VISIBLE);
        }
    }

    private void mostrarSinValoraciones() {
        // Mostrar todas las estrellas vacías
        for (int i = 0; i < 5; i++) {
            estrellas[i].setImageResource(R.drawable.ic_star_border);
        }

        // Mostrar texto "Sin valoraciones"
        if (textValoracionHotel != null) {
            textValoracionHotel.setText("Sin valoraciones");
            textValoracionHotel.setVisibility(View.VISIBLE);
        }
    }

    // Método para configurar los tabs (extraído para organizar el código)
    private void setupTabs(View view) {
        TextView tabPrecios = view.findViewById(R.id.tabPrecios);
        TextView tabInformacion = view.findViewById(R.id.tabInformacion);
        RecyclerView recyclerHabitaciones = view.findViewById(R.id.recyclerHabitaciones);
        ScrollView layoutInformacion = view.findViewById(R.id.layoutInformacion);

        tabPrecios.setOnClickListener(v -> {
            tabPrecios.setTextColor(Color.BLACK);
            tabPrecios.setTypeface(null, Typeface.BOLD);
            tabInformacion.setTextColor(Color.GRAY);
            tabInformacion.setTypeface(null, Typeface.NORMAL);

            recyclerHabitaciones.setVisibility(View.VISIBLE);
            layoutInformacion.setVisibility(View.GONE);
        });

        tabInformacion.setOnClickListener(v -> {
            tabInformacion.setTextColor(Color.BLACK);
            tabInformacion.setTypeface(null, Typeface.BOLD);
            tabPrecios.setTextColor(Color.GRAY);
            tabPrecios.setTypeface(null, Typeface.NORMAL);

            recyclerHabitaciones.setVisibility(View.GONE);
            layoutInformacion.setVisibility(View.VISIBLE);
        });
    }

    // Método para cargar datos del hotel (extraído y mejorado)
    private void cargarDatosHotel(String hotelId) {
        db.collection("hoteles").document(hotelId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        hotel = documentSnapshot.toObject(Hotel.class);
                        assert hotel != null;
                        Log.d("DETALLE_HOTEL", "Hotel encontrado: " + hotel.getNombre());

                        if (textTituloHotel != null) {
                            textTituloHotel.setText(hotel.getNombre());
                        }

                        // Configurar ViewPager con indicadores
                        if (hotel.getFotosHotelUrls() != null && !hotel.getFotosHotelUrls().isEmpty()) {
                            setupViewPagerWithIndicators(hotel.getFotosHotelUrls());
                        }

                        // NUEVO: Cargar servicios del hotel
                        cargarServicios(hotelId);
                    } else {
                        Log.w("DETALLE_HOTEL", "No se encontró el hotel con ID: " + hotelId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DETALLE_HOTEL", "Error al obtener hotel", e);
                });
    }

    private void cargarServicios(String hotelId) {
        db.collection("hoteles")
                .document(hotelId)
                .collection("servicios")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaServicios.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Servicio servicio = doc.toObject(Servicio.class);
                        if (servicio != null) {
                            servicio.setId(doc.getId());
                            listaServicios.add(servicio);
                        }
                    }

                    servicioAdapter.notifyDataSetChanged();
                    Log.d("DETALLE_HOTEL", "Servicios cargados: " + listaServicios.size());
                })
                .addOnFailureListener(e -> {
                    Log.e("DETALLE_HOTEL", "Error al cargar servicios", e);
                });
    }


    // Método para configurar ViewPager con indicadores
    private void setupViewPagerWithIndicators(List<String> imagenesUrls) {
        // Configurar el adapter
        ImagenHotelAdapter adapterImagenes = new ImagenHotelAdapter(getContext(), imagenesUrls);
        viewPagerImagenesHotel.setAdapter(adapterImagenes);

        // Si hay más de una imagen, mostrar indicadores
        if (imagenesUrls.size() > 1) {
            setupIndicadores(imagenesUrls.size());
            setupPageChangeListener();
        }
    }

    //  Método para configurar los indicadores (dots)
    private void setupIndicadores(int cantidadImagenes) {
        layoutIndicadores.removeAllViews();

        // Crear dots para cada imagen
        for (int i = 0; i < cantidadImagenes; i++) {
            ImageView dot = new ImageView(getContext());
            dot.setImageResource(R.drawable.dot_indicador);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);

            dot.setLayoutParams(params);
            dot.setSelected(i == 0); // Primer dot activo

            layoutIndicadores.addView(dot);
        }

        // Mostrar indicadores y contador
        layoutIndicadores.setVisibility(View.VISIBLE);
        textContadorImagenes.setVisibility(View.VISIBLE);

        // Actualizar contador inicial
        updateContador(0, cantidadImagenes);
    }

    // Método para configurar el listener de cambio de página
    private void setupPageChangeListener() {
        viewPagerImagenesHotel.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicadores(position);
                updateContador(position, layoutIndicadores.getChildCount());
            }
        });
    }

    private void updateIndicadores(int position) {
        for (int i = 0; i < layoutIndicadores.getChildCount(); i++) {
            ImageView dot = (ImageView) layoutIndicadores.getChildAt(i);
            dot.setSelected(i == position);
        }
    }

    private void updateContador(int position, int total) {
        String contador = (position + 1) + "/" + total;
        textContadorImagenes.setText(contador);
    }

    public void onHabitacionClick(Habitacion2 habitacion){
        habitacion.setFechaFin(fechaFinGlobal);
        habitacion.setFechaInicio(fechaInicioGlobal);
        assert getArguments() != null;
        String hotelId = getArguments().getString(ARG_HOTEL_ID);
        DetalleHabitacionClienteFragment detalleHabitacionClienteFragment = DetalleHabitacionClienteFragment.newInstance(habitacion, hotelId);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_busqueda, detalleHabitacionClienteFragment)
                .addToBackStack(null)
                .commit();
    }

    private void cargarHabitaciones(String hotelId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<Habitacion> lista = new ArrayList<>();

        db.collection("hoteles").document(hotelId).collection("habitaciones").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Habitacion habitacion = doc.toObject(Habitacion.class);
                        habitacion.setId(doc.getId());
                        StringBuilder descripcion = new StringBuilder();

                        // • 1 habitación / • 2 habitaciones
                        int cantHab = habitacion.getCantidadHabitaciones();
                        descripcion.append("• ").append(cantHab).append(" habitación");
                        if (cantHab != 1) descripcion.append("es");
                        descripcion.append("\n");

                        int adultos = habitacion.getCapacidadAdultos();
                        descripcion.append("• ").append(adultos).append(" cama");
                        if (adultos != 1) descripcion.append("s");
                        descripcion.append(" matrimoniale");
                        if (adultos != 1) descripcion.append("s");
                        descripcion.append("\n");

                        int ninos = habitacion.getCapacidadNinos();
                        if (ninos > 0) {
                            descripcion.append("• ").append(ninos).append(" niño");
                            if (ninos != 1) descripcion.append("s");
                            descripcion.append("\n");
                        }

                        List<String> equipamiento = habitacion.getEquipamiento();
                        if (equipamiento != null) {
                            for (String item : equipamiento) {
                                descripcion.append("• ").append(item).append("\n");
                            }
                        }

                        // Asignar descripción
                        habitacion.setDescripcion(descripcion.toString().trim());
                        lista.add(habitacion);
                    }

                    // Ahora filtrar habitaciones que no estén reservadas en las fechas globales
                    filtrarHabitacionesDisponibles(lista, hotelId);
                })
                .addOnFailureListener(e -> {
                    Log.e("DETALLE_HOTEL", "Error al obtener habitaciones", e);
                });
    }

    private void filtrarHabitacionesDisponibles(List<Habitacion> listaOriginal, String hotelId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Consultar todas las reservas
        db.collection("reservas").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> habitacionesReservadas = new ArrayList<>();

                    for (DocumentSnapshot reservaDoc : queryDocumentSnapshots) {
                        String idHabitacion = reservaDoc.getString("idHabitacion");
                        String fechaEntrada = reservaDoc.getString("fechaEntrada");
                        String fechaSalida = reservaDoc.getString("fechaSalida");

                        // Verificar si hay intersección de fechas
                        if (idHabitacion != null && fechaEntrada != null && fechaSalida != null) {
                            if (hayInterseccionFechas(fechaInicioGlobal, fechaFinGlobal, fechaEntrada, fechaSalida)) {
                                habitacionesReservadas.add(idHabitacion);
                            }
                        }
                    }

                    // Filtrar la lista original removiendo habitaciones reservadas
                    List<Habitacion> listaFiltrada = new ArrayList<>();
                    for (Habitacion habitacion : listaOriginal) {
                        if (!habitacionesReservadas.contains(habitacion.getId())) {
                            listaFiltrada.add(habitacion);
                        }
                    }

                    // Continuar con el procesamiento de la habitación más barata
                    procesarListaFinal(listaFiltrada, hotelId);
                })
                .addOnFailureListener(e -> {
                    Log.e("DETALLE_HOTEL", "Error al obtener reservas", e);
                    // En caso de error, usar la lista original
                    procesarListaFinal(listaOriginal, hotelId);
                });
    }

    private boolean hayInterseccionFechas(String inicioGlobal, String finGlobal, String inicioReserva, String finReserva) {
        try {
            // Asumiendo que las fechas están en formato "yyyy-MM-dd" o similar
            // Puedes ajustar el formato según tu implementación
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");

            java.util.Date inicioGlobalDate = sdf.parse(inicioGlobal);
            java.util.Date finGlobalDate = sdf.parse(finGlobal);
            java.util.Date inicioReservaDate = sdf.parse(inicioReserva);
            java.util.Date finReservaDate = sdf.parse(finReserva);

            // Hay intersección si:
            // El inicio de la reserva es antes del fin global Y
            // El fin de la reserva es después del inicio global
            return inicioReservaDate.before(finGlobalDate) && finReservaDate.after(inicioGlobalDate);

        } catch (java.text.ParseException e) {
            Log.e("DETALLE_HOTEL", "Error al parsear fechas", e);
            // En caso de error al parsear, asumir que hay conflicto para ser conservadores
            return true;
        }
    }

    private void procesarListaFinal(List<Habitacion> lista, String hotelId) {
        if (!lista.isEmpty()) {
            Habitacion masBarata = lista.get(0);
            double precioMinimo = masBarata.getPrecioPorNoche();

            for (Habitacion h : lista) {
                double precioActual = h.getPrecioPorNoche();
                if (precioActual < precioMinimo) {
                    masBarata = h;
                    precioMinimo = precioActual;
                }
            }

            // Limpiar etiquetas anteriores
            for (Habitacion h : lista) {
                h.setEtiqueta(null);
            }

            // Asignar etiqueta solo a la más barata
            masBarata.setEtiqueta("El precio más bajo");

            // Mover al inicio de la lista
            lista.remove(masBarata);
            lista.add(0, masBarata);
        }

        HabitacionAdapter adapter = new HabitacionAdapter(getContext(), lista, this, hotelId, fechaInicioGlobal, fechaFinGlobal);
        recyclerHabitaciones.setAdapter(adapter);
    }

    private void abrirValoraciones(String hotelId) {
        Intent intent = new Intent(getContext(), ValoracionesActivity.class);
        intent.putExtra("hotelId", hotelId);
        intent.putExtra("nombreHotel", hotel != null ? hotel.getNombre() : "Hotel");
        startActivity(intent);
    }
}