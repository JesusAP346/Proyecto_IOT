package com.example.proyecto_iot.administradorHotel.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.administradorHotel.entity.ReservaCompletaHotel;
import com.example.proyecto_iot.databinding.ItemReservaAdminBinding;

import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    public interface OnItemClickListener {
        void onVerDetallesClick(ReservaCompletaHotel reservaCompleta);
    }
    private List<ReservaCompletaHotel> listaReservasCompletas;
    private final Context context;
    private final OnItemClickListener listener;

    public ReservaAdapter(List<ReservaCompletaHotel> listaReservasCompletas, Context context, OnItemClickListener listener) {
        this.listaReservasCompletas = listaReservasCompletas;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ItemReservaAdminBinding binding = ItemReservaAdminBinding.inflate(inflater, parent, false);
        return new ReservaViewHolder(binding);
    }

    public void actualizarLista(List<ReservaCompletaHotel> nuevasReservas) {
        this.listaReservasCompletas = nuevasReservas;
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        ReservaCompletaHotel reservaCompleta = listaReservasCompletas.get(position);

        String[] nombres = reservaCompleta.getUsuario().getNombres().split(" ");
        String[] apellidos = reservaCompleta.getUsuario().getApellidos().split(" ");
        String nombreHuesped = nombres[0] + " " + apellidos[0];

        String tipoHabitacion = reservaCompleta.getHabitacion().getTipo();
        String fechaEntrada = reservaCompleta.getReserva().getFechaEntrada();
        String fechaSalida = reservaCompleta.getReserva().getFechaSalida();

        holder.binding.nombreHuesped.setText(nombreHuesped);
        holder.binding.tipoHabitacion.setText(tipoHabitacion);
        holder.binding.checkin.setText(fechaEntrada);
        holder.binding.checkout.setText(fechaSalida);

        holder.binding.btnVerDetalles.setOnClickListener(v -> {
            listener.onVerDetallesClick(reservaCompleta);
        });
    }

    @Override
    public int getItemCount() {
        return listaReservasCompletas.size();
    }

    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        ItemReservaAdminBinding binding;

        public ReservaViewHolder(ItemReservaAdminBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
