package com.example.proyecto_iot.cliente.busqueda;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DetalleHotelFragment extends Fragment implements HabitacionAdapter.OnHabitacionClickListener{

    private static final String ARG_HOTEL_ID = "hotel_id";

    private RecyclerView recyclerHabitaciones;
    private FirebaseFirestore db;

    private ViewPager2 viewPagerImagenesHotel;

    // 🆕 Nuevas variables para los indicadores
    private LinearLayout layoutIndicadores;
    private TextView textContadorImagenes;

    private Hotel hotel;
    private TextView textTituloHotel;

    public DetalleHotelFragment() {
        // Constructor vacío requerido
    }

    public static DetalleHotelFragment newInstance(Hotel hotel) {
        DetalleHotelFragment fragment = new DetalleHotelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HOTEL_ID, hotel.getId());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String hotelId = getArguments().getString(ARG_HOTEL_ID);
            Log.d("DETALLE_HOTEL", "Hotel ID recibido: " + hotelId);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_hotel, container, false);
        db = FirebaseFirestore.getInstance();

        // 🆕 Inicializar las nuevas vistas
        initViews(view);

        recyclerHabitaciones = view.findViewById(R.id.recyclerHabitaciones);
        recyclerHabitaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Habitacion> lista = new ArrayList<>();
        HabitacionAdapter adapter = new HabitacionAdapter(getContext(), lista, (this));
        recyclerHabitaciones.setAdapter(adapter);

        // Tu código existente para los tabs
        setupTabs(view);

        textTituloHotel = view.findViewById(R.id.textTituloHotel);

        if (getArguments() != null) {
            String hotelId = getArguments().getString(ARG_HOTEL_ID);
            Log.d("DETALLE_HOTEL", "Hotel ID recibido: " + hotelId);
            cargarHabitaciones(hotelId);
            cargarDatosHotel(hotelId);
        }

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        FloatingActionButton fabChat = view.findViewById(R.id.fabChat);
        fabChat.setOnClickListener(v -> {
            ChatBottomSheet chatBottomSheet = new ChatBottomSheet();
            chatBottomSheet.show(getParentFragmentManager(), "ChatBottomSheet");
        });

        return view;
    }

    // 🆕 Método para inicializar las vistas de indicadores
    private void initViews(View view) {
        viewPagerImagenesHotel = view.findViewById(R.id.viewPagerImagenesHotel);
        layoutIndicadores = view.findViewById(R.id.layoutIndicadores);
        textContadorImagenes = view.findViewById(R.id.textContadorImagenes);
    }

    // 🆕 Método para configurar los tabs (extraído para organizar el código)
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

    // 🆕 Método para cargar datos del hotel (extraído y mejorado)
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

                        // 🆕 Configurar ViewPager con indicadores
                        if (hotel.getFotosHotelUrls() != null && !hotel.getFotosHotelUrls().isEmpty()) {
                            setupViewPagerWithIndicators(hotel.getFotosHotelUrls());
                        }
                    } else {
                        Log.w("DETALLE_HOTEL", "No se encontró el hotel con ID: " + hotelId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("DETALLE_HOTEL", "Error al obtener hotel", e);
                });
    }

    // 🆕 Método para configurar ViewPager con indicadores
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


    public void onHabitacionClick(Habitacion habitacion, int position){
        DetalleHabitacionClienteFragment detalleHabitacionClienteFragment = DetalleHabitacionClienteFragment.newInstance(habitacion);

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

                    if (!lista.isEmpty()) {
                        Habitacion masBarata = lista.get(0);
                        double precioMinimo = masBarata.getPrecio();

                        for (Habitacion h : lista) {
                            double precioActual = h.getPrecio();
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

                    HabitacionAdapter adapter = new HabitacionAdapter(getContext(), lista, this);
                    recyclerHabitaciones.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    Log.e("DETALLE_HOTEL", "Error al obtener habitaciones", e);
                });
    }
}