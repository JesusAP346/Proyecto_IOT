package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.squareup.picasso.Picasso;

public class FragmentPerfilUsuariosSuperadmin extends Fragment {

    private String dni, nombre, numeroTelefono, imagenPerfil, correo, direccion, fechaNacimiento,
            rol, estadoCuenta, nivelCompletado, calificacion;
    private int habitacionesRegistradas;

    public FragmentPerfilUsuariosSuperadmin() {}

    // Cambiado para aceptar UsuariosEntity (por campos)
    public static FragmentPerfilUsuariosSuperadmin newInstance(com.example.proyecto_iot.SuperAdmin.database.UsuariosEntity usuario) {
        FragmentPerfilUsuariosSuperadmin fragment = new FragmentPerfilUsuariosSuperadmin();
        Bundle args = new Bundle();
        args.putString("dni", usuario.dni);
        args.putString("nombre", usuario.nombre);
        args.putString("numeroTelefono", usuario.numeroTelefono);
        args.putString("imagenPerfil", usuario.imagenPerfil);
        args.putString("correo", usuario.correo);
        args.putString("direccion", usuario.direccion);
        args.putString("fechaNacimiento", usuario.fechaNacimiento);
        args.putString("rol", usuario.rol);
        args.putInt("habitacionesRegistradas", usuario.habitacionesRegistradas);
        args.putString("estadoCuenta", usuario.estadoCuenta);
        args.putString("nivelCompletado", usuario.nivelCompletado);
        args.putString("calificacion", usuario.calificacion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dni = getArguments().getString("dni");
            nombre = getArguments().getString("nombre");
            numeroTelefono = getArguments().getString("numeroTelefono");
            imagenPerfil = getArguments().getString("imagenPerfil");
            correo = getArguments().getString("correo");
            direccion = getArguments().getString("direccion");
            fechaNacimiento = getArguments().getString("fechaNacimiento");
            rol = getArguments().getString("rol");
            habitacionesRegistradas = getArguments().getInt("habitacionesRegistradas");
            estadoCuenta = getArguments().getString("estadoCuenta");
            nivelCompletado = getArguments().getString("nivelCompletado");
            calificacion = getArguments().getString("calificacion");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil_usuarios_superadmin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageView ivFoto = view.findViewById(R.id.ivFotoUsuario);
        TextView tvNombre = view.findViewById(R.id.tvNombreUsuario);
        TextView tvViajes = view.findViewById(R.id.tvViajes);
        TextView tvCalificacion = view.findViewById(R.id.tvCalificacion);
        TextView tvDocumento = view.findViewById(R.id.tvDocumento);
        TextView tvCorreo = view.findViewById(R.id.tvCorreo);
        TextView tvTelefono = view.findViewById(R.id.tvTelefono);
        TextView tvDireccion = view.findViewById(R.id.tvDireccion);
        TextView tvHabitaciones = view.findViewById(R.id.tvHabitaciones);
        TextView tvEstado = view.findViewById(R.id.tvEstado);

        Picasso.get().load(imagenPerfil).into(ivFoto);
        tvNombre.setText(nombre);
        tvViajes.setText(nivelCompletado); // usando campo `nivelCompletado` como "viajes"
        tvCalificacion.setText(calificacion);
        tvDocumento.setText("✔ Documento de identidad: " + dni);
        tvCorreo.setText("✔ Correo electrónico: " + correo);
        tvTelefono.setText("✔ Teléfono: " + numeroTelefono);
        tvDireccion.setText("✔ Dirección: " + direccion);
        tvHabitaciones.setText("✔ Habitaciones registradas: " + habitacionesRegistradas);
        tvEstado.setText("✔ Estado de cuenta: " + estadoCuenta);
    }
}
