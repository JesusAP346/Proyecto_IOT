package com.example.proyecto_iot.SuperAdmin.database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "usuarios")
public class UsuariosEntity {

    @PrimaryKey
    @NonNull
    public String dni;

    public String nombre;
    public String numeroTelefono;
    public String imagenPerfil;
    public String correo;
    public String direccion;
    public String fechaNacimiento;
    public String rol;
    public int habitacionesRegistradas;
    public String estadoCuenta;
    public String nivelCompletado;
    public String calificacion;
    public UsuariosEntity(String dni, String nombre, String numeroTelefono, String imagenPerfil,
                          String correo, String direccion, String fechaNacimiento, String rol,
                          int habitacionesRegistradas, String estadoCuenta, String nivelCompletado, String calificacion) {
        this.dni = dni;
        this.nombre = nombre;
        this.numeroTelefono = numeroTelefono;
        this.imagenPerfil = imagenPerfil;
        this.correo = correo;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = rol;
        this.habitacionesRegistradas = habitacionesRegistradas;
        this.estadoCuenta = estadoCuenta;
        this.nivelCompletado = nivelCompletado;
        this.calificacion = calificacion;
    }

}
