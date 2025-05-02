package com.example.proyecto_iot.cliente.busqueda;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;

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
        // Inflar el layout para este fragmento (debes crear este layout)
        View view = inflater.inflate(R.layout.fragment_detalle_hotel, container, false);

        // Configurar las vistas con los datos del hotel
        // (Asumiendo que tienes estas vistas en tu layout)
        /*ImageView imagenHotel = view.findViewById(R.id.imagenDetalleHotel);
        TextView nombreHotel = view.findViewById(R.id.nombreDetalleHotel);
        TextView ubicacionHotel = view.findViewById(R.id.ubicacionDetalleHotel);
        TextView precioHotel = view.findViewById(R.id.precioDetalleHotel);
        // También puedes configurar las estrellas y otros elementos

        imagenHotel.setImageResource(imagenResId);
        nombreHotel.setText(nombre);
        ubicacionHotel.setText(ubicacion);
        precioHotel.setText("A partir de S/. " + precio);*/

        return view;
    }
}