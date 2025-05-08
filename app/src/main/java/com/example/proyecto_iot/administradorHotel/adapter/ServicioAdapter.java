package com.example.proyecto_iot.administradorHotel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.administradorHotel.dto.ServicioDto;
import com.example.proyecto_iot.administradorHotel.entity.Servicio;
import com.example.proyecto_iot.databinding.ItemServicioAdminBinding;

import java.util.List;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {

    public interface OnItemClickListener {
        void onVerDetallesClick(Servicio servicio);
    }

    private final List<ServicioDto> listaServicioDto;
    private final List<Servicio> listaServicioCompleta;
    private final Context context;
    private final OnItemClickListener listener;

    public ServicioAdapter(List<ServicioDto> listaServicioDto, List<Servicio> listaServicioCompleta,
                           Context context, OnItemClickListener listener) {
        this.listaServicioDto = listaServicioDto;
        this.listaServicioCompleta = listaServicioCompleta;
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
        ServicioDto dto = listaServicioDto.get(position);
        Servicio servicio = listaServicioCompleta.get(position);

        holder.binding.textoTitulo.setText(dto.getNombre());
        holder.binding.imagenServicio.setImageResource(dto.getImagenResId());

        holder.binding.btnVerDetalles.setOnClickListener(v -> {
            listener.onVerDetallesClick(servicio);
        });
    }

    @Override
    public int getItemCount() {
        return listaServicioDto.size();
    }

    public static class ServicioViewHolder extends RecyclerView.ViewHolder {
        ItemServicioAdminBinding binding;

        public ServicioViewHolder(ItemServicioAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
