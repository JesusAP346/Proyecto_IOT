package com.example.proyecto_iot.SuperAdmin.domain;

public class AdministradoresDomain  {

    String nombreAdmin;
    String numeroAdmin;
    String imagenAdmin;
    String correo;
    String direccion;
    String fechaNacimiento;
    String rol;

    public AdministradoresDomain(String nombreAdmin, String numeroAdmin, String imagenAdmin, String correo, String direccion, String fechaNacimiento, String rol) {
        this.nombreAdmin = nombreAdmin;
        this.numeroAdmin = numeroAdmin;
        this.imagenAdmin = imagenAdmin;
        this.correo = correo;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
        this.rol = rol;
    }

    public String getNombreAdmin() {
        return nombreAdmin;
    }

    public void setNombreAdmin(String nombreAdmin) {
        this.nombreAdmin = nombreAdmin;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getImagenAdmin() {
        return imagenAdmin;
    }

    public void setImagenAdmin(String imagenAdmin) {
        this.imagenAdmin = imagenAdmin;
    }

    public String getNumeroAdmin() {
        return numeroAdmin;
    }

    public void setNumeroAdmin(String numeroAdmin) {
        this.numeroAdmin = numeroAdmin;
    }
}
