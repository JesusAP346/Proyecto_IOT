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
import com.example.proyecto_iot.SuperAdmin.domain.TaxistaDomain;
import com.squareup.picasso.Picasso;

public class FragmentPerfilTaxistasSuperadmin extends Fragment {

    private static final String ARG_TAXISTA = "taxista";
    private TaxistaDomain taxista;

    public FragmentPerfilTaxistasSuperadmin() {
        // Required empty public constructor
    }

    public static FragmentPerfilTaxistasSuperadmin newInstance(TaxistaDomain taxista) {
        FragmentPerfilTaxistasSuperadmin fragment = new FragmentPerfilTaxistasSuperadmin();
        Bundle args = new Bundle();
        args.putParcelable("taxista", taxista); // ✅ Usar putParcelable

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            taxista = getArguments().getParcelable(ARG_TAXISTA);
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

        // Vistas del layout (actualizadas)
        ImageView ivFoto = view.findViewById(R.id.ivFotoUsuario);
        ImageView ivFotoAuto = view.findViewById(R.id.ivFotoAuto);
        TextView tvNombre = view.findViewById(R.id.tvNombreUsuario);
        TextView tvViajes = view.findViewById(R.id.tvViajes);
        TextView tvCalificacion = view.findViewById(R.id.tvCalificacion);
        TextView tvDocumento = view.findViewById(R.id.tvDocumento);
        TextView tvCorreo = view.findViewById(R.id.tvCorreo);
        TextView tvTelefono = view.findViewById(R.id.tvTelefono);
        TextView tvEstado = view.findViewById(R.id.tvEstado);


        // Asignar valores si taxista no es null
        if (taxista != null) {
            Picasso.get().load(taxista.getImagenPerfil()).into(ivFoto);
            Picasso.get().load(taxista.getImagenAuto()).into(ivFotoAuto);

            tvNombre.setText(taxista.getNombre());
            tvViajes.setText(taxista.getIndiceViajes());
            tvCalificacion.setText(String.valueOf(taxista.getCalificacion()));
            tvDocumento.setText("✔ Documento de identidad: " + taxista.getEstadoCuenta());
            tvCorreo.setText("✔ Correo electrónico: " + taxista.getCorreo());
            tvTelefono.setText("✔ Teléfono: " + taxista.getNumeroTelefono());
            tvEstado.setText("✔ Estado de cuenta: " + taxista.getEstadoCuenta());

        }
    }
}
