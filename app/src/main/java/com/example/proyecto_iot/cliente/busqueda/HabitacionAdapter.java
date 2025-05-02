package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class HabitacionAdapter extends RecyclerView.Adapter<HabitacionAdapter.HabitacionViewHolder> {

    private Context context;
    private List<Habitacion> listaHabitaciones;

    public HabitacionAdapter(Context context, List<Habitacion> listaHabitaciones) {
        this.context = context;
        this.listaHabitaciones = listaHabitaciones;
    }

    @NonNull
    @Override
    public HabitacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(context).inflate(R.layout.item_habitacion, parent, false);
        return new HabitacionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitacionViewHolder holder, int position) {
        Habitacion habitacion = listaHabitaciones.get(position);

        holder.textPrecio.setText(habitacion.getPrecio());
        holder.textEtiqueta.setText(habitacion.getEtiqueta());
        holder.textDescripcion.setText(habitacion.getDescripcion());

        // agregar listeners para manejar clics
        holder.btnReservar.setOnClickListener(v -> {
        });

        holder.btnVerDetalle.setOnClickListener(v -> {

        });
    }

    @Override
    public int getItemCount() {
        return listaHabitaciones.size();
    }

    public static class HabitacionViewHolder extends RecyclerView.ViewHolder {
        TextView textPrecio, textEtiqueta, textDescripcion;
        Button btnReservar, btnVerDetalle;

        public HabitacionViewHolder(@NonNull View itemView) {
            super(itemView);
            textPrecio = itemView.findViewById(R.id.textPrecio);
            textEtiqueta = itemView.findViewById(R.id.textEtiqueta);
            textDescripcion = itemView.findViewById(R.id.textDescripcion);
            btnReservar = itemView.findViewById(R.id.btnReservar);
            btnVerDetalle = itemView.findViewById(R.id.btnVerDetalle);
        }
    }
}

