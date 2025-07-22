package com.example.proyecto_iot.administradorHotel.entity;

public class DatosTaxi {
    private String nombreTaxista;
    private String modeloAuto;
    private String placaAuto;
    private String fotourl;
    private double latitudTaxista;
    private double longitudTaxista;
    private double latitudHotel;
    private double longitudHotel;
    private String estado;

    public DatosTaxi(String nombreTaxista, String modeloAuto, String placaAuto,
                     double latitudTaxista, double longitudTaxista,
                     double latitudHotel, double longitudHotel, String estado, String fotourl) {
        this.nombreTaxista = nombreTaxista;
        this.modeloAuto = modeloAuto;
        this.placaAuto = placaAuto;
        this.latitudTaxista = latitudTaxista;
        this.longitudTaxista = longitudTaxista;
        this.latitudHotel = latitudHotel;
        this.longitudHotel = longitudHotel;
        this.estado = estado;
        this.fotourl = fotourl;
    }

    // Getters
    public String getNombreTaxista() {
        return nombreTaxista;
    }

    public String getModeloAuto() {
        return modeloAuto;
    }

    public String getPlacaAuto() {
        return placaAuto;
    }

    public double getLatitudTaxista() {
        return latitudTaxista;
    }

    public double getLongitudTaxista() {
        return longitudTaxista;
    }

    public double getLatitudHotel() {
        return latitudHotel;
    }

    public double getLongitudHotel() {
        return longitudHotel;
    }

    public String getEstado() {
        return estado;
    }

    public String getFotourl() {
        return fotourl;
    }


}
