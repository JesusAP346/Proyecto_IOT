package com.example.proyecto_iot.SuperAdmin.database;

import androidx.room.*;

import java.util.List;

@Dao
public interface UsuariosDao {
    @Query("SELECT * FROM usuarios")
    List<UsuariosEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(UsuariosEntity usuario);

    @Delete
    void delete(UsuariosEntity usuario);

    @Query("DELETE FROM usuarios WHERE dni = :dni")
    void deleteByDni(String dni);

    @Query("UPDATE usuarios SET estadoCuenta = :estado WHERE dni = :dni")
    void updateEstado(String dni, String estado);
}
