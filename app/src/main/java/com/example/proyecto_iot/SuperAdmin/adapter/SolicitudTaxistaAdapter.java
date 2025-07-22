package com.example.proyecto_iot.SuperAdmin.adapter;

import android.content.Context; // Necesario para Toast y potencialmente otras operaciones
import android.content.Intent;
import android.net.Uri;
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
import com.example.proyecto_iot.dtos.LogSA;
import com.example.proyecto_iot.dtos.Usuario; // Importa tu DTO de Usuario
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class SolicitudTaxistaAdapter extends RecyclerView.Adapter<SolicitudTaxistaAdapter.ViewHolder> {

    private final List<Usuario> listaSolicitudes; // Cambiamos a tu DTO Usuario
    private final Context context; // Añadimos el contexto para los Toast
    private final FirebaseFirestore db; // Instancia de Firestore

    public SolicitudTaxistaAdapter(List<Usuario> listaSolicitudes, Context context) {
        this.listaSolicitudes = listaSolicitudes;
        this.context = context;
        this.db = FirebaseFirestore.getInstance(); // Inicializa Firestore
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitudtaxistas_superadmin, parent, false); // Asegúrate que este layout exista
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = listaSolicitudes.get(position);

        // Mapea los campos del DTO Usuario a tus TextViews
        holder.nombre.setText(usuario.getNombres() + " " + usuario.getApellidos());
        holder.numero.setText("Teléfono: " + usuario.getNumCelular());
        holder.modelo.setText("Modelo: " + (usuario.getPlacaAuto() != null && !usuario.getPlacaAuto().isEmpty() ? "Definido" : "(sin definir)")); // Asume que si tiene placa, hay modelo asociado
        holder.placa.setText("Placa: " + (usuario.getPlacaAuto() != null && !usuario.getPlacaAuto().isEmpty() ? usuario.getPlacaAuto() : "(sin definir)"));
        holder.rol.setText("Rol: Solicitud Pendiente"); // O simplemente "Pendiente" si prefieres

        // Carga la foto de perfil usando Picasso
        if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
            Picasso.get().load(usuario.getUrlFotoPerfil()).into(holder.imagen);
        } else {
            // Establece una imagen por defecto si no hay URL de perfil
            holder.imagen.setImageResource(R.drawable.ic_generic_user); // Asegúrate de tener una imagen por defecto
        }

        // --- Lógica para Aceptar Solicitud ---
        holder.btnAceptar.setOnClickListener(v -> {
            // 1. Actualizar el rol y el estado de la solicitud en Firestore
            Map<String, Object> updates = new HashMap<>();
            updates.put("idRol", "Taxista");
            updates.put("estadoSolicitudTaxista", "aprobada");
            updates.put("calificacionTaxista", "5.0");
            updates.put("numeroViajes", "0");

            db.collection("usuarios").document(usuario.getId())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Solicitud de " + usuario.getNombres() + " aceptada. Ahora es taxista.", Toast.LENGTH_SHORT).show();
                        agregarLog(usuario,"Solicitud aceptada", "Se aprobó la solicitud de " + usuario.getNombres() + " " + usuario.getApellidos(), "Solicitudes");
                        // 2. Simular envío de correo abriendo la app de correo
                        sendAcceptanceEmailIntent(usuario.getEmail(), usuario.getNombres() + " " + usuario.getApellidos());

                        // 3. Eliminar visualmente de la lista si la actualización fue exitosa
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listaSolicitudes.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al aceptar solicitud: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        // --- Lógica para Rechazar Solicitud ---
        holder.btnRechazar.setOnClickListener(v -> {
            // Actualizar el estado de la solicitud en Firestore a "rechazada"
            Map<String, Object> updates = new HashMap<>();
            updates.put("estadoSolicitudTaxista", "rechazada"); // Marca la solicitud como rechazada
            // Opcional: podrías cambiar el rol si no quieres que sigan siendo "cliente" o el rol que tuvieran
            // updates.put("idRol", "Cliente"); // O el rol por defecto si no es taxista

            db.collection("usuarios").document(usuario.getId())
                    .update(updates)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Solicitud de " + usuario.getNombres() + " rechazada.", Toast.LENGTH_SHORT).show();
                        agregarLog(usuario, "Solicitud rechazada", "Se rechazó la solicitud de " + usuario.getNombres() + " " + usuario.getApellidos(), "Solicitudes");
                        // Eliminar visualmente de la lista si la actualización fue exitosa
                        int pos = holder.getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listaSolicitudes.remove(pos);
                            notifyItemRemoved(pos);
                            // notifyItemRangeChanged(pos, listaSolicitudes.size()); // Esto no es estrictamente necesario si solo eliminas uno
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al rechazar solicitud: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre, numero, modelo, placa, rol;
        Button btnAceptar, btnRechazar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagen_list_usuarios); // ID del ImageView para la foto de perfil
            nombre = itemView.findViewById(R.id.nameTaxista); // ID para el nombre completo
            numero = itemView.findViewById(R.id.numberTaxista); // ID para el número de teléfono
            modelo = itemView.findViewById(R.id.modeloCarro); // ID para el modelo del carro (si lo muestras)
            placa = itemView.findViewById(R.id.placaCarro);     // ID para la placa del carro
            rol = itemView.findViewById(R.id.rolUsuario);       // ID para el rol/estado de la solicitud
            btnAceptar = itemView.findViewById(R.id.btnAceptar); // ID del botón Aceptar
            btnRechazar = itemView.findViewById(R.id.btnRechazar); // ID del botón Rechazar
        }
    }

    private void sendAcceptanceEmailIntent(String recipientEmail, String userName) {
        String appName = "TuNombreDeAplicacion"; // Reemplaza
        String appLink = "https://play.google.com/store/apps/details?id=tu.paquete.app"; // Reemplaza

        String subject = "¡Felicidades! Tu solicitud de taxista ha sido aprobada - " + appName;

        String body = "Estimado/a " + userName + ",\n\n" +
                "Nos complace informarte que tu solicitud para unirte a la flota de taxistas de " + appName + " ha sido APROBADA.\n\n" +
                "¡Bienvenido/a a nuestro equipo! Estamos emocionados de tenerte a bordo y estamos seguros de que contribuirás al éxito de nuestra plataforma.\n\n" +
                "Como taxista registrado, ahora puedes:\n" +
                "- Empezar a recibir solicitudes de viaje.\n" +
                "- Gestionar tu disponibilidad.\n" +
                "- Acceder a todas las herramientas de taxista en la aplicación.\n\n" +
                "Para comenzar, te invitamos a iniciar sesión en la aplicación y revisar tu perfil de taxista. Si tienes alguna pregunta, no dudes en contactar a nuestro equipo de soporte.\n\n" +
                "Gracias por ser parte de " + appName + ".\n\n" +
                "Atentamente,\n" +
                "El Equipo de " + appName + "\n\n" +
                "Puedes ir a la aplicación aquí: " + appLink;

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Solo aplicaciones de correo deben manejar esto
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            // Verificar si hay una aplicación que pueda manejar el Intent de correo
            if (emailIntent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(Intent.createChooser(emailIntent, "Enviar correo usando..."));
                Toast.makeText(context, "Se abrió la aplicación de correo para enviar la notificación.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context, "No hay una aplicación de correo instalada.", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error al intentar abrir la aplicación de correo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void agregarLog(Usuario usuario, String titulo, String mensaje, String tipoLog) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String uidEditor = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uidEditor != null) {
            db.collection("usuarios").document(uidEditor)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Usuario editor = documentSnapshot.toObject(Usuario.class);
                            String nombreEditor = editor.getNombres() + " " + editor.getApellidos();

                            LogSA log = new LogSA(
                                    null,  // idLog se asignará al guardar
                                    titulo,
                                    mensaje,
                                    nombreEditor,
                                    "Super Admin",
                                    "Cliente",  // Rol editado: Cliente (porque es quien solicitó)
                                    uidEditor,
                                    usuario.getNombres() + " " + usuario.getApellidos(),  // Nombre del cliente/taxista
                                    new Date(),
                                    tipoLog
                            );

                            // Guardar con ID generado
                            DocumentReference logRef = db.collection("logs").document();
                            String idLogGenerado = logRef.getId();
                            log.setIdLog(idLogGenerado);

                            logRef.set(log);
                        }
                    });
        }
    }

}