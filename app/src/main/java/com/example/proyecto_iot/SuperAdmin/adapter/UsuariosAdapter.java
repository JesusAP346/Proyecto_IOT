package com.example.proyecto_iot.SuperAdmin.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.database.AppDatabase;
import com.example.proyecto_iot.SuperAdmin.database.UsuariosEntity;
import com.example.proyecto_iot.SuperAdmin.fragmentos.FragmentPerfilUsuariosSuperadmin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder> {

    private final List<UsuariosEntity> usuariosList;
    private final Context context;
    private final AppDatabase db;
    public interface OnUsuarioActualizadoListener {
        void onUsuarioActualizado();
    }

    private OnUsuarioActualizadoListener listener;

    public void setOnUsuarioActualizadoListener(OnUsuarioActualizadoListener listener) {
        this.listener = listener;
    }

    public UsuariosAdapter(List<UsuariosEntity> usuariosList, Context context) {
        this.usuariosList = usuariosList;
        this.context = context;
        this.db = Room.databaseBuilder(context, AppDatabase.class, "usuarios-db")
                .allowMainThreadQueries() // para pruebas
                .build();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuarios_superadmin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsuariosEntity usuario = usuariosList.get(position);

        holder.nombre.setText(usuario.nombre);
        holder.numero.setText("Teléfono: " + usuario.numeroTelefono);
        holder.ubicacion.setText("Dirección: " + usuario.direccion);
        holder.hotel.setText("Habitaciones: " + usuario.habitacionesRegistradas);
        holder.rol.setText("Rol: " + usuario.rol);
        holder.estado.setText("Estado cuenta: " + usuario.estadoCuenta);

        Picasso.get().load(usuario.imagenPerfil).into(holder.imagen);

        holder.itemView.setOnClickListener(view -> {
            FragmentPerfilUsuariosSuperadmin fragment = FragmentPerfilUsuariosSuperadmin.newInstance(usuario);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        holder.btnOpciones.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(v.getContext());
            View sheetView = LayoutInflater.from(v.getContext())
                    .inflate(R.layout.bottom_sheet_usuarios_administradores, null);

            ImageView ivFoto = sheetView.findViewById(R.id.ivFotoAdmin);
            TextView tvNombre = sheetView.findViewById(R.id.tvNombreAdmin);
            TextView tvNumero = sheetView.findViewById(R.id.tvNumeroAdmin);
            TextView btnEditar = sheetView.findViewById(R.id.btnEditar);
            TextView btnEliminar = sheetView.findViewById(R.id.btnEliminar);
            TextView btnActivar = sheetView.findViewById(R.id.btnActivar);

            tvNombre.setText(usuario.nombre);
            tvNumero.setText(usuario.numeroTelefono);
            Picasso.get().load(usuario.imagenPerfil).into(ivFoto);

            if (usuario.estadoCuenta.equalsIgnoreCase("activo")) {
                btnActivar.setText("Desactivar");
                btnActivar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_desactivo, 0, 0, 0);
            } else {
                btnActivar.setText("Activar");
                btnActivar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_activo, 0, 0, 0);
            }

            btnEditar.setOnClickListener(view -> {
                FragmentPerfilUsuariosSuperadmin fragment = FragmentPerfilUsuariosSuperadmin.newInstance(usuario);
                dialog.dismiss();
                ((AppCompatActivity) view.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .addToBackStack(null)
                        .commit();
            });

            btnEliminar.setOnClickListener(view -> {
                dialog.dismiss();
                int positionToRemove = holder.getAdapterPosition();
                if (positionToRemove != RecyclerView.NO_POSITION) {
                    db.usuariosDao().deleteByDni(usuario.dni);
                    usuariosList.remove(positionToRemove);
                    notifyItemRemoved(positionToRemove);
                    notifyItemRangeChanged(positionToRemove, usuariosList.size());
                    mostrarNotificacion("Se eliminó a " + usuario.nombre);
                    Toast.makeText(v.getContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();
                }
            });

            btnActivar.setOnClickListener(view -> {
                dialog.dismiss();
                String nuevoEstado = usuario.estadoCuenta.equalsIgnoreCase("activo") ? "suspendido" : "activo";
                usuario.estadoCuenta = nuevoEstado;

                db.usuariosDao().updateEstado(usuario.dni, nuevoEstado);
                notifyItemChanged(holder.getAdapterPosition());

                Toast.makeText(v.getContext(), usuario.nombre + " " + (nuevoEstado.equals("activo") ? "activado" : "desactivado"), Toast.LENGTH_SHORT).show();

                // Notifica al fragmento para que se reaplique el filtro
                if (listener != null) {
                    listener.onUsuarioActualizado();
                }
            });


            dialog.setContentView(sheetView);
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return usuariosList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre;
        TextView numero;
        ImageButton btnOpciones;
        TextView ubicacion;
        TextView hotel;
        TextView rol;
        TextView estado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagen_list_usuarios);
            nombre = itemView.findViewById(R.id.nameAdministradores);
            numero = itemView.findViewById(R.id.numberUsuario);
            ubicacion = itemView.findViewById(R.id.ubicaUsuario);
            hotel = itemView.findViewById(R.id.hotelUsuario);
            rol = itemView.findViewById(R.id.rolUsuario);
            estado = itemView.findViewById(R.id.estadoCuentaUsuario);
            btnOpciones = itemView.findViewById(R.id.btnOpciones);
        }
    }

    public void updateList(List<UsuariosEntity> nuevaLista) {
        usuariosList.clear();
        usuariosList.addAll(nuevaLista);
        notifyDataSetChanged();
    }

    private void mostrarNotificacion(String mensaje) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "canal_usuarios")
                    .setSmallIcon(R.drawable.icon_deleteadmin) // asegúrate de que el ícono exista
                    .setContentTitle("Usuario eliminado")
                    .setContentText(mensaje)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
            notificationManager.notify((int) System.currentTimeMillis(), builder.build());
        }
    }


}
