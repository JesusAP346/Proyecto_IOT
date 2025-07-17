package com.example.proyecto_iot.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class InicioSesionActivitySuperAdmin extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView btnCerrarSesion, btnActualizar;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion_super_admin); // ✅ Asegúrate de usar el layout correcto para SuperAdmin

        auth = FirebaseAuth.getInstance();

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnActualizar = findViewById(R.id.btnActualizar);
        backButton = findViewById(R.id.backseguridadadmin);

        // Botón cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(InicioSesionActivitySuperAdmin.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Botón actualizar contraseña
        btnActualizar.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesionActivitySuperAdmin.this, RegisterActivityCambiarContraSA.class);
            startActivity(intent);
        });

        // Botón back
        backButton.setOnClickListener(v -> finish());
    }
}
