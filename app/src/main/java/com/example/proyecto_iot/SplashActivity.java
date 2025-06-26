package com.example.proyecto_iot;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.SuperAdmin.PagPrincipalSuperAdmin;
import com.example.proyecto_iot.administradorHotel.PagPrincipalAdmin;
import com.example.proyecto_iot.cliente.busqueda.ClienteBusquedaActivity;
import com.example.proyecto_iot.login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        new Handler().postDelayed(this::checkUserAuthentication, 1500);
    }

    private void checkUserAuthentication() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            db.collection("usuarios").document(currentUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String rol = documentSnapshot.getString("idRol");
                            String idUsuario = documentSnapshot.getId();

                            if (rol != null) {
                                redirectToRoleActivity(rol, idUsuario);
                            } else {
                                // Si no tiene rol definido, cerrar sesión y ir al login
                                auth.signOut();
                                redirectToLogin();
                            }
                        } else {
                            // Si el documento del usuario no existe, cerrar sesión
                            auth.signOut();
                            redirectToLogin();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Error al cargar datos, ir al login
                        redirectToLogin();
                    });
        } else {
            redirectToLogin();
        }
    }

    private void redirectToRoleActivity(String rol, String idUsuario) {
        Intent intent;

        switch (rol.toLowerCase()) {
            case "cliente":
                intent = new Intent(SplashActivity.this, ClienteBusquedaActivity.class);
                intent.putExtra("idUsuario", idUsuario);
                break;

            case "superadmin":
                intent = new Intent(SplashActivity.this, PagPrincipalSuperAdmin.class);
                intent.putExtra("idUsuario", idUsuario);
                break;

            case "taxista":
                intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("idUsuario", idUsuario);
                break;

            case "administrador":
                intent = new Intent(SplashActivity.this, PagPrincipalAdmin.class);
                intent.putExtra("idUsuario", idUsuario);
                break;

            default:
                // Rol no reconocido, cerrar sesión y ir al login
                auth.signOut();
                redirectToLogin();
                return;
        }

        startActivity(intent);
        finish();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}