package com.example.proyecto_iot.cliente.busqueda;

import com.example.proyecto_iot.R;

import java.util.ArrayList;
import java.util.List;

public class RepositorioHotel {
    private static RepositorioHotel instancia;
    private List<Hotel> hoteles;

    private RepositorioHotel() {
        hoteles = new ArrayList<>();

        List<String> servicios1 = List.of("Wi-Fi", "Piscina", "Gimnasio", "Desayuno incluido", "Recepción 24h", "Aire acondicionado", "Estacionamiento gratuito");
        List<String> servicios2 = List.of("Wi-Fi", "Spa", "Restaurante", "Bar", "Servicio a la habitación", "Transporte al aeropuerto", "Centro de negocios");

        hoteles.add(new Hotel("Hotel Caribe", "San Miguel", 2550, R.drawable.hotel1, 5, servicios1, 1, false));
        hoteles.add(new Hotel("Hotel Las Rosas", "San Juan de Lurigancho", 355, R.drawable.hotel2, 4, servicios2, 2, false));
        hoteles.add(new Hotel("Hotel dfghj", "Comas", 2550, R.drawable.hotel1, 5, servicios1, 1, false));
        hoteles.add(new Hotel("Hotel noseeee", "Carabayllo", 355, R.drawable.hotel2, 4, servicios2, 2, true));
        hoteles.add(new Hotel("Hotel Cbe", "Jesús María", 2550, R.drawable.hotel1, 5, servicios1, 1, false));
        hoteles.add(new Hotel("Hotel Laosas", "San Miguel", 355, R.drawable.hotel2, 4, servicios2, 2, true));
    }

    public static RepositorioHotel getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioHotel();
        }
        return instancia;
    }

    public List<Hotel> obtenerTodos() {
        return hoteles;
    }

    public List<Hotel> obtenerFavoritos() {
        List<Hotel> favoritos = new ArrayList<>();
        for (Hotel hotel : hoteles) {
            if (hotel.isFavorito()) {
                favoritos.add(hotel);
            }
        }
        return favoritos;
    }

    public void actualizarHotel(Hotel hotelActualizado) {
        for (int i = 0; i < hoteles.size(); i++) {
            Hotel h = hoteles.get(i);
            if (h.getId() == hotelActualizado.getId()) {
                hoteles.set(i, hotelActualizado);
                break;
            }
        }
    }
}

