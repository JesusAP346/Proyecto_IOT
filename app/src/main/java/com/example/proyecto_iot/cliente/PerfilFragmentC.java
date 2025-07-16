package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.MainActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.cliente.modoTaxistaFormulario.RegistroTaxistaActivity;
import com.example.proyecto_iot.dtos.Usuario;
import com.example.proyecto_iot.login.LoginActivity;
import com.example.proyecto_iot.taxista.perfil.InformacionPersonalActivity;
import com.example.proyecto_iot.taxista.perfil.PerfilTaxistaActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;


import android.widget.ImageView;
import android.widget.TextView;
import com.example.proyecto_iot.dtos.Usuario;
import com.squareup.picasso.Picasso;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.ListenerRegistration;


public class PerfilFragmentC extends Fragment {

    private Button btnModoCliente;
    private Button btnCerrarSesion;
    private FirebaseAuth auth;
    private LinearLayout cardPerfil;

    private ImageView ivFotoPerfil;
    private TextView tvNombrePerfil;
    private ListenerRegistration listenerRegistration;
    private FirebaseFirestore db;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil_cliente, container, false);

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Inicializar botones
        btnModoCliente = view.findViewById(R.id.btnModoCliente);
//        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);
        cardPerfil = view.findViewById(R.id.cardPerfil);


        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        tvNombrePerfil = view.findViewById(R.id.tvNombrePerfil);
        db = FirebaseFirestore.getInstance();


        // Listener para cambiar a modo taxista
        btnModoCliente.setOnClickListener(v -> {
            if (auth.getCurrentUser() == null) {
                Toast.makeText(getContext(), "No se encontró sesión activa", Toast.LENGTH_SHORT).show();
                return;
            }

            String uid = auth.getCurrentUser().getUid();

            FirebaseFirestore.getInstance()
                    .collection("usuarios")
                    .document(uid)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String idRol = documentSnapshot.getString("idRol");
                            Object estadoSolicitud = documentSnapshot.get("estadoSolicitudTaxista"); // puede ser null

                            if ("Cliente".equalsIgnoreCase(idRol) && estadoSolicitud == null) {
                                Intent intent = new Intent(getContext(), RegistroTaxistaActivity.class);
                                startActivity(intent);
                            } else if ("Taxista".equals(idRol)) {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getContext(), "Ya enviaste una solicitud o no tienes permiso para registrarte como taxista", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "Datos de usuario no encontrados", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                    });
        });

        cardPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), PerfilClienteActivity.class);
            startActivity(intent);
        });

//        // Listener para cerrar sesión
//        btnCerrarSesion.setOnClickListener(v -> {
//            cerrarSesion();
//        });

        // Listeners para otras opciones
        view.findViewById(R.id.informacionPersonal).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), EditarPerfilClienteActivity.class);
            startActivity(intent);
        });

        view.findViewById(R.id.seguridadPersonal).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), InicioSesionActivity.class);
            startActivity(intent);
        });

        escucharDatosUsuarioEnTiempoReal();

        return view;
    }

    private void cerrarSesion() {


        auth.signOut();

        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    private void escucharDatosUsuarioEnTiempoReal() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            return;
        }

        DocumentReference docRef = db.collection("usuarios").document(uid);

        listenerRegistration = docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                if (usuario != null) {
                    actualizarCardPerfil(usuario);
                }
            }
        });
    }

    private void actualizarCardPerfil(Usuario usuario) {
        String nombreCompleto = "";
        if (usuario.getNombres() != null) nombreCompleto += usuario.getNombres();
        if (usuario.getApellidos() != null) nombreCompleto += " " + usuario.getApellidos();

        tvNombrePerfil.setText(nombreCompleto.trim());

        if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
            Picasso.get()
                    .load(usuario.getUrlFotoPerfil())
                    .placeholder(R.drawable.ic_generic_user)
                    .into(ivFotoPerfil);
        } else {
            ivFotoPerfil.setImageResource(R.drawable.ic_generic_user);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

}