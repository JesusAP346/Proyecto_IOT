package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.SuperAdmin.AdminDataStore;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

public class FragmentGestionAdministradorSuperadmin extends Fragment {

    public FragmentGestionAdministradorSuperadmin() {
        // Constructor vacío obligatorio
    }

    // Método factory: crea una instancia del fragment y le pasa los datos
    public static FragmentGestionAdministradorSuperadmin newInstance(AdministradoresDomain admin, boolean editar, int index) {
        FragmentGestionAdministradorSuperadmin fragment = new FragmentGestionAdministradorSuperadmin();
        Bundle args = new Bundle();
        args.putString("nombre", admin.getNombreAdmin());
        args.putString("numero", admin.getNumeroAdmin());
        args.putString("correo", admin.getCorreo());
        args.putString("direccion", admin.getDireccion());
        args.putString("fechaNacimiento", admin.getFechaNacimiento());
        args.putString("rol", admin.getRol());
        args.putString("imagen", admin.getImagenAdmin());
        args.putBoolean("modoEdicion", editar);
        args.putInt("index", index); // 🔥 nuevo
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el layout
        View view = inflater.inflate(R.layout.fragment_gestion_administrador_superadmin, container, false);

        // Vincular vistas
        TextInputEditText etNombre = view.findViewById(R.id.etNombre);
        TextInputEditText etNumero = view.findViewById(R.id.etNumero);
        TextInputEditText etCorreo = view.findViewById(R.id.etCorreo);
        TextInputEditText etDireccion = view.findViewById(R.id.etDireccion);
        TextInputEditText etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        TextInputEditText etRol = view.findViewById(R.id.etRol);
        ImageView ivFoto = view.findViewById(R.id.ivFotoAdmin);
        android.widget.Button btnEditarGuardar = view.findViewById(R.id.btnEditarGuardar);

        if (getArguments() != null) {
            // Obtener datos
            String nombre = getArguments().getString("nombre");
            String numero = getArguments().getString("numero");
            String correo = getArguments().getString("correo");
            String direccion = getArguments().getString("direccion");
            String fechaNacimiento = getArguments().getString("fechaNacimiento");
            String rol = getArguments().getString("rol");
            String imagenUrl = getArguments().getString("imagen");
            int index = getArguments().getInt("index", -1);
            boolean modoEdicion = getArguments().getBoolean("modoEdicion", false);

            // Setear valores
            etNombre.setText(nombre);
            etNumero.setText(numero);
            etCorreo.setText(correo);
            etDireccion.setText(direccion);
            etFechaNacimiento.setText(fechaNacimiento);
            etRol.setText(rol);
            Picasso.get().load(imagenUrl).into(ivFoto);

            // Habilitar/deshabilitar según modo
            etNombre.setEnabled(modoEdicion);
            etNumero.setEnabled(modoEdicion);
            etCorreo.setEnabled(modoEdicion);
            etDireccion.setEnabled(modoEdicion);
            etFechaNacimiento.setEnabled(modoEdicion);
            etRol.setEnabled(modoEdicion);
            btnEditarGuardar.setText(modoEdicion ? "Guardar" : "Editar");

            // Estado actual editable
            final boolean[] enModoEdicion = {modoEdicion};

            btnEditarGuardar.setOnClickListener(v -> {
                if (!enModoEdicion[0]) {
                    // Activar modo edición
                    enModoEdicion[0] = true;
                    btnEditarGuardar.setText("Guardar");
                    etNombre.setEnabled(true);
                    etNumero.setEnabled(true);
                    etCorreo.setEnabled(true);
                    etDireccion.setEnabled(true);
                    etFechaNacimiento.setEnabled(true);
                    etRol.setEnabled(true);
                } else {
                    // Validaciones
                    if (etNombre.getText().toString().trim().isEmpty()) {
                        etNombre.setError("Este campo es obligatorio");
                        return;
                    }

                    if (etCorreo.getText().toString().trim().isEmpty()) {
                        etCorreo.setError("Este campo es obligatorio");
                        return;
                    }

                    // Obtener datos actualizados
                    String nuevoNombre = etNombre.getText().toString().trim();
                    String nuevoNumero = etNumero.getText().toString().trim();
                    String nuevoCorreo = etCorreo.getText().toString().trim();
                    String nuevaDireccion = etDireccion.getText().toString().trim();
                    String nuevaFecha = etFechaNacimiento.getText().toString().trim();
                    String nuevoRol = etRol.getText().toString().trim();

                    // Actualizar lista global
                    if (index != -1) {
                        AdministradoresDomain actualizado = new AdministradoresDomain(
                                nuevoNombre,
                                nuevoNumero,
                                imagenUrl,
                                nuevoCorreo,
                                nuevaDireccion,
                                nuevaFecha,
                                nuevoRol
                        );
                        AdminDataStore.actualizarAdmin(index, actualizado);
                    }

                    // Desactivar modo edición
                    enModoEdicion[0] = false;
                    btnEditarGuardar.setText("Editar");
                    etNombre.setEnabled(false);
                    etNumero.setEnabled(false);
                    etCorreo.setEnabled(false);
                    etDireccion.setEnabled(false);
                    etFechaNacimiento.setEnabled(false);
                    etRol.setEnabled(false);

                    android.widget.Toast.makeText(getContext(), "Cambios guardados correctamente", android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        }

        return view;
    }

}
