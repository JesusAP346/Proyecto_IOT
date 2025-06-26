package com.example.proyecto_iot.cliente;

public class TaxiItem {
    private String placa;
    private String nombreConductor;
    private String destino;
    private int fotoResId;
    private boolean activa;  // si el botón debe estar activo
    private String idServicio;  // nuevo campo
    public TaxiItem(String placa, String nombreConductor, String destino, int fotoResId, boolean activa, String idServicio) {
        this.placa = placa;
        this.nombreConductor = nombreConductor;
        this.destino = destino;
        this.fotoResId = fotoResId;
        this.activa = activa;
        this.idServicio = idServicio;
    }

    public String getPlaca() { return placa; }
    public String getNombreConductor() { return nombreConductor; }
    public String getDestino() { return destino; }
    public int getFotoResId() { return fotoResId; }
    public boolean isActiva() { return activa; }
    public String getIdServicio() {
        return idServicio;
    }
}
