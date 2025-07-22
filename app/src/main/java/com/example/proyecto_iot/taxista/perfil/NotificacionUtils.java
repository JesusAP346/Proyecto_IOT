package com.example.proyecto_iot.taxista.perfil;

import android.content.Context;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
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

    public static void agregarNotificacion(Context context, String mensaje, String hora, int iconoResId) {
        List<Notificacion> notificaciones = new ArrayList<>();

        try {
            // Leer notificaciones existentes
            FileInputStream fis = context.openFileInput("notificaciones.json");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) sb.append(linea);
            br.close();

            Type listType = new TypeToken<List<NotificacionDTO>>() {}.getType();
            List<NotificacionDTO> dtoList = new Gson().fromJson(sb.toString(), listType);
            notificaciones = NotificacionUtils.fromDTOList(dtoList);
        } catch (Exception ignored) {
            // Si no existe el archivo o hay error, seguimos con lista vacía
        }

        // Agregar nueva notificación
        notificaciones.add(new Notificacion(mensaje, hora, iconoResId));

        // Guardar actualizado
        List<NotificacionDTO> dtoList = NotificacionUtils.toDTOList(notificaciones);
        String nuevoJson = new Gson().toJson(dtoList);

        try (FileOutputStream fos = context.openFileOutput("notificaciones.json", Context.MODE_PRIVATE);
             FileWriter writer = new FileWriter(fos.getFD())) {
            writer.write(nuevoJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
