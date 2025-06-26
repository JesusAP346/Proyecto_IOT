package com.example.proyecto_iot.administradorHotel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.databinding.ItemHabitacionAdminBinding;

import java.util.List;

public class HabitacionAdapter extends RecyclerView.Adapter<HabitacionAdapter.HabitacionViewHolder> {

    public interface OnItemClickListener {
        void onVerDetallesClick(HabitacionHotel habitacion);
    }

    private final List<HabitacionHotel> listaHabitaciones;
    private final Context context;
    private final OnItemClickListener listener;

    public HabitacionAdapter(List<HabitacionHotel> listaHabitaciones, Context context, OnItemClickListener listener) {
        this.listaHabitaciones = listaHabitaciones;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemHabitacionAdminBinding binding = ItemHabitacionAdminBinding.inflate(inflater, parent, false);
        return new HabitacionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitacionViewHolder holder, int position) {
        HabitacionHotel habitacion = listaHabitaciones.get(position);

        holder.binding.textoTitulo.setText(habitacion.getTipo());
        holder.binding.textoCantidad.setText("Habitaciones registradas: " + habitacion.getCantidadHabitaciones());

        // Cargar la primera imagen desde la lista de fotos
        if (habitacion.getFotosUrls() != null && !habitacion.getFotosUrls().isEmpty()) {
            Glide.with(context)
                    .load(habitacion.getFotosUrls().get(0))
                    .into(holder.binding.imagenHabitacion);
        } else {
            holder.binding.imagenHabitacion.setImageResource(android.R.drawable.ic_menu_gallery); // imagen por defecto
        }

        holder.binding.btnVerDetalles.setOnClickListener(v -> {
            listener.onVerDetallesClick(habitacion);
        });
    }

    @Override
    public int getItemCount() {
        return listaHabitaciones.size();
    }

    public static class HabitacionViewHolder extends RecyclerView.ViewHolder {
        ItemHabitacionAdminBinding binding;

        public HabitacionViewHolder(ItemHabitacionAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
