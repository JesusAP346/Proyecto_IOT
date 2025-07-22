package com.example.proyecto_iot.administradorHotel.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.administradorHotel.entity.NotificacionAdmin;
import com.example.proyecto_iot.databinding.ItemNotificacionAdminBinding;

import java.util.List;

public class NotificacionAdapter extends RecyclerView.Adapter<NotificacionAdapter.NotificacionViewHolder> {

    private final List<NotificacionAdmin> lista;

    public NotificacionAdapter(List<NotificacionAdmin> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public NotificacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemNotificacionAdminBinding binding = ItemNotificacionAdminBinding.inflate(inflater, parent, false);
        return new NotificacionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionViewHolder holder, int position) {
        NotificacionAdmin item = lista.get(position);
        holder.binding.tvMensaje.setText(item.getMensaje());
        holder.binding.tvHora.setText(item.getHora());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class NotificacionViewHolder extends RecyclerView.ViewHolder {
        ItemNotificacionAdminBinding binding;

        public NotificacionViewHolder(@NonNull ItemNotificacionAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
