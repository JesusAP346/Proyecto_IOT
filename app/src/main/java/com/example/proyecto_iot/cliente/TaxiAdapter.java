package com.example.proyecto_iot.cliente;


import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyecto_iot.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.List;

public class TaxiAdapter extends RecyclerView.Adapter<TaxiAdapter.ViewHolder> {
    private List<TaxiItem> lista;
    private Context context;

    public TaxiAdapter(List<TaxiItem> lista, Context context) {
        this.lista = lista;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPlaca, tvConductor, tvDestino;
        ImageView imgFoto, imgQR1, imgQR2;
        Button btnVerUbicacion;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPlaca = itemView.findViewById(R.id.tvPlaca);
            tvConductor = itemView.findViewById(R.id.tvConductor);
            tvDestino = itemView.findViewById(R.id.tvDestino);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            imgQR1 = itemView.findViewById(R.id.imgQR1);
            //imgQR2 = itemView.findViewById(R.id.imgQR2);
            btnVerUbicacion = itemView.findViewById(R.id.btnVerUbicacion);
        }
    }

    @NonNull
    @Override
    public TaxiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_solicitud_taxi, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TaxiAdapter.ViewHolder holder, int position) {
        TaxiItem item = lista.get(position);

        holder.tvPlaca.setText(item.getPlaca());
        holder.tvConductor.setText(item.getNombreConductor());
        holder.tvDestino.setText("Destino: " + item.getDestino());
        holder.imgFoto.setImageResource(item.getFotoResId());
        holder.imgQR1.setImageResource(R.drawable.qrcode_solid);
       // holder.imgQR2.setImageResource(R.drawable.qrcode_solid);
        String contenidoQR = "serviciotaxi:#" + item.getIdServicio();
        Bitmap bitmapQR = generarCodigoQR(contenidoQR, 500, 500);  // tamaño del QR
        holder.imgQR1.setImageBitmap(bitmapQR);

        /* Cambiar color y estado del botón
        if (item.isActiva()) {
            holder.btnVerUbicacion.setEnabled(true);
            holder.btnVerUbicacion.setText("Ver ubicación");
            holder.btnVerUbicacion.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
        } else {
            holder.btnVerUbicacion.setEnabled(false);
            holder.btnVerUbicacion.setText("Ver ubicación");
            holder.btnVerUbicacion.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));
        }*/

        if (item.isActiva()) {
            // Habilitar el botón
            holder.btnVerUbicacion.setEnabled(true);
            holder.btnVerUbicacion.setText("Ver ubicación");
            holder.btnVerUbicacion.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));

            // Acción al hacer clic
            holder.btnVerUbicacion.setOnClickListener(v -> {
                Intent intent = new Intent(context, MapaTaxiActivity.class);
                intent.putExtra("nombre", item.getNombreConductor());
                intent.putExtra("placa", item.getPlaca());
                intent.putExtra("tiempo", "2 min.");
                intent.putExtra("distancia", "0.85 km");
                // ⬅⬅ Agregar estas dos líneas
                intent.putExtra("latitud", item.getLatitud());
                intent.putExtra("longitud", item.getLongitud());
                context.startActivity(intent);
            });
        } else {
            // Deshabilitar botón
            holder.btnVerUbicacion.setEnabled(false);
            holder.btnVerUbicacion.setText("Ver ubicación");
            holder.btnVerUbicacion.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

            // Quitar acción
            holder.btnVerUbicacion.setOnClickListener(null);
        }







        /* Acción si está activa
        holder.btnVerUbicacion.setOnClickListener(v -> {
            if (item.isActiva()) {
                // Abrir mapa o realizar acción
                // Por ahora podrías hacer un Toast o Log
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    private Bitmap generarCodigoQR(String contenido, int ancho, int alto) {
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    contenido, BarcodeFormat.QR_CODE, ancho, alto, null
            );
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.createBitmap(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
