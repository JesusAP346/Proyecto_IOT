package com.example.proyecto_iot.taxista;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {

    private final List<CarouselItemModel> items;

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
        holder.imageView.setImageResource(item.imageResId);
        holder.titleText.setText(item.title);
        holder.subText.setText(item.subtitle);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView titleText;
        TextView subText;

        ViewHolder(View view) {
            super(view);
            imageView = view.findViewById(R.id.imageView);
            titleText = view.findViewById(R.id.titleText);
            subText = view.findViewById(R.id.subText);
        }
    }
}
