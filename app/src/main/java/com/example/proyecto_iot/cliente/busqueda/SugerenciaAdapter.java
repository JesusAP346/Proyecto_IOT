package com.example.proyecto_iot.cliente.busqueda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class SugerenciaAdapter extends RecyclerView.Adapter<SugerenciaAdapter.ViewHolder> {
    public interface OnItemClickListener {
        void onItemClick(String lugar);
    }

    private List<String> lista;
    private OnItemClickListener listener;

    public SugerenciaAdapter(List<String> lista, OnItemClickListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public ViewHolder(View v) {
            super(v);
            // Buscar el TextView dentro del layout personalizado
            textView = v.findViewById(R.id.textSugerencia);
            if (textView == null) {
                // Fallback al layout estándar si no se encuentra el personalizado
                textView = v.findViewById(android.R.id.text1);
            }
        }
    }

    @Override
    public SugerenciaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        try {
            // Intentar usar layout personalizado primero
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_sugerencia, parent, false);
        } catch (Exception e) {
            // Usar layout estándar como fallback
            v = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String texto = lista.get(position);
        holder.textView.setText(texto);

        // Mejorar la respuesta táctil
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(texto);
            }
        });

        // Añadir efecto visual al hacer clic
        holder.itemView.setClickable(true);
        holder.itemView.setFocusable(true);

        // Configurar padding y estilo del text
        holder.textView.setPadding(16, 12, 16, 12);
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }
}