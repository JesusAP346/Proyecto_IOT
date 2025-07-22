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
