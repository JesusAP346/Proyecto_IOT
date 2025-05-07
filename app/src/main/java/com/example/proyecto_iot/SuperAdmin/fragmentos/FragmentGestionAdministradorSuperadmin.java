package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

public class FragmentGestionAdministradorSuperadmin extends Fragment {

    public FragmentGestionAdministradorSuperadmin() {
        // Constructor vacío obligatorio
    }

    // Método factory: crea una instancia del fragment y le pasa los datos
    public static FragmentGestionAdministradorSuperadmin newInstance(AdministradoresDomain admin) {
        FragmentGestionAdministradorSuperadmin fragment = new FragmentGestionAdministradorSuperadmin();
        Bundle args = new Bundle();
        args.putString("nombre", admin.getNombreAdmin());
        args.putString("numero", admin.getNumeroAdmin());
        args.putString("correo", admin.getCorreo());
        args.putString("direccion", admin.getDireccion());
        args.putString("fechaNacimiento", admin.getFechaNacimiento());
        args.putString("rol", admin.getRol());
        args.putString("imagen", admin.getImagenAdmin());
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

        // Obtener datos del Bundle
        if (getArguments() != null) {
            etNombre.setText(getArguments().getString("nombre"));
            etNumero.setText(getArguments().getString("numero"));
            etCorreo.setText(getArguments().getString("correo"));
            etDireccion.setText(getArguments().getString("direccion"));
            etFechaNacimiento.setText(getArguments().getString("fechaNacimiento"));
            etRol.setText(getArguments().getString("rol"));

            String imagenUrl = getArguments().getString("imagen");
            Picasso.get().load(imagenUrl).into(ivFoto);
        }

        return view;
    }
}
