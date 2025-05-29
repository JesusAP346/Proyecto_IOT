package com.example.proyecto_iot.cliente.pago;

public class Tarjeta {
    private String banco;
    private String numero;
    private String titular;
    private String tipo;
    private String marca;
    private String fechaVenc;

    private int id;

    public Tarjeta(String banco, String numero, String titular, String tipo, String marca, String fechaVenc, int id) {
        this.banco = banco;
        this.numero = numero;
        this.titular = titular;
        this.tipo = tipo;
        this.marca = marca;
        this.fechaVenc = fechaVenc;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTitular() {
        return titular;
    }

    public void setTitular(String titular) {
        this.titular = titular;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getFechaVenc() {
        return fechaVenc;
    }

    public void setFechaVenc(String fechaVenc) {
        this.fechaVenc = fechaVenc;
    }
}

