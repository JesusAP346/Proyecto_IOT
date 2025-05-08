package com.example.proyecto_iot.SuperAdmin.adapter;

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
import com.example.proyecto_iot.SuperAdmin.TaxistasDataStore;
import com.example.proyecto_iot.SuperAdmin.domain.TaxistaDomain;
import com.example.proyecto_iot.SuperAdmin.domain.UsuariosDomain;
import com.squareup.picasso.Picasso;

import java.util.List;

public class SolicitudTaxistaAdapter extends RecyclerView.Adapter<SolicitudTaxistaAdapter.ViewHolder> {

    private final List<UsuariosDomain> listaSolicitudes;

    public SolicitudTaxistaAdapter(List<UsuariosDomain> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_solicitudtaxistas_superadmin, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UsuariosDomain usuario = listaSolicitudes.get(position);

        holder.nombre.setText(usuario.getNombre());
        holder.numero.setText("Teléfono: " + usuario.getNumeroTelefono());
        holder.modelo.setText("Modelo: (sin definir)");
        holder.placa.setText("Placa: (sin definir)");
        holder.rol.setText("Rol: Pendiente");

        Picasso.get().load(usuario.getImagenPerfil()).into(holder.imagen);

        holder.btnAceptar.setOnClickListener(v -> {
            // Convertir a taxista y agregarlo al DataStore
            TaxistaDomain nuevo = new TaxistaDomain(
                    usuario.getNombre(),
                    usuario.getNumeroTelefono(),
                    usuario.getImagenPerfil(),
                    usuario.getCorreo(),
                    "Activo",
                    usuario.getTotalViajes(),
                    Double.parseDouble(usuario.getCalificacion()),
                    "Color por definir",
                    "Modelo por definir",
                    "Placa por definir",
                    "https://cdn.pixabay.com/photo/2012/05/29/00/43/car-49278_1280.jpg"
            );
            TaxistasDataStore.taxistasList.add(nuevo);

            Toast.makeText(v.getContext(), "Solicitud aceptada: " + usuario.getNombre(), Toast.LENGTH_SHORT).show();

            // Eliminar visualmente
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listaSolicitudes.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, listaSolicitudes.size());
            }
        });

        holder.btnRechazar.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Solicitud rechazada: " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                listaSolicitudes.remove(pos);
                notifyItemRemoved(pos);
                notifyItemRangeChanged(pos, listaSolicitudes.size());
            }
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
            imagen = itemView.findViewById(R.id.imagen_list_usuarios);
            nombre = itemView.findViewById(R.id.nameTaxista);
            numero = itemView.findViewById(R.id.numberTaxista);
            modelo = itemView.findViewById(R.id.modeloCarro);
            placa = itemView.findViewById(R.id.placaCarro);
            rol = itemView.findViewById(R.id.rolUsuario);
            btnAceptar = itemView.findViewById(R.id.btnAceptar);
            btnRechazar = itemView.findViewById(R.id.btnRechazar);
        }
    }
}
