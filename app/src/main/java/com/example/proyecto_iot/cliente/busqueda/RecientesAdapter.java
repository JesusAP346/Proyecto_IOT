package com.example.proyecto_iot.cliente.busqueda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class RecientesAdapter extends RecyclerView.Adapter<RecientesAdapter.RecienteViewHolder> {

    private final List<RecienteItem> lista;

    public RecientesAdapter(List<RecienteItem> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public RecienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reciente, parent, false);
        return new RecienteViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecienteViewHolder holder, int position) {
        RecienteItem item = lista.get(position);
        holder.txtNombreHotel.setText(item.nombre);
        holder.txtUbicacion.setText("📍 " + item.ubicacion);
        holder.txtFechas.setText(item.fechas);
        holder.imgHotel.setImageResource(item.imagenResId); // imagen local
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class RecienteViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreHotel, txtUbicacion, txtFechas;
        ImageView imgHotel;

        RecienteViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreHotel = itemView.findViewById(R.id.txtNombreHotel);
            txtUbicacion = itemView.findViewById(R.id.txtUbicacion);
            txtFechas = itemView.findViewById(R.id.txtFechas);
            imgHotel = itemView.findViewById(R.id.imgHotel);
        }
    }
}

