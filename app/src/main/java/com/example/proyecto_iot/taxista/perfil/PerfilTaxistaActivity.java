package com.example.proyecto_iot.taxista.perfil;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.proyecto_iot.databinding.ActivityPerfilTaxistaBinding;
import com.example.proyecto_iot.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;

public class PerfilTaxistaActivity extends AppCompatActivity {

    private ActivityPerfilTaxistaBinding binding;
    private Uri cameraImageUri;
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePhotoLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilTaxistaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        cargarImagenInterna(); // cargar imagen local

        initLaunchers();

        binding.btnBack.setOnClickListener(v -> finish());

        binding.btnEditar.setOnClickListener(v -> mostrarBottomSheetEditarFoto());

        cargarDatosDesdeFirestore(); // 🔥 Datos del taxista
    }

    private void cargarDatosDesdeFirestore() {
        if (auth.getCurrentUser() != null) {
            String uid = auth.getCurrentUser().getUid();

            db.collection("usuarios").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String nombres = documentSnapshot.getString("nombres");
                    String apellidos = documentSnapshot.getString("apellidos");
                    String color = documentSnapshot.getString("colorAuto");
                    String modelo = documentSnapshot.getString("modeloAuto");
                    String placa = documentSnapshot.getString("placaAuto");
                    String urlFoto = documentSnapshot.getString("urlFotoPerfil");

                    binding.tvNombreTaxista.setText(nombres + " " + apellidos);
                    binding.tvDatosAuto.setText("Color: " + color + "\nModelo: " + modelo + "\nPlaca: " + placa);
                    binding.nombreeTaxista.setText("Confirmamos a : "  + nombres);

                    if (urlFoto != null && !urlFoto.isEmpty()) {
                        Picasso.get().load(urlFoto).into(binding.ivFotoPerfil);
                    }
                }
            }).addOnFailureListener(e -> {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar perfil", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void mostrarBottomSheetEditarFoto() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_taxista, null);

        sheetView.findViewById(R.id.btnCambiarFoto).setOnClickListener(e -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            pickImageLauncher.launch(intent);
            dialog.dismiss();
        });

        sheetView.findViewById(R.id.btnTomarFoto).setOnClickListener(e -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                } else {
                    abrirCamara();
                }
            } else {
                abrirCamara();
            }
            dialog.dismiss();
        });

        sheetView.findViewById(R.id.btnListo).setOnClickListener(e -> {
            dialog.dismiss();
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
        });

        dialog.setContentView(sheetView);
        dialog.show();
    }

    private void initLaunchers() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            binding.ivFotoPerfil.setImageURI(selectedImageUri);
                            guardarImagenInterna(selectedImageUri);
                        }
                    }
                });

        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && cameraImageUri != null) {
                        binding.ivFotoPerfil.setImageURI(cameraImageUri);
                        guardarImagenInterna(cameraImageUri);
                    }
                });

        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        abrirCamara();
                    } else {
                        Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void abrirCamara() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Nueva Foto");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Foto tomada desde la app");

        cameraImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        takePhotoLauncher.launch(intent);
    }

    private void guardarImagenInterna(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            try (FileOutputStream fos = openFileOutput("perfil_taxista.jpg", MODE_PRIVATE)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarImagenInterna() {
        try {
            File file = new File(getFilesDir(), "perfil_taxista.jpg");
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                binding.ivFotoPerfil.setImageURI(uri);
            } else {
                binding.ivFotoPerfil.setImageResource(R.drawable.roberto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            binding.ivFotoPerfil.setImageResource(R.drawable.roberto);
        }
    }
}
