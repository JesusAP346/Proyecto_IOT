package com.example.proyecto_iot.cliente;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.entity.AwsService;
import com.example.proyecto_iot.SuperAdmin.entity.UploadResponse;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RegisterFotoPerfilClienteActivity extends AppCompatActivity {

    private ImageView fotoPerfil;
    private ImageButton botonSubirFoto;
    private Button botonGuardar, botonCancelar;

    private Uri fotoUri;
    private String imagenSeleccionada = "";

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private DocumentReference userDocRef;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<Intent> camaraLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_foto_perfil_cliente);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();
        String uid = auth.getCurrentUser().getUid();
        userDocRef = firestore.collection("usuarios").document(uid);

        fotoPerfil = findViewById(R.id.fotoPerfil);
        botonSubirFoto = findViewById(R.id.botonSubirFoto);
        botonGuardar = findViewById(R.id.botonGuardar);
        botonCancelar = findViewById(R.id.botonCancelar);

        inicializarLaunchers();
        cargarFotoActual();

        botonSubirFoto.setOnClickListener(v -> mostrarDialogoSeleccionFoto());

        botonGuardar.setOnClickListener(v -> {
            if (fotoUri != null) {
                subirImagenAWS(fotoUri, url -> {
                    userDocRef.update("urlFotoPerfil", url)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "Foto actualizada correctamente", Toast.LENGTH_SHORT).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(this, "Error al actualizar foto", Toast.LENGTH_SHORT).show();
                            });
                });
            } else {
                Toast.makeText(this, "Selecciona una foto antes de guardar", Toast.LENGTH_SHORT).show();
            }
        });

        botonCancelar.setOnClickListener(v -> finish());
    }

    private void cargarFotoActual() {
        userDocRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                if (usuario != null && usuario.getUrlFotoPerfil() != null && !usuario.getUrlFotoPerfil().isEmpty()) {
                    Picasso.get().load(usuario.getUrlFotoPerfil()).placeholder(R.drawable.ic_generic_user).into(fotoPerfil);
                    hacerImagenCircular(fotoPerfil);
                }
            }
        });
    }

    private void inicializarLaunchers() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) abrirCamara();
                    else Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
        );

        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        fotoUri = result.getData().getData();
                        fotoPerfil.setImageURI(fotoUri);
                        fotoPerfil.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        hacerImagenCircular(fotoPerfil);
                    }
                }
        );

        camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap imageBitmap = (Bitmap) result.getData().getExtras().get("data");
                        fotoPerfil.setImageBitmap(imageBitmap);
                        fotoPerfil.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        hacerImagenCircular(fotoPerfil);
                        fotoUri = guardarBitmapComoFile(imageBitmap);
                    }
                }
        );
    }

    private void mostrarDialogoSeleccionFoto() {
        String[] opciones = {"Tomar foto", "Seleccionar de galería"};
        new AlertDialog.Builder(this)
                .setTitle("Seleccionar foto de perfil")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) verificarPermisosCamara();
                    else abrirGaleria();
                })
                .show();
    }

    private void verificarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            camaraLauncher.launch(intent);
        }
    }

    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        galeriaLauncher.launch(intent);
    }

    private void hacerImagenCircular(ImageView imageView) {
        imageView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setOval(0, 0, view.getWidth(), view.getHeight());
            }
        });
        imageView.setClipToOutline(true);
    }

    private Uri guardarBitmapComoFile(Bitmap bitmap) {
        try {
            File file = new File(getCacheDir(), "foto_perfil_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
            return Uri.fromFile(file);
        } catch (Exception e) {
            return null;
        }
    }

    private void subirImagenAWS(Uri uri, OnImageUploadCompleteListener listener) {
        try {
            File file = uriToFile(uri, "foto_perfil_" + System.currentTimeMillis());
            if (file == null) {
                Toast.makeText(this, "No se pudo procesar la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://mm5k8l79xd.execute-api.us-west-2.amazonaws.com/")
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            AwsService service = retrofit.create(AwsService.class);

            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/jpeg"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            service.subirImagen(body).enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        imagenSeleccionada = response.body().getUrl();
                        Picasso.get().load(imagenSeleccionada).into(fotoPerfil);
                        listener.onComplete(imagenSeleccionada);
                    } else {
                        Toast.makeText(RegisterFotoPerfilClienteActivity.this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Toast.makeText(RegisterFotoPerfilClienteActivity.this, "Fallo al subir imagen: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File uriToFile(Uri uri, String fileName) {
        try {
            File dir = new File(getCacheDir(), "temp_images");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName + ".jpg");

            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface OnImageUploadCompleteListener {
        void onComplete(String imageUrl);
    }
}
