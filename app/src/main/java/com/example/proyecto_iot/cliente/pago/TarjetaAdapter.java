package com.example.proyecto_iot.cliente.pago;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class TarjetaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TARJETA = 0;
    private static final int TYPE_AGREGAR = 1;

    private List<Tarjeta> tarjetaList;
    private Context context;
    private int tarjetaSeleccionada = -1; // Índice de la tarjeta seleccionada
    private OnTarjetaSelectedListener listener;

    // Interface para comunicar la selección
    public interface OnTarjetaSelectedListener {
        void onTarjetaSelected(boolean haySeleccion);
    }

    public TarjetaAdapter(List<Tarjeta> tarjetaList) {
        this.tarjetaList = tarjetaList;
    }

    public void setOnTarjetaSelectedListener(OnTarjetaSelectedListener listener) {
        this.listener = listener;
    }

    public Tarjeta getTarjetaSeleccionada() {
        if (tarjetaSeleccionada >= 0 && tarjetaSeleccionada < tarjetaList.size()) {
            return tarjetaList.get(tarjetaSeleccionada);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < tarjetaList.size()) ? TYPE_TARJETA : TYPE_AGREGAR;
    }

    @Override
    public int getItemCount() {
        return tarjetaList.size() + 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        this.context = parent.getContext();
        if (viewType == TYPE_TARJETA) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_tarjeta, parent, false);
            return new TarjetaViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_agregar_tarjeta, parent, false);
            return new AgregarViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof TarjetaViewHolder) {
            Tarjeta tarjeta = tarjetaList.get(position);
            TarjetaViewHolder vh = (TarjetaViewHolder) holder;

            vh.tvBanco.setText(tarjeta.getBanco());
            // Mostrar solo los últimos 4 dígitos
            String numeroTarjeta = tarjeta.getNumero();
            if (numeroTarjeta.length() >= 4) {
                vh.tvNumero.setText("**** " + numeroTarjeta.substring(numeroTarjeta.length() - 4));
            } else {
                vh.tvNumero.setText(numeroTarjeta);
            }
            vh.tvTitular.setText(tarjeta.getTitular());
            vh.tvTipo.setText(tarjeta.getTipo());

            if (tarjeta.getMarca().equalsIgnoreCase("Visa")) {
                vh.imgLogo.setImageResource(R.drawable.ic_visa);
            } else if (tarjeta.getMarca().equalsIgnoreCase("Mastercard")) {
                vh.imgLogo.setImageResource(R.drawable.ic_mastercard);
            } else {
                vh.imgLogo.setImageResource(R.drawable.ic_mastercard);  //Cambiar cuando me acuerde :D
            }

            // Configurar apariencia según selección
            if (position == tarjetaSeleccionada) {
                vh.constraintLayout.setBackgroundResource(R.drawable.bg_tarjeta_seleccionada);
                vh.constraintLayout.setElevation(8f);
            } else {
                vh.constraintLayout.setBackgroundResource(R.drawable.bg_tarjeta_azul);
                vh.constraintLayout.setElevation(4f);
            }

            // Click en la tarjeta para seleccionar
            vh.itemView.setOnClickListener(v -> {
                int currentPosition = vh.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    int previousSelected = tarjetaSeleccionada;
                    tarjetaSeleccionada = currentPosition;

                    // Actualizar vista de tarjetas
                    if (previousSelected != -1 && previousSelected < tarjetaList.size()) {
                        notifyItemChanged(previousSelected);
                    }
                    notifyItemChanged(currentPosition);

                    // Notificar que hay una tarjeta seleccionada
                    if (listener != null) {
                        listener.onTarjetaSelected(true);
                    }
                }
            });

            // Click en botón eliminar
            vh.btnEliminar.setOnClickListener(v -> {
                int currentPosition = vh.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION && currentPosition < tarjetaList.size()) {
                    // Eliminar tarjeta de la lista y del storage
                    Tarjeta tarjetaAEliminar = tarjetaList.get(currentPosition);
                    TarjetaStorage.eliminarTarjeta(context, tarjetaAEliminar);
                    tarjetaList.remove(currentPosition);

                    // Ajustar índice de selección si es necesario
                    if (tarjetaSeleccionada == currentPosition) {
                        tarjetaSeleccionada = -1;
                        if (listener != null) {
                            listener.onTarjetaSelected(false);
                        }
                    } else if (tarjetaSeleccionada > currentPosition) {
                        tarjetaSeleccionada--;
                    }

                    notifyItemRemoved(currentPosition);
                    notifyItemRangeChanged(currentPosition, getItemCount());
                }
            });

        } else if (holder instanceof AgregarViewHolder) {
            AgregarViewHolder vh = (AgregarViewHolder) holder;
            vh.itemView.setOnClickListener(v -> {
                Context context = v.getContext();
                Intent intent = new Intent(context, AgregarTarjetaActivity.class);
                context.startActivity(intent);
            });
        }
    }

    static class TarjetaViewHolder extends RecyclerView.ViewHolder {
        TextView tvBanco, tvNumero, tvTitular, tvTipo;
        ImageView imgLogo;
        ImageButton btnEliminar;
        ConstraintLayout constraintLayout;

        public TarjetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBanco = itemView.findViewById(R.id.tvBanco);
            tvNumero = itemView.findViewById(R.id.tvNumero);
            tvTitular = itemView.findViewById(R.id.tvTitular);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            imgLogo = itemView.findViewById(R.id.imgLogo);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            constraintLayout = (ConstraintLayout) itemView;
        }
    }

    static class AgregarViewHolder extends RecyclerView.ViewHolder {
        public AgregarViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}