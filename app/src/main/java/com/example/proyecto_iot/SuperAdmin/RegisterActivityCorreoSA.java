package com.example.proyecto_iot.SuperAdmin;

import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivityCorreoSA extends AppCompatActivity {

    private TextInputLayout emailLayout;
    private TextInputEditText etCorreo;
    private Button btnGuardar;
    private Button btnCancelar;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    private DocumentReference userDocRef;
    private CollectionReference usuariosRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_correo_sa);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Sesión expirada", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = currentUser.getUid();
        userDocRef = firestore.collection("usuarios").document(uid);
        usuariosRef = firestore.collection("usuarios");

        // Vistas
        emailLayout = findViewById(R.id.textInputLayoutCorreo);
        etCorreo = findViewById(R.id.etCorreo);
        btnGuardar = findViewById(R.id.botonGuardar);
        btnCancelar = findViewById(R.id.botonCancelar);

        cargarCorreoActual();

        btnGuardar.setOnClickListener(v -> validarYGuardar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarCorreoActual() {
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                if (usuario != null && usuario.getEmail() != null) {
                    etCorreo.setText(usuario.getEmail());
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar correo", Toast.LENGTH_SHORT).show();
        });
    }

    private void validarYGuardar() {
        emailLayout.setError(null);
        String nuevoCorreo = etCorreo.getText().toString().trim();

        if (nuevoCorreo.isEmpty()) {
            emailLayout.setError("El correo electrónico es obligatorio");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(nuevoCorreo).matches()) {
            emailLayout.setError("Formato de correo inválido");
            return;
        }

        // Verificar que no esté registrado por otro usuario (excepto él mismo)
        usuariosRef.whereEqualTo("email", nuevoCorreo)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean correoYaRegistrado = false;
                    for (var doc : querySnapshot.getDocuments()) {
                        if (!doc.getId().equals(currentUser.getUid())) {
                            correoYaRegistrado = true;
                            break;
                        }
                    }

                    if (correoYaRegistrado) {
                        emailLayout.setError("Este correo ya está registrado");
                        return;
                    }

                    actualizarCorreo(nuevoCorreo);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al validar correo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void actualizarCorreo(String nuevoCorreo) {
        btnGuardar.setEnabled(false);

        currentUser.updateEmail(nuevoCorreo)
                .addOnSuccessListener(aVoid -> {
                    userDocRef.update("email", nuevoCorreo)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Correo actualizado correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                btnGuardar.setEnabled(true);
                                Toast.makeText(this, "Error al actualizar Firestore", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    btnGuardar.setEnabled(true);
                    Toast.makeText(this, "Error al actualizar FirebaseAuth", Toast.LENGTH_SHORT).show();
                });
    }
}
