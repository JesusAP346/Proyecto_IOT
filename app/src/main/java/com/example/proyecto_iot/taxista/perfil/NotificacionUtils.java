package com.example.proyecto_iot.taxista.perfil;

import java.util.ArrayList;
import java.util.List;

public class NotificacionUtils {

    public static NotificacionDTO toDTO(Notificacion n) {
        return new NotificacionDTO(n.getMensaje(), n.getHora(), n.getIconoResId());
    }

    public static Notificacion fromDTO(NotificacionDTO dto) {
        return new Notificacion(dto.mensaje, dto.hora, dto.iconoResId);
    }

    public static List<NotificacionDTO> toDTOList(List<Notificacion> lista) {
        List<NotificacionDTO> dtoList = new ArrayList<>();
        for (Notificacion n : lista) {
            dtoList.add(toDTO(n));
        }
        return dtoList;
    }

    public static List<Notificacion> fromDTOList(List<NotificacionDTO> dtoList) {
        List<Notificacion> lista = new ArrayList<>();
        for (NotificacionDTO dto : dtoList) {
            lista.add(fromDTO(dto));
        }
        return lista;
    }
}
