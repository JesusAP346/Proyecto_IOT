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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.proyecto_iot.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileOutputStream;

public class PerfilTaxistaActivity extends AppCompatActivity {
    private ImageView imageView;        // Imagen dentro del BottomSheet
    private ImageView ivFotoPerfil;     // Imagen en el layout principal
    private Uri cameraImageUri;

    private ActivityResultLauncher<Intent> pickImageLauncher;
    private ActivityResultLauncher<Intent> takePhotoLauncher;
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_taxista);

        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);
        cargarImagenInterna(); // Carga la imagen guardada (o imagen por defecto)

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        TextView btnEditar = findViewById(R.id.btnEditar);

        initLaunchers();

        btnEditar.setOnClickListener(v -> {
            BottomSheetDialog dialog = new BottomSheetDialog(PerfilTaxistaActivity.this);
            View sheetView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_taxista, null);

            TextView btnCambiarGaleria = sheetView.findViewById(R.id.btnCambiarFoto);
            TextView btnTomarFoto = sheetView.findViewById(R.id.btnTomarFoto);
            imageView = sheetView.findViewById(R.id.ivFoto);

            TextView btnListo = sheetView.findViewById(R.id.btnListo);

            // Carga la imagen guardada o por defecto para mostrar en el BottomSheet
            try {
                String filename = "perfil_taxista.jpg";
                File file = new File(getFilesDir(), filename);
                if (file.exists()) {
                    Uri uri = Uri.fromFile(file);
                    imageView.setImageURI(uri);
                } else {
                    imageView.setImageResource(R.drawable.roberto);
                }
            } catch (Exception e) {
                e.printStackTrace();
                imageView.setImageResource(R.drawable.roberto);
            }

            btnCambiarGaleria.setOnClickListener(e -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                pickImageLauncher.launch(intent);
                dialog.dismiss();
            });

            btnTomarFoto.setOnClickListener(e -> {
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

            btnListo.setOnClickListener(e -> {
                dialog.dismiss();
                Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
            });

            dialog.setContentView(sheetView);
            dialog.show();
        });
    }

    private void initLaunchers() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            imageView.setImageURI(selectedImageUri);
                            ivFotoPerfil.setImageURI(selectedImageUri);
                            guardarImagenInterna(selectedImageUri);
                        }
                    }
                });

        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        if (cameraImageUri != null) {
                            imageView.setImageURI(cameraImageUri);
                            ivFotoPerfil.setImageURI(cameraImageUri);
                            guardarImagenInterna(cameraImageUri);
                        }
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
            String filename = "perfil_taxista.jpg";
            try (FileOutputStream fos = openFileOutput(filename, MODE_PRIVATE)) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarImagenInterna() {
        try {
            String filename = "perfil_taxista.jpg";
            File file = new File(getFilesDir(), filename);
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                ivFotoPerfil.setImageURI(uri);
            } else {
                ivFotoPerfil.setImageResource(R.drawable.roberto);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ivFotoPerfil.setImageResource(R.drawable.roberto);
        }
    }
}
