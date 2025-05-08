package com.example.proyecto_iot.cliente.pago;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class TarjetaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TARJETA = 0;
    private static final int TYPE_AGREGAR = 1;

    private List<Tarjeta> tarjetaList;
    private Context context;

    public TarjetaAdapter(List<Tarjeta> tarjetaList) {
        this.tarjetaList = tarjetaList;
    }

    @Override
    public int getItemViewType(int position) {
        return (position < tarjetaList.size()) ? TYPE_TARJETA : TYPE_AGREGAR;
    }

    @Override
    public int getItemCount() {
        return tarjetaList.size() + 1; // +1 para el botón "Agregar tarjeta"
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
            vh.tvNumero.setText(tarjeta.getNumero());
            vh.tvTitular.setText(tarjeta.getTitular());
            vh.tvTipo.setText(tarjeta.getTipo());

            if (tarjeta.getMarca().equalsIgnoreCase("Visa")) {
                vh.imgLogo.setImageResource(R.drawable.ic_visa);
            } else if (tarjeta.getMarca().equalsIgnoreCase("Mastercard")) {
                vh.imgLogo.setImageResource(R.drawable.ic_mastercard);
            } else {
                vh.imgLogo.setImageResource(R.drawable.ic_mastercard);  //Cambiar cuando me acuerde :D
            }
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

        public TarjetaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBanco = itemView.findViewById(R.id.tvBanco);
            tvNumero = itemView.findViewById(R.id.tvNumero);
            tvTitular = itemView.findViewById(R.id.tvTitular);
            tvTipo = itemView.findViewById(R.id.tvTipo);
            imgLogo = itemView.findViewById(R.id.imgLogo);
        }
    }

    static class AgregarViewHolder extends RecyclerView.ViewHolder {
        public AgregarViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
