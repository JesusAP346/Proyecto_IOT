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
import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;
import com.example.proyecto_iot.SuperAdmin.fragmentos.FragmentGestionAdministradorSuperadmin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdministradoresAdapter extends RecyclerView.Adapter<AdministradoresAdapter.ViewHolder> {

    List<AdministradoresDomain> items;
    public AdministradoresAdapter(List<AdministradoresDomain> items){
        this.items = items;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_administradores,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {




        holder.nombreAdmin.setText(items.get(position).getNombreAdmin());
        holder.numeroAdmin.setText(items.get(position).getNumeroAdmin());
        Picasso.get().load(items.get(position).getImagenAdmin()).into(holder.imagenAdmin);

        AdministradoresDomain admin = items.get(position);


        //Ver perfil
        holder.itemView.setOnClickListener(view -> {
            FragmentGestionAdministradorSuperadmin fragment = FragmentGestionAdministradorSuperadmin.newInstance(admin, false, position);
            ((AppCompatActivity) view.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });



        //Ver el BottonSheet
        holder.btnOpciones.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(v.getContext());
            View sheetView = LayoutInflater.from(v.getContext())
                    .inflate(R.layout.bottom_sheet_superadmin_administradores, null);

            ImageView ivFoto = sheetView.findViewById(R.id.ivFotoAdmin);
            TextView tvNombre = sheetView.findViewById(R.id.tvNombreAdmin);
            TextView tvNumero = sheetView.findViewById(R.id.tvNumeroAdmin);
            TextView btnEditar = sheetView.findViewById(R.id.btnEditar);
            TextView btnEliminar = sheetView.findViewById(R.id.btnEliminar);

            tvNombre.setText(admin.getNombreAdmin());
            tvNumero.setText(admin.getNumeroAdmin());
            Picasso.get().load(admin.getImagenAdmin()).into(ivFoto);

            btnEditar.setOnClickListener(view -> {
                dialog.dismiss();
                FragmentGestionAdministradorSuperadmin fragment = FragmentGestionAdministradorSuperadmin.newInstance(admin, true, position);
                ((AppCompatActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .addToBackStack(null)
                        .commit();

            });

            btnEliminar.setOnClickListener(view -> {
                dialog.dismiss();

                int positionToRemove = holder.getAdapterPosition();
                if (positionToRemove != RecyclerView.NO_POSITION) {
                    items.remove(positionToRemove);
                    notifyItemRemoved(positionToRemove);
                    notifyItemRangeChanged(positionToRemove, items.size()); // opcional, para animaciones suaves
                }

                Toast.makeText(v.getContext(), "Administrador eliminado", Toast.LENGTH_SHORT).show();

            });

            dialog.setContentView(sheetView);
            dialog.show();
        });


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imagenAdmin;
        TextView nombreAdmin;
        TextView numeroAdmin;
        ImageButton btnOpciones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenAdmin= itemView.findViewById(R.id.imagen_list_adminitradores);
            nombreAdmin= itemView.findViewById(R.id.nameAdministradores);
            numeroAdmin= itemView.findViewById(R.id.numberAdministradores);
            btnOpciones = itemView.findViewById(R.id.btnOpciones);

        }
    }
}
