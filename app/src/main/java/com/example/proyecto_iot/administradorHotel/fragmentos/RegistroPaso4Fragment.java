package com.example.proyecto_iot.administradorHotel.fragmentos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.proyecto_iot.R;
import com.example.proyecto_iot.administradorHotel.entity.FotoItem;
import com.example.proyecto_iot.administradorHotel.entity.HabitacionHotel;
import com.example.proyecto_iot.administradorHotel.entity.UploadResponse;
import com.example.proyecto_iot.administradorHotel.services.AwsService;
import com.example.proyecto_iot.administradorHotel.viewmodel.HabitacionViewModel;
import com.example.proyecto_iot.databinding.FragmentRegistroPaso4Binding;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
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


public class RegistroPaso4Fragment extends Fragment {

    private List<FotoItem> todasLasFotos = new ArrayList<>();
    FragmentRegistroPaso4Binding binding;
    private HabitacionViewModel viewModel;

    FirebaseFirestore db;
    FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegistroPaso4Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(HabitacionViewModel.class);

        HabitacionHotel habitacion = viewModel.getHabitacion().getValue();
        if (habitacion == null) return;

        // Mostrar info de la habitación
        binding.textTipoHabitacion.setText(habitacion.getTipo());

        String capacidad = habitacion.getCapacidadAdultos() + " Adultos, " + habitacion.getCapacidadNinos() + " Niños";
        binding.textCapacidad.setText(capacidad);

        String tamano = habitacion.getTamanho() + " m²";
        binding.textTamano.setText(tamano);

        String precio = "S/ " + habitacion.getPrecioPorNoche();
        binding.textPrecio.setText(precio);

        String cantidad = habitacion.getCantidadHabitaciones() + " habitaciones";
        binding.textCantidadHabitaciones.setText(cantidad);

        if (habitacion.getEquipamiento() != null && !habitacion.getEquipamiento().isEmpty()) {
            mostrarEquipamientoEnLista(binding.contenedorEquipamiento, habitacion.getEquipamiento());
        }

        if (habitacion.getServicio() != null && !habitacion.getServicio().isEmpty()) {
            mostrarServiciosEnFilas(binding.contenedorServicios, habitacion.getServicio());
        }

        List<FotoItem> fotosGuardadas = viewModel.getListaFotosSeleccionadas();

        if (fotosGuardadas != null && !fotosGuardadas.isEmpty()) {
            for (int i = 0; i < fotosGuardadas.size(); i++) {
                todasLasFotos.add(fotosGuardadas.get(i));
                crearVistaPreviaFoto(fotosGuardadas.get(i), i);
            }
        }

        binding.btnRegistrarhabitacion.setOnClickListener(v -> {
            guardarHabitacion(viewModel);
        });

    }

    private void mostrarServiciosEnFilas(ViewGroup contenedor, List<String> items) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (String item : items) {
            TextView tv = new TextView(getContext());
            tv.setText("• " + item);
            tv.setTextSize(14);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tv.setPadding(0, 8, 0, 8);
            contenedor.addView(tv);
        }
    }

    private void mostrarEquipamientoEnLista(ViewGroup contenedor, List<String> items) {
        LayoutInflater inflater = LayoutInflater.from(getContext());

        LinearLayout fila = null;
        for (int i = 0; i < items.size(); i++) {
            if (i % 2 == 0) {
                fila = new LinearLayout(getContext());
                fila.setOrientation(LinearLayout.HORIZONTAL);
                fila.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                fila.setPadding(0, 8, 0, 8);
                contenedor.addView(fila);
            }

            TextView tv = new TextView(getContext());
            tv.setText(items.get(i));
            tv.setText("• " + items.get(i));
            tv.setTextSize(14);
            tv.setTextColor(getResources().getColor(android.R.color.black));
            tv.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            if (i % 2 == 0) {
                ((LinearLayout.LayoutParams) tv.getLayoutParams()).setMarginEnd(8);
            }

            fila.addView(tv);
        }
    }


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
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(fotoItem.uri);
                Bitmap bitmapFromUri = BitmapFactory.decodeStream(inputStream);
                imageView.setImageBitmap(bitmapFromUri);
            } catch (FileNotFoundException e) {
                imageView.setImageResource(R.drawable.ic_gallery);
            }
        }

        // Click en la imagen para abrir en pantalla completa
        imageView.setOnClickListener(v -> mostrarImagenCompleta(fotoItem.uri, fotoItem.bitmap));

        // Espaciador para empujar el botón hacia la derecha
        View spacer = new View(requireContext());
        LinearLayout.LayoutParams spacerParams = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f);
        spacer.setLayoutParams(spacerParams);


        // Agregar vistas al contenedor
        container.addView(imageView);
        container.addView(spacer);

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
            try {
                InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri); // ← corrección de this
                Bitmap originalBitmap = BitmapFactory.decodeStream(inputStream);
                Bitmap resizedBitmap = redimensionarBitmap(originalBitmap, maxWidth, maxHeight);
                imageView.setImageBitmap(resizedBitmap);
            } catch (FileNotFoundException e) {
                imageView.setImageResource(R.drawable.ic_gallery);
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


    ////////////////////////METODOS PARA EL GUARDADO DE IMAGENES EN AWS//////////////////////////

    //METODO PRINCIPAL PARA SUBIR TODAS LAS IMÁGENES
    private void subirTodasLasImagenes(Consumer<List<String>> onTodasSubidas, Consumer<String> onError) {
        if (todasLasFotos.isEmpty()) {
            // No hay imágenes, continuar con lista vacía
            onTodasSubidas.accept(new ArrayList<>());
            return;
        }

        List<String> urlsImagenes = new ArrayList<>();
        AtomicInteger contador = new AtomicInteger(0);
        AtomicInteger errores = new AtomicInteger(0);

        // Mostrar progreso
        binding.progressBarGuardar.setVisibility(View.VISIBLE);
        binding.btnRegistrarhabitacion.setText("Subiendo imágenes...");

        for (int i = 0; i < todasLasFotos.size(); i++) {
            FotoItem fotoItem = todasLasFotos.get(i);
            final int index = i;

            subirImagenAWS(fotoItem, index,
                    // OnSuccess
                    url -> {
                        urlsImagenes.add(url);
                        int completadas = contador.incrementAndGet();

                        // Actualizar progreso
                        requireActivity().runOnUiThread(() -> {
                            binding.btnRegistrarhabitacion.setText("Subiendo " + completadas + "/" + todasLasFotos.size());
                        });

                        // Si todas las imágenes se subieron
                        if (completadas == todasLasFotos.size()) {
                            requireActivity().runOnUiThread(() -> {
                                onTodasSubidas.accept(urlsImagenes);
                            });
                        }
                    },
                    // OnError
                    error -> {
                        errores.incrementAndGet();
                        int completadas = contador.incrementAndGet();

                        requireActivity().runOnUiThread(()  -> {
                            Toast.makeText(requireContext(), "Error subiendo imagen " + (index + 1) + ": " + error, Toast.LENGTH_SHORT).show();

                            // Si todas las imágenes se procesaron (exitosas + errores)
                            if (completadas == todasLasFotos.size()) {
                                if (errores.get() == todasLasFotos.size()) {
                                    // Todas fallaron
                                    onError.accept("Error al subir todas las imágenes");
                                } else {
                                    // Algunas se subieron exitosamente
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


    private void guardarHabitacion(HabitacionViewModel viewModel) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "Usuario no autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mostrar overlay oscuro

        // Estado de guardado
        binding.btnRegistrarhabitacion.setEnabled(false);
        binding.progressBarGuardar.setVisibility(View.VISIBLE);
        binding.btnRegistrarhabitacion.setText("Guardando...");

        String idAdministrador = currentUser.getUid();
        db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .document(idAdministrador)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        Toast.makeText(requireContext(), "No se encontró el usuario", Toast.LENGTH_SHORT).show();
                        binding.progressBarGuardar.setVisibility(View.GONE);
                        binding.btnRegistrarhabitacion.setEnabled(true);
                        binding.btnRegistrarhabitacion.setText("Finalizar");
                        return;
                    }

                    String idHotel = documentSnapshot.getString("idHotel");
                    if (idHotel == null || idHotel.isEmpty()) {
                        Toast.makeText(requireContext(), "No se encontró ID de hotel", Toast.LENGTH_SHORT).show();
                        binding.progressBarGuardar.setVisibility(View.GONE);
                        binding.btnRegistrarhabitacion.setEnabled(true);
                        binding.btnRegistrarhabitacion.setText("Finalizar");
                        return;
                    }

                    // Subir las imágenes a AWS
                    subirTodasLasImagenes(urls -> {
                        HabitacionHotel habitacion = viewModel.getHabitacion().getValue();
                        if (habitacion == null) {
                            Toast.makeText(requireContext(), "Error: Habitación vacía", Toast.LENGTH_SHORT).show();
                            binding.progressBarGuardar.setVisibility(View.GONE);
                            binding.btnRegistrarhabitacion.setEnabled(true);
                            binding.btnRegistrarhabitacion.setText("Finalizar");
                            return;
                        }

                        habitacion.setFotosUrls(urls);

                        requireActivity().runOnUiThread(() -> {
                            binding.btnRegistrarhabitacion.setText("Guardando servicio...");
                        });

                        db.collection("hoteles")
                                .document(idHotel)
                                .collection("habitaciones")
                                .add(habitacion)
                                .addOnSuccessListener(docRef -> {
                                    String idGenerado = docRef.getId();

                                    docRef.update("id", idGenerado)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(requireContext(), "Habitación registrada con éxito", Toast.LENGTH_LONG).show();
                                                binding.progressBarGuardar.setVisibility(View.GONE);
                                                binding.btnRegistrarhabitacion.setEnabled(true);
                                                binding.btnRegistrarhabitacion.setText("Finalizar");

                                                Intent resultIntent = new Intent();
                                                resultIntent.putExtra("redireccion", "habitaciones");
                                                requireActivity().setResult(Activity.RESULT_OK, resultIntent);
                                                requireActivity().finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(requireContext(), "Error al guardar ID: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                binding.progressBarGuardar.setVisibility(View.GONE);
                                                binding.btnRegistrarhabitacion.setEnabled(true);
                                                binding.btnRegistrarhabitacion.setText("Finalizar");
                                            });
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(requireContext(), "Error al guardar habitación: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.progressBarGuardar.setVisibility(View.GONE);
                                    binding.btnRegistrarhabitacion.setEnabled(true);
                                    binding.btnRegistrarhabitacion.setText("Finalizar");
                                });

                    }, error -> {
                        Toast.makeText(requireContext(), "Error al subir imágenes: " + error, Toast.LENGTH_SHORT).show();
                        binding.progressBarGuardar.setVisibility(View.GONE);
                        binding.btnRegistrarhabitacion.setEnabled(true);
                        binding.btnRegistrarhabitacion.setText("Finalizar");
                    });

                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Error al obtener usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBarGuardar.setVisibility(View.GONE);
                    binding.btnRegistrarhabitacion.setEnabled(true);
                    binding.btnRegistrarhabitacion.setText("Finalizar");
                });
    }





    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}