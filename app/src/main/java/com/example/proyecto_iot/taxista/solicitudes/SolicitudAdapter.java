package com.example.proyecto_iot.taxista.solicitudes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.taxista.perfil.Notificacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SolicitudAdapter extends RecyclerView.Adapter<SolicitudAdapter.ViewHolder> {

    private List<Solicitud> lista;
    private static final String CANAL_ID = "canal_viajes";
    private static final String FILE_NOTIFICACIONES = "notificaciones.json";

    public SolicitudAdapter(List<Solicitud> lista) {
        this.lista = lista;
    }

    @NonNull
    @Override
    public SolicitudAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitud, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Solicitud solicitud = lista.get(position);

        holder.nombre.setText(solicitud.nombre);
        holder.telefono.setText(solicitud.telefono);
        holder.viajes.setText(solicitud.viajes + " viajes");
        holder.tiempoDistancia.setText(solicitud.tiempoDistancia);
        holder.origen.setText(solicitud.origen);
        holder.distrito.setText(solicitud.distrito);
        holder.destino.setText(solicitud.destino);

        // Cargar imagen con Glide si la URL es válida, sino usar imagen local
        if (solicitud.urlFotoPerfil != null && !solicitud.urlFotoPerfil.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(solicitud.urlFotoPerfil)
                    .placeholder(R.drawable.usuario_10)
                    .error(R.drawable.usuario_10)
                    .into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.usuario_10);
        }

        if ("pendiente".equals(solicitud.estado)) {
            holder.btnAceptar.setVisibility(View.VISIBLE);
            holder.btnRechazar.setVisibility(View.VISIBLE);
            holder.btnAceptar.setText("Aceptar");
        } else if ("aceptado".equals(solicitud.estado)) {
            holder.btnAceptar.setVisibility(View.VISIBLE);
            holder.btnRechazar.setVisibility(View.GONE);
            holder.btnAceptar.setText("Ver");
        }

        holder.btnAceptar.setOnClickListener(v -> {
            Context context = v.getContext();

            // 🔁 ACTUALIZAR EN FIRESTORE
            FirebaseFirestore.getInstance()
                    .collection("servicios_taxi")
                    .document(solicitud.idDocumento)
                    .update(
                            "estado", "aceptado",
                            "idTaxista", FirebaseAuth.getInstance().getCurrentUser().getUid()
                    )
                    .addOnSuccessListener(aVoid -> {
                        String mensaje = "Has aceptado la solicitud de " + solicitud.nombre;

                        lanzarNotificacion(context, mensaje,
                                solicitud.nombre,
                                solicitud.telefono,
                                solicitud.viajes + " viajes",
                                solicitud.origen,
                                R.drawable.usuario_10, // ya no se usa directamente si tienes la URL
                                solicitud.latDestino,
                                solicitud.lngDestino);

                        Notificacion notificacion = new Notificacion(
                                mensaje,
                                obtenerHoraActual(),
                                R.drawable.ic_taxi
                        );
                        guardarNotificacionEnStorage(context, notificacion);

                        Intent intent = new Intent(context, MapsActivity.class);
                        intent.putExtra("nombre", solicitud.nombre);
                        intent.putExtra("telefono", solicitud.telefono);
                        intent.putExtra("viajes", solicitud.viajes + " viajes");
                        intent.putExtra("hotel", solicitud.origen);
                        intent.putExtra("imagenPerfilUrl", solicitud.urlFotoPerfil);
                        intent.putExtra("latDestino", solicitud.latDestino);
                        intent.putExtra("lngDestino", solicitud.lngDestino);
                        intent.putExtra("idServicio", solicitud.idDocumento);



                        context.startActivity(intent);
                    })
                    .addOnFailureListener(e -> {
                        // Manejo de error
                        e.printStackTrace();
                    });
        });

        holder.btnRechazar.setOnClickListener(v -> {
            // Lógica de rechazo si deseas
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre, telefono, viajes, tiempoDistancia;
        TextView origen, distrito, destino;
        Button btnAceptar, btnRechazar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagePerfil);
            nombre = itemView.findViewById(R.id.tvNombre);
            telefono = itemView.findViewById(R.id.tvTelefono);
            viajes = itemView.findViewById(R.id.tvViajes);
            tiempoDistancia = itemView.findViewById(R.id.tvTiempoDistancia);
            origen = itemView.findViewById(R.id.tvOrigen);
            distrito = itemView.findViewById(R.id.tvDistrito);
            destino = itemView.findViewById(R.id.tvDestino);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }

    // Notificaciones
    private void crearCanalNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null && manager.getNotificationChannel(CANAL_ID) == null) {
                NotificationChannel canal = new NotificationChannel(
                        CANAL_ID,
                        "Notificaciones de Viajes",
                        NotificationManager.IMPORTANCE_DEFAULT);
                canal.setDescription("Notificaciones sobre viajes aceptados");
                manager.createNotificationChannel(canal);
            }
        }
    }

    private void lanzarNotificacion(Context context, String mensaje,
                                    String nombre, String telefono, String viajes,
                                    String hotel, int imagenPerfil,
                                    double latDestino, double lngDestino) {
        crearCanalNotificacion(context);

        Intent intent = new Intent(context, MapsActivity.class);
        intent.putExtra("nombre", nombre);
        intent.putExtra("telefono", telefono);
        intent.putExtra("viajes", viajes);
        intent.putExtra("hotel", hotel);
        intent.putExtra("imagenPerfil", imagenPerfil);
        intent.putExtra("latDestino", latDestino);
        intent.putExtra("lngDestino", lngDestino);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CANAL_ID)
                .setSmallIcon(R.drawable.ic_taxi)
                .setContentTitle("Viaje iniciado")
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                == android.content.pm.PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1001, builder.build());
        }
    }

    private void guardarNotificacionEnStorage(Context context, Notificacion notificacion) {
        List<Notificacion> lista = leerNotificacionesDesdeStorage(context);
        lista.add(notificacion);

        Gson gson = new Gson();
        String json = gson.toJson(lista);

        try (FileOutputStream fos = context.openFileOutput(FILE_NOTIFICACIONES, Context.MODE_PRIVATE);
             FileWriter writer = new FileWriter(fos.getFD())) {
            writer.write(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Notificacion> leerNotificacionesDesdeStorage(Context context) {
        List<Notificacion> lista = new ArrayList<>();
        try (FileInputStream fis = context.openFileInput(FILE_NOTIFICACIONES);
             InputStreamReader isr = new InputStreamReader(fis);
             BufferedReader br = new BufferedReader(isr)) {
            StringBuilder sb = new StringBuilder();
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea);
            }
            Type listType = new TypeToken<List<Notificacion>>() {}.getType();
            lista = new Gson().fromJson(sb.toString(), listType);
        } catch (Exception e) {
            // Ignora si el archivo no existe
        }
        return lista;
    }

    private String obtenerHoraActual() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }
}
