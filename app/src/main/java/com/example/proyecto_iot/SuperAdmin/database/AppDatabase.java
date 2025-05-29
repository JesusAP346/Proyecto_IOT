package com.example.proyecto_iot.SuperAdmin.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {UsuariosEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UsuariosDao usuariosDao();
}
