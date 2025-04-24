package com.example.proyecto_iot.cliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.Reserva;

import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private Context context;
    private List<Reserva> reservas;

    public ReservaAdapter(Context context, List<Reserva> reservas) {
        this.context = context;
        this.reservas = reservas;
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservas.get(position);
        holder.tvNombreHotel.setText(reserva.getNombreHotel());
        holder.tvUbicacion.setText("📍 " + reserva.getUbicacion());
        holder.tvEstado.setText("Estado: " + reserva.getEstado());
        holder.imgHotel.setImageResource(reserva.getImagen());
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreHotel, tvUbicacion, tvEstado;
        ImageView imgHotel;
        Button btnVerInfo;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreHotel = itemView.findViewById(R.id.tvNombreHotel);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            imgHotel = itemView.findViewById(R.id.imgHotel);
            btnVerInfo = itemView.findViewById(R.id.btnVerInfo);
        }
    }
}
