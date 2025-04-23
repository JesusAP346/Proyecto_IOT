package com.example.proyecto_iot.taxista;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder> {

    private List<Solicitud> lista;

    public SolicitudAdapter(List<Solicitud> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public SolicitudAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Solicitud solicitud = lista.get(position);

        holder.nombre.setText(solicitud.nombre);
        holder.telefono.setText(solicitud.telefono);
        holder.viajes.setText(solicitud.viajes + " viajes");
        holder.tiempoDistancia.setText(solicitud.tiempoDistancia);
        holder.origen.setText(solicitud.origen);
        holder.distrito.setText(solicitud.distrito);
        holder.destino.setText(solicitud.destino);
        holder.imagen.setImageResource(solicitud.imagenPerfil);

        // Lógica de los botones
        holder.btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Abrir MapsActivity desde aquí

                Context context = v.getContext(); // ← obtenemos el contexto real
                Intent intent = new Intent(context, MapsActivity.class);
                context.startActivity(intent);
            }
        });

        holder.btnRechazar.setOnClickListener(v -> {
            // lógica rechazar
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
}
