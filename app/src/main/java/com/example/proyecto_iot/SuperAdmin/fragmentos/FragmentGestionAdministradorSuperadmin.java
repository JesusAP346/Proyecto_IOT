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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.example.proyecto_iot.dtos.Usuario;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;



import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.SuperAdmin.AdminDataStore;
import com.example.proyecto_iot.SuperAdmin.domain.AdministradoresDomain;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class FragmentGestionAdministradorSuperadmin extends Fragment {

    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String imagenSeleccionada = "";

    private ImageView ivFoto;

    public FragmentGestionAdministradorSuperadmin() {}

    public static FragmentGestionAdministradorSuperadmin newInstance(AdministradoresDomain admin, boolean editar, int index) {
        FragmentGestionAdministradorSuperadmin fragment = new FragmentGestionAdministradorSuperadmin();
        Bundle args = new Bundle();
        args.putString("nombre", admin.getNombreAdmin());
        args.putString("numero", admin.getNumeroAdmin());
        args.putString("correo", admin.getCorreo());
        args.putString("direccion", admin.getDireccion());
        args.putString("fechaNacimiento", admin.getFechaNacimiento());
        args.putString("rol", admin.getRol());
        args.putString("imagen", admin.getImagenAdmin());
        args.putBoolean("modoEdicion", editar);
        args.putInt("index", index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Registrar el imagePickerLauncher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        imagenSeleccionada = imageUri.toString();
                        Picasso.get().load(imagenSeleccionada).into(ivFoto);
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gestion_administrador_superadmin, container, false);

        TextInputEditText etNombre = view.findViewById(R.id.etNombre);
        TextInputEditText etApellidos = view.findViewById(R.id.etApellidos);
        TextInputEditText etNumero = view.findViewById(R.id.etNumero);
        TextInputEditText etCorreo = view.findViewById(R.id.etCorreo);
        TextInputEditText etDireccion = view.findViewById(R.id.etDireccion);
        TextInputEditText etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);


        ivFoto = view.findViewById(R.id.ivFotoAdmin);
        android.widget.Button btnEditarGuardar = view.findViewById(R.id.btnEditarGuardar);

        if (getArguments() != null) {
            String nombre = getArguments().getString("nombre");
            String apellidos = getArguments().getString("apellidos");
            String numero = getArguments().getString("numero");
            String correo = getArguments().getString("correo");
            String direccion = getArguments().getString("direccion");
            String fechaNacimiento = getArguments().getString("fechaNacimiento");
            String rol = getArguments().getString("rol");
            String imagenUrl = getArguments().getString("imagen");
            int index = getArguments().getInt("index", -1);
            boolean modoEdicion = getArguments().getBoolean("modoEdicion", false);

            imagenSeleccionada = imagenUrl != null ? imagenUrl : "";

            etNombre.setText(nombre);
            etApellidos.setText(apellidos);
            etNumero.setText(numero);
            etCorreo.setText(correo);
            etDireccion.setText(direccion);
            etFechaNacimiento.setText(fechaNacimiento);

            if (!imagenSeleccionada.isEmpty()) {
                Picasso.get().load(imagenSeleccionada).into(ivFoto);
            }

            etNombre.setEnabled(modoEdicion);
            etApellidos.setEnabled(modoEdicion);
            etNumero.setEnabled(modoEdicion);
            etCorreo.setEnabled(modoEdicion);
            etDireccion.setEnabled(modoEdicion);
            etFechaNacimiento.setEnabled(modoEdicion);

            btnEditarGuardar.setText(modoEdicion ? "Guardar" : "Editar");

            final boolean[] enModoEdicion = {modoEdicion};

            ivFoto.setOnClickListener(v -> {
                if (enModoEdicion[0]) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    imagePickerLauncher.launch(intent);
                }
            });

            etFechaNacimiento.setOnClickListener(v -> {
                if (enModoEdicion[0]) {
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
                if (!enModoEdicion[0]) {
                    enModoEdicion[0] = true;
                    btnEditarGuardar.setText("Guardar");
                    etNombre.setEnabled(true);
                    etApellidos.setEnabled(true);
                    etNumero.setEnabled(true);
                    etCorreo.setEnabled(true);
                    etDireccion.setEnabled(true);
                    etFechaNacimiento.setEnabled(true);

                } else {

                    if (!validarCampos(view)) return;



                    String nuevoNombre = etNombre.getText().toString().trim();
                    String nuevoApellido = etApellidos.getText().toString().trim();
                    String nuevoNumero = etNumero.getText().toString().trim();
                    String nuevoCorreo = etCorreo.getText().toString().trim();
                    String nuevaDireccion = etDireccion.getText().toString().trim();
                    String nuevaFecha = etFechaNacimiento.getText().toString().trim();


                    // Guardar imagen local
                    String nombreArchivo = "foto_admin_" + nuevoCorreo + ".jpg";
                    File archivoLocal = new File(requireContext().getFilesDir(), nombreArchivo);
                    try {
                        InputStream input = requireContext().getContentResolver().openInputStream(Uri.parse(imagenSeleccionada));
                        FileOutputStream output = new FileOutputStream(archivoLocal);
                        byte[] buffer = new byte[4096];
                        int bytesRead;
                        while ((bytesRead = input.read(buffer)) != -1) {
                            output.write(buffer, 0, bytesRead);
                        }
                        output.close();
                        input.close();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al guardar imagen local", Toast.LENGTH_SHORT).show();
                        e.printStackTrace(); // Para depurar en Logcat si deseas
                        return;
                    }


                    // Crear objeto Usuario
                    Usuario usuario = new Usuario();
                    usuario.setNombres(nuevoNombre);
                    usuario.setApellidos(nuevoApellido);
                    usuario.setNumCelular(nuevoNumero);
                    usuario.setEmail(nuevoCorreo);
                    usuario.setDireccion(nuevaDireccion);
                    usuario.setFechaNacimiento(nuevaFecha);
                    usuario.setIdRol("Administrador");
                    usuario.setUrlFotoPerfil(archivoLocal.getAbsolutePath());
                    usuario.setEstadoCuenta(true);
                    usuario.setUltimaActualizacion(String.valueOf(System.currentTimeMillis()));
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    String fechaFormateada = sdf.format(new Date());
                    usuario.setActualizadoPor("superadmin " + fechaFormateada);


                    FirebaseFirestore db = FirebaseFirestore.getInstance();

                    if (index != -1 && getArguments() != null) {
                        // 🔄 Edición
                        String idDocumento = getArguments().getString("id"); // Asegúrate de pasar esto al fragmento
                        db.collection("usuarios").document(idDocumento)
                                .set(usuario)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(getContext(), "Administrador actualizado", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al actualizar", Toast.LENGTH_SHORT).show());
                    } else {
                        // ➕ Creación
                        usuario.setFechaRegistro(String.valueOf(System.currentTimeMillis()));
                        db.collection("usuarios").add(usuario)
                                .addOnSuccessListener(docRef -> {
                                    Toast.makeText(getContext(), "Administrador agregado exitosamente", Toast.LENGTH_SHORT).show();
                                    requireActivity().getSupportFragmentManager().popBackStack();
                                })
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error al registrar", Toast.LENGTH_SHORT).show());
                    }

                    enModoEdicion[0] = false;
                    btnEditarGuardar.setText("Editar");
                    etNombre.setEnabled(false);
                    etNumero.setEnabled(false);
                    etCorreo.setEnabled(false);
                    etDireccion.setEnabled(false);
                    etFechaNacimiento.setEnabled(false);

                }
            });

        }

        return view;
    }
    private boolean validarCampos(View view) {
        // Referencias a EditTexts
        TextInputEditText etNombre = view.findViewById(R.id.etNombre);
        TextInputEditText etApellidos = view.findViewById(R.id.etApellidos);
        TextInputEditText etNumero = view.findViewById(R.id.etNumero);
        TextInputEditText etCorreo = view.findViewById(R.id.etCorreo);
        TextInputEditText etDireccion = view.findViewById(R.id.etDireccion);
        TextInputEditText etFechaNacimiento = view.findViewById(R.id.etFechaNacimiento);
        TextView tvErrorImagen = view.findViewById(R.id.tvErrorImagen);


        // Referencias a Layouts
        TextInputLayout layoutNombre = view.findViewById(R.id.layoutNombre);
        TextInputLayout layoutApellidos = view.findViewById(R.id.layoutApellidos);
        TextInputLayout layoutNumero = view.findViewById(R.id.layoutNumero);
        TextInputLayout layoutCorreo = view.findViewById(R.id.layoutCorreo);
        TextInputLayout layoutDireccion = view.findViewById(R.id.layoutDireccion);
        TextInputLayout layoutFechaNacimiento = view.findViewById(R.id.layoutFechaNacimiento);

        // Limpiar errores anteriores
        layoutNombre.setErrorEnabled(false);
        layoutApellidos.setErrorEnabled(false);
        layoutNumero.setErrorEnabled(false);
        layoutCorreo.setErrorEnabled(false);
        layoutDireccion.setErrorEnabled(false);
        layoutFechaNacimiento.setErrorEnabled(false);

        boolean esValido = true;

        // Nombre
        String nombre = etNombre.getText().toString().trim();
        if (nombre.isEmpty()) {
            layoutNombre.setErrorEnabled(true);
            layoutNombre.setError("Este campo es obligatorio");
            esValido = false;
        } else if (nombre.length() < 2) {
            layoutNombre.setErrorEnabled(true);
            layoutNombre.setError("Debe tener al menos 2 caracteres");
            esValido = false;
        }

        // Apellidos
        String apellidos = etApellidos.getText().toString().trim();
        if (apellidos.isEmpty()) {
            layoutApellidos.setErrorEnabled(true);
            layoutApellidos.setError("Este campo es obligatorio");
            esValido = false;
        } else if (apellidos.length() < 2) {
            layoutApellidos.setErrorEnabled(true);
            layoutApellidos.setError("Debe tener al menos 2 caracteres");
            esValido = false;
        }

        // Número de celular
        String numero = etNumero.getText().toString().trim();
        if (numero.isEmpty()) {
            layoutNumero.setErrorEnabled(true);
            layoutNumero.setError("Este campo es obligatorio");
            esValido = false;
        } else if (!numero.matches("\\d{9}")) {
            layoutNumero.setErrorEnabled(true);
            layoutNumero.setError("Debe tener exactamente 9 dígitos");
            esValido = false;
        }

        // Correo
        String correo = etCorreo.getText().toString().trim();
        if (correo.isEmpty()) {
            layoutCorreo.setErrorEnabled(true);
            layoutCorreo.setError("Este campo es obligatorio");
            esValido = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            layoutCorreo.setErrorEnabled(true);
            layoutCorreo.setError("Correo electrónico inválido");
            esValido = false;
        }

        // Dirección
        String direccion = etDireccion.getText().toString().trim();
        if (direccion.isEmpty()) {
            layoutDireccion.setErrorEnabled(true);
            layoutDireccion.setError("Este campo es obligatorio");
            esValido = false;
        }

        // Fecha de nacimiento
        String fecha = etFechaNacimiento.getText().toString().trim();
        if (fecha.isEmpty()) {
            layoutFechaNacimiento.setErrorEnabled(true);
            layoutFechaNacimiento.setError("Debe ingresar una fecha de nacimiento");
            esValido = false;
        } else if (!fecha.matches("\\d{2}/\\d{2}/\\d{4}")) {
            layoutFechaNacimiento.setErrorEnabled(true);
            layoutFechaNacimiento.setError("Formato esperado: dd/MM/yyyy");
            esValido = false;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date fechaNacimiento = sdf.parse(fecha);
                Calendar hoy = Calendar.getInstance();
                Calendar fechaNac = Calendar.getInstance();
                fechaNac.setTime(fechaNacimiento);

                int edad = hoy.get(Calendar.YEAR) - fechaNac.get(Calendar.YEAR);
                if (hoy.get(Calendar.DAY_OF_YEAR) < fechaNac.get(Calendar.DAY_OF_YEAR)) {
                    edad--;
                }

                if (edad < 18) {
                    layoutFechaNacimiento.setErrorEnabled(true);
                    layoutFechaNacimiento.setError("Debes tener al menos 18 años");
                    esValido = false;
                }

            } catch (ParseException e) {
                layoutFechaNacimiento.setErrorEnabled(true);
                layoutFechaNacimiento.setError("Fecha inválida");
                esValido = false;
            }
        }

        if (imagenSeleccionada == null || imagenSeleccionada.isEmpty()) {
            tvErrorImagen.setVisibility(View.VISIBLE);
            esValido = false;
        } else {
            tvErrorImagen.setVisibility(View.GONE);
        }

        return esValido;
    }




}
