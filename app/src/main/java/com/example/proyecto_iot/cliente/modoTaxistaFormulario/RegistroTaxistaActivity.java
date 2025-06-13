package com.example.proyecto_iot.cliente.modoTaxistaFormulario;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
// Importa tu clase Usuario
// import com.example.proyecto_iot.modelo.Usuario;

public class RegistroTaxistaActivity extends AppCompatActivity {

    private EditText etNumeroPlaca, etModeloAuto, etColorAuto;
    private ImageView ivFotoPlaca, ivFotoAuto;
    private Button btnTomarFotoPlaca, btnTomarFotoAuto, btnEnviar;

    private boolean fotoPlacaTomada = false;
    private boolean fotoAutoTomada = false;
    private boolean isCapturingPlaca = false;

    private static final int CAMERA_PERMISSION_CODE = 100;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // Launcher para capturar fotos
    private ActivityResultLauncher<Intent> cameraLauncher;

    // Launcher para permisos
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_taxista);

        // Inicializar Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        initViews();
        setupCameraLauncher();
        setupPermissionLauncher();
        setupListeners();
    }

    private void initViews() {
        etNumeroPlaca = findViewById(R.id.etNumeroPlaca);
        etModeloAuto = findViewById(R.id.etModeloAuto);
        etColorAuto = findViewById(R.id.etColorAuto);
        ivFotoPlaca = findViewById(R.id.ivFotoPlaca);
        ivFotoAuto = findViewById(R.id.ivFotoAuto);
        btnTomarFotoPlaca = findViewById(R.id.btnTomarFotoPlaca);
        btnTomarFotoAuto = findViewById(R.id.btnTomarFotoAuto);
        btnEnviar = findViewById(R.id.btnEnviar);
    }

    private void setupCameraLauncher() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        if (extras != null) {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");

                            if (imageBitmap != null) {
                                if (isCapturingPlaca) {
                                    ivFotoPlaca.setImageBitmap(imageBitmap);
                                    fotoPlacaTomada = true;
                                    btnTomarFotoPlaca.setText("Foto de Placa ✓");
                                } else {
                                    ivFotoAuto.setImageBitmap(imageBitmap);
                                    fotoAutoTomada = true;
                                    btnTomarFotoAuto.setText("Foto del Auto ✓");
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "No se pudo capturar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupPermissionLauncher() {
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        abrirCamara();
                    } else {
                        Toast.makeText(this, "Se necesita permiso de cámara para tomar fotos", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void setupListeners() {
        btnTomarFotoPlaca.setOnClickListener(v -> {
            isCapturingPlaca = true;
            verificarPermisoYAbrirCamara();
        });

        btnTomarFotoAuto.setOnClickListener(v -> {
            isCapturingPlaca = false;
            verificarPermisoYAbrirCamara();
        });

        btnEnviar.setOnClickListener(v -> enviarInformacion());
    }

    private void verificarPermisoYAbrirCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Verificar que hay una app de cámara disponible
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                cameraLauncher.launch(takePictureIntent);
            } catch (Exception e) {
                Toast.makeText(this, "Error al abrir la cámara: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarInformacion() {
        String numeroPlaca = etNumeroPlaca.getText().toString().trim();
        String modeloAuto = etModeloAuto.getText().toString().trim();
        String colorAuto = etColorAuto.getText().toString().trim();

        // Validar que todos los campos estén completos
        if (numeroPlaca.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el número de placa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (modeloAuto.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el modelo del auto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (colorAuto.isEmpty()) {
            Toast.makeText(this, "Por favor ingrese el color del auto", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fotoPlacaTomada) {
            Toast.makeText(this, "Por favor tome la foto de la placa", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!fotoAutoTomada) {
            Toast.makeText(this, "Por favor tome la foto del auto", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar datos en Firebase
        actualizarDatosEnFirebase(numeroPlaca, modeloAuto, colorAuto);
    }

    private void actualizarDatosEnFirebase(String placaAuto, String modeloAuto, String colorAuto) {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Primero obtener el usuario actual de Firestore
            db.collection("usuarios").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Convertir el documento a objeto Usuario
                            Usuario usuario = documentSnapshot.toObject(Usuario.class);

                            if (usuario != null) {
                                // Actualizar los campos del usuario
                                usuario.setPlacaAuto(placaAuto);
                                usuario.setModeloAuto(modeloAuto);
                                usuario.setColorAuto(colorAuto);
                                usuario.setEstadoSolicitudTaxista("pendiente");

                                // Guardar el usuario actualizado en Firestore
                                db.collection("usuarios").document(userId)
                                        .set(usuario)
                                        .addOnSuccessListener(aVoid -> {
                                            // Éxito
                                            String mensaje = "Información registrada exitosamente:\n" +
                                                    "Número de Placa: " + placaAuto + "\n" +
                                                    "Modelo: " + modeloAuto + "\n" +
                                                    "Color: " + colorAuto + "\n" +
                                                    "Estado: Pendiente de aprobación";

                                            Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

                                            // Opcional: cerrar la actividad o navegar a otra pantalla
                                            finish();
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error al actualizar usuario: " + e.getMessage(),
                                                    Toast.LENGTH_LONG).show();
                                        });
                            } else {
                                Toast.makeText(this, "Error al convertir datos del usuario", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Usuario no encontrado en la base de datos", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al obtener datos del usuario: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
        } else {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show();
        }
    }
}