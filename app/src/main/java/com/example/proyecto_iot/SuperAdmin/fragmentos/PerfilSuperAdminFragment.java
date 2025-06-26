package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.login.LoginActivity;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PerfilSuperAdminFragment extends Fragment {

    private TextView tvNombreCompleto, tvCorreo;
    private ImageView btnBack, ivFotoPerfil;
    private MaterialButton btnEditarPerfil, btnCerrarSesion;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    public PerfilSuperAdminFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_perfil_super_admin, container, false);

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Vincular vistas
        tvNombreCompleto = view.findViewById(R.id.tvNombreCompleto);
        tvCorreo = view.findViewById(R.id.tvCorreo);
        btnBack = view.findViewById(R.id.btnBack);
        ivFotoPerfil = view.findViewById(R.id.ivFotoPerfil);
        btnEditarPerfil = view.findViewById(R.id.btnEditarPerfil);
        btnCerrarSesion = view.findViewById(R.id.btnCerrarSesion);

        // Acción botón atrás
        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        // Acción cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Cargar datos del usuario actual
        cargarDatosPerfil();

        return view;
    }

    private void cargarDatosPerfil() {
        String uid = auth.getCurrentUser().getUid();

        firestore.collection("usuarios").document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                        if (usuario != null) {
                            String nombre = (usuario.getNombres() != null) ? usuario.getNombres() : "";
                            String apellido = (usuario.getApellidos() != null) ? usuario.getApellidos() : "";
                            tvNombreCompleto.setText(nombre + " " + apellido);
                            tvCorreo.setText(usuario.getEmail());
                        }
                    } else {
                        Toast.makeText(getContext(), "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar perfil", Toast.LENGTH_SHORT).show();
                });
    }
}
