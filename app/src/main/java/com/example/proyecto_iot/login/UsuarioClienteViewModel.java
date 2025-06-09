package com.example.proyecto_iot.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto_iot.dtos.UsuarioCliente;

public class UsuarioClienteViewModel extends ViewModel {
    private final MutableLiveData<UsuarioCliente> usuarioCliente = new MutableLiveData<>(new UsuarioCliente());

    public LiveData<UsuarioCliente> getUsuarioCliente() {
        return usuarioCliente;
    }

    public void actualizarCampo(String campo, String valor) {
        UsuarioCliente usuario = usuarioCliente.getValue();
        if (usuario == null) usuario = new UsuarioCliente();

        switch (campo) {
            case "id":
                usuario.setId(valor);
                break;
            case "nombres":
                usuario.setNombres(valor);
                break;
            case "apellidos":
                usuario.setApellidos(valor);
                break;
            case "tipoDocumento":
                usuario.setTipoDocumento(valor);
                break;
            case "numDocumento":
                usuario.setNumDocumento(valor);
                break;
            case "fechaNacimiento":
                usuario.setFechaNacimiento(valor);
                break;
            case "numCelular":
                usuario.setNumCelular(valor);
                break;
            case "email":
                usuario.setEmail(valor);
                break;
            case "departamento":
                usuario.setDepartamento(valor);
                break;
            case "provincia":
                usuario.setProvincia(valor);
                break;
            case "distrito":
                usuario.setDistrito(valor);
                break;
            case "direccion":
                usuario.setDireccion(valor);
                break;
            case "password":
                usuario.setPassword(valor);
                break;
            default:
                throw new IllegalArgumentException("Campo desconocido: " + campo);
        }

        usuarioCliente.setValue(usuario);
    }

}

