package com.example.proyecto_iot.login;

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
import com.example.proyecto_iot.cliente.busqueda.ClienteBusquedaActivity;
import com.example.proyecto_iot.databinding.ActivityLoginBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth auth;
    FirebaseFirestore db;

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


            auth.signInWithEmailAndPassword(correo, password)
                    .addOnSuccessListener(authResult -> {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();

                            db.collection("usuarios").document(uid).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String rol = documentSnapshot.getString("idRol");
                                            if ("Cliente".equalsIgnoreCase(rol)) {
                                                Toast.makeText(LoginActivity.this, "Bienvenido, cliente", Toast.LENGTH_SHORT).show();
                                                String idUsuario = documentSnapshot.getId();
                                                Intent intent = new Intent(LoginActivity.this, ClienteBusquedaActivity.class);
                                                intent.putExtra("idUsuario", idUsuario);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Acceso solo para clientes", Toast.LENGTH_LONG).show();
                                                FirebaseAuth.getInstance().signOut();
                                            }
                                        } else {
                                            Toast.makeText(LoginActivity.this, "Datos de usuario no encontrados", Toast.LENGTH_LONG).show();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(LoginActivity.this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
                                        e.printStackTrace();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(LoginActivity.this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                    });
        });
    }
}
