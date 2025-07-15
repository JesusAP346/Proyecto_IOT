package com.example.proyecto_iot.administradorHotel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.administradorHotel.dto.ServicioDto;
import com.example.proyecto_iot.administradorHotel.entity.Servicio;
import com.example.proyecto_iot.administradorHotel.entity.ServicioHotel;
import com.example.proyecto_iot.databinding.ItemServicioAdminBinding;

import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {

    public interface OnItemClickListener {
        void onVerDetallesClick(ServicioHotel servicio);
    }

    private final List<ServicioHotel> listaServicios;
    private final Context context;
    private final OnItemClickListener listener;

    public ServicioAdapter(List<ServicioHotel> listaServicios,
                           Context context, OnItemClickListener listener) {
        this.listaServicios = listaServicios;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemServicioAdminBinding binding = ItemServicioAdminBinding.inflate(inflater, parent, false);
        return new ServicioViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {

        ServicioHotel servicio = listaServicios.get(position);

        holder.binding.textoTitulo.setText(servicio.getNombre());

        // Cargar la primera imagen desde la lista de fotos
        if (servicio.getFotosUrls() != null && !servicio.getFotosUrls().isEmpty()) {
            Glide.with(context)
                    .load(servicio.getFotosUrls().get(0))
                    .into(holder.binding.imagenServicio);
        } else {
            holder.binding.imagenServicio.setImageResource(android.R.drawable.ic_menu_gallery); // imagen por defecto
        }

        holder.binding.btnVerDetalles.setOnClickListener(v -> {
            listener.onVerDetallesClick(servicio);
        });
    }

    @Override
    public int getItemCount() {
        return listaServicios.size();
    }

    public static class ServicioViewHolder extends RecyclerView.ViewHolder {
        ItemServicioAdminBinding binding;

        public ServicioViewHolder(ItemServicioAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
