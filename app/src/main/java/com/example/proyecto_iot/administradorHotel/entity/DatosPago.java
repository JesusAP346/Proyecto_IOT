package com.example.proyecto_iot.administradorHotel.entity;

import java.io.Serializable;

public class DatosPago  implements Serializable {
    private String banco;
    private String marca;
    private String numeroTarjetaEnmascarado;
    private String tipo;
    private String titular;

    public DatosPago() {
        // Constructor vacío requerido para Firestore
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getNumeroTarjetaEnmascarado() {
        return numeroTarjetaEnmascarado;
    }

    public void setNumeroTarjetaEnmascarado(String numeroTarjetaEnmascarado) {
        this.numeroTarjetaEnmascarado = numeroTarjetaEnmascarado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }
}
