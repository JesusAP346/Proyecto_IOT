package com.example.proyecto_iot.SuperAdmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;

import java.util.List;

public class UsuarioHistorialAdapter extends RecyclerView.Adapter<UsuarioHistorialAdapter.ViewHolder> {

    private List<Usuario> listaUsuarios;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Usuario usuario);
    }

    public UsuarioHistorialAdapter(List<Usuario> listaUsuarios, OnItemClickListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuarios_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);

        String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();
        holder.nombre.setText(nombreCompleto);

        String rolStr = usuario.getIdRol();
        holder.rol.setText(rolStr);

        String estado = usuario.estadoCuenta ? "Activo" : "Suspendido";
        holder.estado.setText(estado);

        if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(usuario.getUrlFotoPerfil())
                    .placeholder(R.drawable.ic_user)
                    .into(holder.foto);
        } else {
            holder.foto.setImageResource(R.drawable.ic_user);
        }

        holder.itemView.setOnClickListener(v -> listener.onItemClick(usuario));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public void updateList(List<Usuario> nuevaLista) {
        this.listaUsuarios = nuevaLista;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nombre, rol, estado;
        ImageView foto;

        ViewHolder(View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.nameAdministradores);
            rol = itemView.findViewById(R.id.rolUsuario);
            estado = itemView.findViewById(R.id.estadoCuentaUsuario);
            foto = itemView.findViewById(R.id.imagen_list_usuarios);
        }
    }


}
