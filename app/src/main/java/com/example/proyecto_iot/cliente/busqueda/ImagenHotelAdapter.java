package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;

import java.util.List;

public class ImagenHotelAdapter extends RecyclerView.Adapter<ImagenHotelAdapter.ImagenViewHolder> {

    private List<String> listaUrls;
    private Context context;

    public ImagenHotelAdapter(Context context, List<String> listaUrls) {
        this.context = context;
        this.listaUrls = listaUrls;
    }

    @NonNull
    @Override
    public ImagenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_imagen_hotel, parent, false);
        return new ImagenViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagenViewHolder holder, int position) {
        String url = listaUrls.get(position);
        Glide.with(context)
                .load(url)
                .placeholder(R.drawable.placeholder_hotel)
                .error(R.drawable.error_imagen)
                .into(holder.imagen);
    }

    @Override
    public int getItemCount() {
        return listaUrls.size();
    }

    static class ImagenViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;

        public ImagenViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imageViewHotelItem);
        }
    }
}
