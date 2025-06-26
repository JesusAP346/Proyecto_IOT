package com.example.proyecto_iot.cliente.busqueda;

public class SugerenciaItem {
    private String descripcion;
    private String placeId;
    private double lat;
    private double lng;

    public SugerenciaItem(String descripcion, String placeId) {
        this.descripcion = descripcion;
        this.placeId = placeId;
    }

    // Getters y setters
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPlaceId() { return placeId; }
    public void setPlaceId(String placeId) { this.placeId = placeId; }

    public double getLat() { return lat; }
    public void setLat(double lat) { this.lat = lat; }

    public double getLng() { return lng; }
    public void setLng(double lng) { this.lng = lng; }
}