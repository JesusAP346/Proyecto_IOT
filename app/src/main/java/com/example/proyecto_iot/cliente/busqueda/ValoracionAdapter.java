package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class ValoracionAdapter extends RecyclerView.Adapter<ValoracionAdapter.ValoracionViewHolder> {

    private Context context;
    private List<Valoracion> listaValoraciones;

    public ValoracionAdapter(Context context, List<Valoracion> listaValoraciones) {
        this.context = context;
        this.listaValoraciones = listaValoraciones;
    }

    @NonNull
    @Override
    public ValoracionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_valoracion, parent, false);
        return new ValoracionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ValoracionViewHolder holder, int position) {
        Valoracion valoracion = listaValoraciones.get(position);

        holder.textNombreUsuario.setText(valoracion.getNombreUsuario());

        // Mostrar estrellas
        mostrarEstrellas(holder, valoracion.getEstrellas());

        // Mostrar observaciones si existen
        if (valoracion.getObservaciones() != null && !valoracion.getObservaciones().isEmpty()) {
            holder.textObservaciones.setText("Observaciones: " +valoracion.getObservaciones());
            holder.textObservaciones.setVisibility(View.VISIBLE);
        } else {
            holder.textObservaciones.setVisibility(View.GONE);
        }

        // Mostrar respuesta sobre el servicio
        if (valoracion.getServicio() != null && !valoracion.getServicio().isEmpty()) {
            holder.textServicio.setText("¿Qué tal le pareció el servicio?: " + valoracion.getServicio());
            holder.textServicio.setVisibility(View.VISIBLE);
        } else {
            holder.textServicio.setVisibility(View.GONE);
        }

        // Mostrar respuesta sobre si volvería
        if (valoracion.getVolveria() != null && !valoracion.getVolveria().isEmpty()) {
            holder.textVolveria.setText("¿Volvería a hospedarse aquí? ¿Por qué?: " + valoracion.getVolveria());
            holder.textVolveria.setVisibility(View.VISIBLE);
        } else {
            holder.textVolveria.setVisibility(View.GONE);
        }
    }

    private void mostrarEstrellas(ValoracionViewHolder holder, int cantidadEstrellas) {
        ImageView[] estrellas = {
                holder.star1, holder.star2, holder.star3, holder.star4, holder.star5
        };

        for (int i = 0; i < 5; i++) {
            estrellas[i].setImageResource(
                    i < cantidadEstrellas ? R.drawable.ic_star : R.drawable.ic_star_border
            );
        }
    }

    @Override
    public int getItemCount() {
        return listaValoraciones.size();
    }

    public static class ValoracionViewHolder extends RecyclerView.ViewHolder {
        TextView textNombreUsuario, textObservaciones, textServicio, textVolveria;
        ImageView star1, star2, star3, star4, star5;

        public ValoracionViewHolder(@NonNull View itemView) {
            super(itemView);
            textNombreUsuario = itemView.findViewById(R.id.textNombreUsuario);
            textObservaciones = itemView.findViewById(R.id.textObservaciones);
            textServicio = itemView.findViewById(R.id.textServicio);
            textVolveria = itemView.findViewById(R.id.textVolveria);

            star1 = itemView.findViewById(R.id.star1);
            star2 = itemView.findViewById(R.id.star2);
            star3 = itemView.findViewById(R.id.star3);
            star4 = itemView.findViewById(R.id.star4);
            star5 = itemView.findViewById(R.id.star5);
        }
    }
}