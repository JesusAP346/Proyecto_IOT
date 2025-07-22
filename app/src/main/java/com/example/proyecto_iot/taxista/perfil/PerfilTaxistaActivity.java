package com.example.proyecto_iot.taxista.perfil;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.databinding.ActivityPerfilTaxistaBinding;
import com.example.proyecto_iot.administradorHotel.services.AwsService;
import com.example.proyecto_iot.administradorHotel.entity.UploadResponse;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

        mostrarDatosDesdeCache();
        cargarImagenInterna();
        initLaunchers();

        binding.btnBack.setOnClickListener(v -> {
            setResult(RESULT_OK);
            finish();
        });

        binding.btnEditar.setOnClickListener(v -> mostrarBottomSheetEditarFoto());

        cargarDatosDesdeFirestore();
    }

    private void mostrarDatosDesdeCache() {
        SharedPreferences prefs = getSharedPreferences("perfil", MODE_PRIVATE);
        String nombreCompleto = prefs.getString("nombreCompleto", "Cargando...");
        String datosAuto = prefs.getString("datosAuto", "");
        String urlFoto = prefs.getString("urlFotoPerfil", "");

        binding.tvNombreTaxista.setText(nombreCompleto);
        binding.tvDatosAuto.setText(datosAuto);
        binding.nombreeTaxista.setText("Confirmamos a : " + nombreCompleto.split(" ")[0]);

        if (!urlFoto.isEmpty()) {
            Picasso.get()
                    .load(urlFoto)
                    .placeholder(R.drawable.ic_perfil_circulo)
                    .error(R.drawable.ic_perfil_circulo)
                    .into(binding.ivFotoPerfil);
        } else {
            binding.ivFotoPerfil.setImageResource(R.drawable.ic_perfil_circulo);
        }
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

                    String nombreCompleto = nombres + " " + apellidos;
                    String datosAuto = "Color: " + color + "\nModelo: " + modelo + "\nPlaca: " + placa;

                    binding.tvNombreTaxista.setText(nombreCompleto);
                    binding.tvDatosAuto.setText(datosAuto);
                    binding.nombreeTaxista.setText("Confirmamos a : " + nombres);

                    if (urlFoto != null && !urlFoto.isEmpty()) {
                        Picasso.get()
                                .load(urlFoto)
                                .placeholder(R.drawable.ic_perfil_circulo)
                                .into(binding.ivFotoPerfil);
                    }

                    // Guardar en SharedPreferences
                    SharedPreferences.Editor editor = getSharedPreferences("perfil", MODE_PRIVATE).edit();
                    editor.putString("nombreCompleto", nombreCompleto);
                    editor.putString("datosAuto", datosAuto);
                    editor.putString("urlFotoPerfil", urlFoto);
                    editor.apply();
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
                            subirImagenAS3YActualizarFirestore(selectedImageUri);
                        }
                    }
                });

        takePhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && cameraImageUri != null) {
                        binding.ivFotoPerfil.setImageURI(cameraImageUri);
                        guardarImagenInterna(cameraImageUri);
                        subirImagenAS3YActualizarFirestore(cameraImageUri);
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

    private void subirImagenAS3YActualizarFirestore(Uri uri) {
        try {
            File file = uriToFile(uri, this);

            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            OkHttpClient client = new OkHttpClient.Builder().build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://mm5k8l79xd.execute-api.us-west-2.amazonaws.com/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AwsService awsService = retrofit.create(AwsService.class);

            awsService.subirImagen(body).enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String imageUrl = response.body().getUrl();
                        Log.d("S3UPLOAD", "Imagen subida: " + imageUrl);

                        String uid = auth.getCurrentUser().getUid();
                        db.collection("usuarios").document(uid)
                                .update("urlFotoPerfil", imageUrl)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(PerfilTaxistaActivity.this, "Foto actualizada correctamente", Toast.LENGTH_SHORT).show();
                                    setResult(RESULT_OK);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    e.printStackTrace();
                                    Toast.makeText(PerfilTaxistaActivity.this, "Error al actualizar Firestore", Toast.LENGTH_SHORT).show();
                                });

                    } else {
                        Toast.makeText(PerfilTaxistaActivity.this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Toast.makeText(PerfilTaxistaActivity.this, "Fallo: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("S3UPLOAD", "Error", t);
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("S3UPLOAD", "Excepción", e);
        }
    }

    private File uriToFile(Uri uri, Context context) throws Exception {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        File file = File.createTempFile("upload_temp", ".jpg", context.getCacheDir());
        OutputStream outputStream = new FileOutputStream(file);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();
        return file;
    }

    private void cargarImagenInterna() {
        try {
            File file = new File(getFilesDir(), "perfil_taxista.jpg");
            if (file.exists()) {
                Uri uri = Uri.fromFile(file);
                binding.ivFotoPerfil.setImageURI(uri);
            } else {
                binding.ivFotoPerfil.setImageResource(R.drawable.ic_perfil_circulo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            binding.ivFotoPerfil.setImageResource(R.drawable.ic_perfil_circulo);
        }
    }
}
