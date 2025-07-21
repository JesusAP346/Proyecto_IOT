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
import androidx.core.content.ContextCompat;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.dtos.LogSA;
import com.example.proyecto_iot.dtos.Usuario;
import com.example.proyecto_iot.SuperAdmin.entity.AwsService;
import com.example.proyecto_iot.SuperAdmin.entity.UploadResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
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

public class RegistroTaxistaActivity extends AppCompatActivity {

    private EditText etNumeroPlaca, etModeloAuto, etColorAuto;
    private ImageView ivFotoPlaca, ivFotoAuto;
    private Button btnTomarFotoPlaca, btnTomarFotoAuto, btnEnviar;

    private boolean fotoPlacaTomada = false;
    private boolean fotoAutoTomada = false;
    private boolean isCapturingPlaca = false;

    private static final int CAMERA_PERMISSION_CODE = 100;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    private Bitmap bitmapFotoPlaca;
    private Bitmap bitmapFotoAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_taxista);

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
                                    bitmapFotoPlaca = imageBitmap;
                                    ivFotoPlaca.setImageBitmap(imageBitmap);
                                    fotoPlacaTomada = true;
                                    btnTomarFotoPlaca.setText("Foto de Placa ✓");
                                } else {
                                    bitmapFotoAuto = imageBitmap;
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            cameraLauncher.launch(takePictureIntent);
        } else {
            Toast.makeText(this, "No se encontró una aplicación de cámara", Toast.LENGTH_SHORT).show();
        }
    }

    private void enviarInformacion() {
        String numeroPlaca = etNumeroPlaca.getText().toString().trim();
        String modeloAuto = etModeloAuto.getText().toString().trim();
        String colorAuto = etColorAuto.getText().toString().trim();

        if (numeroPlaca.isEmpty() || modeloAuto.isEmpty() || colorAuto.isEmpty()
                || !fotoPlacaTomada || !fotoAutoTomada) {
            Toast.makeText(this, "Completa todos los campos y toma las fotos", Toast.LENGTH_SHORT).show();
            return;
        }

        File filePlaca = bitmapToFile(bitmapFotoPlaca, "foto_placa_" + mAuth.getUid());
        subirImagenAWS(filePlaca, urlPlaca -> {
            if (urlPlaca == null) return;

            File fileAuto = bitmapToFile(bitmapFotoAuto, "foto_auto_" + mAuth.getUid());
            subirImagenAWS(fileAuto, urlAuto -> {
                if (urlAuto == null) return;
                actualizarDatosEnFirebase(numeroPlaca, modeloAuto, colorAuto, urlPlaca, urlAuto);
            });
        });
    }

    private File bitmapToFile(Bitmap bitmap, String fileName) {
        try {
            File dir = new File(getCacheDir(), "temp_images");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName + ".jpg");

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void subirImagenAWS(File file, OnUrlLista callback) {
        if (file == null) {
            Toast.makeText(this, "Archivo inválido", Toast.LENGTH_SHORT).show();
            callback.onUrlLista(null);
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
                    callback.onUrlLista(response.body().getUrl());
                } else {
                    Toast.makeText(RegistroTaxistaActivity.this, "Error al subir imagen", Toast.LENGTH_SHORT).show();
                    callback.onUrlLista(null);
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Toast.makeText(RegistroTaxistaActivity.this, "Fallo al subir imagen: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                callback.onUrlLista(null);
            }
        });
    }

    private void actualizarDatosEnFirebase(String placa, String modelo, String color, String urlPlaca, String urlAuto) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            db.collection("usuarios").document(userId).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    if (usuario != null) {
                        usuario.setPlacaAuto(placa);
                        usuario.setModeloAuto(modelo);
                        usuario.setColorAuto(color);
                        usuario.setEstadoSolicitudTaxista("pendiente");
                        usuario.setUrlFotoPlaca(urlPlaca);
                        usuario.setUrlFotoAuto(urlAuto);

                        db.collection("usuarios").document(userId).set(usuario).addOnSuccessListener(aVoid -> {






                            LogSA log = new LogSA(
                                    null,
                                    "Postulación a taxista",
                                    "El usuario " + usuario.getNombres() + " envió una solicitud para ser taxista",
                                    usuario.getNombres() + " " + usuario.getApellidos() ,
                                    "Super Admin",
                                    userId,
                                    usuario.getNombres() + " " + usuario.getApellidos(),
                                    new Date(),
                                    "SolicitudTaxista"

                            );

                            FirebaseFirestore.getInstance().collection("logs").add(log);




                            Toast.makeText(this, "Postulación enviada con éxito", Toast.LENGTH_LONG).show();
                            finish();
                        });
                    }
                }
            });
        }
    }

    interface OnUrlLista {
        void onUrlLista(String url);
    }
}
