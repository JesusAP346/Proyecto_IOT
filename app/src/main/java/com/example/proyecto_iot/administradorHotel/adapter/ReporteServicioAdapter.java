package com.example.proyecto_iot.administradorHotel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.ReporteServicio;

import java.util.List;

public class ReporteServicioAdapter extends RecyclerView.Adapter<ReporteServicioAdapter.ViewHolder> {

    private List<ReporteServicio> lista;

    public ReporteServicioAdapter(List<ReporteServicio> lista) {
        this.lista = lista;
    }

    public void actualizarLista(List<ReporteServicio> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servicio_reporte, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReporteServicio item = lista.get(position);
        holder.txtNombre.setText(item.getNombre());
        holder.txtCantidad.setText(String.valueOf(item.getCantidad()));
        holder.txtTotal.setText(String.format("S/. %.2f", item.getTotal()));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCantidad, txtTotal;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtServicio);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            txtTotal = itemView.findViewById(R.id.txtTotal);
        }
    }
}
