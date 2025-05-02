package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private List<Hotel> listaHoteles;
    private Context context;

    private OnHotelClickListener listener;

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }


    public HotelAdapter(Context context, List<Hotel> listaHoteles, OnHotelClickListener listener) {
        this.context = context;
        this.listaHoteles = listaHoteles;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HotelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hotel_oferta, parent, false);
        return new HotelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HotelViewHolder holder, int position) {
        Hotel hotel = listaHoteles.get(position);
        holder.nombre.setText(hotel.getNombre());
        holder.ubicacion.setText(hotel.getUbicacion());
        holder.precio.setText("A partir de S/. " + hotel.getPrecio());
        holder.imagen.setImageResource(hotel.getImagenResId());

        for (int i = 0; i < 5; i++) {
            holder.estrellas[i].setImageResource(i < hotel.getEstrellas() ? R.drawable.ic_star : R.drawable.ic_star_border);
        }

        holder.btnVerHotel.setOnClickListener(v -> {
            Toast.makeText(context, "Hotel: " + hotel.getNombre(), Toast.LENGTH_SHORT).show();
        });
        holder.btnVerHotel.setOnClickListener(v -> {
            listener.onHotelClick(hotel);
        });

    }

    @Override
    public int getItemCount() {
        return listaHoteles.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre, ubicacion, precio;
        ImageView[] estrellas = new ImageView[5];
        Button btnVerHotel;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imageHotel);
            nombre = itemView.findViewById(R.id.textNombreHotel);
            ubicacion = itemView.findViewById(R.id.textUbicacion);
            precio = itemView.findViewById(R.id.textPrecio);
            btnVerHotel = itemView.findViewById(R.id.btnVerHotel);

            estrellas[0] = itemView.findViewById(R.id.star1);
            estrellas[1] = itemView.findViewById(R.id.star2);
            estrellas[2] = itemView.findViewById(R.id.star3);
            estrellas[3] = itemView.findViewById(R.id.star4);
            estrellas[4] = itemView.findViewById(R.id.star5);
        }
    }
}

