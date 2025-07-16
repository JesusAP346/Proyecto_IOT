package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.widget.Button;
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

public class RegisterActivityNumeroCliente extends AppCompatActivity {

    private TextInputLayout numeroCelularLayout;
    private TextInputEditText etNumeroCelular;
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
        setContentView(R.layout.activity_register_numero_cliente);

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

        numeroCelularLayout = findViewById(R.id.textInputLayoutCorreo);
        etNumeroCelular = findViewById(R.id.etCorreo);
        btnGuardar = findViewById(R.id.botonGuardar);
        btnCancelar = findViewById(R.id.botonCancelar);

        cargarNumeroCelularActual();

        btnGuardar.setOnClickListener(v -> validarYGuardar());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarNumeroCelularActual() {
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                if (usuario != null && usuario.getNumCelular() != null) {
                    etNumeroCelular.setText(usuario.getNumCelular());
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar número de celular", Toast.LENGTH_SHORT).show();
        });
    }

    private void validarYGuardar() {
        numeroCelularLayout.setError(null);
        String nuevoNumeroCelular = etNumeroCelular.getText().toString().trim();

        if (!validarNumeroCelular(nuevoNumeroCelular)) {
            return;
        }

        usuariosRef.whereEqualTo("numCelular", nuevoNumeroCelular)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    boolean numeroYaRegistrado = false;
                    for (var doc : querySnapshot.getDocuments()) {
                        if (!doc.getId().equals(currentUser.getUid())) {
                            numeroYaRegistrado = true;
                            break;
                        }
                    }

                    if (numeroYaRegistrado) {
                        numeroCelularLayout.setError("Este número de celular ya está registrado");
                        return;
                    }

                    actualizarNumeroCelular(nuevoNumeroCelular);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al validar número: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void actualizarNumeroCelular(String nuevoNumeroCelular) {
        btnGuardar.setEnabled(false);

        userDocRef.update("numCelular", nuevoNumeroCelular)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Número de celular actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    btnGuardar.setEnabled(true);
                    Toast.makeText(this, "Error al actualizar Firestore", Toast.LENGTH_SHORT).show();
                });
    }

    private boolean validarNumeroCelular(String numeroCelular) {
        if (numeroCelular.isEmpty()) {
            numeroCelularLayout.setError("El número de celular es obligatorio");
            return false;
        }

        if (!numeroCelular.matches("\\d+")) {
            numeroCelularLayout.setError("El número de celular solo debe contener números");
            return false;
        }

        if (numeroCelular.length() != 9) {
            numeroCelularLayout.setError("El número de celular debe tener 9 dígitos");
            return false;
        }

        return true;
    }
}
