package com.example.proyecto_iot.cliente;

public class TaxiItem {
    private String placa;
    private String nombreConductor;
    private String destino;
    private int fotoResId;
    private boolean activa;  // si el botón debe estar activo
    private String idServicio;  // nuevo campo
    private double latitud;
    private double longitud;
    private double latCliente;
    private double lonCliente;

    private String idTaxista; // NUEVO CAMPO
    private String urlFotoTaxista;
    private String telefonoTaxista;

    public String getTelefonoTaxista() {
        return telefonoTaxista;
    }

    public void setTelefonoTaxista(String telefonoTaxista) {
        this.telefonoTaxista = telefonoTaxista;
    }

    public String getUrlFotoTaxista() {
        return urlFotoTaxista;
    }
    public double getLatCliente() { return latCliente; }
    public double getLonCliente() { return lonCliente; }

    public double getLatitud() {
        return latitud; }
    public double getLongitud() {
        return longitud; }

    public String getIdTaxista() {
        return idTaxista;
    }

    /*
        public TaxiItem(String placa, String nombreConductor, String destino, int fotoResId, boolean activa, String idServicio, double latitud, double longitud) {
            this.placa = placa;
            this.nombreConductor = nombreConductor;
            this.destino = destino;
            this.fotoResId = fotoResId;
            this.activa = activa;
            this.idServicio = idServicio;
            this.latitud = latitud;
            this.longitud = longitud;
        }
    */
    public TaxiItem(String placa, String nombreConductor, String destino, int fotoResId,
                    boolean activa, String idServicio, double latitud, double longitud,
                    double latCliente, double lonCliente, String idTaxista, String urlFotoTaxista) {
        this.placa = placa;
        this.nombreConductor = nombreConductor;
        this.destino = destino;
        this.fotoResId = fotoResId;
        this.activa = activa;
        this.idServicio = idServicio;
        this.latitud = latitud;
        this.longitud = longitud;
        this.latCliente = latCliente;
        this.lonCliente = lonCliente;
        this.idTaxista = idTaxista;
        this.urlFotoTaxista = urlFotoTaxista;
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
