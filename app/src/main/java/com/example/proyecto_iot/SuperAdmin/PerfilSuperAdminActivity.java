package com.example.proyecto_iot.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.example.proyecto_iot.login.LoginActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class PerfilSuperAdminActivity extends AppCompatActivity {

    private TextView tvNombreCompleto, tvCorreo, gmail, numero, ciudadNatal;
    private ImageView btnBack, ivFotoPerfil;
    private MaterialButton btnEditarPerfil, btnCerrarSesion;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_super_admin);

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Vincular vistas
        tvNombreCompleto = findViewById(R.id.tvNombreCompleto);
        tvCorreo = findViewById(R.id.tvCorreo);
        gmail=findViewById(R.id.gmail);
        numero=findViewById(R.id.numero);
        ciudadNatal=findViewById(R.id.ciudadNatal);

        btnBack = findViewById(R.id.btnBack);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);

        // Acción botón atrás
        btnBack.setOnClickListener(v -> finish());

        // Acción cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(PerfilSuperAdminActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Cargar datos del usuario actual
        cargarDatosPerfil();
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
                            gmail.setText(usuario.getEmail());
                            numero.setText(usuario.getNumCelular());
                            ciudadNatal.setText(usuario.getDistrito() + " " +usuario.getProvincia() + " " + usuario.getDepartamento()   );
                            // ✅ Cargar imagen con Picasso si la URL no está vacía
                            if (usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
                                Picasso.get()
                                        .load(usuario.getUrlFotoPerfil())
                                        .placeholder(R.drawable.ic_generic_user) // imagen mientras carga
                                        .into(ivFotoPerfil);
                            }
                        }
                    } else {
                        Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al cargar perfil", Toast.LENGTH_SHORT).show();
                });
    }

}
