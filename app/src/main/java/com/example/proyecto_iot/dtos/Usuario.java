package com.example.proyecto_iot.dtos;


public class Usuario {
    public String id;
    public String idRol;

    public String nombres;
    public String apellidos;
    public String tipoDocumento;
    public String numDocumento;
    public String fechaNacimiento;
    public String numCelular;
    public String email;
    public String departamento;
    public String provincia;
    public String distrito;
    public String direccion;
    public String password;
    public String urlFotoPerfil;

    public boolean estadoCuenta;
    public String fechaRegistro;

    public String ultimaActualizacion;
    public String actualizadoPor;

    public String idHotel;

    public String placaAuto;
    public String urlFotoAuto;
    public double ubicacionLat;
    public double ubicacionLng;
    public String estadoSolicitudTaxista;

    public boolean tarjetaRegistrada;

    // Getters y Setters
    public String getEstadoSolicitudTaxista() {
        return estadoSolicitudTaxista;
    }
    
    public void setEstadoSolicitudTaxista(String estadoSolicitudTaxista) {
        this.estadoSolicitudTaxista = estadoSolicitudTaxista;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdRol() {
        return idRol;
    }

    public void setIdRol(String idRol) {
        this.idRol = idRol;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNumCelular() {
        return numCelular;
    }

    public void setNumCelular(String numCelular) {
        this.numCelular = numCelular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrlFotoPerfil() {
        return urlFotoPerfil;
    }

    public void setUrlFotoPerfil(String urlFotoPerfil) {
        this.urlFotoPerfil = urlFotoPerfil;
    }

    public boolean isEstadoCuenta() {
        return estadoCuenta;
    }

    public void setEstadoCuenta(boolean estadoCuenta) {
        this.estadoCuenta = estadoCuenta;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    public void setUltimaActualizacion(String ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public String getActualizadoPor() {
        return actualizadoPor;
    }

    public void setActualizadoPor(String actualizadoPor) {
        this.actualizadoPor = actualizadoPor;
    }

    public String getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(String idHotel) {
        this.idHotel = idHotel;
    }

    public String getPlacaAuto() {
        return placaAuto;
    }

    public void setPlacaAuto(String placaAuto) {
        this.placaAuto = placaAuto;
    }

    public String getUrlFotoAuto() {
        return urlFotoAuto;
    }

    public void setUrlFotoAuto(String urlFotoAuto) {
        this.urlFotoAuto = urlFotoAuto;
    }

    public double getUbicacionLat() {
        return ubicacionLat;
    }

    public void setUbicacionLat(double ubicacionLat) {
        this.ubicacionLat = ubicacionLat;
    }

    public double getUbicacionLng() {
        return ubicacionLng;
    }

    public void setUbicacionLng(double ubicacionLng) {
        this.ubicacionLng = ubicacionLng;
    }

    public boolean isTarjetaRegistrada() {
        return tarjetaRegistrada;
    }

    public void setTarjetaRegistrada(boolean tarjetaRegistrada) {
        this.tarjetaRegistrada = tarjetaRegistrada;
    }
}

