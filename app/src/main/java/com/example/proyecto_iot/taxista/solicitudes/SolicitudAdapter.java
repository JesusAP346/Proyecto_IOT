package com.example.proyecto_iot.taxista.solicitudes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.taxista.perfil.Notificacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder> {

    private final List<Solicitud> lista;
    private final OnSolicitudClickListener listener;
    private static final String CANAL_ID = "canal_viajes";
    private static final String FILE_NOTIFICACIONES = "notificaciones.json";

    public interface OnSolicitudClickListener {
        void onSolicitudAceptada(Solicitud solicitud);
    }

    public SolicitudAdapter(List<Solicitud> lista, OnSolicitudClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Solicitud solicitud = lista.get(position);
        Context context = holder.itemView.getContext();

        holder.nombre.setText(solicitud.nombre);
        holder.telefono.setText(solicitud.telefono);
        holder.viajes.setText(solicitud.viajes + " viajes");
        holder.tiempoDistancia.setText(solicitud.tiempoDistancia);
        holder.origen.setText(solicitud.origen);
        holder.distrito.setText(solicitud.distrito);
        holder.destino.setText(solicitud.destino);

        if (solicitud.urlFotoPerfil != null && !solicitud.urlFotoPerfil.isEmpty()) {
            Glide.with(context)
                    .load(solicitud.urlFotoPerfil)
                    .placeholder(R.drawable.usuario_10)
                    .error(R.drawable.usuario_10)
                    .into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.usuario_10);
        }

        if ("pendiente".equals(solicitud.estado)) {
            holder.btnAceptar.setVisibility(View.VISIBLE);
            holder.btnRechazar.setVisibility(View.VISIBLE);
            holder.btnAceptar.setText("Aceptar");
        } else if ("aceptado".equals(solicitud.estado)) {
            holder.btnAceptar.setVisibility(View.VISIBLE);
            holder.btnRechazar.setVisibility(View.GONE);
            holder.btnAceptar.setText("Ver");
        }

        holder.btnAceptar.setOnClickListener(v -> {
            listener.onSolicitudAceptada(solicitud);
        });

        holder.btnRechazar.setOnClickListener(v -> {
            // Acción personalizada si deseas manejar el rechazo
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre, telefono, viajes, tiempoDistancia;
        TextView origen, distrito, destino;
        Button btnAceptar, btnRechazar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagePerfil);
            nombre = itemView.findViewById(R.id.tvNombre);
            telefono = itemView.findViewById(R.id.tvTelefono);
            viajes = itemView.findViewById(R.id.tvViajes);
            tiempoDistancia = itemView.findViewById(R.id.tvTiempoDistancia);
            origen = itemView.findViewById(R.id.tvOrigen);
            distrito = itemView.findViewById(R.id.tvDistrito);
            destino = itemView.findViewById(R.id.tvDestino);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }

    // Métodos para guardar notificaciones (sin cambios)
    private void guardarNotificacionEnStorage(Context context, Notificacion notificacion) {
        List<Notificacion> lista = leerNotificacionesDesdeStorage(context);
        lista.add(notificacion);

        Gson gson = new Gson();
        String json = gson.toJson(lista);

        try (FileOutputStream fos = context.openFileOutput(FILE_NOTIFICACIONES, Context.MODE_PRIVATE);
             FileWriter writer = new FileWriter(fos.getFD())) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Notificacion> leerNotificacionesDesdeStorage(Context context) {
        List<Notificacion> lista = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FILE_NOTIFICACIONES);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
            Type listType = new TypeToken<List<Notificacion>>() {}.getType();
            lista = new Gson().fromJson(sb.toString(), listType);
        } catch (Exception e) {
            // Ignorar si el archivo no existe
        }
        return lista;
    }

    private String obtenerHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
