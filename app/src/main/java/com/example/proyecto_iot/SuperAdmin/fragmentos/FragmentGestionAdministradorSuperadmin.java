package com.example.proyecto_iot.SuperAdmin.fragmentos;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.proyecto_iot.dtos.Usuario; // Correct DTO import

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.Serializable; // Import Serializable
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.HashMap; // For partial updates if needed
import java.util.Map; // For partial updates if needed

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
                        imagenSeleccionada = imageUri.toString(); // Store the Uri string
                        Picasso.get().load(imagenSeleccionada).into(ivFoto);
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
            if (!isEditMode) { // If currently in "view" mode, switch to "edit" mode
                isEditMode = true;
                btnEditarGuardar.setText("Guardar");
                etNombre.setEnabled(true);
                etApellidos.setEnabled(true);
                etNumero.setEnabled(true);
                etCorreo.setEnabled(true);
                etDireccion.setEnabled(true);
                etFechaNacimiento.setEnabled(true);
                ivFoto.setClickable(true); // Allow image selection
            } else { // If currently in "edit" mode, try to save
                if (!validarCampos(view)) {
                    return; // Stop if validation fails
                }

                // Update currentAdmin object with new values from UI
                currentAdmin.setNombres(etNombre.getText().toString().trim());
                currentAdmin.setApellidos(etApellidos.getText().toString().trim());
                currentAdmin.setNumCelular(etNumero.getText().toString().trim());
                currentAdmin.setEmail(etCorreo.getText().toString().trim());
                currentAdmin.setDireccion(etDireccion.getText().toString().trim());
                currentAdmin.setFechaNacimiento(etFechaNacimiento.getText().toString().trim());
                currentAdmin.setUrlFotoPerfil(imagenSeleccionada); // Store the selected image URI/URL

                // Set fixed properties for administrators
                currentAdmin.setIdRol("Administrador"); // Ensure the role is "Administrador"
                currentAdmin.setEstadoCuenta(true); // Assuming active account
                currentAdmin.setUltimaActualizacion(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                currentAdmin.setActualizadoPor("superadmin"); // You might want to get the actual superadmin's ID/name


                // --- LOGIC FOR SAVING TO FIRESTORE ---
                if (currentAdmin.getId() != null && !currentAdmin.getId().isEmpty()) {
                    // 🔄 Edit (Update existing document)
                    // Use a Map for partial updates, or set the whole object if all fields are populated
                    db.collection("usuarios").document(currentAdmin.getId())
                            .set(currentAdmin) // Overwrites the document
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(getContext(), "Administrador actualizado exitosamente", Toast.LENGTH_SHORT).show();
                                // After successful save, switch back to view mode
                                isEditMode = false;
                                btnEditarGuardar.setText("Editar");
                                etNombre.setEnabled(false);
                                etApellidos.setEnabled(false);
                                etNumero.setEnabled(false);
                                etCorreo.setEnabled(false);
                                etDireccion.setEnabled(false);
                                etFechaNacimiento.setEnabled(false);
                                ivFoto.setClickable(false); // Disable image selection
                                requireActivity().getSupportFragmentManager().popBackStack(); // Go back to list
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar administrador: " + e.getMessage(), Toast.LENGTH_LONG).show());
                } else {
                    // ➕ Create (Add new document)
                    currentAdmin.setFechaRegistro(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));
                    db.collection("usuarios").add(currentAdmin)
                            .addOnSuccessListener(documentReference -> {
                                // After adding, you might want to save the generated ID back to the object if needed
                                // currentAdmin.setId(documentReference.getId()); // This is optional, as you're popping back
                                Toast.makeText(getContext(), "Administrador agregado exitosamente", Toast.LENGTH_SHORT).show();
                                requireActivity().getSupportFragmentManager().popBackStack(); // Go back to list
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al registrar administrador: " + e.getMessage(), Toast.LENGTH_LONG).show());
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
}