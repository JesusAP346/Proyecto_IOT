package com.example.proyecto_iot.cliente.busqueda;

import java.io.Serializable;

public class ServicioAdicionalReserva implements Serializable {
    private String idServicioAdicional;
    private int cantDias;

    public ServicioAdicionalReserva(String idServicioAdicional, int cantDias) {
        this.idServicioAdicional = idServicioAdicional;
        this.cantDias = cantDias;
    }

    public String getIdServicioAdicional() {
        return idServicioAdicional;
    }

    public void setIdServicioAdicional(String idServicioAdicional) {
        this.idServicioAdicional = idServicioAdicional;
    }

    public int getCantDias() {
        return cantDias;
    }

    public void setCantDias(int cantDias) {
        this.cantDias = cantDias;
    }
}

