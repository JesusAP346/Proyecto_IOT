package com.example.proyecto_iot.SuperAdmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.example.proyecto_iot.SuperAdmin.fragmentos.FragmentPerfilUsuariosSuperadmin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder> {

    private List<Usuario> usuariosList;

    public UsuariosAdapter(List<Usuario> usuariosList) {
        this.usuariosList = usuariosList;
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
        Usuario usuario = usuariosList.get(position);

        holder.nombre.setText(usuario.getNombres() + " " + usuario.getApellidos());
        holder.numero.setText("Teléfono: " + usuario.getNumCelular());
        holder.direccion.setText("Dirección: " + usuario.getDireccion());
        holder.habitaciones.setText("Habitaciones: " + usuario.getIdHotel()); // Asumiendo idHotel como habitaciones
        holder.estadoCuenta.setText("Estado cuenta: " + (usuario.isEstadoCuenta() ? "Activo" : "Inactivo"));

        if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
            Picasso.get().load(usuario.getUrlFotoPerfil()).into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.ic_generic_user);
        }

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
            TextView btnEliminar = sheetView.findViewById(R.id.btnEliminar);
            TextView btnEditar = sheetView.findViewById(R.id.btnEditar);
            TextView btnActivar = sheetView.findViewById(R.id.btnActivar);

            tvNombre.setText(usuario.getNombres() + " " + usuario.getApellidos());
            tvNumero.setText(usuario.getNumCelular());

            if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
                Picasso.get().load(usuario.getUrlFotoPerfil()).into(ivFoto);
            } else {
                ivFoto.setImageResource(R.drawable.ic_generic_user);
            }

            btnEditar.setOnClickListener(view -> {
                dialog.dismiss();
                FragmentPerfilUsuariosSuperadmin fragment = FragmentPerfilUsuariosSuperadmin.newInstance(usuario);
                ((AppCompatActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .addToBackStack(null)
                        .commit();
            });

            btnEliminar.setOnClickListener(view -> {
                dialog.dismiss();

                new AlertDialog.Builder(v.getContext())
                        .setTitle("Confirmar eliminación")
                        .setMessage("¿Estás seguro de eliminar a " + usuario.getNombres() + " " + usuario.getApellidos() + "?")
                        .setPositiveButton("Sí, eliminar", (dialogInterface, i) -> {
                            if (usuario.getId() != null && !usuario.getId().isEmpty()) {
                                FirebaseFirestore.getInstance().collection("usuarios").document(usuario.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(v.getContext(), "Usuario eliminado correctamente.", Toast.LENGTH_SHORT).show();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(v.getContext(), "Error al eliminar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(v.getContext(), "ID no encontrado para eliminar.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss())
                        .show();
            });

            btnActivar.setOnClickListener(view -> {
                dialog.dismiss();
                boolean nuevoEstado = !usuario.isEstadoCuenta();
                FirebaseFirestore.getInstance().collection("usuarios")
                        .document(usuario.getId())
                        .update("estadoCuenta", nuevoEstado)
                        .addOnSuccessListener(aVoid -> {
                            String msg = nuevoEstado ? "Usuario activado." : "Usuario desactivado.";
                            Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
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
        TextView direccion;
        TextView habitaciones;
        TextView estadoCuenta;
        ImageButton btnOpciones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagen_list_usuarios);
            nombre = itemView.findViewById(R.id.nameAdministradores);
            numero = itemView.findViewById(R.id.numberUsuario);
            direccion = itemView.findViewById(R.id.ubicaUsuario);
            habitaciones = itemView.findViewById(R.id.hotelUsuario);
            estadoCuenta = itemView.findViewById(R.id.estadoCuentaUsuario);
            btnOpciones = itemView.findViewById(R.id.btnOpciones);
        }
    }

    public void updateList(List<Usuario> nuevaLista) {
        usuariosList.clear();
        usuariosList.addAll(nuevaLista);
        notifyDataSetChanged();
    }
}
