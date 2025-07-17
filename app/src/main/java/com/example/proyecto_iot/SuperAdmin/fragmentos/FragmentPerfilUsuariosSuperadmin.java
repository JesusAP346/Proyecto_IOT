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
import com.example.proyecto_iot.dtos.Usuario;
import com.squareup.picasso.Picasso;

public class FragmentPerfilUsuariosSuperadmin extends Fragment {

    private Usuario usuario;

    public FragmentPerfilUsuariosSuperadmin() {}

    public static FragmentPerfilUsuariosSuperadmin newInstance(Usuario usuario) {
        FragmentPerfilUsuariosSuperadmin fragment = new FragmentPerfilUsuariosSuperadmin();
        Bundle args = new Bundle();
        args.putSerializable("usuario", usuario);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usuario = (Usuario) getArguments().getSerializable("usuario");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil_usuarios_superadmin, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ImageView ivFoto = view.findViewById(R.id.ivFotoPerfil);
        TextView tvNombreCompleto = view.findViewById(R.id.tvNombreCompleto);
        TextView tvEstadoCuenta = view.findViewById(R.id.tvEstadoCuenta);
        TextView tvHabitaciones = view.findViewById(R.id.tvHabitaciones);
        TextView tvTipoDocumento = view.findViewById(R.id.tvTipoDocumento);
        TextView tvNumeroDocumento = view.findViewById(R.id.tvNumeroDocumento);
        TextView tvFechaNacimiento = view.findViewById(R.id.tvFechaNacimiento);
        TextView tvDepartamento = view.findViewById(R.id.tvDepartamento);
        TextView tvProvincia = view.findViewById(R.id.tvProvincia);
        TextView tvDistrito = view.findViewById(R.id.tvDistrito);
        TextView tvDireccion = view.findViewById(R.id.tvDireccion);
        TextView tvTelefono = view.findViewById(R.id.tvTelefono);
        TextView tvCorreo = view.findViewById(R.id.tvCorreo);

        if (usuario != null) {
            String nombreCompleto = (usuario.getNombres() != null ? usuario.getNombres() : "") +
                    " " +
                    (usuario.getApellidos() != null ? usuario.getApellidos() : "");

            tvNombreCompleto.setText(nombreCompleto);
            tvEstadoCuenta.setText(usuario.isEstadoCuenta() ? "Activo" : "Inactivo");
            tvHabitaciones.setText(usuario.getIdHotel() != null ? usuario.getIdHotel() : "0"); // O usa el campo correcto si es un número

            tvTipoDocumento.setText(usuario.getTipoDocumento() != null ? usuario.getTipoDocumento() : "");
            tvNumeroDocumento.setText(usuario.getNumDocumento() != null ? usuario.getNumDocumento() : "");
            tvFechaNacimiento.setText(usuario.getFechaNacimiento() != null ? usuario.getFechaNacimiento() : "");

            tvDepartamento.setText(usuario.getDepartamento() != null ? usuario.getDepartamento() : "");
            tvProvincia.setText(usuario.getProvincia() != null ? usuario.getProvincia() : "");
            tvDistrito.setText(usuario.getDistrito() != null ? usuario.getDistrito() : "");
            tvDireccion.setText(usuario.getDireccion() != null ? usuario.getDireccion() : "");

            tvTelefono.setText(usuario.getNumCelular() != null ? usuario.getNumCelular() : "");
            tvCorreo.setText(usuario.getEmail() != null ? usuario.getEmail() : "");

            if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
                Picasso.get().load(usuario.getUrlFotoPerfil()).into(ivFoto);
            } else {
                ivFoto.setImageResource(R.drawable.ic_generic_user);
            }
        }
    }
}
