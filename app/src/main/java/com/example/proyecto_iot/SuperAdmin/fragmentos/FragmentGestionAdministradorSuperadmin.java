package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.AdminDataStore;
import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

public class FragmentGestionAdministradorSuperadmin extends Fragment {

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String imagenSeleccionada = "";

    private ImageView ivFoto;

    public FragmentGestionAdministradorSuperadmin() {}

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
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Registrar el imagePickerLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        imagenSeleccionada = imageUri.toString();
                        Picasso.get().load(imagenSeleccionada).into(ivFoto);
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_administrador_superadmin, container, false);

        TextInputEditText etNombre = view.findViewById(R.id.etNombre);
        TextInputEditText etNumero = view.findViewById(R.id.etNumero);
        TextInputEditText etCorreo = view.findViewById(R.id.etCorreo);
        TextInputEditText etDireccion = view.findViewById(R.id.etDireccion);
        TextInputEditText etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        TextInputEditText etRol = view.findViewById(R.id.etRol);
        ivFoto = view.findViewById(R.id.ivFotoAdmin);
        android.widget.Button btnEditarGuardar = view.findViewById(R.id.btnEditarGuardar);

        if (getArguments() != null) {
            String nombre = getArguments().getString("nombre");
            String numero = getArguments().getString("numero");
            String correo = getArguments().getString("correo");
            String direccion = getArguments().getString("direccion");
            String fechaNacimiento = getArguments().getString("fechaNacimiento");
            String rol = getArguments().getString("rol");
            String imagenUrl = getArguments().getString("imagen");
            int index = getArguments().getInt("index", -1);
            boolean modoEdicion = getArguments().getBoolean("modoEdicion", false);

            imagenSeleccionada = imagenUrl != null ? imagenUrl : "";

            etNombre.setText(nombre);
            etNumero.setText(numero);
            etCorreo.setText(correo);
            etDireccion.setText(direccion);
            etFechaNacimiento.setText(fechaNacimiento);
            etRol.setText(rol);
            if (!imagenSeleccionada.isEmpty()) {
                Picasso.get().load(imagenSeleccionada).into(ivFoto);
            }

            etNombre.setEnabled(modoEdicion);
            etNumero.setEnabled(modoEdicion);
            etCorreo.setEnabled(modoEdicion);
            etDireccion.setEnabled(modoEdicion);
            etFechaNacimiento.setEnabled(modoEdicion);
            etRol.setEnabled(modoEdicion);
            btnEditarGuardar.setText(modoEdicion ? "Guardar" : "Editar");

            final boolean[] enModoEdicion = {modoEdicion};

            ivFoto.setOnClickListener(v -> {
                if (enModoEdicion[0]) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    imagePickerLauncher.launch(intent);
                }
            });

            btnEditarGuardar.setOnClickListener(v -> {
                if (!enModoEdicion[0]) {
                    enModoEdicion[0] = true;
                    btnEditarGuardar.setText("Guardar");
                    etNombre.setEnabled(true);
                    etNumero.setEnabled(true);
                    etCorreo.setEnabled(true);
                    etDireccion.setEnabled(true);
                    etFechaNacimiento.setEnabled(true);
                    etRol.setEnabled(true);
                } else {
                    if (etNombre.getText().toString().trim().isEmpty()) {
                        etNombre.setError("Este campo es obligatorio");
                        return;
                    }
                    if (etCorreo.getText().toString().trim().isEmpty()) {
                        etCorreo.setError("Este campo es obligatorio");
                        return;
                    }

                    String nuevoNombre = etNombre.getText().toString().trim();
                    String nuevoNumero = etNumero.getText().toString().trim();
                    String nuevoCorreo = etCorreo.getText().toString().trim();
                    String nuevaDireccion = etDireccion.getText().toString().trim();
                    String nuevaFecha = etFechaNacimiento.getText().toString().trim();
                    String nuevoRol = etRol.getText().toString().trim();

                    if (index != -1) {
                        // Edición
                        AdministradoresDomain actualizado = new AdministradoresDomain(
                                nuevoNombre, nuevoNumero, imagenSeleccionada,
                                nuevoCorreo, nuevaDireccion, nuevaFecha, nuevoRol
                        );
                        AdminDataStore.actualizarAdmin(index, actualizado);
                        Toast.makeText(getContext(), "Administrador actualizado", Toast.LENGTH_SHORT).show();
                    } else {
                        // Creación
                        for (AdministradoresDomain admin : AdminDataStore.adminsList) {
                            if (admin.getCorreo().equalsIgnoreCase(nuevoCorreo) || admin.getNumeroAdmin().equals(nuevoNumero)) {
                                Toast.makeText(getContext(), "Ya existe un administrador con ese correo o número", Toast.LENGTH_SHORT).show();
                                return;
                            }
                        }
                        AdministradoresDomain nuevoAdmin = new AdministradoresDomain(
                                nuevoNombre, nuevoNumero, imagenSeleccionada,
                                nuevoCorreo, nuevaDireccion, nuevaFecha, nuevoRol
                        );
                        AdminDataStore.adminsList.add(nuevoAdmin);
                        Toast.makeText(getContext(), "Administrador agregado exitosamente", Toast.LENGTH_SHORT).show();
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }

                    enModoEdicion[0] = false;
                    btnEditarGuardar.setText("Editar");
                    etNombre.setEnabled(false);
                    etNumero.setEnabled(false);
                    etCorreo.setEnabled(false);
                    etDireccion.setEnabled(false);
                    etFechaNacimiento.setEnabled(false);
                    etRol.setEnabled(false);
                }
            });
        }

        return view;
    }
}
