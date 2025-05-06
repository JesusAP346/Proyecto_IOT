package com.example.proyecto_iot.SuperAdmin.domain;

public class AdministradoresDomain  {

    String nombreAdmin;
    String numeroAdmin;
    String imagenAdmin;

    public AdministradoresDomain(String nombreAdmin, String imagenAdmin, String numeroAdmin) {
        this.nombreAdmin = nombreAdmin;
        this.imagenAdmin = imagenAdmin;
        this.numeroAdmin = numeroAdmin;
    }

    public String getNombreAdmin() {
        return nombreAdmin;
    }

    public void setNombreAdmin(String nombreAdmin) {
        this.nombreAdmin = nombreAdmin;
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
