package com.example.proyecto_iot.cliente.busqueda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class HotelAdapter extends RecyclerView.Adapter<HotelAdapter.HotelViewHolder> {

    private List<Hotel> listaHoteles;
    private Context context;
    private OnHotelClickListener listener;

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel, int position);
    }

    public interface OnFavoritoClickListener {
        void onFavoritoClick(Hotel hotel, int position, boolean nuevoEstado);
    }

    private OnFavoritoClickListener favListener;

    public HotelAdapter(Context context, List<Hotel> listaHoteles, OnHotelClickListener listener, OnFavoritoClickListener favListener) {
        this.context = context;
        this.listaHoteles = listaHoteles;
        this.listener = listener;
        this.favListener = favListener;
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
        holder.imagen.setImageResource(R.drawable.hotel1);

        List<String> fotos = hotel.getFotosHotelUrls();
        if (fotos != null && !fotos.isEmpty()) {
            Glide.with(context)
                    .load(fotos.get(0))
                    .placeholder(R.drawable.placeholder_hotel)
                    .error(R.drawable.error_imagen)
                    .into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.placeholder_hotel);
        }

        // Cargar valoraciones desde Firebase
        cargarValoraciones(hotel.getId(), holder);

        holder.btnVerHotel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHotelClick(hotel, position);
            }
        });
    }

    private void cargarValoraciones(String hotelId, HotelViewHolder holder) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("hoteles")
                .document(hotelId)
                .collection("valoraciones")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (task.getResult().isEmpty()) {
                            // No hay valoraciones
                            mostrarSinValoraciones(holder);
                        } else {
                            // Calcular promedio
                            double sumaEstrellas = 0;
                            int cantidadValoraciones = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Double estrellas = document.getDouble("estrellas");
                                if (estrellas != null) {
                                    sumaEstrellas += estrellas;
                                    cantidadValoraciones++;
                                }
                            }

                            if (cantidadValoraciones > 0) {
                                double promedio = sumaEstrellas / cantidadValoraciones;
                                int estrellasRedondeadas = (int) Math.ceil(promedio);
                                mostrarEstrellas(holder, estrellasRedondeadas, promedio);
                            } else {
                                mostrarSinValoraciones(holder);
                            }
                        }
                    } else {
                        // Error al cargar, mostrar estado por defecto
                        mostrarSinValoraciones(holder);
                    }
                });
    }

    private void mostrarEstrellas(HotelViewHolder holder, int estrellas, double promedioExacto) {
        // Actualizar las estrellas visuales
        for (int i = 0; i < 5; i++) {
            holder.estrellas[i].setImageResource(
                    i < estrellas ? R.drawable.ic_star : R.drawable.ic_star_border
            );
        }

        // Mostrar el texto con el promedio
        holder.textValoracion.setText(String.format("%.1f estrellas", promedioExacto));
        holder.textValoracion.setVisibility(View.VISIBLE);
    }

    private void mostrarSinValoraciones(HotelViewHolder holder) {
        // Mostrar todas las estrellas vacías
        for (int i = 0; i < 5; i++) {
            holder.estrellas[i].setImageResource(R.drawable.ic_star_border);
        }

        // Mostrar texto "Sin valoraciones"
        holder.textValoracion.setText("Sin valoraciones");
        holder.textValoracion.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return listaHoteles.size();
    }

    public static class HotelViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre, ubicacion, precio, textValoracion;
        ImageView[] estrellas = new ImageView[5];
        Button btnVerHotel;

        public HotelViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imageHotel);
            nombre = itemView.findViewById(R.id.textNombreHotel);
            ubicacion = itemView.findViewById(R.id.textUbicacion);
            precio = itemView.findViewById(R.id.textPrecio);
            btnVerHotel = itemView.findViewById(R.id.btnVerHotel);
            textValoracion = itemView.findViewById(R.id.textValoracion);

            estrellas[0] = itemView.findViewById(R.id.star1);
            estrellas[1] = itemView.findViewById(R.id.star2);
            estrellas[2] = itemView.findViewById(R.id.star3);
            estrellas[3] = itemView.findViewById(R.id.star4);
            estrellas[4] = itemView.findViewById(R.id.star5);
        }
    }

    public void actualizarLista(List<Hotel> nuevaLista) {
        this.listaHoteles = nuevaLista;
        notifyDataSetChanged();
    }
}