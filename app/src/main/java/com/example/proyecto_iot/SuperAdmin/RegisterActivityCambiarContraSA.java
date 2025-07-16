package com.example.proyecto_iot.SuperAdmin;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivityCambiarContraSA extends AppCompatActivity {

    private TextInputLayout layoutPasswordActual, layoutPasswordNueva, layoutPasswordConfirmar;
    private TextInputEditText editPasswordActual, editPasswordNueva, editPasswordConfirmar;
    private Button btnGuardar, btnCancelar;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_cambiar_contra_sa);

        // Vincular views
        layoutPasswordActual = findViewById(R.id.layoutPasswordActual);
        layoutPasswordNueva = findViewById(R.id.layoutPasswordNueva);
        layoutPasswordConfirmar = findViewById(R.id.layoutPasswordConfirmar);

        editPasswordActual = findViewById(R.id.editPasswordActual);
        editPasswordNueva = findViewById(R.id.editPasswordNueva);
        editPasswordConfirmar = findViewById(R.id.editPasswordConfirmar);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnCancelar = findViewById(R.id.btnCancelar);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        btnCancelar.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> validarYActualizar());
    }

    private void validarYActualizar() {
        // Limpiar errores previos
        layoutPasswordActual.setError(null);
        layoutPasswordNueva.setError(null);
        layoutPasswordConfirmar.setError(null);

        String passwordActual = editPasswordActual.getText() != null ? editPasswordActual.getText().toString().trim() : "";
        String passwordNueva = editPasswordNueva.getText() != null ? editPasswordNueva.getText().toString().trim() : "";
        String passwordConfirmar = editPasswordConfirmar.getText() != null ? editPasswordConfirmar.getText().toString().trim() : "";

        boolean esValido = true;

        if (TextUtils.isEmpty(passwordActual)) {
            layoutPasswordActual.setError("Debes ingresar la contraseña actual");
            esValido = false;
        }

        if (TextUtils.isEmpty(passwordNueva)) {
            layoutPasswordNueva.setError("Debes ingresar una nueva contraseña");
            esValido = false;
        } else if (passwordNueva.length() < 6) {
            layoutPasswordNueva.setError("Mínimo 6 caracteres");
            esValido = false;
        }

        if (!passwordNueva.equals(passwordConfirmar)) {
            layoutPasswordConfirmar.setError("Las contraseñas no coinciden");
            esValido = false;
        }

        if (!esValido) return;

        // Reautenticación antes de cambiar contraseña
        if (currentUser != null && currentUser.getEmail() != null) {
            currentUser.reauthenticate(EmailAuthProvider.getCredential(currentUser.getEmail(), passwordActual))
                    .addOnSuccessListener(authResult -> {
                        // Ahora actualizar contraseña
                        currentUser.updatePassword(passwordNueva)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(RegisterActivityCambiarContraSA.this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(RegisterActivityCambiarContraSA.this, "Error al actualizar contraseña: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        layoutPasswordActual.setError("Contraseña actual incorrecta");
                    });
        } else {
            Toast.makeText(this, "Error: Sesión no iniciada correctamente", Toast.LENGTH_LONG).show();
        }
    }
}
