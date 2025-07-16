package com.example.proyecto_iot.cliente;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivityDniCliente extends AppCompatActivity {

    private TextInputLayout inputTipoDoc, layoutNumeroDoc;
    private AutoCompleteTextView dropdownTipoDoc;
    private TextInputEditText editTextNumeroDoc;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private CollectionReference usuariosRef;
    private String uidActual;

    private final String[] tiposDocumento = {"DNI", "Carnet de Extranjería", "Pasaporte"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_dni_cliente);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        uidActual = auth.getCurrentUser().getUid();
        usuariosRef = firestore.collection("usuarios");

        inputTipoDoc = findViewById(R.id.textInputLayoutTipoDoc);
        layoutNumeroDoc = findViewById(R.id.textInputLayoutNumeroDoc);
        dropdownTipoDoc = findViewById(R.id.dropdownTipoDoc);
        editTextNumeroDoc = findViewById(R.id.editTextNumeroDoc);

        Button btnGuardar = findViewById(R.id.botonGuardar);
        Button btnCancelar = findViewById(R.id.botonCancelar);

        dropdownTipoDoc.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, tiposDocumento));

        cargarDatosActuales();

        btnGuardar.setOnClickListener(v -> guardarCambios());
        btnCancelar.setOnClickListener(v -> finish());
    }

    private void cargarDatosActuales() {
        usuariosRef.document(uidActual).get().addOnSuccessListener(snapshot -> {
            if (snapshot.exists()) {
                Usuario usuario = snapshot.toObject(Usuario.class);
                if (usuario != null) {
                    if (!TextUtils.isEmpty(usuario.getTipoDocumento())) {
                        dropdownTipoDoc.setText(usuario.getTipoDocumento(), false);
                    }
                    if (!TextUtils.isEmpty(usuario.getNumDocumento())) {
                        editTextNumeroDoc.setText(usuario.getNumDocumento());
                    }
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al cargar datos", Toast.LENGTH_SHORT).show());
    }

    private void guardarCambios() {
        inputTipoDoc.setError(null);
        layoutNumeroDoc.setError(null);

        String tipoDocumento = dropdownTipoDoc.getText() != null ? dropdownTipoDoc.getText().toString().trim() : "";
        String numDocumento = editTextNumeroDoc.getText() != null ? editTextNumeroDoc.getText().toString().trim() : "";

        boolean isValid = true;

        if (tipoDocumento.isEmpty()) {
            inputTipoDoc.setError("Debe seleccionar un tipo de documento");
            isValid = false;
        }

        if (numDocumento.isEmpty()) {
            layoutNumeroDoc.setError("Este campo es obligatorio");
            isValid = false;
        } else if (!numDocumento.matches("\\d+")) {
            layoutNumeroDoc.setError("Solo se permiten números");
            isValid = false;
        } else if (tipoDocumento.equals("DNI") && numDocumento.length() != 8) {
            layoutNumeroDoc.setError("DNI debe tener 8 dígitos");
            isValid = false;
        }

        if (!isValid) return;

        usuariosRef.whereEqualTo("numDocumento", numDocumento).get().addOnSuccessListener(querySnapshot -> {
            boolean documentoUsado = false;
            for (var doc : querySnapshot.getDocuments()) {
                if (!doc.getId().equals(uidActual)) {
                    documentoUsado = true;
                    break;
                }
            }

            if (documentoUsado) {
                layoutNumeroDoc.setError("Número de documento ya registrado");
            } else {
                usuariosRef.document(uidActual).update(
                        "tipoDocumento", tipoDocumento,
                        "numDocumento", numDocumento
                ).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Documento actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                }).addOnFailureListener(e -> Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show());
            }
        }).addOnFailureListener(e -> Toast.makeText(this, "Error al validar documento", Toast.LENGTH_SHORT).show());
    }
}
