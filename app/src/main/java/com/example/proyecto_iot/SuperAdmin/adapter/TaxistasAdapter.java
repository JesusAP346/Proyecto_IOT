package com.example.proyecto_iot.SuperAdmin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog; // Necesario para el diálogo de confirmación
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario; // ¡Importante: Usar tu DTO Usuario!
// import com.example.proyecto_iot.SuperAdmin.TaxistasDataStore; // Ya no se usará, la eliminación va a Firebase
// import com.example.proyecto_iot.SuperAdmin.domain.TaxistaDomain; // ¡Remover esta importación!
import com.example.proyecto_iot.SuperAdmin.fragmentos.FragmentPerfilTaxistasSuperadmin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore; // Importar FirebaseFirestore
import com.squareup.picasso.Picasso;

import java.util.List;

public class TaxistasAdapter extends RecyclerView.Adapter<TaxistasAdapter.ViewHolder> {

    // Cambiado de TaxistaDomain a Usuario
    private final List<Usuario> taxistasList;

    // Constructor ahora toma List<Usuario>
    public TaxistasAdapter(List<Usuario> taxistasList) {
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
        // Cambiado de TaxistaDomain a Usuario
        Usuario taxista = taxistasList.get(position);

        // --- ADAPTACIÓN DE ATRIBUTOS DEL DTO USUARIO ---
        holder.nombre.setText(taxista.getNombres() + " " + taxista.getApellidos());
        holder.numero.setText("Teléfono: " + taxista.getNumCelular());
        holder.rol.setText("Modelo del auto: " + taxista.getModeloAuto());
        // Mapeo los campos del DTO Usuario a los TextViews existentes
        holder.modelo.setText("Placa del auto: " + taxista.getPlacaAuto()); // Antes era Modelo, ahora muestra Placa

        holder.placa.setText("Estado cuenta: " + (taxista.isEstadoCuenta() ? "Activo" : "Inactivo"));
        // Esto puede ser estático o venir de taxista.getIdRol()

        // Cargar imagen de perfil con Picasso
        // Asegúrate de que urlFotoPerfil es el atributo correcto en tu DTO Usuario
        if (taxista.getUrlFotoPerfil() != null && !taxista.getUrlFotoPerfil().isEmpty()) {
            Picasso.get().load(taxista.getUrlFotoPerfil()).into(holder.imagen);
        } else {
            holder.imagen.setImageResource(R.drawable.ic_generic_user); // Imagen por defecto si no hay URL
        }
        // --- FIN ADAPTACIÓN DE ATRIBUTOS ---

        holder.btnOpciones.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(v.getContext());
            View sheetView = LayoutInflater.from(v.getContext())
                    .inflate(R.layout.bottom_sheet_taxistas_superadmin, null);

            // Revisa si estos IDs son correctos para tu bottom_sheet_taxistas_superadmin.xml
            ImageView ivFoto = sheetView.findViewById(R.id.ivFotoAdmin);
            TextView tvNombre = sheetView.findViewById(R.id.tvNombreAdmin);
            TextView tvNumero = sheetView.findViewById(R.id.tvNumeroAdmin);
            TextView btnEditar = sheetView.findViewById(R.id.btnEditar);
            TextView btnEliminar = sheetView.findViewById(R.id.btnEliminar);
            TextView btnActivar = sheetView.findViewById(R.id.btnActivar);

            // Rellenar vistas del BottomSheet con datos del Usuario
            tvNombre.setText(taxista.getNombres() + " " + taxista.getApellidos());
            tvNumero.setText(taxista.getNumCelular());
            if (taxista.getUrlFotoPerfil() != null && !taxista.getUrlFotoPerfil().isEmpty()) {
                Picasso.get().load(taxista.getUrlFotoPerfil()).into(ivFoto);
            } else {
                ivFoto.setImageResource(R.drawable.ic_generic_user); // Imagen por defecto
            }

            btnEditar.setOnClickListener(view -> {
                dialog.dismiss();
                // ASUMIDO: FragmentPerfilTaxistasSuperadmin.newInstance() ahora acepta un objeto Usuario
                FragmentPerfilTaxistasSuperadmin fragment = FragmentPerfilTaxistasSuperadmin.newInstance(taxista);

                ((AppCompatActivity) view.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .addToBackStack(null)
                        .commit();
            });

            // Al preparar el BottomSheet:
            if (taxista.isEstadoCuenta()) {
                btnActivar.setText("Desactivar");
                btnActivar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_desactivo, 0, 0, 0);
            } else {
                btnActivar.setText("Activar");
                btnActivar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_activar, 0, 0, 0);
            }

            btnActivar.setOnClickListener(view -> {
                dialog.dismiss();
                boolean nuevoEstado = !taxista.isEstadoCuenta();
                FirebaseFirestore.getInstance().collection("usuarios")
                        .document(taxista.getId())
                        .update("estadoCuenta", nuevoEstado)
                        .addOnSuccessListener(aVoid -> {
                            String msg = nuevoEstado ? "Usuario activado." : "Usuario suspendido.";

                            Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                            taxista.setEstadoCuenta(nuevoEstado);
                            notifyDataSetChanged();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(v.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            });

            btnEliminar.setOnClickListener(view -> {
                dialog.dismiss(); // Cierra el BottomSheetDialog

                // --- INICIO: AlertDialog de confirmación ---
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Confirmar eliminación")
                        .setMessage("¿Estás seguro de que quieres eliminar al taxista " + taxista.getNombres() + " " + taxista.getApellidos() + "? Esta acción no se puede deshacer.")
                        .setPositiveButton("Sí, eliminar", (dialogInterface, i) -> {
                            // Lógica de eliminación de Firebase Firestore (igual que en AdministradoresAdapter)
                            if (taxista.getId() != null && !taxista.getId().isEmpty()) {
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("usuarios").document(taxista.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(v.getContext(), "Taxista " + taxista.getNombres() + " " + taxista.getApellidos() + " eliminado de Firestore.", Toast.LENGTH_LONG).show();
                                            // NO necesitas modificar 'taxistasList' o llamar a notifyItemRemoved().
                                            // El SnapshotListener en tu fragment_taxistas_superadmin se encargará de esto automáticamente.
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(v.getContext(), "Error al eliminar taxista: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(v.getContext(), "ID del taxista no encontrado para eliminar.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancelar", (dialogInterface, i) -> {
                            // No hace nada, simplemente cierra el diálogo
                            dialogInterface.dismiss();
                        })
                        .show();
                // --- FIN: AlertDialog de confirmación ---
            });

            dialog.setContentView(sheetView);
            dialog.show();
        });

        holder.itemView.setOnClickListener(view -> {
            // ASUMIDO: FragmentPerfilTaxistasSuperadmin.newInstance() ahora acepta un objeto Usuario
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
        TextView nombre, numero, modelo, placa, rol; // 'modelo' y 'placa' se reusarán para otros atributos
        ImageButton btnOpciones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // Estos IDs deben coincidir con tu item_taxistas_superadmin.xml
            imagen = itemView.findViewById(R.id.imagen_list_usuarios);
            nombre = itemView.findViewById(R.id.nameTaxista);
            numero = itemView.findViewById(R.id.numberTaxista);
            modelo = itemView.findViewById(R.id.modeloCarro); // Este mostrará la placa del auto
            placa = itemView.findViewById(R.id.placaCarro);   // Este mostrará el estado de la solicitud
            rol = itemView.findViewById(R.id.rolUsuario);
            btnOpciones = itemView.findViewById(R.id.btnOpciones);
        }
    }
    public void updateList(List<Usuario> lista) {
        taxistasList.clear();
        taxistasList.addAll(lista);
        notifyDataSetChanged();
    }


}