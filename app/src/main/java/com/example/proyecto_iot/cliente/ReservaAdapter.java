package com.example.proyecto_iot.cliente;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.proyecto_iot.cliente.busqueda.Hotel;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ReservaAdapter extends RecyclerView.Adapter<ReservaAdapter.ReservaViewHolder> {

    private Context context;
    private List<Reserva> reservas;
    private FirebaseFirestore db;

    public ReservaAdapter(Context context, List<Reserva> reservas) {
        this.context = context;
        this.reservas = reservas;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ReservaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reserva, parent, false);
        return new ReservaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservaViewHolder holder, int position) {
        Reserva reserva = reservas.get(position);

        // Datos básicos de la reserva
        holder.tvNombreHotel.setText(reserva.getNombreHotel());
        holder.tvUbicacion.setText("📍 " + reserva.getUbicacion());
        holder.tvEstado.setText("Estado: " + reserva.getEstado());

        // Cargar información del hotel desde Firestore
        cargarInformacionHotel(reserva.getIdHotel(), holder);

        holder.btnVerInfo.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetalleReservaActivity.class);
            intent.putExtra("nombre", reserva.getNombreHotel());
            intent.putExtra("estado", reserva.getEstado());
            intent.putExtra("entrada", reserva.getFechaEntrada());
            intent.putExtra("salida", reserva.getFechaSalida());
            intent.putExtra("monto", reserva.getMonto());
            intent.putExtra("imagen", reserva.getImagen());
            intent.putExtra("idReserva", reserva.getId());
            intent.putExtra("idHotel", reserva.getIdHotel());
            intent.putExtra("idHabitacion", reserva.getIdHabitacion());

            holder.itemView.getContext().startActivity(intent);
        });
    }

    private void cargarInformacionHotel(String idHotel, ReservaViewHolder holder) {
        if (idHotel == null || idHotel.isEmpty()) {
            Log.w("ReservaAdapter", "ID de hotel vacío o nulo");
            // Cargar imagen por defecto
            holder.imgHotel.setImageResource(R.drawable.hotel1);
            return;
        }

        Log.d("ReservaAdapter", "Buscando hotel con ID: " + idHotel);

        db.collection("hoteles")
                .document(idHotel)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("ReservaAdapter", "Hotel encontrado en Firestore");
                            Hotel hotel = document.toObject(Hotel.class);
                            if (hotel != null) {
                                Log.d("ReservaAdapter", "Hotel convertido correctamente");
                                Log.d("ReservaAdapter", "Fotos del hotel: " + hotel.getFotosHotelUrls());
                                cargarImagenHotel(hotel, holder);
                            } else {
                                Log.w("ReservaAdapter", "No se pudo convertir el documento a objeto Hotel");
                                holder.imgHotel.setImageResource(R.drawable.hotel1);
                            }
                        } else {
                            Log.w("ReservaAdapter", "No existe hotel con ID: " + idHotel);
                            holder.imgHotel.setImageResource(R.drawable.hotel1);
                        }
                    } else {
                        Log.e("ReservaAdapter", "Error al obtener hotel", task.getException());
                        holder.imgHotel.setImageResource(R.drawable.hotel1);
                    }
                });
    }

    private void cargarImagenHotel(Hotel hotel, ReservaViewHolder holder) {
        String urlImagen = hotel.getPrimeraFoto();
        Log.d("ReservaAdapter", "URL imagen obtenida: " + urlImagen);

        if (urlImagen != null && !urlImagen.isEmpty()) {
            // Usar Glide para cargar la imagen desde URL
            Glide.with(context)
                    .load(urlImagen)
                    .placeholder(R.drawable.hotel1) // Imagen mientras carga
                    .error(R.drawable.hotel1) // Imagen si falla la carga
                    .centerCrop()
                    .into(holder.imgHotel);
        } else {
            // Si no hay URL de imagen, usar imagen por defecto
            holder.imgHotel.setImageResource(R.drawable.hotel1);
        }
    }

    @Override
    public int getItemCount() {
        return reservas.size();
    }

    public static class ReservaViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreHotel, tvUbicacion, tvEstado;
        ImageView imgHotel;
        Button btnVerInfo;

        public ReservaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreHotel = itemView.findViewById(R.id.tvNombreHotel);
            tvUbicacion = itemView.findViewById(R.id.tvUbicacion);
            tvEstado = itemView.findViewById(R.id.tvEstado);
            imgHotel = itemView.findViewById(R.id.imgHotel);
            btnVerInfo = itemView.findViewById(R.id.btnVerInfo);
        }
    }
}