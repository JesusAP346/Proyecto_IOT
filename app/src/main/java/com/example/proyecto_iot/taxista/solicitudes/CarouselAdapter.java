package com.example.proyecto_iot.taxista.solicitudes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(CarouselItemModel item);
    }

    private List<CarouselItemModel> items;
    private OnItemClickListener listener;

    // ESTE constructor es el que necesitas
    public CarouselAdapter(List<CarouselItemModel> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public CarouselAdapter(List<CarouselItemModel> items) {
        this.items = items;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carousel_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CarouselItemModel item = items.get(position);
        /*holder.imageView.setImageResource(item.imageResId);*/
        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            Glide.with(holder.imageView.getContext())
                    .load(item.imageUrl)
                    .placeholder(R.drawable.hotel1) // opcional
                    .into(holder.imageView);
        } else {
            holder.imageView.setImageResource(item.imageResId); // imagen por defecto si no hay URL
        }

        holder.titleText.setText(item.title);
        holder.subText.setText(item.subtitle);
        holder.titleText.setText(item.title);
        holder.subText.setText(item.subtitle);
        holder.locationText.setText(item.location);
        holder.starsText.setText(item.stars);




        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView subText;
        TextView locationText;
        TextView starsText;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            titleText = view.findViewById(R.id.titleText);
            subText = view.findViewById(R.id.subText);
            locationText = view.findViewById(R.id.locationText);
            starsText = view.findViewById(R.id.starsText);
        }
    }

}
