package com.example.proyecto_iot.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.proyecto_iot.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressDialog progressDialog;
    private TextInputEditText editTextEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Inicializar vistas
        editTextEmail = findViewById(R.id.editTextEmail);
        Button botonEnviar = findViewById(R.id.botonEnviar);
        Button botonRegresar = findViewById(R.id.botonRegresar);

        // Configurar listeners
        botonEnviar.setOnClickListener(v -> sendPasswordResetEmail());
        botonRegresar.setOnClickListener(v -> finish());
    }

    private void sendPasswordResetEmail() {
        String email = editTextEmail.getText() != null ? editTextEmail.getText().toString().trim() : "";

        // Validar email
        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa tu correo electrónico", Toast.LENGTH_SHORT).show();
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Por favor ingresa un correo válido", Toast.LENGTH_SHORT).show();
            editTextEmail.requestFocus();
            return;
        }

        // Mostrar progress dialog
        showLoadingDialog();

        // Enviar email de restablecimiento
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    hideLoadingDialog();

                    if (task.isSuccessful()) {
                        // Email enviado exitosamente
                        Toast.makeText(this,
                                "Se ha enviado un enlace de restablecimiento a tu correo",
                                Toast.LENGTH_LONG).show();

                        // Regresar al LoginActivity después de un pequeño delay
                        new android.os.Handler().postDelayed(() -> finish(), 2000);

                    } else {
                        // Error al enviar email
                        String errorMessage = "Error al enviar el correo";

                        // Personalizar mensaje según el tipo de error
                        if (task.getException() != null) {
                            String exception = task.getException().getClass().getSimpleName();

                            switch (exception) {
                                case "FirebaseAuthInvalidUserException":
                                    errorMessage = "No existe una cuenta con este correo electrónico";
                                    break;
                                case "FirebaseAuthInvalidCredentialsException":
                                    errorMessage = "El correo electrónico no es válido";
                                    break;
                                case "FirebaseNetworkException":
                                    errorMessage = "Error de conexión. Verifica tu internet";
                                    break;
                                default:
                                    errorMessage = "Error al enviar el correo. Inténtalo de nuevo";
                                    break;
                            }
                        }

                        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showLoadingDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Enviando correo...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }
}