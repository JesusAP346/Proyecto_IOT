package com.example.proyecto_iot.cliente.pago;

public class Tarjeta {
    private String banco;
    private String numero;
    private String titular;
    private String tipo;
    private String marca;

    public Tarjeta(String banco, String numero, String titular, String tipo, String marca) {
        this.banco = banco;
        this.numero = numero;
        this.titular = titular;
        this.tipo = tipo;
        this.marca = marca;
    }

    // Getters...
    public String getBanco() { return banco; }
    public String getNumero() { return numero; }
    public String getTitular() { return titular; }
    public String getTipo() { return tipo; }
    public String getMarca() { return marca; }
}

