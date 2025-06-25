package com.example.proyecto_iot.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.MainActivity;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.PagPrincipalSuperAdmin;
import com.example.proyecto_iot.administradorHotel.PagPrincipalAdmin;
import com.example.proyecto_iot.administradorHotel.RegistroPrimeraVez;
import com.example.proyecto_iot.cliente.busqueda.ClienteBusquedaActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseFirestore db;
    ProgressDialog progressDialog; // Nuevo: progreso modal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Botones y campos
        TextView textCrearCuenta = findViewById(R.id.registro_nuevo_usuario);
        TextView txtForgotPassword = findViewById(R.id.forgotPassID);
        Button btnIngresar = findViewById(R.id.btnIngresar);
        TextInputEditText editTextUsuario = findViewById(R.id.editTextUsuario);
        TextInputEditText editTextPassword = findViewById(R.id.editTextPassword);

        // Navegar a crear cuenta
        textCrearCuenta.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });

        // Navegar a recuperar contraseña
        txtForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Botón ingresar
        btnIngresar.setOnClickListener(v -> {
            String correo = editTextUsuario.getText() != null ? editTextUsuario.getText().toString().trim() : "";
            String password = editTextPassword.getText() != null ? editTextPassword.getText().toString() : "";

            if (correo.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
                Toast.makeText(this, "Correo inválido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Ingresa tu contraseña", Toast.LENGTH_SHORT).show();
                return;
            }

            showLoadingDialog(); // 👈 Mostrar diálogo de carga

            auth.signInWithEmailAndPassword(correo, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            db.collection("usuarios").document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        hideLoadingDialog(); // 👈 Ocultar diálogo

                                        if (documentSnapshot.exists()) {
                                            String rol = documentSnapshot.getString("idRol");
                                            if ("Cliente".equalsIgnoreCase(rol)) {
                                                String idUsuario = documentSnapshot.getId();
                                                Intent intent = new Intent(LoginActivity.this, ClienteBusquedaActivity.class);
                                                intent.putExtra("idUsuario", idUsuario);
                                                startActivity(intent);
                                                finish();
                                            } else if ("Administrador".equals(rol)) {
                                                String idUsuario = documentSnapshot.getId();
                                                String idHotel = documentSnapshot.getString("idHotel");

                                                if (idHotel == null || idHotel.isEmpty()) {
                                                    // No tiene hotel aún, va al registro
                                                    Intent intent = new Intent(LoginActivity.this, RegistroPrimeraVez.class);
                                                    intent.putExtra("idUsuario", idUsuario);
                                                    startActivity(intent);
                                                } else {
                                                    // Ya tiene hotel, va al panel principal
                                                    Intent intent = new Intent(LoginActivity.this, PagPrincipalAdmin.class);
                                                    intent.putExtra("idUsuario", idUsuario);
                                                    intent.putExtra("idHotel", idHotel); // importante para que luego lo uses en los fragmentos
                                                    startActivity(intent);
                                                }

                                                finish(); // cerrar LoginActivity
                                            }
                                            else if ("Taxista".equals(rol)) {
                                                String idUsuario = documentSnapshot.getId();
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                                intent.putExtra("idUsuario", idUsuario);
                                                startActivity(intent);
                                                finish();
                                            }else if ("SuperAdmin".equals(rol)) {
                                                Intent intent = new Intent(LoginActivity.this, PagPrincipalSuperAdmin.class);
                                                startActivity(intent);
                                            }else {
                                                Toast.makeText(this, "Rol no permitido", Toast.LENGTH_SHORT).show();
                                                auth.signOut();
                                            }
                                        } else {
                                            Toast.makeText(this, "Datos de usuario no encontrados", Toast.LENGTH_LONG).show();
                                            auth.signOut();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        hideLoadingDialog(); // 👈 Ocultar diálogo
                                        Toast.makeText(this, "Error al cargar sesión", Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            hideLoadingDialog(); // 👈 Ocultar diálogo
                        }
                    })
                    .addOnFailureListener(e -> {
                        hideLoadingDialog(); // 👈 Ocultar diálogo
                        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    });
        });
    }

    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Cargando...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
