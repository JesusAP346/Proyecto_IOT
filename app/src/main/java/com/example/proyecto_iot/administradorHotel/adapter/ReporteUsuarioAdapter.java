package com.example.proyecto_iot.administradorHotel.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.ReporteUsuario;

import java.util.List;

public class ReporteUsuarioAdapter extends RecyclerView.Adapter<ReporteUsuarioAdapter.ViewHolder> {

    private List<ReporteUsuario> lista;

    public ReporteUsuarioAdapter(List<ReporteUsuario> lista) {
        this.lista = lista;
    }

    public void actualizarLista(List<ReporteUsuario> nuevaLista) {
        this.lista = nuevaLista;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuario_reporte, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReporteUsuario item = lista.get(position);
        holder.txtUsuario.setText(item.getIdUsuario());
        holder.txtMonto.setText(String.format("S/. %.2f", item.getMontoTotal()));
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtUsuario, txtMonto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtUsuario = itemView.findViewById(R.id.txtUsuario);
            txtMonto = itemView.findViewById(R.id.txtMontoTotal);
        }
    }
}
