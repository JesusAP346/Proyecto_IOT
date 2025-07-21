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
import com.example.proyecto_iot.dtos.LogSA;
import com.example.proyecto_iot.dtos.Usuario;
import com.example.proyecto_iot.SuperAdmin.fragmentos.FragmentGestionAdministradorSuperadmin;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import java.util.Date;
import java.util.List;

public class AdministradoresAdapter extends RecyclerView.Adapter<AdministradoresAdapter.ViewHolder> {

    // Cambiado de AdministradoresDomain a Usuario
    List<Usuario> items;
    public AdministradoresAdapter(List<Usuario> items){
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

        // Cambiado de AdministradoresDomain a Usuario
        Usuario admin = items.get(position);

        // --- CAMBIOS AQUÍ: Usar los métodos getter de la clase Usuario ---
        holder.nombreAdmin.setText(admin.getNombres()+" "+admin.getApellidos()); // Usa getNombres()
        holder.numeroAdmin.setText("Teléfono: " + admin.getNumCelular()); // Usa getNumCelular()
        holder.correoAdmin.setText("Correo: " +admin.getEmail());
        String estado = admin.estadoCuenta ? "Activo" : "Suspendido";
        holder.estadoAdmin.setText("Estado cuenta: " + estado);



        // Asumiendo que 'urlFotoPerfil' es el campo de la URL de la imagen en tu clase Usuario
        if (admin.getUrlFotoPerfil() != null && !admin.getUrlFotoPerfil().isEmpty()) {
            Picasso.get().load(admin.getUrlFotoPerfil()).into(holder.imagenAdmin);
        } else {
            holder.imagenAdmin.setImageResource(R.drawable.ic_generic_user); // Imagen por defecto
        }
        // --- FIN CAMBIOS ---


        //Ver perfil
        holder.itemView.setOnClickListener(view -> {
            // Pasa el objeto Usuario
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
            TextView btnActivar = sheetView.findViewById(R.id.btnActivar);

            // --- CAMBIOS AQUÍ: Usar los métodos getter de la clase Usuario ---
            tvNombre.setText(admin.getNombres()+ " " + admin.getApellidos());
            tvNumero.setText(admin.getNumCelular());
            if (admin.getUrlFotoPerfil() != null && !admin.getUrlFotoPerfil().isEmpty()) {
                Picasso.get().load(admin.getUrlFotoPerfil()).into(ivFoto);
            } else {
                ivFoto.setImageResource(R.drawable.ic_generic_user); // Imagen por defecto
            }
            // --- FIN CAMBIOS ---

            btnEditar.setOnClickListener(view -> {
                dialog.dismiss();
                // Pasa el objeto Usuario
                FragmentGestionAdministradorSuperadmin fragment = FragmentGestionAdministradorSuperadmin.newInstance(admin, true, position);
                ((AppCompatActivity) v.getContext()).getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .addToBackStack(null)
                        .commit();
            });
            // Al preparar el BottomSheet:
            if (admin.isEstadoCuenta()) {
                btnActivar.setText("Desactivar");
                btnActivar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_desactivo, 0, 0, 0);
            } else {
                btnActivar.setText("Activar");
                btnActivar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_activar, 0, 0, 0);
            }

            btnActivar.setOnClickListener(view -> {
                dialog.dismiss();
                boolean nuevoEstado = !admin.isEstadoCuenta();
                FirebaseFirestore.getInstance().collection("usuarios")
                        .document(admin.getId())
                        .update("estadoCuenta", nuevoEstado)
                        .addOnSuccessListener(aVoid -> {
                            String msg = nuevoEstado ? "Usuario activado." : "Usuario suspendido.";

                            Toast.makeText(v.getContext(), msg, Toast.LENGTH_SHORT).show();
                            admin.setEstadoCuenta(nuevoEstado);
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
                        .setMessage("¿Estás seguro de que quieres eliminar a " + admin.getNombres() + " " + admin.getApellidos() + "? Esta acción no se puede deshacer.")
                        .setPositiveButton("Sí, eliminar", (dialogInterface, i) -> {
                            // Lógica de eliminación si el usuario confirma
                            if (admin.getId() != null && !admin.getId().isEmpty()) {
                                com.google.firebase.firestore.FirebaseFirestore db = com.google.firebase.firestore.FirebaseFirestore.getInstance();
                                db.collection("usuarios").document(admin.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(v.getContext(), "Administrador " + admin.getNombres() + " " + admin.getApellidos() + " eliminado de Firestore.", Toast.LENGTH_LONG).show();

                                            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                            if (currentUser != null) {
                                                String uidEditor = currentUser.getUid();

                                                db.collection("usuarios").document(uidEditor)
                                                        .get()
                                                        .addOnSuccessListener(documentSnapshot -> {
                                                            if (documentSnapshot.exists()) {
                                                                Usuario editor = documentSnapshot.toObject(Usuario.class);
                                                                String nombreEditor = editor.getNombres() + " " + editor.getApellidos();
                                                                String nombreEliminado = admin.getNombres() + " " + admin.getApellidos();

                                                                LogSA log = new LogSA(
                                                                        null,
                                                                        "Eliminación de Administrador",
                                                                        "Se eliminó al Administrador " + nombreEliminado,
                                                                        nombreEditor,
                                                                        "Super Admin",
                                                                        uidEditor,
                                                                        nombreEliminado,
                                                                        new Date()
                                                                );

                                                                db.collection("logs").add(log);
                                                            }
                                                        });
                                            }

                                            // No necesitas modificar 'items' aquí, el SnapshotListener en el fragmento lo hará.
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(v.getContext(), "Error al eliminar administrador: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        });
                            } else {
                                Toast.makeText(v.getContext(), "ID del administrador no encontrado para eliminar.", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imagenAdmin;
        TextView nombreAdmin;
        TextView numeroAdmin;
        TextView estadoAdmin;
        TextView correoAdmin;
        ImageButton btnOpciones;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenAdmin= itemView.findViewById(R.id.imagen_list_adminitradores);
            nombreAdmin= itemView.findViewById(R.id.nameAdministradores);
            numeroAdmin= itemView.findViewById(R.id.numberAdministradores);
            estadoAdmin = itemView.findViewById(R.id.estadoAdministrador);
            correoAdmin = itemView.findViewById(R.id.correoAdministrador);
            btnOpciones = itemView.findViewById(R.id.btnOpciones);
        }
    }
    public void updateList(List<Usuario> lista) {
        this.items = lista;
        notifyDataSetChanged();
    }

}