package com.example.proyecto_iot.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

public class ActivityPerfilSuperAdmin extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    private ImageView btnBack, ivFotoPerfil;
    private TextView tvNombrePerfil;
    private LinearLayout cardPerfil, informacionPersonal, seguridadPersonal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_super_admin2); // usa el nuevo layout con flechita y cardPerfil

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnBack = findViewById(R.id.btnBack);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);

        cardPerfil = findViewById(R.id.cardPerfil);
        informacionPersonal = findViewById(R.id.informacionPersonal);
        seguridadPersonal = findViewById(R.id.seguridadPersonal);

        btnBack.setOnClickListener(v -> finish());

        cardPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityPerfilSuperAdmin.this, PerfilSuperAdminActivity.class);
            startActivity(intent);
        });

        informacionPersonal.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityPerfilSuperAdmin.this, EditarPerfilSuperAdminActivity.class);
            startActivity(intent);
        });

        seguridadPersonal.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityPerfilSuperAdmin.this, InicioSesionActivitySuperAdmin.class);
            startActivity(intent);
        });

        escucharDatosUsuarioEnTiempoReal();
    }

    private void escucharDatosUsuarioEnTiempoReal() {
        String uid = auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;

        if (uid == null) {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show();
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
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}
