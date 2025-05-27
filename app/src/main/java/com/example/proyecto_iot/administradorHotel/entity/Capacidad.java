package com.example.proyecto_iot.administradorHotel.entity;

import java.util.Arrays;
import java.util.List;
public class Capacidad {

    public static List<String> obtenerCapacidades() {
        return Arrays.asList(
                "Seleccionar",
                "1 adulto",
                "2 adultos",
                "2 adultos y 1 niño",
                "2 adultos y 2 niños",
                "3 adultos",
                "3 adultos, 1 niño",
                "4 personas",
                "5 personas"
        );
    }
}
