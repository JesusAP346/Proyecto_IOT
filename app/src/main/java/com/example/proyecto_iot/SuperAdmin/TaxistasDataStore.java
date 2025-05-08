package com.example.proyecto_iot.SuperAdmin;

import com.example.proyecto_iot.SuperAdmin.domain.TaxistaDomain;

import java.util.ArrayList;
import java.util.List;

public class TaxistasDataStore {

    public static List<TaxistaDomain> taxistasList = new ArrayList<>();

    static {
        taxistasList.add(new TaxistaDomain("Elena Ramos", "910234567", "https://randomuser.me/api/portraits/women/50.jpg",
                "elena.ramos@email.com", "Activo", "102 de 120", 4.85,
                "Gris", "Toyota Corolla", "C2A-519", "https://cdn.pixabay.com/photo/2012/05/29/00/43/car-49278_1280.jpg"));

        taxistasList.add(new TaxistaDomain("Luis Gamarra", "924678345", "https://randomuser.me/api/portraits/men/52.jpg",
                "luisg@email.com", "Suspendido", "45 de 70", 4.02,
                "Negro", "Honda Civic", "D7F-821", "https://autoland.com.pe/tipos-carros/"));

        taxistasList.add(new TaxistaDomain("Marisol Torres", "956128934", "https://randomuser.me/api/portraits/women/60.jpg",
                "marisol.torres@email.com", "Activo", "110 de 130", 4.92,
                "Rojo", "Mazda 3", "B3M-948", "https://cdn.pixabay.com/photo/2017/03/27/14/56/mazda-3-2179835_1280.jpg"));

        taxistasList.add(new TaxistaDomain("Jorge Valverde", "987456321", "https://randomuser.me/api/portraits/men/65.jpg",
                "jorgev@email.com", "Activo", "85 de 95", 4.67,
                "Blanco", "Volkswagen Jetta", "H6R-284", "https://cdn.pixabay.com/photo/2017/05/11/19/38/vw-2302015_1280.jpg"));

        taxistasList.add(new TaxistaDomain("Lucía Mendez", "934785120", "https://randomuser.me/api/portraits/women/25.jpg",
                "lucia.mendez@email.com", "Activo", "99 de 110", 4.78,
                "Azul", "Hyundai Elantra", "E9T-019", "https://cdn.pixabay.com/photo/2016/12/10/20/20/car-1890491_1280.jpg"));

        taxistasList.add(new TaxistaDomain("Carlos Peña", "912348976", "https://randomuser.me/api/portraits/men/72.jpg",
                "carlos.pena@email.com", "Suspendido", "60 de 90", 4.33,
                "Verde", "Chevrolet Spark", "F1G-403", "https://cdn.pixabay.com/photo/2016/10/26/22/59/chevrolet-1778261_1280.jpg"));

    }

    public static void actualizarTaxista(int index, TaxistaDomain nuevo) {
        taxistasList.set(index, nuevo);
    }
}
