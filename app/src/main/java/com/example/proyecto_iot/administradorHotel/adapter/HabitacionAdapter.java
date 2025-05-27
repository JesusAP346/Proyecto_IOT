package com.example.proyecto_iot.administradorHotel.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.dto.HabitacionDto;
import com.example.proyecto_iot.administradorHotel.entity.Habitacion;
import com.example.proyecto_iot.administradorHotel.fragmentos.DetalleHabitacionFragment;
import com.example.proyecto_iot.databinding.ItemHabitacionAdminBinding;


import java.util.List;

public class HabitacionAdapter extends RecyclerView.Adapter<HabitacionAdapter.HabitacionViewHolder> {

    public interface OnItemClickListener {
        void onVerDetallesClick(Habitacion habitacion);
    }

    private final List<HabitacionDto> listaDto;
    private final List<Habitacion> listaHabitacionCompleta;
    private final Context context;
    private final OnItemClickListener listener;

    public HabitacionAdapter(List<HabitacionDto> listaDto, List<Habitacion> listaHabitacionCompleta, Context context, OnItemClickListener listener) {
        this.listaDto = listaDto;
        this.listaHabitacionCompleta = listaHabitacionCompleta;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemHabitacionAdminBinding binding = ItemHabitacionAdminBinding.inflate(inflater, parent, false); // CAMBIO AQUI
        return new HabitacionViewHolder(binding);
    }
    @Override
    public void onBindViewHolder(@NonNull HabitacionViewHolder holder, int position) {
        HabitacionDto dto = listaDto.get(position);
        Habitacion habitacion = listaHabitacionCompleta.get(position);

        holder.binding.textoTitulo.setText(dto.getTipo());
        holder.binding.textoCantidad.setText("Habitaciones registradas: " + dto.getCantidadHabitaciones());
        holder.binding.imagenHabitacion.setImageResource(dto.getFotoResId());

        holder.binding.btnVerDetalles.setOnClickListener(v -> {
            listener.onVerDetallesClick(habitacion);
        });
    }

    @Override
    public int getItemCount() {
        return listaDto.size();
    }

    public static class HabitacionViewHolder extends RecyclerView.ViewHolder {
        ItemHabitacionAdminBinding binding; // CAMBIO AQUI

        public HabitacionViewHolder(ItemHabitacionAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
