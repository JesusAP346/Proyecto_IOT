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
import com.example.proyecto_iot.BuildConfig;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.taxista.perfil.Notificacion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder> {

    private final List<Solicitud> lista;
    private final OnSolicitudClickListener listener;
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
        holder.tiempoDistancia.setText("Calculando...");
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

        holder.btnAceptar.setOnClickListener(v -> listener.onSolicitudAceptada(solicitud));

        obtenerTiempoDistancia(
                context,
                solicitud.latTaxista,
                solicitud.lngTaxista,
                solicitud.latDestino,
                solicitud.lngDestino,
                holder.tiempoDistancia
        );
    }

    private void obtenerTiempoDistancia(Context context, double lat1, double lng1,
                                        double lat2, double lng2, TextView tvTiempoDistancia) {
        String apiKey = BuildConfig.MAPS_API_KEY;
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                lat1 + "," + lng1 + "&destination=" + lat2 + "," + lng2 + "&key=" + apiKey;

        new Thread(() -> {
            try {
                java.net.URL direccionUrl = new java.net.URL(url);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) direccionUrl.openConnection();
                conn.setRequestMethod("GET");

                java.io.InputStream in = new java.io.BufferedInputStream(conn.getInputStream());
                java.util.Scanner scanner = new java.util.Scanner(in).useDelimiter("\\A");
                final String response = scanner.hasNext() ? scanner.next() : "";

                ((android.app.Activity) context).runOnUiThread(() -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        JSONArray routes = json.getJSONArray("routes");
                        if (routes.length() > 0) {
                            JSONObject leg = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0);
                            String duracionTexto = leg.getJSONObject("duration").getString("text");
                            String distanciaTexto = leg.getJSONObject("distance").getString("text");
                            tvTiempoDistancia.setText(duracionTexto + "\n" + distanciaTexto);
                        } else {
                            tvTiempoDistancia.setText("No encontrado");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        tvTiempoDistancia.setText("Error");
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                tvTiempoDistancia.setText("Error");
            }
        }).start();
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
}
