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
import com.example.proyecto_iot.SuperAdmin.TaxistasDataStore;
import com.example.proyecto_iot.SuperAdmin.domain.TaxistaDomain;
import com.example.proyecto_iot.SuperAdmin.fragmentos.FragmentPerfilTaxistasSuperadmin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.util.List;

public class TaxistasAdapter extends RecyclerView.Adapter<TaxistasAdapter.ViewHolder> {

    private final List<TaxistaDomain> taxistasList;

    public TaxistasAdapter(List<TaxistaDomain> taxistasList) {
        this.taxistasList = taxistasList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_taxistas_superadmin, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaxistaDomain taxista = taxistasList.get(position);

        holder.nombre.setText(taxista.getNombre());
        holder.numero.setText("Teléfono: " + taxista.getNumeroTelefono());
        holder.modelo.setText("Modelo: " + taxista.getModeloAuto());
        holder.placa.setText("Placa: " + taxista.getPlacaAuto());
        holder.rol.setText("Rol: Taxista");

        Picasso.get().load(taxista.getImagenPerfil()).into(holder.imagen);

        holder.btnOpciones.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(v.getContext());
            View sheetView = LayoutInflater.from(v.getContext())
                    .inflate(R.layout.bottom_sheet_taxistas_superadmin, null);

            ImageView ivFoto = sheetView.findViewById(R.id.ivFotoAdmin);
            TextView tvNombre = sheetView.findViewById(R.id.tvNombreAdmin);
            TextView tvNumero = sheetView.findViewById(R.id.tvNumeroAdmin);
            TextView btnEditar = sheetView.findViewById(R.id.btnEditar);
            TextView btnEliminar = sheetView.findViewById(R.id.btnEliminar);

            tvNombre.setText(taxista.getNombre());
            tvNumero.setText(taxista.getNumeroTelefono());
            Picasso.get().load(taxista.getImagenPerfil()).into(ivFoto);

            btnEditar.setOnClickListener(view -> {
                dialog.dismiss();
                Toast.makeText(v.getContext(), "Funcionalidad aún no implementada", Toast.LENGTH_SHORT).show();
            });

            btnEliminar.setOnClickListener(view -> {
                dialog.dismiss();
                int pos = holder.getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    TaxistasDataStore.taxistasList.remove(pos);
                    taxistasList.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, taxistasList.size());
                }
                Toast.makeText(v.getContext(), "Taxista eliminado", Toast.LENGTH_SHORT).show();
            });

            dialog.setContentView(sheetView);
            dialog.show();
        });

        holder.itemView.setOnClickListener(view -> {
            FragmentPerfilTaxistasSuperadmin fragment = FragmentPerfilTaxistasSuperadmin.newInstance(taxista);

            ((AppCompatActivity) view.getContext()).getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frame_layout, fragment)
                    .addToBackStack(null)
                    .commit();
        });

    }

    @Override
    public int getItemCount() {
        return taxistasList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagen;
        TextView nombre, numero, modelo, placa, rol;
        ImageButton btnOpciones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagen_list_usuarios);
            nombre = itemView.findViewById(R.id.nameTaxista);
            numero = itemView.findViewById(R.id.numberTaxista);
            modelo = itemView.findViewById(R.id.modeloCarro);
            placa = itemView.findViewById(R.id.placaCarro);
            rol = itemView.findViewById(R.id.rolUsuario);
            btnOpciones = itemView.findViewById(R.id.btnOpciones);
        }
    }
}
