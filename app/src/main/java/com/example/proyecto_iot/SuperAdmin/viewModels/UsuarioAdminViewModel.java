package com.example.proyecto_iot.SuperAdmin.viewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyecto_iot.dtos.Usuario;

public class UsuarioAdminViewModel extends ViewModel {
    private final MutableLiveData<Usuario> usuarioAdmin = new MutableLiveData<>(new Usuario());

    public LiveData<Usuario> getUsuarioAdmin() {
        return usuarioAdmin;
    }

    public void setUsuario(Usuario usuario) {
        usuarioAdmin.setValue(usuario);
    }

    public void actualizarCampo(String campo, String valor) {
        Usuario usuario = usuarioAdmin.getValue();
        if (usuario == null) usuario = new Usuario();

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
            case "placaAuto":
                usuario.setPlacaAuto(valor);
                break;
            case "colorAuto":
                usuario.setColorAuto(valor);
                break;
            case "modeloAuto":
                usuario.setModeloAuto(valor);
                break;
            default:
                throw new IllegalArgumentException("Campo desconocido: " + campo);
        }

        usuarioAdmin.setValue(usuario);
    }
}
