package com.example.proyecto_iot.cliente.busqueda;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.List;

public class DetalleHotelFragment extends Fragment {

    private static final String ARG_NOMBRE = "nombre";
    private static final String ARG_UBICACION = "ubicacion";
    private static final String ARG_PRECIO = "precio";
    private static final String ARG_IMAGEN_RES_ID = "imagenResId";
    private static final String ARG_ESTRELLAS = "estrellas";

    private String nombre;
    private String ubicacion;
    private int precio;
    private int imagenResId;
    private int estrellas;

    private RecyclerView recyclerHabitaciones;

    public DetalleHotelFragment() {
        // Constructor vacío requerido
    }

    public static DetalleHotelFragment newInstance(Hotel hotel) {
        DetalleHotelFragment fragment = new DetalleHotelFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NOMBRE, hotel.getNombre());
        args.putString(ARG_UBICACION, hotel.getUbicacion());
        args.putInt(ARG_PRECIO, hotel.getPrecio());
        args.putInt(ARG_IMAGEN_RES_ID, hotel.getImagenResId());
        args.putInt(ARG_ESTRELLAS, hotel.getEstrellas());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            nombre = getArguments().getString(ARG_NOMBRE);
            ubicacion = getArguments().getString(ARG_UBICACION);
            precio = getArguments().getInt(ARG_PRECIO);
            imagenResId = getArguments().getInt(ARG_IMAGEN_RES_ID);
            estrellas = getArguments().getInt(ARG_ESTRELLAS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detalle_hotel, container, false);

        recyclerHabitaciones = view.findViewById(R.id.recyclerHabitaciones);

        recyclerHabitaciones.setLayoutManager(new LinearLayoutManager(getContext()));

        // Datos de prueba (hardcoded UwUWUWUWUWUWU)
        List<Habitacion> lista = new ArrayList<>();
        lista.add(new Habitacion("S/. 355 por noche", "El precio más bajo que tenemos", "• 1 habitación\n• 2 camas individuales\n• Jacuzzi\n• Aparcamiento cerrado\n• Wi-Fi"));
        lista.add(new Habitacion("S/. 420 por noche", "Excelente para familias", "• 1 habitación\n• 2 camas matrimoniales\n• Balcón con vista\n• Wi-Fi"));
        lista.add(new Habitacion("S/. 470 por noche", "Excelente para familias", "• 1 habitación\n• 3 camas matrimoniales\n• Balcón con vista\n• Wi-Fi"));
        lista.add(new Habitacion("S/. 890 por noche", "Excelente para familias", "• 1 habitación\n• 2 camas matrimoniales\n• Balcón con vista\n• Wi-Fi\n• Jacuzzi UwU"));
        lista.add(new Habitacion("S/. 5000 por noche", "Excelente para familias", "• 1 habitación\n• 2 camas matrimoniales\n• Balcón con vista\n• Wi-Fi\n• Jacuzzi UwU"));

        HabitacionAdapter adapter = new HabitacionAdapter(lista, getContext(), (HotelAdapter.OnHotelClickListener) this);
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

        Hotel hotel = getArguments() != null ? (Hotel) getArguments().getSerializable("hotel") : null;
        if (hotel != null) {
            LinearLayout layoutServicios = view.findViewById(R.id.layoutServicios);
            layoutServicios.removeAllViews();

            for (String servicio : hotel.getServicios()) {
                TextView servicioView = new TextView(getContext());
                servicioView.setText("- " + servicio);
                servicioView.setTextSize(14);
                servicioView.setTextColor(Color.DKGRAY);
                layoutServicios.addView(servicioView);
            }
        }

        ImageButton btnBack = view.findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });




        return view;
    }
}