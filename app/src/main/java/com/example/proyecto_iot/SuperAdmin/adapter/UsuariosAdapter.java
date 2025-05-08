package com.example.proyecto_iot.SuperAdmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.UsuariosDataStore;
import com.example.proyecto_iot.SuperAdmin.domain.UsuariosDomain;
import com.example.proyecto_iot.SuperAdmin.fragmentos.FragmentPerfilUsuariosSuperadmin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

public class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.ViewHolder> {

    private final List<UsuariosDomain> usuariosList;

    public UsuariosAdapter(List<UsuariosDomain> usuariosList) {
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
        UsuariosDomain usuario = usuariosList.get(position);

        holder.nombre.setText(usuario.getNombre());
        holder.numero.setText("Teléfono: " + usuario.getNumeroTelefono());
        holder.ubicacion.setText("Dirección: " + usuario.getDireccion());
        holder.hotel.setText("Habitaciones: " + usuario.getHabitacionesRegistradas());
        holder.rol.setText("Rol: " + usuario.getRol());
        holder.estado.setText("Estado cuenta: " + usuario.getEstadoCuenta());

        Picasso.get().load(usuario.getImagenPerfil()).into(holder.imagen);

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

            tvNombre.setText(usuario.getNombre());
            tvNumero.setText(usuario.getNumeroTelefono());
            Picasso.get().load(usuario.getImagenPerfil()).into(ivFoto);

            // Cambiar texto e ícono del botón según el estado del usuario
            if (usuario.getEstadoCuenta().equalsIgnoreCase("activo")) {
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
                    UsuariosDataStore.usuariosList.remove(positionToRemove); // elimina del origen original
                    usuariosList.remove(positionToRemove);                   // elimina del adapter
                    notifyItemRemoved(positionToRemove);
                    notifyItemRangeChanged(positionToRemove, usuariosList.size());

                }

                Toast.makeText(v.getContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show();

            });

            btnActivar.setOnClickListener(view -> {
                dialog.dismiss();
                if (usuario.getEstadoCuenta().equalsIgnoreCase("activo")) {
                    usuario.setEstadoCuenta("suspendido");
                    Toast.makeText(v.getContext(), usuario.getNombre() + " desactivado", Toast.LENGTH_SHORT).show();

                } else {
                    usuario.setEstadoCuenta("activo");
                    Toast.makeText(v.getContext(), usuario.getNombre() + " activado", Toast.LENGTH_SHORT).show();

                }
                notifyItemChanged(holder.getAdapterPosition());
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
            imagen = itemView.findViewById(R.id.imagen_list_usuarios);  // asegúrate que este ID esté en item_usuarios_superadmin.xml
            nombre = itemView.findViewById(R.id.nameAdministradores);         // idem
            numero = itemView.findViewById(R.id.numberUsuario);              // idem
            ubicacion=itemView.findViewById(R.id.ubicaUsuario);
            hotel=itemView.findViewById(R.id.hotelUsuario);
            rol=itemView.findViewById(R.id.rolUsuario);
            estado=itemView.findViewById(R.id.estadoCuentaUsuario);
            btnOpciones = itemView.findViewById(R.id.btnOpciones);

        }
    }

    //Metodo que dijimos que se crea para el buscador
    public void updateList(List<UsuariosDomain> nuevaLista) {
        usuariosList.clear();
        usuariosList.addAll(nuevaLista);
        notifyDataSetChanged();
    }

}
