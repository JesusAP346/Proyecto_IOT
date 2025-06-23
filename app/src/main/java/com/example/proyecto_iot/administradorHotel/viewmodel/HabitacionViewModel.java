package com.example.proyecto_iot.administradorHotel.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.Servicio;

import java.util.List;

public class HabitacionViewModel extends ViewModel {

    private final MutableLiveData<HabitacionHotel> habitacionLiveData = new MutableLiveData<>(new HabitacionHotel());

    public LiveData<HabitacionHotel> getHabitacion() {
        return habitacionLiveData;
    }

    @SuppressWarnings("unchecked")
    public void actualizarCampo(String campo, Object valor) {
        HabitacionHotel hab = habitacionLiveData.getValue();
        if (hab == null) hab = new HabitacionHotel();

        switch (campo) {
            case "id":
                hab.setId((String) valor);
                break;
            case "tipo":
                hab.setTipo((String) valor);
                break;
            case "capacidadAdultos":
                hab.setCapacidadAdultos((Integer) valor);
                break;
            case "capacidadNinos":
                hab.setCapacidadNinos((Integer) valor);
                break;
            case "tamanho":
                hab.setTamanho((Integer) valor);
                break;
            case "precioPorNoche":
                hab.setPrecioPorNoche((Double) valor);
                break;
            case "cantidadHabitaciones":
                hab.setCantidadHabitaciones((Integer) valor);
                break;
            case "equipamiento":
                hab.setEquipamiento((List<String>) valor);
                break;
            case "servicio":
                hab.setServicio((List<String>) valor);
                break;
            case "fotosUrls":
                hab.setFotosUrls((List<String>) valor);
                break;
            default:
                throw new IllegalArgumentException("Campo no reconocido: " + campo);
        }

        habitacionLiveData.setValue(hab);
    }
}
