package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.FotoItem;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.UploadResponse;
import com.example.proyecto_iot.administradorHotel.services.AwsService;
import com.example.proyecto_iot.databinding.DialogAgregarEquipamientoBinding;
import com.example.proyecto_iot.databinding.FragmentEditarHabitacionfragmentBinding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditarHabitacionfragment extends Fragment {

    private FragmentEditarHabitacionfragmentBinding binding;
    private final List<String> equipamientosSeleccionados = new ArrayList<>();
    private final List<String> serviciosSeleccionados = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    private Uri uriFotoCamara = null;
    private boolean isMenuVisible = false;

    // Lista unificada para todas las fotos
    private List<FotoItem> todasLasFotos = new ArrayList<>();

    private static final int CAMERA_PERMISSION_REQUEST = 100;

    // Activity Result Launchers
    private ActivityResultLauncher<Intent> galeriaLauncher;
    private ActivityResultLauncher<Intent> camaraLauncher;
    private ActivityResultLauncher<Intent> archivoLauncher;

    private List<String> urlsOriginales = new ArrayList<>();

    private final List<String> opcionesEquipamiento = Arrays.asList("Seleccionar", "TV", "Toallas", "Wifi", "Escritorio", "Caja fuerte", "Aire acondicionado", "Mini bar", "Secador de cabello", "Plancha", "Cafetera", "Teléfono", "Ropa de cama extra",
            "Lámpara de lectura", "Espejo ", "Alarma", "Cortinas blackout", "Detector de humo",
            "Utensilios de cocina", "Mesa de noche", "Sillas", "Estante para maletas");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentEditarHabitacionfragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HabitacionHotel habitacion = (HabitacionHotel) getArguments().getSerializable("habitacion");
        if (habitacion == null) {
            Toast.makeText(requireContext(), "No se pudo cargar la habitación", Toast.LENGTH_SHORT).show();
            return;
        }

        // Setear campos
        binding.editTextTipoHabitacion.setText(habitacion.getTipo());
        binding.textCantidadAdultos.setText(String.valueOf(habitacion.getCapacidadAdultos()));
        binding.textCantidadNinos.setText(String.valueOf(habitacion.getCapacidadNinos()));
        binding.editTextTamano.setText(String.valueOf(habitacion.getTamanho()));
        binding.editTextPrecio.setText(String.format("%.2f", habitacion.getPrecioPorNoche()));
        binding.editTextHabitacionesRegistradas.setText(String.valueOf(habitacion.getCantidadHabitaciones()));

        // Cargar listas de equipamiento y servicio
        if (habitacion.getEquipamiento() != null) {
            equipamientosSeleccionados.addAll(habitacion.getEquipamiento());
        }
        if (habitacion.getServicio() != null) {
            serviciosSeleccionados.addAll(habitacion.getServicio());
        }

        renderizarEquipamientos();
        renderizarServicios();
        setupAgregarEquipamientoButton();
        setupEquipamientoSpinner();
        setupSpinnerServicios();

        // Inicializar menú y launchers
        inicializarLaunchers();
        configurarEventos();

        // Cargar imágenes actuales desde Firebase
        urlsOriginales.clear();
        urlsOriginales.addAll(habitacion.getFotosUrls());
        for (String url : habitacion.getFotosUrls()) {
            agregarFoto(new FotoItem(Uri.parse(url)));
        }

        // Acción de actualización
        binding.btnActualizar.setOnClickListener(v -> actualizarDatosHabitacion(habitacion));

        binding.btnMasAdultos.setOnClickListener(v -> {
            int count = Integer.parseInt(binding.textCantidadAdultos.getText().toString());
            if (count < 20) {
                binding.textCantidadAdultos.setText(String.valueOf(count + 1));
            }
        });

        binding.btnMenosAdultos.setOnClickListener(v -> {
            int count = Integer.parseInt(binding.textCantidadAdultos.getText().toString());
            if (count > 0) {
                binding.textCantidadAdultos.setText(String.valueOf(count - 1));
            }
        });

        binding.btnMasNinos.setOnClickListener(v -> {
            int count = Integer.parseInt(binding.textCantidadNinos.getText().toString());
            if (count < 10) {
                binding.textCantidadNinos.setText(String.valueOf(count + 1));
            }
        });

        binding.btnMenosNinos.setOnClickListener(v -> {
            int count = Integer.parseInt(binding.textCantidadNinos.getText().toString());
            if (count > 0) {
                binding.textCantidadNinos.setText(String.valueOf(count - 1));
            }
        });

        // Botón para retroceder
        binding.backdetallehabitacion.setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack());
    }


    private void actualizarDatosHabitacion(HabitacionHotel habitacion) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("¿Estás segura?")
                .setMessage("Tus cambios no podrán ser revertidos")
                .setPositiveButton("Sí, actualizar", (dialog, which) -> {
                    String tipo = binding.editTextTipoHabitacion.getText().toString().trim();
                    String tamanoStr = binding.editTextTamano.getText().toString().trim();
                    String precioStr = binding.editTextPrecio.getText().toString().trim();
                    String cantidadStr = binding.editTextHabitacionesRegistradas.getText().toString().trim();
                    String adultosStr = binding.textCantidadAdultos.getText().toString().trim();
                    String ninosStr = binding.textCantidadNinos.getText().toString().trim();

                    if (tipo.isEmpty() || tamanoStr.isEmpty() || precioStr.isEmpty() || cantidadStr.isEmpty()) {
                        Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double tamano = Double.parseDouble(tamanoStr);
                    double precio = Double.parseDouble(precioStr);
                    int cantidad = Integer.parseInt(cantidadStr);
                    int adultos = Integer.parseInt(adultosStr);
                    int ninos = Integer.parseInt(ninosStr);

                    // Separar fotos actuales y nuevas
                    List<String> urlsActuales = new ArrayList<>();
                    List<FotoItem> nuevasFotos = new ArrayList<>();

                    for (FotoItem foto : todasLasFotos) {
                        if (foto.uri != null && foto.uri.toString().startsWith("http")) {
                            urlsActuales.add(foto.uri.toString());
                        } else {
                            nuevasFotos.add(foto);
                        }
                    }

                    // Eliminar imágenes que ya no están
                    for (String urlOriginal : urlsOriginales) {
                        if (!urlsActuales.contains(urlOriginal)) {
                            borrarImagenDeAWS(urlOriginal);
                        }
                    }

                    List<String> urlsFinales = new ArrayList<>(urlsActuales);

                    if (nuevasFotos.isEmpty()) {
                        guardarHabitacionEnFirestore(habitacion, tipo, tamano, precio, cantidad, adultos, ninos, urlsFinales);
                    } else {
                        subirSoloNuevasImagenes(nuevasFotos, urlsSubidas -> {
                            urlsFinales.addAll(urlsSubidas);
                            guardarHabitacionEnFirestore(habitacion, tipo, tamano, precio, cantidad, adultos, ninos, urlsFinales);
                        }, error -> {
                            binding.progressBarGuardar.setVisibility(View.GONE);
                            Toast.makeText(requireContext(), "Error al subir imágenes: " + error, Toast.LENGTH_SHORT).show();
                        });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
    private void guardarHabitacionEnFirestore(HabitacionHotel habitacion, String tipo, double tamano, double precio,
                                              int cantidad, int adultos, int ninos, List<String> urlsFinales) {
        binding.progressBarGuardar.setVisibility(View.VISIBLE);
        binding.btnActualizar.setEnabled(false);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (currentUser == null) {
            Toast.makeText(requireContext(), "No se ha iniciado sesión", Toast.LENGTH_SHORT).show();
            binding.progressBarGuardar.setVisibility(View.GONE);
            binding.btnActualizar.setEnabled(true);
            return;
        }

        String idAdmin = currentUser.getUid();

        db.collection("usuarios").document(idAdmin).get()
                .addOnSuccessListener(usuarioSnapshot -> {
                    if (!usuarioSnapshot.exists()) {
                        Toast.makeText(requireContext(), "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                        binding.progressBarGuardar.setVisibility(View.GONE);
                        binding.btnActualizar.setEnabled(true);
                        return;
                    }

                    String idHotel = usuarioSnapshot.getString("idHotel");
                    if (idHotel == null || idHotel.isEmpty()) {
                        Toast.makeText(requireContext(), "No se encontró el hotel asociado", Toast.LENGTH_SHORT).show();
                        binding.progressBarGuardar.setVisibility(View.GONE);
                        binding.btnActualizar.setEnabled(true);
                        return;
                    }

                    db.collection("hoteles").document(idHotel)
                            .collection("habitaciones").document(habitacion.getId())
                            .update(
                                    "tipo", tipo,
                                    "tamanho", tamano,
                                    "precioPorNoche", precio,
                                    "cantidadHabitaciones", cantidad,
                                    "capacidadAdultos", adultos,
                                    "capacidadNinos", ninos,
                                    "equipamiento", equipamientosSeleccionados,
                                    "servicio", serviciosSeleccionados,
                                    "fotosUrls", urlsFinales
                            )
                            .addOnSuccessListener(unused -> {
                                binding.progressBarGuardar.setVisibility(View.GONE);
                                Toast.makeText(requireContext(), "Habitación actualizada con éxito", Toast.LENGTH_SHORT).show();

                                // ✅ Recargar DetalleHabitacionFragment con datos actualizados
                                habitacion.setTipo(tipo);
                                habitacion.setTamanho((int) tamano);
                                habitacion.setPrecioPorNoche(precio);
                                habitacion.setCantidadHabitaciones(cantidad);
                                habitacion.setCapacidadAdultos(adultos);
                                habitacion.setCapacidadNinos(ninos);
                                habitacion.setEquipamiento(new ArrayList<>(equipamientosSeleccionados));
                                habitacion.setServicio(new ArrayList<>(serviciosSeleccionados));
                                habitacion.setFotosUrls(new ArrayList<>(urlsFinales));

                                DetalleHabitacionFragment detalleFragment = new DetalleHabitacionFragment();
                                Bundle args = new Bundle();
                                args.putSerializable("habitacion", habitacion);
                                detalleFragment.setArguments(args);

                                requireActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.frame_layout, detalleFragment)
                                        .commit();
                            })
                            .addOnFailureListener(e -> {
                                binding.progressBarGuardar.setVisibility(View.GONE);
                                binding.btnActualizar.setEnabled(true);
                                Toast.makeText(requireContext(), "Error al actualizar habitación", Toast.LENGTH_SHORT).show();
                            });

                })
                .addOnFailureListener(e -> {
                    binding.progressBarGuardar.setVisibility(View.GONE);
                    binding.btnActualizar.setEnabled(true);
                    Toast.makeText(requireContext(), "Error al obtener datos del usuario", Toast.LENGTH_SHORT).show();
                });
    }



    private void borrarImagenDeAWS(String urlPublica) {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(
                okhttp3.MediaType.parse("application/json"),
                "{\"url\":\"" + urlPublica + "\"}"
        );

        okhttp3.Request request = new okhttp3.Request.Builder()
                .url("https://jp717cwlmj.execute-api.us-west-2.amazonaws.com/borrar-imagen")
                .post(body)
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                Log.e("BORRAR_AWS", "Error al borrar imagen", e);
            }

            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
                if (response.isSuccessful()) {
                    Log.d("BORRAR_AWS", "Imagen borrada correctamente");
                } else {
                    Log.e("BORRAR_AWS", "Fallo al borrar imagen: " + response.code());
                }
            }
        });
    }



    private void setupEquipamientoSpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, opcionesEquipamiento);
        binding.spinnerEquipamiento.setAdapter(adapter);

        binding.spinnerEquipamiento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String seleccionado = opcionesEquipamiento.get(position);
                if (!seleccionado.equals("Seleccionar") && !equipamientosSeleccionados.contains(seleccionado)) {
                    equipamientosSeleccionados.add(seleccionado);
                    renderizarEquipamientos();
                }
                binding.spinnerEquipamiento.setSelection(0);
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });
        verificarEquipamientosVisibles();




    }

    private void setupSpinnerServicios() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();

        if (currentUser == null) {
            Toast.makeText(requireContext(), "No se ha iniciado sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        db.collection("usuarios").document(uid).get()
                .addOnSuccessListener(userDoc -> {
                    if (!userDoc.exists()) {
                        Toast.makeText(requireContext(), "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String idHotel = userDoc.getString("idHotel");
                    if (idHotel == null || idHotel.isEmpty()) {
                        Toast.makeText(requireContext(), "No se encontró el hotel asociado", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    db.collection("hoteles").document(idHotel).collection("servicios")
                            .get()
                            .addOnSuccessListener(serviciosSnapshot -> {
                                List<String> nombresServicios = new ArrayList<>();
                                nombresServicios.add("Seleccionar");

                                for (var doc : serviciosSnapshot) {
                                    String nombre = doc.getString("nombre");
                                    if (nombre != null && !nombre.isEmpty()) {
                                        nombresServicios.add(nombre);
                                    }
                                }

                                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_dropdown_item, nombresServicios);
                                binding.spinnerServicios.setAdapter(adapter);

                                binding.spinnerServicios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        String seleccionado = nombresServicios.get(position);
                                        if (!seleccionado.equals("Seleccionar") && !serviciosSeleccionados.contains(seleccionado)) {
                                            serviciosSeleccionados.add(seleccionado);
                                            renderizarServicios();
                                        }
                                        binding.spinnerServicios.setSelection(0);
                                    }
                                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                                });
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(requireContext(), "Error al obtener servicios: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Error al obtener usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void renderizarEquipamientos() {
        binding.layoutEquipamientoDinamico.removeAllViews();
        verificarEquipamientosVisibles();

        for (int i = 0; i < equipamientosSeleccionados.size(); i += 2) {
            LinearLayout fila = new LinearLayout(requireContext());
            fila.setOrientation(LinearLayout.HORIZONTAL);
            fila.setWeightSum(2);
            fila.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            fila.setPadding(0, 4, 0, 4);

            fila.addView(crearChipEquipamiento(equipamientosSeleccionados.get(i)));

            if (i + 1 < equipamientosSeleccionados.size()) {
                fila.addView(crearChipEquipamiento(equipamientosSeleccionados.get(i + 1)));
            } else {
                View espacio = new View(requireContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0, 1);
                espacio.setLayoutParams(params);
                fila.addView(espacio);
            }
            binding.layoutEquipamientoDinamico.addView(fila);
        }
    }

    private View crearChipEquipamiento(String texto) {
        LinearLayout chip = new LinearLayout(requireContext());
        chip.setOrientation(LinearLayout.HORIZONTAL);
        chip.setBackgroundResource(R.drawable.customedittext);
        chip.setPadding(16, 12, 16, 12);
        chip.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
        params.setMargins(0, 0, 8, 0);
        chip.setLayoutParams(params);

        TextView textView = new TextView(requireContext());
        textView.setText(texto);
        textView.setTextSize(14);
        textView.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
        textView.setTextColor(getResources().getColor(android.R.color.black));

        ImageView icono = new ImageView(requireContext());
        icono.setImageResource(R.drawable.ic_delete);
        icono.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(36, 36);
        iconParams.setMargins(16, 0, 0, 0);
        icono.setLayoutParams(iconParams);

        icono.setOnClickListener(v -> {
            equipamientosSeleccionados.remove(texto);
            renderizarEquipamientos();
        });

        chip.addView(textView);
        chip.addView(icono);
        return chip;
    }

    private void renderizarServicios() {
        binding.layoutServicioDinamico.removeAllViews();
        verificarServiciosVisibles();

        for (String servicio : serviciosSeleccionados) {
            LinearLayout chip = new LinearLayout(requireContext());
            chip.setOrientation(LinearLayout.HORIZONTAL);
            chip.setBackgroundResource(R.drawable.customedittext);
            chip.setPadding(16, 12, 16, 12);
            chip.setGravity(Gravity.CENTER_VERTICAL);
            LinearLayout.LayoutParams chipParams = new LinearLayout.LayoutParams((int) (getResources().getDisplayMetrics().widthPixels * 0.55), ViewGroup.LayoutParams.WRAP_CONTENT);
            chipParams.setMargins(0, 8, 0, 0);
            chip.setLayoutParams(chipParams);

            TextView textView = new TextView(requireContext());
            textView.setText(servicio);
            textView.setTextSize(16);
            textView.setTextColor(getResources().getColor(android.R.color.black));
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f);
            textView.setLayoutParams(tvParams);

            ImageView icono = new ImageView(requireContext());
            icono.setImageResource(R.drawable.ic_delete);
            icono.setColorFilter(getResources().getColor(android.R.color.holo_red_dark));
            LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(40, 40);
            iconParams.setMargins(16, 0, 0, 0);
            icono.setLayoutParams(iconParams);

            icono.setOnClickListener(v -> {
                serviciosSeleccionados.remove(servicio);
                renderizarServicios();
            });

            chip.addView(textView);
            chip.addView(icono);
            binding.layoutServicioDinamico.addView(chip);
        }
    }

    private void verificarEquipamientosVisibles() {
        binding.textEquipamientoVacio.setVisibility(equipamientosSeleccionados.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void verificarServiciosVisibles() {
        binding.textServicioVacio.setVisibility(serviciosSeleccionados.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setupAgregarEquipamientoButton() {
        binding.btnAgregarEquipamiento.setOnClickListener(v -> mostrarDialogoAgregarEquipamiento());
    }

    private void mostrarDialogoAgregarEquipamiento() {
        DialogAgregarEquipamientoBinding dialogBinding = DialogAgregarEquipamientoBinding.inflate(getLayoutInflater());

        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle("Agregar Equipamiento")
                .setView(dialogBinding.getRoot())
                .setPositiveButton("AGREGAR", (dialogInterface, i) -> {
                    String nuevoEquipamiento = dialogBinding.editEquipamiento.getText().toString().trim();
                    nuevoEquipamiento = nuevoEquipamiento.substring(0, 1).toUpperCase() + nuevoEquipamiento.substring(1).toLowerCase();

                    if (!nuevoEquipamiento.isEmpty()) {
                        if (!equipamientosSeleccionados.contains(nuevoEquipamiento)) {
                            equipamientosSeleccionados.add(nuevoEquipamiento);
                            renderizarEquipamientos();
                        } else {
                            Toast.makeText(requireContext(), "Este equipamiento ya está agregado", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Por favor ingrese un nombre válido", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .create();

        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.getWindow().setLayout(
                    (int) (getResources().getDisplayMetrics().widthPixels * 0.85),
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }
    }

    ////////////////////////Para lanzar la calería, cámara o archivos////////////////////////////
    private void inicializarLaunchers() {
        // Launcher para galería
        galeriaLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri imageUri = result.getData().getData();
                            if (imageUri != null) {
                                agregarFoto(new FotoItem(imageUri));
                            }
                        }
                        ocultarMenu();
                    }
                });

        // Launcher para cámara
        // Launcher para cámara
        camaraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            if (uriFotoCamara != null) {
                                agregarFoto(new FotoItem(uriFotoCamara));
                            } else {
                                Toast.makeText(requireContext(), "No se pudo recuperar la imagen de la cámara", Toast.LENGTH_SHORT).show();
                            }
                        }
                        ocultarMenu();
                    }
                });


        // Launcher para archivos
        archivoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                            Uri fileUri = result.getData().getData();
                            if (fileUri != null) {
                                agregarFoto(new FotoItem(fileUri));
                            }
                        }
                        ocultarMenu();
                    }
                });
    }

    //Para mostrar/ocultar el menu de para la selccion de fotos
    private void configurarEventos() {
        // Click en el botón principal para mostrar/ocultar menú
        binding.btnAgregarFoto.setOnClickListener(v -> {
            if (isMenuVisible) {
                ocultarMenu();
            } else {
                mostrarMenu();
            }
        });

        // Opciones del menú
        binding.opcionGaleria.setOnClickListener(v -> abrirGaleria());
        binding.opcionTomarFoto.setOnClickListener(v -> abrirCamara());
        binding.opcionSeleccionarArchivo.setOnClickListener(v -> abrirSelectorArchivos());
    }


    // Menú de Selección de Fotos //////////////////////////////////////////////////7

    private void mostrarMenu() {
        // Esperar a que el layout esté completamente dibujado
        binding.btnAgregarFoto.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                binding.btnAgregarFoto.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                posicionarMenuEncima();
            }
        });

        // Si el layout ya está dibujado, posicionar inmediatamente
        if (binding.btnAgregarFoto.getHeight() > 0) {
            posicionarMenuEncima();
        }
        // MOSTRAR FONDO PARA INTERCEPTAR TOQUES
        binding.fondoToque.setVisibility(View.VISIBLE);
        binding.fondoToque.setOnClickListener(v -> ocultarMenu());
    }

    private void ocultarMenu() {
        if (isMenuVisible) {
            // Animación de salida
            binding.menuOpciones.animate()
                    .alpha(0f)
                    .scaleY(0.8f)
                    .setDuration(150)
                    .withEndAction(() -> {

                        binding.menuOpciones.setVisibility(View.GONE);
                        binding.fondoToque.setVisibility(View.GONE);
                    })
                    .start();

            isMenuVisible = false;
        }
    }

    //Menú de opciones para la seleccion  de fotos
    private void posicionarMenuEncima() {
        // Obtener las dimensiones de la pantalla desde el Activity
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        // Calcular la posición del botón en la pantalla
        int[] buttonLocation = new int[2];
        binding.btnAgregarFoto.getLocationOnScreen(buttonLocation);

        // Medir el menú para obtener su altura
        binding.menuOpciones.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        );
        int menuHeight = binding.menuOpciones.getMeasuredHeight();

        // Calcular la posición Y del menú (por encima del botón)
        int buttonTopY = buttonLocation[1];
        int statusBarHeight = getStatusBarHeight(); // esto no cambia, funciona igual
        int menuTopY = buttonTopY - menuHeight;

        // Asegurar que el menú no se salga de la pantalla por arriba
        if (menuTopY < statusBarHeight + 50) {
            menuTopY = statusBarHeight + 50;
        }

        // Configurar la posición del menú
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) binding.menuOpciones.getLayoutParams();
        params.topMargin = menuTopY;
        binding.menuOpciones.setLayoutParams(params);

        // Mostrar el overlay y el menú con animación suave
        binding.menuOpciones.setVisibility(View.VISIBLE);
        binding.menuOpciones.setAlpha(0f);
        binding.menuOpciones.setScaleY(0.8f);
        binding.menuOpciones.animate()
                .alpha(1f)
                .scaleY(1f)
                .setDuration(200)
                .start();

        isMenuVisible = true;
    }


    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }


    //Abrir Galería, Cámara y Archivos //////////////////////////////////////////////
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.setType("image/*");
        galeriaLauncher.launch(intent);
    }

    private void abrirCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    requireActivity(),
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        } else {
            try {
                File archivoFoto = File.createTempFile("foto_camara_", ".jpg", requireContext().getCacheDir());
                uriFotoCamara = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".provider", archivoFoto);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFotoCamara); // 📸 FULL QUALITY
                camaraLauncher.launch(intent);
            } catch (IOException e) {
                Toast.makeText(requireContext(), "No se pudo crear archivo para la foto", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void abrirSelectorArchivos() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        archivoLauncher.launch(Intent.createChooser(intent, "Seleccionar imagen"));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara(); // vuelve a lanzar
            } else {
                Toast.makeText(requireContext(), "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /////////Lógica para Agregar, eliminar y mostrar (previsualizar) las fotos/////////////////////

    // Metodo unificado para agregar fotos
    private void agregarFoto(FotoItem fotoItem) {
        todasLasFotos.add(fotoItem);
        crearVistaPreviaFoto(fotoItem, todasLasFotos.size() - 1);
        binding.containerFotos.setVisibility(View.VISIBLE);

        // Validación en tiempo real
        if (todasLasFotos.size() >= 4 && binding.errorTipoFotos.getVisibility() == View.VISIBLE) {
            binding.errorTipoFotos.setVisibility(View.GONE);
        }
    }


    // Metodo unificado para crear vista previa
    private void crearVistaPreviaFoto(FotoItem fotoItem, int index) {
        // Crear CardView como contenedor principal
        CardView cardView = new CardView(requireContext());
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                (int) (50 * getResources().getDisplayMetrics().density));
        cardParams.setMargins(0, 2, 0, 2);
        cardView.setLayoutParams(cardParams);
        cardView.setCardElevation(2);
        cardView.setRadius(6);

        // Asignar un tag con el índice para identificar la vista
        cardView.setTag(index);

        // Crear LinearLayout horizontal dentro del CardView
        LinearLayout container = new LinearLayout(requireContext());
        container.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setGravity(Gravity.CENTER_VERTICAL);
        container.setPadding(6, 6, 6, 6);

        // ImageView para mostrar la foto
        ImageView imageView = new ImageView(requireContext());
        int imageSize = (int) (38 * getResources().getDisplayMetrics().density);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(imageSize, imageSize);
        imageParams.setMargins(0, 0, 8, 0);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.image_border));

        // Cargar imagen según el tipo
        if (fotoItem.bitmap != null) {
            imageView.setImageBitmap(fotoItem.bitmap);
        } else if (fotoItem.uri != null) {
            String uriStr = fotoItem.uri.toString();
            if (uriStr.startsWith("http") || uriStr.startsWith("https")) {
                // ✅ Carga desde internet (AWS S3)
                Glide.with(this)
                        .load(fotoItem.uri)
                        .placeholder(R.drawable.ic_gallery)
                        .error(R.drawable.ic_gallery)
                        .into(imageView);
            } else {
                try {
                    ParcelFileDescriptor pfd = requireContext().getContentResolver().openFileDescriptor(fotoItem.uri, "r");
                    if (pfd != null) {
                        FileDescriptor fd = pfd.getFileDescriptor();
                        Bitmap bitmapFromUri = BitmapFactory.decodeFileDescriptor(fd);
                        imageView.setImageBitmap(bitmapFromUri);
                        pfd.close();
                    } else {
                        Log.e("PREVIEW_ERROR", "PFD es null");
                        imageView.setImageResource(R.drawable.ic_gallery);
                    }
                } catch (Exception e) {
                    Log.e("PREVIEW_ERROR", "Error leyendo imagen desde FileProvider URI: " + e.getMessage());
                    imageView.setImageResource(R.drawable.ic_gallery);
                }


            }
        }


        // Click en la imagen para abrir en pantalla completa
        imageView.setOnClickListener(v -> mostrarImagenCompleta(fotoItem.uri, fotoItem.bitmap));

        // Espaciador para empujar el botón hacia la derecha
        View spacer = new View(requireContext());
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        spacer.setLayoutParams(spacerParams);

        // Botón eliminar
        MaterialButton btnEliminar = new MaterialButton(requireContext());
        btnEliminar.setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete));
        btnEliminar.setIconSize(42);
        btnEliminar.setIconTint(ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray));
        btnEliminar.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
        btnEliminar.setCornerRadius(8);
        btnEliminar.setStrokeWidth(0);
        btnEliminar.setMinWidth(0);
        btnEliminar.setMinHeight(0);
        btnEliminar.setText("");

        LinearLayout.LayoutParams btnParams = new LinearLayout.LayoutParams(
                (int) (24 * getResources().getDisplayMetrics().density),
                (int) (24 * getResources().getDisplayMetrics().density));
        btnEliminar.setLayoutParams(btnParams);
        btnEliminar.setPadding(0, 0, 0, 0);

        // Evento para eliminar foto - CORREGIDO
        btnEliminar.setOnClickListener(v -> eliminarFoto(cardView));

        // Agregar vistas al contenedor
        container.addView(imageView);
        container.addView(spacer);
        container.addView(btnEliminar);
        cardView.addView(container);

        // Agregar al contenedor principal
        binding.containerFotos.addView(cardView);
    }
    //metodo para mostrar la imagen en la vista previa
    private void mostrarImagenCompleta(Uri imageUri, Bitmap bitmap) {
        Dialog dialog = new Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            dialog.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS |
                            WindowManager.LayoutParams.FLAG_FULLSCREEN);

            dialog.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE);
        }

        // 🔧 Corrección para Fragment
        DisplayMetrics displayMetrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getRealMetrics(displayMetrics); // ← cambio aquí

        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        RelativeLayout layout = new RelativeLayout(requireContext()); // ← corrección de this
        layout.setLayoutParams(new ViewGroup.LayoutParams(screenWidth, screenHeight));
        layout.setBackgroundColor(Color.parseColor("#CC000000"));
        layout.setPadding(-50, -50, -50, -50);

        ImageView imageView = new ImageView(requireContext()); // ← corrección de this
        int maxWidth = (int) (screenWidth * 0.8);
        int maxHeight = (int) (screenHeight * 0.6);

        RelativeLayout.LayoutParams imageParams = new RelativeLayout.LayoutParams(maxWidth, maxHeight);
        imageParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        imageParams.setMargins(50, 50, 50, 50);
        imageView.setLayoutParams(imageParams);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setAdjustViewBounds(true);

        if (bitmap != null) {
            Bitmap resizedBitmap = redimensionarBitmap(bitmap, maxWidth, maxHeight);
            imageView.setImageBitmap(resizedBitmap);
        } else if (imageUri != null) {
            String uriStr = imageUri.toString();
            if (uriStr.startsWith("http") || uriStr.startsWith("https")) {
                Glide.with(requireContext())
                        .load(imageUri)
                        .placeholder(R.drawable.ic_gallery)
                        .error(R.drawable.ic_gallery)
                        .into(imageView);
            } else {
                try {
                    InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);
                    Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                    Bitmap resizedBitmap = redimensionarBitmap(originalBitmap, maxWidth, maxHeight);
                    imageView.setImageBitmap(resizedBitmap);
                } catch (FileNotFoundException e) {
                    imageView.setImageResource(R.drawable.ic_gallery);
                }
            }
        }


        MaterialButton btnCerrar = new MaterialButton(requireContext()); // ← corrección de this
        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(
                (int) (48 * getResources().getDisplayMetrics().density),
                (int) (48 * getResources().getDisplayMetrics().density));
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        btnParams.setMargins(66, 98, 16, 66);
        btnCerrar.setLayoutParams(btnParams);

        btnCerrar.setIcon(ContextCompat.getDrawable(requireContext(), android.R.drawable.ic_menu_close_clear_cancel));
        btnCerrar.setIconSize((int) (32 * getResources().getDisplayMetrics().density));
        btnCerrar.setIconTint(ContextCompat.getColorStateList(requireContext(), android.R.color.white));
        btnCerrar.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.transparent));
        btnCerrar.setCornerRadius((int) (24 * getResources().getDisplayMetrics().density));
        btnCerrar.setText("");
        btnCerrar.setPadding(0, 0, 0, 0);
        btnCerrar.setMinWidth(0);
        btnCerrar.setMinHeight(0);

        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        layout.setOnClickListener(v -> dialog.dismiss());

        layout.addView(imageView);
        layout.addView(btnCerrar);

        dialog.setContentView(layout);
        dialog.show();
    }

    private Bitmap redimensionarBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
        if (bitmap == null) return null;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Calcular ratio para mantener proporción
        float ratio = Math.min((float) maxWidth / width, (float) maxHeight / height);

        int newWidth = Math.round(width * ratio);
        int newHeight = Math.round(height * ratio);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    // Metodo  para eliminar fotos
    private void eliminarFoto(CardView cardView) {
        // Encontrar el índice de la vista en el contenedor
        int indexEnContenedor = binding.containerFotos.indexOfChild(cardView);

        if (indexEnContenedor >= 0 && indexEnContenedor < todasLasFotos.size()) {
            // Eliminar de la lista
            todasLasFotos.remove(indexEnContenedor);

            // Eliminar la vista del contenedor
            binding.containerFotos.removeView(cardView);

            // Si no quedan fotos, ocultar el contenedor
            if (todasLasFotos.isEmpty()) {
                binding.containerFotos.setVisibility(View.GONE);
            }

            // Actualizar los listeners de los botones restantes
            actualizarListenersEliminacion();
        }
        if (todasLasFotos.size() >= 2 && binding.errorTipoFotos.getVisibility() == View.VISIBLE) {
            binding.errorTipoFotos.setVisibility(View.GONE);
        }
    }

    // Metodo para actualizar los listeners de eliminación después de eliminar una foto
    private void actualizarListenersEliminacion() {
        for (int i = 0; i < binding.containerFotos.getChildCount(); i++) {
            View childView = binding.containerFotos.getChildAt(i);
            if (childView instanceof CardView) {
                CardView cardView = (CardView) childView;
                LinearLayout container = (LinearLayout) cardView.getChildAt(0);

                // Encontrar el botón eliminar
                MaterialButton btnEliminar = null;
                for (int j = 0; j < container.getChildCount(); j++) {
                    View view = container.getChildAt(j);
                    if (view instanceof MaterialButton) {
                        btnEliminar = (MaterialButton) view;
                        break;
                    }
                }

                if (btnEliminar != null) {
                    // Actualizar el listener con la referencia correcta
                    btnEliminar.setOnClickListener(v -> eliminarFoto(cardView));
                }
            }
        }
    }

    ////////////////////////METODOS PARA EL GUARDADO DE IMAGENES EN AWS//////////////////////////

    //METODO PRINCIPAL PARA SUBIR TODAS LAS IMÁGENES
    private void subirSoloNuevasImagenes(List<FotoItem> nuevasFotos, Consumer<List<String>> onTodasSubidas, Consumer<String> onError) {
        if (nuevasFotos.isEmpty()) {
            onTodasSubidas.accept(new ArrayList<>());
            return;
        }

        List<String> urlsImagenes = new ArrayList<>();
        AtomicInteger contador = new AtomicInteger(0);
        AtomicInteger errores = new AtomicInteger(0);

        binding.progressBarGuardar.setVisibility(View.VISIBLE);
        binding.btnActualizar.setText("Subiendo imágenes...");

        for (int i = 0; i < nuevasFotos.size(); i++) {
            FotoItem fotoItem = nuevasFotos.get(i);
            final int index = i;

            subirImagenAWS(fotoItem, index,
                    url -> {
                        urlsImagenes.add(url);
                        int completadas = contador.incrementAndGet();

                        requireActivity().runOnUiThread(() -> {
                            binding.btnActualizar.setText("Subiendo " + completadas + "/" + nuevasFotos.size());
                        });

                        if (completadas == nuevasFotos.size()) {
                            requireActivity().runOnUiThread(() -> {
                                onTodasSubidas.accept(urlsImagenes);
                            });
                        }
                    },
                    error -> {
                        errores.incrementAndGet();
                        int completadas = contador.incrementAndGet();

                        requireActivity().runOnUiThread(() -> {
                            Log.e("SUBIDA_IMG", "Error en imagen " + (index + 1) + ": " + error);
                            if (completadas == nuevasFotos.size()) {
                                if (errores.get() == nuevasFotos.size()) {
                                    onError.accept("No se pudo subir ninguna imagen");
                                } else {
                                    onTodasSubidas.accept(urlsImagenes);
                                }
                            }
                        });
                    }
            );
        }
    }



    private void subirImagenAWS(FotoItem fotoItem, int index, Consumer<String> onUrlObtenida, Consumer<String> onError) {
        File file = null;

        try {
            if (fotoItem.bitmap != null) {
                // Es de cámara - convertir bitmap a file
                String fileName = "camera_image_" + System.currentTimeMillis() + "_" + index;
                file = bitmapToFile(fotoItem.bitmap, fileName);
            } else if (fotoItem.uri != null) {
                // Es de galería o archivo - obtener path
                file = uriToFile(fotoItem.uri, "selected_image_" + System.currentTimeMillis() + "_" + index);
            }

            if (file == null || !file.exists()) {
                onError.accept("No se pudo procesar la imagen");
                return;
            }

            // VERIFICAR TAMAÑO DEL ARCHIVO
            long fileSize = file.length();
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (fileSize > maxSize) {
                onError.accept("La imagen es demasiado grande. Máximo 10MB");
                return;
            }

            // Crear request body con tipo MIME correcto
            MediaType mediaType = MediaType.parse("image/jpeg");
            RequestBody reqFile = RequestBody.create(file, mediaType);

            // IMPORTANTE: Usar el nombre correcto del archivo
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), reqFile);

            // Configurar Retrofit con timeouts más largos
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

            // Realizar llamada con mejor manejo de errores
            service.subirImagen(body).enqueue(new Callback<UploadResponse>() {
                @Override
                public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                    // Log detallado de la respuesta
                    Log.d("AWS_UPLOAD", "Response code: " + response.code());
                    Log.d("AWS_UPLOAD", "Response message: " + response.message());

                    if (response.isSuccessful() && response.body() != null) {
                        UploadResponse uploadResponse = response.body();
                        Log.d("AWS_UPLOAD", "Upload response: " + uploadResponse.toString());

                        if (uploadResponse.isSuccess() && uploadResponse.getUrl() != null && !uploadResponse.getUrl().isEmpty()) {
                            onUrlObtenida.accept(uploadResponse.getUrl());
                        } else {
                            String errorMsg = uploadResponse.getMessage() != null ?
                                    uploadResponse.getMessage() : "Respuesta sin URL válida";
                            onError.accept("Error en la respuesta: " + errorMsg);
                        }
                    } else {
                        // Obtener el cuerpo del error
                        String errorBody = "";
                        try {
                            if (response.errorBody() != null) {
                                errorBody = response.errorBody().string();
                                Log.e("AWS_UPLOAD", "Error body: " + errorBody);
                            }
                        } catch (Exception e) {
                            Log.e("AWS_UPLOAD", "Error reading error body", e);
                        }

                        onError.accept("Error HTTP " + response.code() + ": " + response.message() +
                                (errorBody.isEmpty() ? "" : "\nDetalle: " + errorBody));
                    }
                }

                @Override
                public void onFailure(Call<UploadResponse> call, Throwable t) {
                    Log.e("AWS_UPLOAD", "Network error", t);
                    onError.accept("Error de conexión: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            Log.e("AWS_UPLOAD", "Exception in subirImagenAWS", e);
            onError.accept("Error al procesar imagen: " + e.getMessage());
        }
    }

    // Metodo mejorado para convertir bitmap a file

    private File bitmapToFile(Bitmap bitmap, String fileName) {
        try {
            // Crear directorio temporal en el caché del contexto
            File dir = new File(requireContext().getCacheDir(), "temp_images"); // ← CORREGIDO
            if (!dir.exists() && !dir.mkdirs()) {
                Log.e("FILE_CREATION", "No se pudo crear directorio temporal");
                return null;
            }

            // Crear archivo con extensión .jpg
            File file = new File(dir, fileName + ".jpg");

            // Comprimir y guardar bitmap en máxima calidad JPEG (100% ultra HD bebé)
            FileOutputStream fos = new FileOutputStream(file);
            boolean compressed = bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // ← No toques esto que es oro
            fos.flush();
            fos.close();

            if (!compressed) {
                Log.e("FILE_CREATION", "Error al comprimir bitmap");
                return null;
            }

            if (!file.exists() || file.length() == 0) {
                Log.e("FILE_CREATION", "Archivo no se creó correctamente");
                return null;
            }

            Log.d("FILE_CREATION", "Archivo HD creado: " + file.getAbsolutePath() + " (" + file.length() + " bytes)");
            return file;
        } catch (Exception e) {
            Log.e("FILE_CREATION", "Error creando archivo HD desde bitmap", e);
            return null;
        }
    }

    // 6. METODO AUXILIAR PARA CONVERTIR URI A FILE
    private File uriToFile(Uri uri, String fileName) {
        try {
            // Crear directorio temporal
            File dir = new File(requireContext().getCacheDir(), "temp_images"); // ← CORREGIDO
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    Log.e("FILE_CREATION", "No se pudo crear directorio temporal");
                    return null;
                }
            }

            // Crear archivo destino con extensión .jpg
            File file = new File(dir, fileName + ".jpg");

            // Copiar contenido del URI al archivo
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri); // ← CORREGIDO
            if (inputStream == null) {
                Log.e("FILE_CREATION", "No se pudo abrir InputStream del URI");
                return null;
            }

            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096]; // Buffer más grande
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // Verificar que el archivo se creó correctamente
            if (!file.exists() || file.length() == 0) {
                Log.e("FILE_CREATION", "Archivo no se creó correctamente desde URI");
                return null;
            }

            Log.d("FILE_CREATION", "Archivo creado desde URI: " + file.getAbsolutePath() + " (" + file.length() + " bytes)");
            return file;
        } catch (Exception e) {
            Log.e("FILE_CREATION", "Error creating file from URI", e);
            return null;
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}