package com.example.proyecto_iot.SuperAdmin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivityNombresSA extends AppCompatActivity {

    private TextInputEditText etNombres, etApellidos;
    private Button botonSiguiente;
    private Button cancelar;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference userDocRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_nombres_sa); // Asegúrate que sea el layout correcto que preparaste

        // Inicializar Firebase
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        String uid = auth.getCurrentUser().getUid();
        userDocRef = firestore.collection("usuarios").document(uid);

        // Vincular vistas
        etNombres = findViewById(R.id.etNombres);
        etApellidos = findViewById(R.id.etApellidos);
        botonSiguiente = findViewById(R.id.botonSiguiente);
        cancelar = findViewById(R.id.cancelar);

        // Cargar nombres/apellidos actuales
        cargarDatosActuales();

        // Guardar cambios
        botonSiguiente.setOnClickListener(v -> guardarCambios());

        // Cancelar
        cancelar.setOnClickListener(v -> finish());
    }

    private void cargarDatosActuales() {
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                if (usuario != null) {
                    if (usuario.getNombres() != null) {
                        etNombres.setText(usuario.getNombres());
                    }
                    if (usuario.getApellidos() != null) {
                        etApellidos.setText(usuario.getApellidos());
                    }
                }
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show();
        });
    }

    private void guardarCambios() {
        String nombres = etNombres.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();

        if (nombres.isEmpty()) {
            etNombres.setError("Ingrese nombres");
            return;
        }

        if (apellidos.isEmpty()) {
            etApellidos.setError("Ingrese apellidos");
            return;
        }

        userDocRef.update("nombres", nombres, "apellidos", apellidos)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Nombres actualizados correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
                });
    }
}
