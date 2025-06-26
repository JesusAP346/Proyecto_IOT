package com.example.proyecto_iot.SuperAdmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.LogSA;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<LogSA> listaLogs;

    public LogAdapter(List<LogSA> listaLogs) {
        this.listaLogs = listaLogs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogSA log = listaLogs.get(position);

        holder.tvTitulo.setText(log.getTitulo());
        holder.tvMensaje.setText(log.getMensaje());

        // Formatear fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fecha = sdf.format(log.getTimestamp());

        // Usuario + fecha
        holder.tvUsuarioFecha.setText("Por el " + log.getRolEditor() + " " + log.getNombreEditor() + " • " + fecha);
    }

    @Override
    public int getItemCount() {
        return listaLogs.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitulo, tvMensaje, tvUsuarioFecha;
        ImageView ivIcono;

        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloLog);
            tvMensaje = itemView.findViewById(R.id.tvMensajeLog);
            tvUsuarioFecha = itemView.findViewById(R.id.tvUsuarioLog);
            ivIcono = itemView.findViewById(R.id.ivIconoLog);
        }
    }
}
