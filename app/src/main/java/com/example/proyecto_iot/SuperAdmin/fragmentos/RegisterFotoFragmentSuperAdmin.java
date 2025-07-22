package com.example.proyecto_iot.SuperAdmin.fragmentos;


import android.util.Log;


import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.entity.AwsService;
import com.example.proyecto_iot.SuperAdmin.entity.UploadResponse;
import com.example.proyecto_iot.SuperAdmin.viewModels.UsuarioAdminViewModel;
import com.example.proyecto_iot.dtos.LogSA;
import com.example.proyecto_iot.dtos.Usuario;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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

public class RegisterFotoFragmentSuperAdmin extends Fragment {

    private String imagenSeleccionada = "";

    private ImageView fotoPerfil;
    private ImageButton botonSubirFoto;
    private Uri fotoUri;
    private UsuarioAdminViewModel viewModel;

    private ActivityResultLauncher<String> requestPermissionLauncher;
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<Intent> camaraLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inicializarLaunchers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_foto, container, false);

        fotoPerfil = view.findViewById(R.id.fotoPerfil);
        botonSubirFoto = view.findViewById(R.id.botonSubirFoto);
        viewModel = new ViewModelProvider(requireActivity()).get(UsuarioAdminViewModel.class);

        botonSubirFoto.setOnClickListener(v -> mostrarDialogoSeleccionFoto());

        Button botonRegresar = view.findViewById(R.id.botonRegresar);
        botonRegresar.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        Button botonSiguiente = view.findViewById(R.id.botonSiguiente);
        botonSiguiente.setOnClickListener(v -> {
            if (fotoUri == null) {
                Toast.makeText(getContext(), "Debes seleccionar o tomar una foto antes de continuar", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Subiendo imagen...", Toast.LENGTH_SHORT).show();

            subirImagenAWS(fotoUri, url -> {
                Usuario admin = viewModel.getUsuarioAdmin().getValue();

                if (admin == null) {
                    Toast.makeText(getContext(), "Error: datos incompletos", Toast.LENGTH_SHORT).show();
                    return;
                }

                admin.setUrlFotoPerfil(url);
                admin.setIdRol("Administrador");
                admin.setFechaRegistro(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));


                Log.d("DEBUG_ADMIN", "==== Datos finales del admin antes de registrar ====");
                Log.d("DEBUG_ADMIN", "Nombre: " + admin.getNombres());
                Log.d("DEBUG_ADMIN", "Apellidos: " + admin.getApellidos());
                Log.d("DEBUG_ADMIN", "Email: " + admin.getEmail());
                Log.d("DEBUG_ADMIN", "NumCelular: " + admin.getNumCelular());
                Log.d("DEBUG_ADMIN", "Direccion: " + admin.getDireccion());
                Log.d("DEBUG_ADMIN", "FechaNacimiento: " + admin.getFechaNacimiento());
                Log.d("DEBUG_ADMIN", "UrlFotoPerfil: " + admin.getUrlFotoPerfil());
                Log.d("DEBUG_ADMIN", "IdRol: " + admin.getIdRol());
                Log.d("DEBUG_ADMIN", "FechaRegistro: " + admin.getFechaRegistro());
                Log.d("DEBUG_ADMIN", "==============================================");





                String password = generateRandomPassword(12);
                crearAdminSinCerrarSesion(admin, password);
            });
        });


        return view;
    }

    private void inicializarLaunchers() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) abrirCamara();
                    else Toast.makeText(getContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                }
        );

        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
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
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
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
        new AlertDialog.Builder(getContext())
                .setTitle("Seleccionar foto de perfil")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) verificarPermisosCamara();
                    else abrirGaleria();
                })
                .show();
    }

    private void verificarPermisosCamara() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            abrirCamara();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void abrirCamara() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
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
            File file = new File(requireContext().getCacheDir(), "foto_admin_" + System.currentTimeMillis() + ".jpg");
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
            return Uri.fromFile(file);
        } catch (Exception e) {
            return null;
        }
    }


    private String generateRandomPassword(int length) {
        String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
        String CHAR_UPPER = CHAR_LOWER.toUpperCase();
        String NUMBER = "0123456789";
        String OTHER_CHAR = "!@#$%^&*()-_+=<>?/{}~"; // Caracteres especiales

        String PASSWORD_CHARS = CHAR_LOWER + CHAR_UPPER + NUMBER + OTHER_CHAR;
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(length);

        // Asegurar al menos una letra minúscula, una mayúscula, un número y un caracter especial
        sb.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        sb.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        sb.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
        sb.append(OTHER_CHAR.charAt(random.nextInt(OTHER_CHAR.length())));

        // Llenar el resto de la contraseña
        for (int i = 4; i < length; i++) { // Empezar desde 4 porque ya agregamos 4 caracteres
            sb.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }

        // Mezclar la cadena para que no tenga un patrón predecible
        char[] passwordArray = sb.toString().toCharArray();
        for (int i = passwordArray.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = passwordArray[i];
            passwordArray[i] = passwordArray[j];
            passwordArray[j] = temp;
        }

        return new String(passwordArray);
    }
    private void sendAdminCredentialsEmailIntent(String recipientEmail, String adminName, String generatedPassword) {
        String subject = "Bienvenido a nuestra plataforma - Credenciales de Administrador";
        String body = "Hola " + adminName + ",\n\n" +
                "¡Bienvenido como administrador a nuestra plataforma!\n" +
                "Tus credenciales de acceso son:\n" +
                "Usuario (Correo): " + recipientEmail + "\n" +
                "Contraseña: " + generatedPassword + "\n\n" +
                "Por favor, cambia tu contraseña después de iniciar sesión por primera vez.\n\n" +
                "Atentamente,\nEl Equipo de Desarrollo";

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // Solo aplicaciones de correo
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipientEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            startActivity(Intent.createChooser(emailIntent, "Enviar credenciales por correo..."));
            Toast.makeText(getContext(), "Correo con credenciales listo para enviar.", Toast.LENGTH_LONG).show();
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getContext(), "No hay clientes de correo instalados para enviar las credenciales.", Toast.LENGTH_LONG).show();
        }
    }

    private void subirImagenAWS(Uri uri, OnImageUploadCompleteListener listener) {
        try {
            File file = uriToFile(uri, "foto_admin_" + System.currentTimeMillis());
            if (file == null) {
                Toast.makeText(getContext(), "No se pudo procesar la imagen", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(getContext(), "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
                        listener.onComplete(imagenSeleccionada);
                    } else {
                        Toast.makeText(getContext(), "Error al subir imagen", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Toast.makeText(getContext(), "Fallo al subir imagen: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File uriToFile(Uri uri, String fileName) {
        try {
            File dir = new File(requireContext().getCacheDir(), "temp_images");
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName + ".jpg");

            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
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

    private void crearAdminSinCerrarSesion(Usuario nuevoAdmin, String contraseñaGenerada) {
        FirebaseApp firebaseApp;
        try {
            firebaseApp = FirebaseApp.getInstance("TempApp");
        } catch (IllegalStateException e) {
            firebaseApp = FirebaseApp.initializeApp(
                    requireContext(),
                    new FirebaseOptions.Builder()
                            .setApiKey("AIzaSyAsh4XzflE6kRCZkOaSqP0Nj0IKltdeiA4")
                            .setApplicationId("1:103898888660:android:4ee1725b49fafeee2bbe41")
                            .setProjectId("proyectoiot-784f3")
                            .build(),
                    "TempApp"
            );
        }


        FirebaseAuth tempAuth = FirebaseAuth.getInstance(firebaseApp);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final Usuario finalNuevoAdmin = nuevoAdmin;
        final String finalPassword = contraseñaGenerada;
        final FirebaseApp finalFirebaseApp = firebaseApp;
        final FirebaseAuth finalTempAuth = tempAuth;

        tempAuth.createUserWithEmailAndPassword(finalNuevoAdmin.getEmail(), finalPassword)
                .addOnSuccessListener(authResult -> {
                    String uid = authResult.getUser().getUid();
                    finalNuevoAdmin.setId(uid);

                    db.collection("usuarios").document(uid).set(finalNuevoAdmin)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Administrador agregado exitosamente.", Toast.LENGTH_SHORT).show();

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser == null) return;

                                String uidEditor = currentUser.getUid();

                                db.collection("usuarios").document(uidEditor).get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                Usuario usuarioEditor = documentSnapshot.toObject(Usuario.class);
                                                if (usuarioEditor == null) return;

                                                String nombreEditor = usuarioEditor.getNombres() + " " + usuarioEditor.getApellidos();
                                                String nombreNuevo = finalNuevoAdmin.getNombres() + " " + finalNuevoAdmin.getApellidos();

                                                LogSA log = new LogSA(
                                                        null,
                                                        "Registro de Administrador",
                                                        "Se registró al Administrador " + nombreNuevo,
                                                        nombreEditor,
                                                        "Super Admin",
                                                        uidEditor,
                                                        nombreNuevo,
                                                        new Date(),
                                                        "AdminRegistro"
                                                );

                                                db.collection("logs").add(log);
                                            }
                                        });

                                sendAdminCredentialsEmailIntent(
                                        finalNuevoAdmin.getEmail(),
                                        finalNuevoAdmin.getNombres() + " " + finalNuevoAdmin.getApellidos(),
                                        finalPassword
                                );


                                // Eliminar app temporal
                                finalTempAuth.signOut();
                                finalFirebaseApp.delete();

                                requireActivity().finish();

                            })
                            .addOnFailureListener(e -> {
                                authResult.getUser().delete();
                                Toast.makeText(getContext(), "Error al guardar en Firestore: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al registrar en Auth: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
    public interface OnImageUploadCompleteListener {
        void onComplete(String imageUrl);
    }

}
