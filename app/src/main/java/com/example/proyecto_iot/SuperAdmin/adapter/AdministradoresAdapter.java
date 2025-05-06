package com.example.proyecto_iot.SuperAdmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imagenAdmin;
        TextView nombreAdmin;
        TextView numeroAdmin;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenAdmin= itemView.findViewById(R.id.imagen_list_adminitradores);
            nombreAdmin= itemView.findViewById(R.id.nameAdministradores);
            numeroAdmin= itemView.findViewById(R.id.numberAdministradores);
        }
    }
}
