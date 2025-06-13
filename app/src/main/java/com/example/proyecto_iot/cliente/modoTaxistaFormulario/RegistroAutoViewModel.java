package com.example.proyecto_iot.cliente.modoTaxistaFormulario;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class RegistroAutoViewModel extends ViewModel {
    private final MutableLiveData<String> placa = new MutableLiveData<>();
    private final MutableLiveData<String> color = new MutableLiveData<>();
    private final MutableLiveData<String> modelo = new MutableLiveData<>();

    public void setPlaca(String value) {
        placa.setValue(value);
    }

    public void setColor(String value) {
        color.setValue(value);
    }

    public void setModelo(String value) {
        modelo.setValue(value);
    }

    public LiveData<String> getPlaca() {
        return placa;
    }

    public LiveData<String> getColor() {
        return color;
    }

    public LiveData<String> getModelo() {
        return modelo;
    }

    // Opcional: obtener todo el objeto Auto
    public Auto getAuto() {
        return new Auto(
                placa.getValue() != null ? placa.getValue() : "",
                color.getValue() != null ? color.getValue() : "",
                modelo.getValue() != null ? modelo.getValue() : ""
        );
    }
}

