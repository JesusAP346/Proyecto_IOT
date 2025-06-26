package com.example.proyecto_iot.cliente.busqueda;

import com.example.proyecto_iot.R;

import java.util.ArrayList;
import java.util.List;

public class RepositorioHotel {
    private static RepositorioHotel instancia;
    private List<Hotel> hoteles;

    private RepositorioHotel() {
        hoteles = new ArrayList<>();


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

