package com.example.proyecto_iot.cliente.busqueda;

import com.example.proyecto_iot.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HotelData {
    public static List<Hotel> getTodosLosHoteles() {
        List<String> servicios1 = Arrays.asList("Wifi", "Piscina", "Desayuno");
        List<String> servicios2 = Arrays.asList("Estacionamiento", "Gimnasio");

        List<Hotel> hoteles = new ArrayList<>();
        hoteles.add(new Hotel("Hotel Caribe", "San Miguel", 2550, R.drawable.hotel1, 5, servicios1, 1, false));
        hoteles.add(new Hotel("Hotel Las Rosas", "San Juan de Lurigancho", 355, R.drawable.hotel2, 4, servicios2, 2, false));
        hoteles.add(new Hotel("Hotel dfghj", "Comas", 2550, R.drawable.hotel1, 5, servicios1, 3, false));
        hoteles.add(new Hotel("Hotel noseeee", "Carabayllo", 355, R.drawable.hotel2, 4, servicios2, 4, false));
        hoteles.add(new Hotel("Hotel Cbe", "Jesus María", 2550, R.drawable.hotel1, 5, servicios1, 5, false));
        hoteles.add(new Hotel("Hotel Laosas", "San Miguel", 355, R.drawable.hotel2, 4, servicios2, 6, false));
        hoteles.add(new Hotel("Hotel MNB", "San Borja", 2550, R.drawable.hotel1, 5, servicios1, 7, false));
        hoteles.add(new Hotel("Hotel FOX", "Surquillo", 355, R.drawable.hotel2, 4, servicios2, 8, false));

        return hoteles;
    }
}
