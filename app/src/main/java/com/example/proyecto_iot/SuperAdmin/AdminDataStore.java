package com.example.proyecto_iot.SuperAdmin;

import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;
import java.util.ArrayList;
import java.util.List;

public class AdminDataStore {
    public static List<AdministradoresDomain> adminsList = new ArrayList<>();

    static {
        adminsList.add(new AdministradoresDomain("Andrea Torres", "987654321", "https://randomuser.me/api/portraits/women/68.jpg", "andrea.torres@email.com", "Av. Los Álamos 123", "1990-03-15", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Luis Mendoza", "912345678", "https://randomuser.me/api/portraits/men/45.jpg", "luis.mendoza@email.com", "Jr. Lima 456", "1988-11-20", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("María Pérez", "956789123", "https://randomuser.me/api/portraits/women/12.jpg", "maria.perez@email.com", "Calle San Juan 789", "1992-07-08", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Carlos Ruiz", "998877665", "https://randomuser.me/api/portraits/men/34.jpg", "carlos.ruiz@email.com", "Av. Miraflores 321", "1985-06-23", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Fernanda Silva", "934567890", "https://randomuser.me/api/portraits/women/24.jpg", "fernanda.silva@email.com", "Jr. Arequipa 654", "1991-12-01", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Diego Ramírez", "976543210", "https://randomuser.me/api/portraits/men/53.jpg", "diego.ramirez@email.com", "Calle Los Olivos 987", "1987-05-12", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Lucía Romero", "923456781", "https://randomuser.me/api/portraits/women/30.jpg", "lucia.romero@email.com", "Av. Primavera 111", "1993-09-30", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Jorge Valdez", "987123654", "https://randomuser.me/api/portraits/men/22.jpg", "jorge.valdez@email.com", "Jr. Cusco 222", "1986-04-17", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Camila Torres", "965432178", "https://randomuser.me/api/portraits/women/15.jpg", "camila.torres@email.com", "Calle Amazonas 333", "1994-10-05", "Admin Hotel"));
        adminsList.add(new AdministradoresDomain("Alonso Rivas", "976812345", "https://randomuser.me/api/portraits/men/28.jpg", "alonso.rivas@email.com", "Av. Grau 444", "1989-08-22", "Admin Hotel"));

    }

    public static void actualizarAdmin(int index, AdministradoresDomain nuevo) {
        adminsList.set(index, nuevo);
    }
}
