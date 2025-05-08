package com.example.proyecto_iot.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.taxista.perfil.Notificacion;


import java.util.List;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.ViewHolder> {

    private List<Notificacion> listaNotificaciones;

    public NotificacionesAdapter(List<Notificacion> listaNotificaciones) {
        this.listaNotificaciones = listaNotificaciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notificacion_taxista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notificacion notificacion = listaNotificaciones.get(position);
        holder.tvMensaje.setText(notificacion.getMensaje());
        holder.tvHora.setText(notificacion.getHora());
        holder.imgIcono.setImageResource(notificacion.getIconoResId());
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvMensaje, tvHora;
        ImageView imgIcono;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
            tvHora = itemView.findViewById(R.id.tvHora);
            imgIcono = itemView.findViewById(R.id.imgIcono);
        }
    }
}
