package com.example.proyecto_iot.cliente;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.login.LoginActivity;
import com.example.proyecto_iot.R;
import com.google.firebase.auth.FirebaseAuth;

public class InicioSesionActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private TextView btnCerrarSesion, btnActualizar;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_sesion);

        auth = FirebaseAuth.getInstance();

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnActualizar = findViewById(R.id.btnActualizar);
        backButton = findViewById(R.id.backseguridadadmin);

        // Botón cerrar sesión
        btnCerrarSesion.setOnClickListener(v -> {
            auth.signOut();
            Intent intent = new Intent(InicioSesionActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Botón actualizar contraseña
        btnActualizar.setOnClickListener(v -> {
            Intent intent = new Intent(InicioSesionActivity.this, RegisterActivityCambiarContraCliente.class);
            startActivity(intent);
        });

        // Botón back
        backButton.setOnClickListener(v -> finish());
    }
}
