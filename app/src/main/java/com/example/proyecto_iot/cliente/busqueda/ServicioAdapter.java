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

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {

    private Context context;
    private List<Servicio> servicios;
    private OnServicioClickListener listener;

    public interface OnServicioClickListener {
        void onVerFotosClick(Servicio servicio);
    }

    public ServicioAdapter(Context context, List<Servicio> servicios, OnServicioClickListener listener) {
        this.context = context;
        this.servicios = servicios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_servicio, parent, false);
        return new ServicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {
        Servicio servicio = servicios.get(position);

        holder.textNombreServicio.setText(servicio.getNombre());
        holder.textDescripcionServicio.setText(servicio.getDescripcion());
        holder.textPrecioServicio.setText(String.format("S/ %.2f por día", servicio.getPrecio()));

        // Mostrar u ocultar botón de fotos según disponibilidad
        if (servicio.getFotosUrls() != null && !servicio.getFotosUrls().isEmpty()) {
            holder.btnVerFotos.setVisibility(View.VISIBLE);
            holder.btnVerFotos.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerFotosClick(servicio);
                }
            });
        } else {
            holder.btnVerFotos.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    public static class ServicioViewHolder extends RecyclerView.ViewHolder {
        TextView textNombreServicio, textDescripcionServicio, textPrecioServicio;
        Button btnVerFotos;

        public ServicioViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombreServicio = itemView.findViewById(R.id.textNombreServicio);
            textDescripcionServicio = itemView.findViewById(R.id.textDescripcionServicio);
            textPrecioServicio = itemView.findViewById(R.id.textPrecioServicio);
            btnVerFotos = itemView.findViewById(R.id.btnVerFotos);
        }
    }
}