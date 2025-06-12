package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario; // ¡¡IMPORTANTE: Usar tu DTO Usuario!!
// import com.example.proyecto_iot.SuperAdmin.domain.TaxistaDomain; // ¡¡ELIMINAR ESTA IMPORTACIÓN!!
import com.squareup.picasso.Picasso;

public class FragmentPerfilTaxistasSuperadmin extends Fragment {

    // Cambiado de "taxista" a "taxista_object" para mayor claridad y evitar posibles conflictos
    private static final String ARG_TAXISTA_OBJ = "taxista_object";
    // Cambiado de TaxistaDomain a Usuario
    private Usuario taxista;

    public FragmentPerfilTaxistasSuperadmin() {
        // Required empty public constructor
    }

    // Método newInstance modificado para aceptar un objeto Usuario
    public static FragmentPerfilTaxistasSuperadmin newInstance(Usuario taxista) { // ¡¡Tipo de parámetro cambiado a Usuario!!
        FragmentPerfilTaxistasSuperadmin fragment = new FragmentPerfilTaxistasSuperadmin();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TAXISTA_OBJ, taxista); // Usar putSerializable si Usuario implementa Serializable
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Recuperar el objeto Usuario del Bundle
            taxista = (Usuario) getArguments().getSerializable(ARG_TAXISTA_OBJ); // Recuperar como Serializable y castear a Usuario
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perfil_taxistas_superadmin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Vistas del layout
        ImageView ivFoto = view.findViewById(R.id.ivFotoUsuario);
        ImageView ivFotoAuto = view.findViewById(R.id.ivFotoAuto);
        TextView tvNombre = view.findViewById(R.id.tvNombreUsuario);
        TextView tvViajes = view.findViewById(R.id.tvViajes);
        TextView tvCalificacion = view.findViewById(R.id.tvCalificacion);
        TextView tvDocumento = view.findViewById(R.id.tvDocumento);
        TextView tvCorreo = view.findViewById(R.id.tvCorreo);
        TextView tvTelefono = view.findViewById(R.id.tvTelefono);
        TextView tvEstado = view.findViewById(R.id.tvEstado);


        // Asignar valores usando los métodos getter de la clase Usuario
        if (taxista != null) {
            // Cargar foto de perfil
            if (taxista.getUrlFotoPerfil() != null && !taxista.getUrlFotoPerfil().isEmpty()) {
                Picasso.get().load(taxista.getUrlFotoPerfil()).into(ivFoto);
            } else {
                ivFoto.setImageResource(R.drawable.ic_generic_user); // Imagen por defecto
            }

            // Cargar foto de auto
            if (taxista.getUrlFotoAuto() != null && !taxista.getUrlFotoAuto().isEmpty()) {
                Picasso.get().load(taxista.getUrlFotoAuto()).into(ivFotoAuto);
            } else {
                ivFotoAuto.setImageResource(R.drawable.ic_taxi); // Imagen por defecto para auto (asegúrate de tener este drawable)
            }


            tvNombre.setText(taxista.getNombres() + " " + taxista.getApellidos()); // Concatenar nombre y apellido
            tvViajes.setText(taxista.getNumeroViajes()); // Asumo que getIndiceViajes() ya devuelve un String
            tvCalificacion.setText(String.valueOf(taxista.getCalificacionTaxista())); // Convertir calificación a String
            tvDocumento.setText("✔ Documento de identidad: " + taxista.getNumDocumento()); // Usar getNumDocumento()
            tvCorreo.setText("✔ Correo electrónico: " + taxista.getEmail());
            tvTelefono.setText("✔ Teléfono: " + taxista.getNumCelular()); // Usar getNumCelular()
            String estado;
            if(taxista.isEstadoCuenta()){
                 estado="Activo";
            }else{
                 estado="Suspendido";
            }
            tvEstado.setText("✔ Estado de cuenta: " + estado);

        }
    }
}