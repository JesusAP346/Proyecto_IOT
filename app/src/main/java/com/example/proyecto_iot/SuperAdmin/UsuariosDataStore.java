package com.example.proyecto_iot.SuperAdmin;

import com.example.proyecto_iot.SuperAdmin.domain.UsuariosDomain;

import java.util.ArrayList;
import java.util.List;

public class UsuariosDataStore {

    public static List<UsuariosDomain> usuariosList = new ArrayList<>();

    static {
        usuariosList.add(new UsuariosDomain("Jostin Pino", "952530495", "https://randomuser.me/api/portraits/men/10.jpg",
                "jostinpino@gmail.com", "Avenida Bolívar 2150", "1995-06-15", "Usuario",
                "7561701", 20, "activo", "98 de 100", "4.96"));

        usuariosList.add(new UsuariosDomain("Valeria Soto", "987123456", "https://randomuser.me/api/portraits/women/11.jpg",
                "valeria.soto@email.com", "Av. Las Flores 120", "1998-01-10", "Usuario",
                "7896543", 15, "activo", "88 de 100", "4.85"));

        usuariosList.add(new UsuariosDomain("Ricardo Quispe", "954321789", "https://randomuser.me/api/portraits/men/25.jpg",
                "ricardoq@email.com", "Calle Puno 321", "1992-03-27", "Usuario",
                "7623456", 10, "suspendido", "76 de 100", "4.60"));

        usuariosList.add(new UsuariosDomain("Marta Salas", "961234578", "https://randomuser.me/api/portraits/women/18.jpg",
                "marta.salas@email.com", "Jr. Moquegua 456", "1990-12-03", "Usuario",
                "7012345", 12, "activo", "84 de 100", "4.78"));

        usuariosList.add(new UsuariosDomain("Daniel Chávez", "965478123", "https://randomuser.me/api/portraits/men/41.jpg",
                "daniel.chavez@email.com", "Av. El Sol 890", "1987-07-14", "Usuario",
                "7698123", 8, "activo", "69 de 100", "4.55"));

        usuariosList.add(new UsuariosDomain("Camila Rojas", "934567892", "https://randomuser.me/api/portraits/women/44.jpg",
                "camila.rojas@email.com", "Calle Misti 101", "1996-09-09", "Usuario",
                "7551234", 5, "activo", "55 de 100", "4.40"));

        usuariosList.add(new UsuariosDomain("José Gutiérrez", "988765432", "https://randomuser.me/api/portraits/men/12.jpg",
                "jose.gutierrez@email.com", "Jr. Tacna 505", "1989-02-01", "Usuario",
                "7421987", 7, "suspendido", "70 de 100", "4.65"));

        usuariosList.add(new UsuariosDomain("Andrea Ramírez", "923456789", "https://randomuser.me/api/portraits/women/19.jpg",
                "andrea.ramirez@email.com", "Av. Los Olivos 321", "1994-04-21", "Usuario",
                "7654321", 14, "activo", "91 de 100", "4.89"));

        usuariosList.add(new UsuariosDomain("Marco Salcedo", "976543219", "https://randomuser.me/api/portraits/men/29.jpg",
                "marco.salcedo@email.com", "Calle Amazonas 909", "1991-11-30", "Usuario",
                "7532198", 11, "activo", "80 de 100", "4.72"));

        usuariosList.add(new UsuariosDomain("Lucía Navarro", "912345987", "https://randomuser.me/api/portraits/women/31.jpg",
                "lucia.navarro@email.com", "Jr. Callao 333", "1997-08-17", "Usuario",
                "7643210", 6, "activo", "87 de 100", "4.81"));
    }



    public static void actualizarUsuario(int index, UsuariosDomain nuevo) {
        usuariosList.set(index, nuevo);
    }
}
