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

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.google.firebase.firestore.FirebaseFirestore;
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
        TextView tvPlaca, tvConductor, tvDestino, tvTelefono;
        ImageView imgFoto, imgQR1, imgQR2;
        Button btnVerUbicacion;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPlaca = itemView.findViewById(R.id.tvPlaca);
            tvConductor = itemView.findViewById(R.id.tvConductor);
            tvDestino = itemView.findViewById(R.id.tvDestino);
            imgFoto = itemView.findViewById(R.id.imgFoto);
            imgQR1 = itemView.findViewById(R.id.imgQR1);
           // tvTelefono = itemView.findViewById(R.id.tvTelefono);

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
        //holder.imgFoto.setImageResource(item.getFotoResId());

        String idTaxista = item.getIdTaxista();
        if (idTaxista != null && !idTaxista.isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(idTaxista)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String urlFoto = documentSnapshot.getString("urlFotoPerfil");
                            String telefono = documentSnapshot.getString("numCelular");
                            if (urlFoto != null && !urlFoto.isEmpty()) {
                                Glide.with(context)
                                        .load(urlFoto)
                                        .placeholder(R.drawable.baseline_account_circle_24)
                                        .error(R.drawable.baseline_account_circle_24)
                                        .centerCrop()
                                        .into(holder.imgFoto);
                            } else {
                                holder.imgFoto.setImageResource(R.drawable.baseline_account_circle_24);
                            }
                            item.setTelefonoTaxista(telefono); // Guarda para pasar por intent

                        } else {
                            holder.imgFoto.setImageResource(R.drawable.baseline_account_circle_24);
                        }


                    })
                    .addOnFailureListener(e -> {
                        holder.imgFoto.setImageResource(R.drawable.baseline_account_circle_24);
                    });
        } else {
            // ID inválido → carga imagen por defecto
            holder.imgFoto.setImageResource(R.drawable.baseline_account_circle_24);
        }






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
                //intent.putExtra("tiempo", "2 min.");
                //intent.putExtra("distancia", "0.85 km");
                // ⬅⬅ Agregar estas dos líneas
                intent.putExtra("latitud", item.getLatitud());
                intent.putExtra("longitud", item.getLongitud());
                intent.putExtra("latCliente", item.getLatCliente()); // ubicación del hotel
                intent.putExtra("lonCliente", item.getLonCliente());
                intent.putExtra("fotoUrl", item.getUrlFotoTaxista());
                intent.putExtra("telefono", item.getTelefonoTaxista());


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
