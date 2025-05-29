package com.example.proyecto_iot.SuperAdmin.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;

import java.util.Arrays;
import java.util.List;

public class UsuariosSeeder {

    public static void insertarUsuariosPorDefecto(Context context) {
        // 1. Verifica SharedPreferences
        SharedPreferences prefs = context.getSharedPreferences("setup_prefs", Context.MODE_PRIVATE);
        boolean yaInsertado = prefs.getBoolean("usuarios_insertados", false);

        if (yaInsertado) return; // Ya se insertaron, no volver a hacer nada

        // 2. Crear instancia de la base de datos
        AppDatabase db = Room.databaseBuilder(context, AppDatabase.class, "usuarios-db")
                .allowMainThreadQueries()
                .build();

        // 3. Lista de usuarios por defecto
        List<UsuariosEntity> lista = Arrays.asList(
                new UsuariosEntity("7561701", "Jostin Pino", "952530495", "https://randomuser.me/api/portraits/men/10.jpg", "jostinpino@gmail.com", "Avenida Bolívar 2150", "1995-06-15", "Usuario", 20, "activo", "98 de 100", "4.96"),
                new UsuariosEntity("7896543", "Valeria Soto", "987123456", "https://randomuser.me/api/portraits/women/11.jpg", "valeria.soto@email.com", "Av. Las Flores 120", "1998-01-10", "Pendiente", 15, "activo", "88 de 100", "4.85"),
                new UsuariosEntity("7623456", "Ricardo Quispe", "954321789", "https://randomuser.me/api/portraits/men/25.jpg", "ricardoq@email.com", "Calle Puno 321", "1992-03-27", "Usuario", 10, "suspendido", "76 de 100", "4.60"),
                new UsuariosEntity("7012345", "Marta Salas", "961234578", "https://randomuser.me/api/portraits/women/18.jpg", "marta.salas@email.com", "Jr. Moquegua 456", "1990-12-03", "Usuario", 12, "activo", "84 de 100", "4.78"),
                new UsuariosEntity("7698123", "Daniel Chávez", "965478123", "https://randomuser.me/api/portraits/men/41.jpg", "daniel.chavez@email.com", "Av. El Sol 890", "1987-07-14", "Usuario", 8, "activo", "69 de 100", "4.55"),
                new UsuariosEntity("7234567", "Lucía Fernández", "981234567", "https://randomuser.me/api/portraits/women/45.jpg", "lucia.fernandez@email.com", "Av. La Cultura 456", "1996-04-22", "Usuario", 14, "activo", "92 de 100", "4.89"),
                new UsuariosEntity("7456789", "Carlos Ramírez", "986547123", "https://randomuser.me/api/portraits/men/55.jpg", "carlos.ramirez@email.com", "Calle Ayacucho 234", "1991-09-30", "Usuario", 18, "suspendido", "70 de 100", "4.42"),
                new UsuariosEntity("7689123", "Andrea López", "988765432", "https://randomuser.me/api/portraits/women/37.jpg", "andrea.lopez@email.com", "Jr. Arequipa 789", "1999-11-11", "Pendiente", 5, "desactivo", "50 de 100", "4.10"),
                new UsuariosEntity("7987654", "Luis Torres", "972345678", "https://randomuser.me/api/portraits/men/63.jpg", "luis.torres@email.com", "Pasaje Lima 123", "1985-08-05", "Usuario", 20, "activo", "95 de 100", "4.98"),
                new UsuariosEntity("7345678", "Camila Rojas", "974321987", "https://randomuser.me/api/portraits/women/29.jpg", "camila.rojas@email.com", "Av. Grau 321", "1993-02-18", "Usuario", 11, "activo", "86 de 100", "4.75")

        );

        // 4. Insertar en la base de datos
        for (UsuariosEntity u : lista) {
            db.usuariosDao().insert(u);
        }

        // 5. Guardar bandera para no reinsertar
        prefs.edit().putBoolean("usuarios_insertados", true).apply();
    }
}
