package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.pago.PasarellaDePago;

import java.util.List;

public class HabitacionAdapter extends RecyclerView.Adapter<HabitacionAdapter.ViewHolder> {

    private List<Habitacion> listaHabitaciones;

    public HabitacionAdapter(List<Habitacion> listaHabitaciones) {
        this.listaHabitaciones = listaHabitaciones;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_habitacion, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habitacion h = listaHabitaciones.get(position);
        holder.textPrecio.setText(h.getPrecio());
        holder.textEtiqueta.setText(h.getEtiqueta());
        holder.textDescripcion.setText(h.getDescripcion());

        holder.btnReservar.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, PasarellaDePago.class);
            intent.putExtra("precio", h.getPrecio());
            intent.putExtra("descripcion", h.getDescripcion());
            context.startActivity(intent);
        });

        holder.btnVerDetalle.setOnClickListener(v -> {
        });
    }

    @Override
    public int getItemCount() {
        return listaHabitaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textPrecio, textEtiqueta, textDescripcion;
        Button btnReservar, btnVerDetalle;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textPrecio = itemView.findViewById(R.id.textPrecio);
            textEtiqueta = itemView.findViewById(R.id.textEtiqueta);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            btnReservar = itemView.findViewById(R.id.btnReservar);
            btnVerDetalle = itemView.findViewById(R.id.btnVerDetalle);
        }
    }
}

