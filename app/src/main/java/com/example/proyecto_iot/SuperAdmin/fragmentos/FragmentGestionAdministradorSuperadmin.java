package com.example.proyecto_iot.SuperAdmin.fragmentos;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.example.proyecto_iot.dtos.LogSA;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_iot.SuperAdmin.entity.AwsService;
import com.example.proyecto_iot.SuperAdmin.entity.UploadResponse;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.proyecto_iot.dtos.Usuario; // Correct DTO import

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.google.android.material.textfield.TextInputEditText;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class FragmentGestionAdministradorSuperadmin extends Fragment {

    // Removed AdminDataStore and AdministradoresDomain imports
    // com.example.proyecto_iot.SuperAdmin.AdminDataStore;
    // com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String imagenSeleccionada = ""; // Stores the URI string or URL

    private ImageView ivFoto;

    // References to UI elements
    private TextInputEditText etNombre, etApellidos, etNumero, etCorreo, etDireccion, etFechaNacimiento;
    private TextInputLayout layoutNombre, layoutApellidos, layoutNumero, layoutCorreo, layoutDireccion, layoutFechaNacimiento;
    private TextView tvErrorImagen;
    private android.widget.Button btnEditarGuardar;

    // Fields to hold the data passed to the fragment
    private Usuario currentAdmin; // Holds the Usuario object being managed
    private boolean isEditMode;   // True if editing an existing admin, false if creating new
    private int originalPosition; // This is less relevant with Firestore, but kept for consistency if you use it for RecyclerView animations etc.

    // Firebase Firestore instance
    private FirebaseFirestore db;

    public FragmentGestionAdministradorSuperadmin() {
        // Constructor vacío requerido
    }

    // --- MODIFICACIÓN CLAVE: newInstance AHORA RECIBE UN OBJETO USUARIO ---
    public static FragmentGestionAdministradorSuperadmin newInstance(Usuario admin, boolean isEditMode, int index) {
        FragmentGestionAdministradorSuperadmin fragment = new FragmentGestionAdministradorSuperadmin();
        Bundle args = new Bundle();
        // Passed the entire Usuario object (must be Serializable)
        args.putSerializable("admin_object", admin);
        args.putBoolean("is_edit_mode", isEditMode);
        args.putInt("original_position", index); // Renamed to 'original_position' for clarity
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // Retrieve arguments
        if (getArguments() != null) {
            // Cast to Usuario
            currentAdmin = (Usuario) getArguments().getSerializable("admin_object");
            isEditMode = getArguments().getBoolean("is_edit_mode", false);
            originalPosition = getArguments().getInt("original_position", -1);
        }

        // Initialize imagePickerLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            subirImagenAWS(imageUri);  // ✅ Sube la imagen y guarda la URL real en imagenSeleccionada
                        }
                    }
                }
        );

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_administrador_superadmin, container, false);

        // Initialize UI components
        etNombre = view.findViewById(R.id.etNombre);
        etApellidos = view.findViewById(R.id.etApellidos);
        etNumero = view.findViewById(R.id.etNumero);
        etCorreo = view.findViewById(R.id.etCorreo);
        etDireccion = view.findViewById(R.id.etDireccion);
        etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        ivFoto = view.findViewById(R.id.ivFotoAdmin);
        btnEditarGuardar = view.findViewById(R.id.btnEditarGuardar);

        // Initialize TextInputLayouts for error handling
        layoutNombre = view.findViewById(R.id.layoutNombre);
        layoutApellidos = view.findViewById(R.id.layoutApellidos);
        layoutNumero = view.findViewById(R.id.layoutNumero);
        layoutCorreo = view.findViewById(R.id.layoutCorreo);
        layoutDireccion = view.findViewById(R.id.layoutDireccion);
        layoutFechaNacimiento = view.findViewById(R.id.layoutFechaNacimiento);
        tvErrorImagen = view.findViewById(R.id.tvErrorImagen); // Ensure this TextView exists in your layout

        // Populate fields based on currentAdmin object and mode
        if (currentAdmin != null) {
            etNombre.setText(currentAdmin.getNombres()); // Use getNombres()
            etApellidos.setText(currentAdmin.getApellidos());
            etNumero.setText(currentAdmin.getNumCelular()); // Use getNumCelular()
            etCorreo.setText(currentAdmin.getEmail());
            etDireccion.setText(currentAdmin.getDireccion());
            etFechaNacimiento.setText(currentAdmin.getFechaNacimiento());

            imagenSeleccionada = currentAdmin.getUrlFotoPerfil() != null ? currentAdmin.getUrlFotoPerfil() : "";

            if (!imagenSeleccionada.isEmpty()) {
                Picasso.get().load(imagenSeleccionada).into(ivFoto);
            } else {
                ivFoto.setImageResource(R.drawable.ic_generic_user); // Set a default image if none
            }
        } else {
            // If currentAdmin is null, it means we are in "add new" mode.
            // Initialize an empty Usuario object.
            currentAdmin = new Usuario();
            currentAdmin.setIdRol("Administrador"); // Default role for new admin
            isEditMode = true; // For new users, start directly in edit/input mode
            btnEditarGuardar.setText("Guardar"); // For new users, the button says "Guardar"
        }

        // Set initial enabled state of fields and button text
        etNombre.setEnabled(isEditMode);
        etApellidos.setEnabled(isEditMode);
        etNumero.setEnabled(isEditMode);
        etCorreo.setEnabled(isEditMode);
        etDireccion.setEnabled(isEditMode);
        etFechaNacimiento.setEnabled(isEditMode);
        // ivFoto is enabled for picking only in edit mode
        ivFoto.setClickable(isEditMode);

        btnEditarGuardar.setText(isEditMode ? "Guardar" : "Editar");

        // Click listener for image selection
        ivFoto.setOnClickListener(v -> {
            if (isEditMode) { // Only allow image selection in edit mode
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                imagePickerLauncher.launch(intent);
            }
        });

        // Click listener for date picker
        etFechaNacimiento.setOnClickListener(v -> {
            if (isEditMode) { // Only allow date picking in edit mode
                final Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getContext(),
                        (view1, selectedYear, selectedMonth, selectedDay) -> {
                            String fecha = String.format(Locale.getDefault(), "%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                            etFechaNacimiento.setText(fecha);
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });


        btnEditarGuardar.setOnClickListener(v -> {
            // Referencias a Firebase (asegúrate de que db y auth estén inicializados en tu Fragmento)
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance(); // Inicializar FirebaseAuth

            if (!isEditMode) { // Si actualmente está en modo "ver", cambia a modo "editar"
                isEditMode = true;
                btnEditarGuardar.setText("Guardar");
                etNombre.setEnabled(true);
                etApellidos.setEnabled(true);
                etNumero.setEnabled(true);
                etCorreo.setEnabled(true);
                etDireccion.setEnabled(true);
                etFechaNacimiento.setEnabled(true);
                ivFoto.setClickable(true); // Permitir selección de imagen
                // Considera habilitar un campo para la contraseña si es un nuevo registro
                // etPassword.setVisibility(View.VISIBLE); // Si tienes un campo de contraseña oculto
            } else { // Si actualmente está en modo "editar", intenta guardar
                if (!validarCampos(view)) {
                    return; // Detener si la validación falla
                }

                // Actualizar el objeto currentAdmin con los nuevos valores de la UI
                currentAdmin.setNombres(etNombre.getText().toString().trim());
                currentAdmin.setApellidos(etApellidos.getText().toString().trim());
                currentAdmin.setNumCelular(etNumero.getText().toString().trim());
                currentAdmin.setEmail(etCorreo.getText().toString().trim());
                currentAdmin.setDireccion(etDireccion.getText().toString().trim());
                currentAdmin.setFechaNacimiento(etFechaNacimiento.getText().toString().trim());

                // Manejar la imagen seleccionada. Asumo que 'imagenSeleccionada' es la URL de la imagen subida.
                // Si 'imagenSeleccionada' es un URI local, deberías subirla a Firebase Storage AQUÍ
                // antes de guardar la URL en currentAdmin.setUrlFotoPerfil().
                // Por simplicidad, asumo que ya tienes la URL final en 'imagenSeleccionada'.
                currentAdmin.setUrlFotoPerfil(imagenSeleccionada);

                // Establecer propiedades fijas para administradores
                currentAdmin.setIdRol("Administrador"); // Asegurar que el rol sea "Administrador"
                currentAdmin.setEstadoCuenta(true); // Asumiendo cuenta activa
                currentAdmin.setUltimaActualizacion(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                currentAdmin.setActualizadoPor("superadmin"); // Obtener el ID/nombre real del superadmin

                // --- LÓGICA PARA GUARDAR EN FIRESTORE ---
                if (currentAdmin.getId() != null && !currentAdmin.getId().isEmpty()) {
                    // 🔄 Editar (Actualizar documento existente) - No requiere Firebase Auth para la edición de datos de perfil
                    db.collection("usuarios").document(currentAdmin.getId())
                            .set(currentAdmin)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Administrador actualizado exitosamente", Toast.LENGTH_SHORT).show();

                                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                if (currentUser != null) {
                                    String uidEditor = currentUser.getUid();

                                    FirebaseFirestore.getInstance().collection("usuarios")
                                            .document(uidEditor)
                                            .get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    Usuario editor = documentSnapshot.toObject(Usuario.class);
                                                    String nombreEditor = editor.getNombres() + " " + editor.getApellidos();

                                                    // Suponiendo que ya tienes el usuario editado (adminEditado)
                                                    String nombreEditado = etNombre.getText().toString().trim() + " " + etApellidos.getText().toString().trim();

                                                    LogSA log = new LogSA(
                                                            null,
                                                            "Edición de Administrador",
                                                            "Se modificó al Administrador " + nombreEditado,
                                                            nombreEditor,
                                                            "Super Admin",
                                                            "Administrador",
                                                            uidEditor,
                                                            nombreEditado,
                                                            new Date(),
                                                            "Edición de usuario"
                                                    );

                                                    FirebaseFirestore.getInstance().collection("logs").add(log);
                                                }
                                            });
                                }


                                isEditMode = false;
                                btnEditarGuardar.setText("Editar");
                                etNombre.setEnabled(false);
                                etApellidos.setEnabled(false);
                                etNumero.setEnabled(false);
                                etCorreo.setEnabled(false);
                                etDireccion.setEnabled(false);
                                etFechaNacimiento.setEnabled(false);
                                ivFoto.setClickable(false);
                                requireActivity().getSupportFragmentManager().popBackStack();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar administrador: " + e.getMessage(), Toast.LENGTH_LONG).show());
                } else {
                    // ➕ Crear (Añadir nuevo documento) - ¡¡Aquí integramos Firebase Auth y el correo!!
                    // 1. Generar contraseña aleatoria
                    String generatedPassword = generateRandomPassword(12); // Contraseña de 12 caracteres

                    currentAdmin.setFechaRegistro(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));

                    // PASO 1: Crear usuario en Firebase Authentication con la contraseña generada
                    crearAdminSinCerrarSesion(currentAdmin, generatedPassword);

                }
            }
        });

        return view;
    }

    // --- VALIDACIÓN DE CAMPOS ---
    private boolean validarCampos(View view) {
        // Limpiar errores anteriores
        layoutNombre.setErrorEnabled(false);
        layoutApellidos.setErrorEnabled(false);
        layoutNumero.setErrorEnabled(false);
        layoutCorreo.setErrorEnabled(false);
        layoutDireccion.setErrorEnabled(false);
        layoutFechaNacimiento.setErrorEnabled(false);
        tvErrorImagen.setVisibility(View.GONE);

        boolean esValido = true;

        // Nombre
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            layoutNombre.setError("Este campo es obligatorio");
            esValido = false;
        } else if (nombre.length() < 2) {
            layoutNombre.setError("Debe tener al menos 2 caracteres");
            esValido = false;
        }

        // Apellidos
        String apellidos = etApellidos.getText().toString().trim();
        if (apellidos.isEmpty()) {
            layoutApellidos.setError("Este campo es obligatorio");
            esValido = false;
        } else if (apellidos.length() < 2) {
            layoutApellidos.setError("Debe tener al menos 2 caracteres");
            esValido = false;
        }

        // Número de celular
        String numero = etNumero.getText().toString().trim();
        if (numero.isEmpty()) {
            layoutNumero.setError("Este campo es obligatorio");
            esValido = false;
        } else if (!numero.matches("\\d{9}")) {
            layoutNumero.setError("Debe tener exactamente 9 dígitos");
            esValido = false;
        }

        // Correo
        String correo = etCorreo.getText().toString().trim();
        if (correo.isEmpty()) {
            layoutCorreo.setError("Este campo es obligatorio");
            esValido = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            layoutCorreo.setError("Correo electrónico inválido");
            esValido = false;
        }

        // Dirección
        String direccion = etDireccion.getText().toString().trim();
        if (direccion.isEmpty()) {
            layoutDireccion.setError("Este campo es obligatorio");
            esValido = false;
        }

        // Fecha de nacimiento
        String fecha = etFechaNacimiento.getText().toString().trim();
        if (fecha.isEmpty()) {
            layoutFechaNacimiento.setError("Debe ingresar una fecha de nacimiento");
            esValido = false;
        } else if (!fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {
            layoutFechaNacimiento.setError("Formato esperado: dd/MM/yyyy");
            esValido = false;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                sdf.setLenient(false); // Make sure parsing is strict
                Date fechaNacimiento = sdf.parse(fecha);
                Calendar hoy = Calendar.getInstance();
                Calendar fechaNac = Calendar.getInstance();
                fechaNac.setTime(fechaNacimiento);

                int edad = hoy.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
                if (hoy.get(Calendar.DAY_OF_YEAR) < fechaNac.get(Calendar.DAY_OF_YEAR)) {
                    edad--;
                }

                if (edad < 18) {
                    layoutFechaNacimiento.setError("Debes tener al menos 18 años");
                    esValido = false;
                } else if (fechaNac.after(hoy)) { // Check if date is in the future
                    layoutFechaNacimiento.setError("La fecha no puede ser en el futuro");
                    esValido = false;
                }

            } catch (ParseException e) {
                layoutFechaNacimiento.setError("Fecha inválida");
                esValido = false;
            }
        }

        if (imagenSeleccionada == null || imagenSeleccionada.isEmpty()) {
            tvErrorImagen.setVisibility(View.VISIBLE);
            esValido = false;
        }

        return esValido;
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

    private void subirImagenAWS(Uri uri) {
        try {
            File file = uriToFile(uri, "foto_admin_" + System.currentTimeMillis());
            if (file == null) {
                Toast.makeText(getContext(), "No se pudo procesar la imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(getContext(), "Subiendo imagen...", Toast.LENGTH_SHORT).show();

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
                        imagenSeleccionada = response.body().getUrl(); // Se guarda la URL
                        Picasso.get().load(imagenSeleccionada).into(ivFoto);
                        Toast.makeText(getContext(), "Imagen subida correctamente", Toast.LENGTH_SHORT).show();
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
        FirebaseApp firebaseApp = FirebaseApp.initializeApp(
                requireContext(),
                new FirebaseOptions.Builder()
                        .setApiKey("AIzaSyAsh4XzflE6kRCZkOaSqP0Nj0IKltdeiA4") // ← current_key
                        .setApplicationId("1:103898888660:android:4ee1725b49fafeee2bbe41") // ← mobilesdk_app_id
                        .setProjectId("proyectoiot-784f3") // ← project_id
                        .build(),
                "TempApp"
        );


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
                                                        "Administrador",
                                                        uidEditor,
                                                        nombreNuevo,
                                                        new Date(),
                                                        "Registro de usuario"
                                                );

                                                db.collection("logs").add(log);
                                            }
                                        });

                                sendAdminCredentialsEmailIntent(
                                        finalNuevoAdmin.getEmail(),
                                        finalNuevoAdmin.getNombres() + " " + finalNuevoAdmin.getApellidos(),
                                        finalPassword
                                );

                                // Salir del modo edición y volver atrás
                                isEditMode = false;
                                btnEditarGuardar.setText("Editar");
                                etNombre.setEnabled(false);
                                etApellidos.setEnabled(false);
                                etNumero.setEnabled(false);
                                etCorreo.setEnabled(false);
                                etDireccion.setEnabled(false);
                                etFechaNacimiento.setEnabled(false);
                                ivFoto.setClickable(false);
                                requireActivity().getSupportFragmentManager().popBackStack();

                                // Eliminar app temporal
                                finalTempAuth.signOut();
                                finalFirebaseApp.delete();
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




}