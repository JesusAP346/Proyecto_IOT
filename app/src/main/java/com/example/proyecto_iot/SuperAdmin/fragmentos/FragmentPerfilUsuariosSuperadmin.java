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
import com.example.proyecto_iot.SuperAdmin.domain.UsuariosDomain;
import com.squareup.picasso.Picasso;

public class FragmentPerfilUsuariosSuperadmin extends Fragment {

    private UsuariosDomain usuario;

    public FragmentPerfilUsuariosSuperadmin() {}

    public static FragmentPerfilUsuariosSuperadmin newInstance(UsuariosDomain usuario) {
        FragmentPerfilUsuariosSuperadmin fragment = new FragmentPerfilUsuariosSuperadmin();
        Bundle args = new Bundle();
        args.putParcelable("usuario", usuario);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usuario = getArguments().getParcelable("usuario");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil_usuarios_superadmin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        if (usuario == null) return;

        // Asignar datos al layout
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

        Picasso.get().load(usuario.getImagenPerfil()).into(ivFoto);
        tvNombre.setText(usuario.getNombre());
        tvViajes.setText(usuario.getTotalViajes());
        tvCalificacion.setText(usuario.getCalificacion());
        tvDocumento.setText("✔ Documento de identidad: " + usuario.getDocumentoIdentidad());
        tvCorreo.setText("✔ Correo electrónico: " + usuario.getCorreo());
        tvTelefono.setText("✔ Teléfono: " + usuario.getNumeroTelefono());
        tvDireccion.setText("✔ Dirección: " + usuario.getDireccion());
        tvHabitaciones.setText("✔ Habitaciones registradas: " + usuario.getHabitacionesRegistradas());
        tvEstado.setText("✔ Estado de cuenta: " + usuario.getEstadoCuenta());
    }
}
