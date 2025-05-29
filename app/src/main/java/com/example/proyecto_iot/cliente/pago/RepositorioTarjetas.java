package com.example.proyecto_iot.cliente.pago;

import java.util.ArrayList;
import java.util.List;

public class RepositorioTarjetas {
    private static RepositorioTarjetas instancia;
    private final List<Tarjeta> tarjetas = new ArrayList<>();

    private RepositorioTarjetas() {}

    public static RepositorioTarjetas getInstancia() {
        if (instancia == null) {
            instancia = new RepositorioTarjetas();
        }
        return instancia;
    }

    public List<Tarjeta> obtenerTarjetas() {
        return tarjetas;
    }

    public void agregarTarjeta(Tarjeta tarjeta) {
        tarjetas.add(tarjeta);
    }

    public void eliminarTarjeta(Tarjeta tarjeta) {
        tarjetas.remove(tarjeta);
    }
}

