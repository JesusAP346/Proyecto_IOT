package com.example.proyecto_iot.SuperAdmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.LogSA;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    private List<LogSA> listaLogs;

    public LogAdapter(List<LogSA> listaLogs) {
        this.listaLogs = listaLogs;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_log, parent, false);
        return new LogViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        LogSA log = listaLogs.get(position);

        holder.tvTitulo.setText(log.getTitulo());
        holder.tvMensaje.setText(log.getMensaje());

        // Formatear fecha
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fecha = sdf.format(log.getTimestamp());

        // Usuario + fecha
        holder.tvUsuarioFecha.setText("Por el " + log.getRolEditor() + " " + log.getNombreEditor() + " • " + fecha);
        //  Aquí agregamos la lógica para cambiar el icono según el tipo de log:
        String tipoLog = log.getTipoLog() != null ? log.getTipoLog().toLowerCase() : "";

        switch (tipoLog) {
            case "solicitudes":
                holder.ivIcono.setImageResource(R.drawable.ic_request);  // ← debes tener este ícono en drawable
                break;
            case "registro de usuario":
                holder.ivIcono.setImageResource(R.drawable.ic_new_user);
                break;
            case "eliminación de usuario":
                holder.ivIcono.setImageResource(R.drawable.icon_deleteadmin);
                break;
            case "reserva de hotel":
                holder.ivIcono.setImageResource(R.drawable.ic_reserva);
                break;
            case "registro de checkout":
                holder.ivIcono.setImageResource(R.drawable.ic_checkout);
                break;
            case "pago de reserva":
                holder.ivIcono.setImageResource(R.drawable.ic_payment);
                break;
            default:
                holder.ivIcono.setImageResource(R.drawable.ic_generic_user);  // icono por defecto
                break;
        }

        // Click normal para mostrar JSON puro en popup
        holder.itemView.setOnClickListener(view -> {
            // Convertir objeto LogSA a JSON (simple)
            StringBuilder json = new StringBuilder("{\n");
            json.append("  \"idLog\": \"").append(log.getIdLog()).append("\",\n");
            json.append("  \"titulo\": \"").append(log.getTitulo()).append("\",\n");
            json.append("  \"mensaje\": \"").append(log.getMensaje()).append("\",\n");
            json.append("  \"nombreEditor\": \"").append(log.getNombreEditor()).append("\",\n");
            //json.append("  \"nombreEditado\": \"").append(log.getNombreEditado()).append("\",\n");
            json.append("  \"rolEditor\": \"").append(log.getRolEditor()).append("\",\n");
            json.append("  \"rolEditado\": \"").append(log.getRolEditado()).append("\",\n");
            //json.append("  \"idEditor\": \"").append(log.getIdEditor()).append("\",\n");
            //json.append("  \"idEditado\": \"").append(log.getIdEditado()).append("\",\n");
            json.append("  \"tipoLog\": \"").append(log.getTipoLog()).append("\",\n");
            json.append("  \"timestamp\": \"").append(sdf.format(log.getTimestamp())).append("\"\n");
            json.append("}");

            new androidx.appcompat.app.AlertDialog.Builder(view.getContext())
                    .setTitle("Detalle JSON del Log")
                    .setMessage(json.toString())
                    .setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss())
                    .show();
        });
        // Lógica de eliminación
        holder.btnEliminar.setOnClickListener(view -> {
            new androidx.appcompat.app.AlertDialog.Builder(view.getContext())
                    .setTitle("Confirmar eliminación")
                    .setMessage("¿Estás seguro de eliminar este log?")
                    .setPositiveButton("Sí, eliminar", (dialog, which) -> {

                        if (log.getIdLog() == null || log.getIdLog().isEmpty()) {
                            Toast.makeText(view.getContext(), "No se puede eliminar: ID de log faltante", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        FirebaseFirestore.getInstance()
                                .collection("logs")
                                .document(log.getIdLog())
                                .delete()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(view.getContext(), "Log eliminado correctamente", Toast.LENGTH_SHORT).show();
                                    listaLogs.remove(position);
                                    notifyItemRemoved(position);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(view.getContext(), "Error al eliminar log", Toast.LENGTH_SHORT).show();
                                });
                    })
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return listaLogs.size();
    }

    public static class LogViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitulo, tvMensaje, tvUsuarioFecha;
        ImageView ivIcono;
        ImageButton btnEliminar;
        public LogViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitulo = itemView.findViewById(R.id.tvTituloLog);
            tvMensaje = itemView.findViewById(R.id.tvMensajeLog);
            tvUsuarioFecha = itemView.findViewById(R.id.tvUsuarioLog);
            ivIcono = itemView.findViewById(R.id.ivIconoLog);
            btnEliminar = itemView.findViewById(R.id.btnEliminarLog);
        }
    }
    public void updateList(List<LogSA> nuevaLista) {
        this.listaLogs = nuevaLista;  // Asegúrate que `listaLogs` sea tu lista interna usada en onBindViewHolder
        notifyDataSetChanged();
    }

}
