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

        recyclerHabitaciones = view.findViewById(R.id.recyclerHabitaciones);

        recyclerHabitaciones.setLayoutManager(new LinearLayoutManager(getContext()));


        List<Habitacion> lista = new ArrayList<>();


        HabitacionAdapter adapter = new HabitacionAdapter(getContext(), lista, (this));
        recyclerHabitaciones.setAdapter(adapter);

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

        textTituloHotel = view.findViewById(R.id.textTituloHotel);

        if (getArguments() != null) {
            String hotelId = getArguments().getString(ARG_HOTEL_ID);
            Log.d("DETALLE_HOTEL", "Hotel ID recibido: " + hotelId);
            cargarHabitaciones(hotelId);

            // 🔍 Obtener el documento del hotel
            assert hotelId != null;
            db.collection("hoteles").document(hotelId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            hotel = documentSnapshot.toObject(Hotel.class);
                            assert hotel != null;
                            Log.d("DETALLE_HOTEL", "Hotel encontrado: " + hotel.getNombre());

                            if (textTituloHotel != null) {
                                textTituloHotel.setText(hotel.getNombre());
                            }
                        } else {
                            Log.w("DETALLE_HOTEL", "No se encontró el hotel con ID: " + hotelId);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("DETALLE_HOTEL", "Error al obtener hotel", e);
                    });
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
                        habitacion.setDescripcion(descripcion.toString().trim()); // sin salto final
                        lista.add(habitacion);
                    }

                    if (!lista.isEmpty()) {
                        Habitacion masBarata = lista.get(0);
                        double precioMinimo = masBarata.getPrecio(); // Asumimos getter devuelve double

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